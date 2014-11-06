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

import org.exoplatform.brandadvocacy.model.*;
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
public class MissionParticipantDAO extends DAO {

  private static final Log log = ExoLogger.getLogger(MissionParticipantDAO.class);

  public static final String node_prop_labelID = "exo:labelID";
  public static final String node_prop_mission_id = "exo:mission_id";
  public static final String node_prop_proposition_id = "exo:proposition_id";
  public static final String node_prop_participant_username = "exo:participant_username";
  public static final String node_prop_url_submitted = "exo:url_submitted";
  public static final String node_prop_address_id = "exo:address_id";
  public static final String node_prop_size = "exo:size";
  public static final String node_prop_status = "exo:status";
  public static final String node_prop_date_submitted = "exo:date_submitted";
  public static final String node_prop_dateCreated = "exo:dateCreated";
  public static final String node_prop_modifiedDate = "exo:modifiedDate";

  public MissionParticipantDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }
  private void setProperties(Node aNode,MissionParticipant missionParticipant) throws RepositoryException {

    aNode.setProperty(node_prop_labelID,missionParticipant.getLabelID());
    aNode.setProperty(node_prop_mission_id,missionParticipant.getMission_id());
    aNode.setProperty(node_prop_proposition_id, missionParticipant.getProposition_id());
    aNode.setProperty(node_prop_participant_username,missionParticipant.getParticipant_username());
    aNode.setProperty(node_prop_url_submitted,missionParticipant.getUrl_submitted());
    aNode.setProperty(node_prop_address_id,missionParticipant.getAddress_id());
    aNode.setProperty(node_prop_size,missionParticipant.getSize().getValue());
    aNode.setProperty(node_prop_status,missionParticipant.getStatus().getValue());
    aNode.setProperty(node_prop_date_submitted,missionParticipant.getDate_submitted());
    aNode.setProperty(node_prop_dateCreated,missionParticipant.getCreatedDate());
    aNode.setProperty(node_prop_modifiedDate,missionParticipant.getModifiedDate());

  }
  private MissionParticipant transferNode2Object(Node node) throws RepositoryException{
    if (null == node)
      return null;
    MissionParticipant missionParticipant = new MissionParticipant();
    missionParticipant.setId(node.getUUID());
    PropertyIterator iter = node.getProperties("exo:*");
    while (iter.hasNext()) {
      Property p = (Property) iter.next();
      String name = p.getName();
      if (name.equals(node_prop_labelID)){
        missionParticipant.setLabelID(p.getString());
      }
      else if(name.equals(node_prop_mission_id)){
        missionParticipant.setMission_id(p.getString());
      } else if(name.equals(node_prop_mission_id)){
        missionParticipant.setMission_id(p.getString());
      } else if(name.equals(node_prop_proposition_id)){
        missionParticipant.setProposition_id(p.getString());
      } else if (name.equals(node_prop_participant_username)){
        missionParticipant.setParticipant_username(p.getString());
      } else if (name.equals(node_prop_url_submitted)){
        missionParticipant.setUrl_submitted(p.getString());
      } else if (name.equals(node_prop_address_id)){
        missionParticipant.setAddress_id(p.getString());
      } else if (name.equals(node_prop_size)){
        missionParticipant.setSize(Size.getSize((int) p.getLong()));
      } else if (name.equals(node_prop_status)){
        missionParticipant.setStatus(Status.getStatus((int) p.getLong()));
      } else if (name.equals(node_prop_date_submitted)){
        missionParticipant.setDate_submitted(p.getLong());
      } else if (name.equals(node_prop_dateCreated)){
        missionParticipant.setCreatedDate(p.getLong());
      } else if(name.equals(node_prop_modifiedDate)){
        missionParticipant.setModifiedDate(p.getLong());
      }
    }
    try{
      missionParticipant.checkValid();
      return missionParticipant;
    }catch (BrandAdvocacyServiceException brade){
      log.error(" ERROR cannot transfert node to mission participant "+brade.getMessage());
    }
    return null;
  }
  private List<MissionParticipant> transferNodes2Objects(List<Node> nodes){
    List<MissionParticipant> missionParticipants = new ArrayList<MissionParticipant>();
    MissionParticipant missionParticipant;
    for (Node node:nodes){
      try {
        missionParticipant = this.transferNode2Object(node);
        if (null != missionParticipant)
          missionParticipants.add(missionParticipant);
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return missionParticipants;
  }
  public Node getOrCreateMissionParticipantHome(String programId) {
    if (null == programId || "".equals(programId)){
      log.error("ERROR cannot get or create mission participant home in program null");
      return null;
    }
    try {
      Node node = this.getNodeById(programId);
      return this.getJcrImplService().getProgramDAO().getOrCreateMissionParticipantHome(node);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return null;
  }
  private List<MissionParticipant> sortByDate(List<MissionParticipant> missionParticipants){
    List<MissionParticipant> result = new LinkedList<MissionParticipant>();
    result.addAll(missionParticipants);
    Collections.sort(result,new Comparator<MissionParticipant>() {
      @Override
      public int compare(MissionParticipant o1, MissionParticipant o2) {
        return (int)(o2.getCreatedDate() - o1.getCreatedDate());
      }
    });
    return result;
  }
  public List<MissionParticipant> searchMissionParticipants(org.exoplatform.brandadvocacy.model.Query query){
    Program program = this.getJcrImplService().getProgramDAO().getProgramById(query.getProgramId());
    if (null != program){
      StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.MISSION_PARTICIPANT_NODE_TYPE +" where ");
      sql.append("jcr:path like '");
      sql.append(JCRImpl.EXTENSION_PATH).append("/").append(Utils.queryEscape(program.getLabelID())).append("/").append(ProgramDAO.node_prop_missionparticipants);
      sql.append("/%'");
      if (null != query.getKeyword()) {
        sql.append(" AND ( ").append(node_prop_labelID).append(" like '%" + query.getKeyword() + "%'");
        sql.append(" OR ").append(node_prop_participant_username).append(" like '%" + query.getKeyword() + "%' ");
        sql.append(" ) ");
      }
      if (query.getStatus() != 0){
        sql.append(" AND ").append(node_prop_status).append(" = '").append(query.getStatus()).append("'");
      }
      sql.append(" ORDER BY "+node_prop_dateCreated+" DESC ");
      List<Node> nodes =  this.getNodesByQuery(sql.toString(),query.getOffset(),query.getLimit());
      return this.transferNodes2Objects(nodes);
    }
    return null;
  }

  public int getTotalMissionParticipants(org.exoplatform.brandadvocacy.model.Query query){

    Program program = this.getJcrImplService().getProgramDAO().getProgramById(query.getProgramId());
    if (null != program) {
      StringBuilder sql = new StringBuilder("select * from " + JCRImpl.MISSION_PARTICIPANT_NODE_TYPE + " where ");
      sql.append("jcr:path like '");
      sql.append(JCRImpl.EXTENSION_PATH).append("/").append(Utils.queryEscape(program.getLabelID())).append("/").append(ProgramDAO.node_prop_missionparticipants);
      sql.append("/%'");
      if (null != query.getKeyword()) {
        sql.append(" AND ( ").append(node_prop_labelID).append(" like '%" + query.getKeyword() + "%'");
        sql.append(" OR ").append(node_prop_participant_username).append(" like '%" + query.getKeyword() + "%' ");
        sql.append(" ) ");
      }
      if (query.getStatus() != 0) {
        sql.append(" AND ").append(node_prop_status).append(" = '").append(query.getStatus()).append("'");
      }
      List<Node> nodes = this.getNodesByQuery(sql.toString(), 0, 0);
      return nodes.size();
    }
    return 0;
  }

  public MissionParticipant addMissionParticipant2Program(String programId, MissionParticipant missionParticipant){
    try{
      missionParticipant.checkValid();
      Node missionParticipantHome = this.getOrCreateMissionParticipantHome(programId);
      if (null != missionParticipantHome){
        Node missionParticipantNode = null;
        if (!missionParticipantHome.hasNode(missionParticipant.getLabelID()))
          missionParticipantNode =  missionParticipantHome.addNode(missionParticipant.getLabelID(),JCRImpl.MISSION_PARTICIPANT_NODE_TYPE);
        else
          missionParticipantNode = missionParticipantHome.getNode(missionParticipant.getLabelID());
        if (null != missionParticipantNode){
          this.setProperties(missionParticipantNode,missionParticipant);
          missionParticipantHome.save();
          return this.transferNode2Object(missionParticipantNode);
        }
      }
    }
    catch (ItemExistsException ie){
      log.error(" === ERROR cannot add existing item "+ie.getMessage());
    }
    catch (RepositoryException e) {
      log.error("=== ERROR cannot add mission participant in "+missionParticipant.getMission_id()+" - Exception "+e.getMessage());
    }
    catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
    return null;
  }
  public MissionParticipant updateMissionParticipantInProgram(String programId, MissionParticipant missionParticipant){
    try {
      missionParticipant.checkValid();
      Node missionParticipantHome = this.getOrCreateMissionParticipantHome(programId);
      if (null != missionParticipantHome && missionParticipantHome.hasNode(missionParticipant.getLabelID())){
        Node missionParticipantNode = missionParticipantHome.getNode(missionParticipant.getLabelID());
        if(null != missionParticipantNode){
          this.setProperties(missionParticipantNode,missionParticipant);
           missionParticipantNode.save();
          return this.transferNode2Object(missionParticipantNode);
        }else
          throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_PARTICIPANT_NOT_EXISTS," cannot update mission participant not exists");
      }
    }
    catch (RepositoryException e) {
      log.error("=== ERROR cannot add mission participant in "+missionParticipant.getMission_id()+" - Exception "+e.getMessage());
    }
    catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
    return null;
  }

  public List<MissionParticipant> getAllMissionParticipantsInProgram(String programId){

    Set<MissionParticipant> missionParticipants = new HashSet<MissionParticipant>();
    try{
      Node missionParticipantHome = this.getOrCreateMissionParticipantHome(programId);
      if (null != missionParticipantHome){
        NodeIterator nodes =  missionParticipantHome.getNodes();
        MissionParticipant missionParticipant;
        while (nodes.hasNext()){
          try{
            missionParticipant = this.transferNode2Object(nodes.nextNode());
            if (null != missionParticipant)
              missionParticipants.add(missionParticipant);
          }catch (RepositoryException re){
            log.error(" === ERROR cannot get mission participant node list ");
          }
        }
        List<MissionParticipant> result = new LinkedList<MissionParticipant>();
        result.addAll(missionParticipants);
        Collections.sort(result,new Comparator<MissionParticipant>() {
          @Override
          public int compare(MissionParticipant o1, MissionParticipant o2) {
            return (int)(o2.getCreatedDate() - o1.getCreatedDate());
          }
        });
        return result;
      }
    }catch (RepositoryException re){
      log.error("=== ERROR cannot find all mission participants");
    }
    return null;

  }
  public List<MissionParticipant> getAllMissionParticipantsInProgramByParticipant(String programId,String username){

    List<MissionParticipant> missionParticipants = new ArrayList<MissionParticipant>();
    try {
      Participant participant = this.getJcrImplService().getParticipantDAO().getParticipantInProgramByUserName(programId,username);
      if (null != participant){
        Set<String> mpids = participant.getMission_participant_ids();
        MissionParticipant missionParticipant;
        for (String mpid:mpids){
          missionParticipant = this.transferNode2Object(this.getNodeById(mpid));
          if (null != missionParticipant)
            missionParticipants.add(missionParticipant);
        }
        return this.sortByDate(missionParticipants);
      }
    } catch (RepositoryException e) {
      log.error("=== ERROR getAllMissionParticipantsByParticipant: cannot transfer node to object "+username);
      e.printStackTrace();
    }
    return missionParticipants;
  }

  public MissionParticipant getMissionParticipantById(String mpId){
    if (null == mpId || "".equals(mpId))
      return null;
    try {
      Node node = this.getNodeById(mpId);
      if(null != node)
        return this.transferNode2Object(node);

    } catch (RepositoryException e) {
      log.error(" brad ERROR: cannot get mission participant by id ");
    }
    return null;
  }

  public int getTotalNumberMPByParticipant(String programId,int status,String username){
    Program program = this.getJcrImplService().getProgramDAO().getProgramById(programId);
    if (null != program){
      StringBuilder sql = new StringBuilder("select jcr:uuid from "+ JCRImpl.MISSION_PARTICIPANT_NODE_TYPE +" where ");
      sql.append("jcr:path like '");
      sql.append(JCRImpl.EXTENSION_PATH).append("/").append(Utils.queryEscape(program.getLabelID())).append("/").append(ProgramDAO.node_prop_missionparticipants);
      sql.append("'");
      sql.append(" AND ").append(node_prop_participant_username).append(" = '").append(username).append("'");
      if (status != 0){
        sql.append(" AND ").append(node_prop_status).append(" = '").append(status).append("'");
      }
      return this.getNodesByQuery(sql.toString(),0,0).size();
    }
    return 0;
  }

  public void removeMissionParticipant(String id){
    try {
      Node node = this.getNodeById(id); //this.getNodeByLabelID(proposition.getMission_id(), proposition.getId());
      if(null != node){
        Session session = node.getSession();
        node.remove();
        session.save();
      }else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PROPOSITION_NOT_EXISTS," cannot remove proposition not exists");
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot removeMissionParticipant "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
  }
  public MissionParticipant getCurrentMissionParticipantByUserName(String programId,String username){
    Program program = this.getJcrImplService().getProgramDAO().getProgramById(programId);
    if (null != program){
      StringBuilder sql = new StringBuilder("select jcr:uuid from "+ JCRImpl.MISSION_PARTICIPANT_NODE_TYPE +" where ");
      sql.append("jcr:path like '");
      sql.append(JCRImpl.EXTENSION_PATH).append("/").append(Utils.queryEscape(program.getLabelID())).append("/").append(ProgramDAO.node_prop_missionparticipants);
      sql.append("/%'");
      sql.append(" AND ").append(node_prop_participant_username).append(" = '").append(username).append("'");
      sql.append(" AND ").append(node_prop_address_id).append(" IS NULL ");
      sql.append(" AND (").append(node_prop_status).append("=").append("1 OR " ).append(node_prop_status).append("=").append(" 2 ) ");
      List<MissionParticipant> missionParticipants = this.transferNodes2Objects(this.getNodesByQuery(sql.toString(),0,1));
      if (null != missionParticipants && missionParticipants.size() > 0){
        return missionParticipants.get(0);
      }
    }
    return null;
  }
}