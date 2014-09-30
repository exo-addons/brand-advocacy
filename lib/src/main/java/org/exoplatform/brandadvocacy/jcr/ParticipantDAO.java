/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.brandadvocacy.jcr;

import org.exoplatform.brandadvocacy.model.Participant;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.*;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class ParticipantDAO extends DAO {

  private static final String node_prop_username = "exo:username";
  private static final String node_prop_mission_participant_ids = "exo:mission_participant_ids";
  public static final String node_prop_addresses = "exo:addresseslist";
  private static final Log log = ExoLogger.getLogger(ParticipantDAO.class);
  public ParticipantDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }

  public void setProperties(Node aNode,Participant participant) throws RepositoryException {

    Set<String> missionParticipantIds = participant.getMission_participant_ids();
    aNode.setProperty(node_prop_mission_participant_ids,missionParticipantIds.toArray(new String[missionParticipantIds.size()]));
    aNode.setProperty(node_prop_username,participant.getUserName());
  }
  public Participant transferNode2Object(Node node) throws RepositoryException{
    Participant participant = new Participant();
    PropertyIterator iter = node.getProperties("exo:*");
    while (iter.hasNext()) {
      Property p = (Property) iter.next();
      String name = p.getName();
      if(name.equals(node_prop_mission_participant_ids)){
        Set<String> missionParticipantIds = new HashSet<String>();
        for (Value mpid:p.getValues()){
          missionParticipantIds.add(mpid.getString());
        }
        participant.setMission_participant_ids(missionParticipantIds);
      }
      else if(name.equals(node_prop_username)){
        participant.setUserName(p.getString());
      }
    }
    return participant;
  }
  public Node getOrCreateParticipantHome() {
    String path = String.format("%s/%s",JCRImpl.EXTENSION_PATH,JCRImpl.PARTICIPANT_PATH);
    return this.getJcrImplService().getOrCreateNode(path);
  }
  public Node getOrCreateAddressHome(Node participantNode) throws RepositoryException {

    Node addressHome = null;
    try {
      addressHome = participantNode.getNode(node_prop_addresses);
    } catch (RepositoryException e) {
      log.error("address list node not exists");
    }
    if(null == addressHome){
      try {
        addressHome = participantNode.addNode(node_prop_addresses,JCRImpl.ADDRESS_LIST_NODE_TYPE);
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return addressHome;
  }
  public Node getNodeByUserName(String username){
    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.PARTICIPANT_NODE_TYPE);
    sql.append(" WHERE ").append(node_prop_username).append(" like '").append(username).append("'");
    Session session;
    try {
      session = this.getJcrImplService().getSession();
      Query query = session.getWorkspace().getQueryManager().createQuery(sql.toString(), Query.SQL);
      QueryResult result = query.execute();
      NodeIterator nodes = result.getNodes();
      if (nodes.hasNext()) {
        return nodes.nextNode();
      }else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PARTICIPANT_NOT_EXISTS,"cannot get participant "+username);
    } catch (RepositoryException e) {
      log.error("ERROR cannot get participant node  "+ username +" Exception "+e.getMessage());
    }
    return null;
  }
  public Node getParticipantNode(String pid){
    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.PARTICIPANT_NODE_TYPE +" where jcr:path like '");
    sql.append(JCRImpl.EXTENSION_PATH).append("/").append(JCRImpl.PARTICIPANT_PATH);
    sql.append("/").append(Utils.queryEscape(pid));
    sql.append("'");
    Session session;
    try {
      session = this.getJcrImplService().getSession();
      Query query = session.getWorkspace().getQueryManager().createQuery(sql.toString(), Query.SQL);
      QueryResult result = query.execute();
      NodeIterator nodes = result.getNodes();
      if (nodes.hasNext()) {
        return nodes.nextNode();
      }else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PARTICIPANT_NOT_EXISTS,"cannot get participant "+pid);
    } catch (RepositoryException e) {
      log.error("ERROR cannot get participant node  "+ pid +" Exception "+e.getMessage());
    }
    return null;
  }
  public Participant addParticipant(Participant participant){
    try{
      participant.checkValid();
      Node participantHomeNode = this.getOrCreateParticipantHome();
      if(null != participantHomeNode){
        Node participantNode = null;
        if (participantHomeNode.hasNode(participant.getUserName())) {
          participantNode = participantHomeNode.getNode(participant.getUserName());
          Participant existingParticipant = this.transferNode2Object(participantNode);
          Set<String> newMPIds = participant.getMission_participant_ids();
          for (String mpid:existingParticipant.getMission_participant_ids()){
            newMPIds.add(mpid);
          }
          participant.setMission_participant_ids(newMPIds);
        }
        else{
          participantNode = participantHomeNode.addNode(participant.getUserName(),JCRImpl.PARTICIPANT_NODE_TYPE);
        }
        if(null != participantNode) {
          this.setProperties(participantNode,participant);
          participantHomeNode.getSession().save();
          return participant;
        }
      }

    }catch (ItemExistsException ie){
      log.error("=== ERROR cannot add existing participant "+participant.getUserName()+" - exception "+ie.getMessage());
    }
    catch (RepositoryException re){
      log.error(" === ERROR cannot add participant "+participant.getUserName()+" - exception ");
      re.printStackTrace();
    }
    catch (BrandAdvocacyServiceException brade){
      log.error(" === ERROR add participant "+ brade.getMessage());

    }
    return null;
  }

  public List<Participant> getAllParticipants(){
    List<Participant> participants = new ArrayList<Participant>();
    try {
      Node participantHomeNode = this.getOrCreateParticipantHome();
      if(null != participantHomeNode){
        NodeIterator nodes = participantHomeNode.getNodes();
        Node participantNode = null;
        Participant participant = null;
        while (nodes.hasNext()) {
          participantNode = (Node) nodes.next();
          participant = this.transferNode2Object(participantNode);
          participant.checkValid();
          participants.add(participant);
        }
        return participants;
      }
      else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PARTICIPANT_NOT_EXISTS,"cannot find participant home node");
    } catch (RepositoryException e) {
      log.error("==== ERROR get all participants "+e.getMessage() );
    }
    return participants;
  }
}
