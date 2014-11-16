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

  private static final String node_prop_program_id = "exo:program_id";
  private static final String node_prop_username = "exo:username";
  private static final String node_prop_mission_participant_ids = "exo:mission_participant_ids";
  private static final String node_prop_mission_ids = "exo:mission_ids";
  public static final String node_prop_addresses = "exo:addresseslist";
  private static final Log log = ExoLogger.getLogger(ParticipantDAO.class);
  public ParticipantDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }

  private void setProperties(Node aNode,Participant participant) throws RepositoryException {
    try{
      participant.checkValid();
      Set<String> missionParticipantIds = participant.getMission_participant_ids();
      Set<String> missionIds = participant.getMission_ids();
      aNode.setProperty(node_prop_program_id,participant.getProgramId());
      aNode.setProperty(node_prop_mission_participant_ids,missionParticipantIds.toArray(new String[missionParticipantIds.size()]));
      aNode.setProperty(node_prop_mission_ids,missionIds.toArray(new String[missionIds.size()]));
      aNode.setProperty(node_prop_username,participant.getUserName());

    }catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot set peroperties for participant node "+brade.getMessage());
    }
  }
  public Participant transferNode2Object(Node node) throws RepositoryException{
    if (null == node)
      return null;
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
      else if(name.equals(node_prop_mission_ids)){
        Set<String> missionIds = new HashSet<String>();
        for (Value mids:p.getValues()){
          missionIds.add(mids.getString());
        }
        participant.setMission_ids(missionIds);
      }
      else if(name.equals(node_prop_username)){
        participant.setUserName(p.getString());
      }
      else if(name.equals(node_prop_program_id)) {
        participant.setProgramId(p.getString());
      }
    }
    try {
      participant.checkValid();
      return participant;
    }catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot transfert node to participant obj "+brade.getMessage());
    }
    return null;
  }
  private Node getOrCreateParticipantHome(String programId){
    if (null == programId || "".equals(programId)){
      log.error("ERROR cannot get or create participant home in program null");
      return null;
    }
    Node programNode = null;
    try {
      programNode = this.getNodeById(programId);
      if (null != programNode){
        return this.getJcrImplService().getProgramDAO().getOrCreateParticipantHome(programNode);
      }
    } catch (RepositoryException e) {
    }
    return null;
  }
  public Node getOrCreateAddressHome(Node participantNode) {
    try {
      return this.getOrCreateNodeCommon(participantNode,node_prop_addresses,JCRImpl.ADDRESS_LIST_NODE_TYPE);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return null;
  }
  public Node getNodeByUserName(String programId,String username){
    Node programNode = null;
    try {
      programNode = this.getJcrImplService().getProgramDAO().getNodeById(programId);
      if (null != programNode){
        Node homeNode = this.getJcrImplService().getProgramDAO().getOrCreateParticipantHome(programNode);
        if (null != homeNode && homeNode.hasNode(username)){
          return homeNode.getNode(username);
        }
      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Node getNodeByQuery(String programId,String username){
    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.PARTICIPANT_NODE_TYPE);
    sql.append(" WHERE ").append(" jcr:path like '/").append( JCRImpl.EXTENSION_PATH).append("/").append(programId);
    sql.append("/").append(ProgramDAO.node_prop_participants).append("/%'");
    sql.append(" AND ").append(node_prop_username).append(" like '").append(username).append("'");
    sql.append(" AND ").append(node_prop_program_id).append(" = '").append(programId).append("'");
    List<Node> nodes =  this.getNodesByQuery(sql.toString(),0,1);
    if (nodes.size() > 0) {
      return nodes.get(0);
    }
    return null;
  }

  public Participant addParticipant2Program(Participant participant){
    try{
      participant.checkValid();
      String programId = participant.getProgramId();
      Node participantHomeNode = this.getOrCreateParticipantHome(programId);
      if(null != participantHomeNode){
        Node participantNode = null;
        if (participantHomeNode.hasNode(participant.getUserName())) {
          participantNode = participantHomeNode.getNode(participant.getUserName());
          Participant existingParticipant = this.transferNode2Object(participantNode);
          Set<String> newMPIds = participant.getMission_participant_ids();
          for (String mpid:existingParticipant.getMission_participant_ids()){
            if(!newMPIds.contains(mpid))
              newMPIds.add(mpid);
          }
          participant.setMission_participant_ids(newMPIds);

          Set<String> newMIds = participant.getMission_ids();
          for (String mid:existingParticipant.getMission_ids()){
            if(!newMIds.contains(mid))
              newMIds.add(mid);
          }
          participant.setMission_ids(newMIds);
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

  public List<Participant> getAllParticipantsInProgram(String programId){
    List<Participant> participants = new ArrayList<Participant>();
    try {
      Node participantHomeNode = this.getOrCreateParticipantHome(programId);
      if(null != participantHomeNode){
        NodeIterator nodes = participantHomeNode.getNodes();
        Node participantNode = null;
        Participant participant = null;
        while (nodes.hasNext()) {
          participantNode = (Node) nodes.next();
          participant = this.transferNode2Object(participantNode);
          if (null != participant)
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

  public Participant getParticipantInProgramByUserName(String programId,String username){
    try {
      return this.transferNode2Object(this.getNodeByUserName(programId,username));
    } catch (RepositoryException e) {
      log.error("ERROR cannot get participant by username");
    }
    return null;
  }
  public Boolean removeMissionParticipant(String programId, String userName, String missionParticipantId){
    Node participantNode = this.getNodeByUserName(programId,userName);

    if(null != participantNode){
      try {
        Participant participant = this.transferNode2Object(participantNode);
        if (null != participant){
          Set<String> mpIds = participant.getMission_participant_ids();
          if (mpIds.contains(missionParticipantId)){
            if(mpIds.remove(missionParticipantId)){
              participant.setMission_participant_ids(mpIds);
              this.setProperties(participantNode,participant);
              participantNode.save();
              return true;
            }
          }
        }
      } catch (RepositoryException e) {
        log.error("Error remove mission participant "+e.getMessage());
      }
    }

    return false;
  }

}
