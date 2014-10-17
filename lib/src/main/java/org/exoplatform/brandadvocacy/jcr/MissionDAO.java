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

import com.google.common.collect.Lists;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Participant;
import org.exoplatform.brandadvocacy.model.Priority;
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
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Sep
 * 9, 2014
 */
public class MissionDAO extends DAO {

  private static final Log   log             = ExoLogger.getLogger(MissionDAO.class);

  public static final String MISSIONS_PATH   = "Missions";

  public static final String node_prop_labelID = "exo:labelID";
  public static final String node_prop_title = "exo:title";  
  public static final String node_prop_third_party_link = "exo:third_party_link";
  public static final String node_prop_priority = "exo:priority";
  public static final String node_prop_active = "exo:active";
  public static final String node_prop_dateCreated = "exo:dateCreated";
  public static final String node_prop_modifiedDate = "exo:modifiedDate";
  public static final String node_prop_managers = "exo:managerslist";
  public static final String node_prop_propositions = "exo:propositionslist";
  
  public MissionDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }

  public Node getOrCreateMissionHome(String programId) {
    if(null == programId || "".equals(programId)){
      log.error("ERROR cannot get mission home for an invalid program id ");
      return null;
    }
    try {
      Node node = this.getNodeById(programId);
      return this.getJcrImplService().getProgramDAO().getOrCreateMissionHome(node);
    } catch (RepositoryException e) {
      log.error("ERROR cannot get mission home for an invalid program "+e.getMessage());
    }
    return null;
  }
  public Node getOrCreateManagerHome(Node missionNode) throws RepositoryException {

    Node managerHome = null;
    try {
      managerHome = missionNode.getNode(node_prop_managers);
    } catch (RepositoryException e) {
      log.error("managers list node not exists");
    }
    if(null == managerHome){
      try {
        managerHome = missionNode.addNode(node_prop_managers,JCRImpl.MANAGER_LIST_NODE_TYPE);
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return managerHome;
  }

  public Node getOrCreatePropositionHome(Node missionNode) throws RepositoryException {

    Node propostionHome = null;
    try {
      propostionHome = missionNode.getNode(node_prop_propositions);
    } catch (RepositoryException e) {
      log.error("prositions list node not exists");
    }
    if(null == propostionHome){
      try {
        propostionHome = missionNode.addNode(node_prop_propositions,JCRImpl.PROPOSITION_LIST_NODE_TYPE);
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return propostionHome;
  }

  private void setPropertiesNode(Node missionNode, Mission m) throws RepositoryException {
    if(null != m.getLabelID() && !"".equals(m.getLabelID()))
      missionNode.setProperty(node_prop_labelID,m.getLabelID());
    missionNode.setProperty(node_prop_title, m.getTitle());
    missionNode.setProperty(node_prop_third_party_link, m.getThird_party_link());
    missionNode.setProperty(node_prop_priority, m.getPriority().getValue());
    missionNode.setProperty(node_prop_active, m.getActive());
    if (0 != m.getCreatedDate())
      missionNode.setProperty(node_prop_dateCreated, m.getCreatedDate());
    if (0 != m.getModifiedDate())
      missionNode.setProperty(node_prop_modifiedDate,m.getModifiedDate());
  }
  public Mission transferNode2Object(Node node) throws RepositoryException {
    Mission m = new Mission();
    m.setId(node.getUUID());
    PropertyIterator iter = node.getProperties("exo:*");
    iter =  node.getProperties();
    Property p;
    String name;
    while (iter.hasNext()) {
      p = iter.nextProperty();
      name = p.getName();
      if (name.equals(node_prop_labelID)) {
        m.setLabelID(p.getString());
      } else if (name.equals(node_prop_title)) {
        m.setTitle(p.getString());
      } else if (name.equals(node_prop_third_party_link)) {
        m.setThird_party_link(p.getString());
      } else if(name.equals(node_prop_priority)){
        m.setPriority(Priority.getPriority((int) p.getLong()));
      } else if(name.equals(node_prop_active)){
        m.setActive(p.getBoolean());
      } else if (name.equals(node_prop_dateCreated)) {
        m.setCreatedDate(p.getLong());
      }
    }
    try {
      m.checkValid();
      return m;
    }
    catch (BrandAdvocacyServiceException brade){
      log.error(" ERROR cannot tranfert node to mission object "+brade.getMessage());
    }
    return null;
  }

  public List<Mission> transferNodes2Objects(List<Node> nodes) {
    List<Mission> missions = new ArrayList<Mission>(nodes.size());
    Mission mission;
    for (Node node:nodes){
      try {
        mission = this.transferNode2Object(node);
        if (null != mission)
          missions.add(mission);
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return missions;
  }
  public Mission addMission2Program(String programId,Mission mission) {
    try{
      mission.checkValid();
      Node homeMissionNode = this.getOrCreateMissionHome(programId);
      if (null != homeMissionNode){
        Node missionNode = homeMissionNode.addNode(mission.getLabelID(),JCRImpl.MISSION_NODE_TYPE);
        this.setPropertiesNode(missionNode,mission);
        homeMissionNode.getSession().save();
        return this.transferNode2Object(missionNode);
      }
    }catch (RepositoryException re){
      log.error("ERROR cannot add mission 2 program "+re.getMessage());
    } catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot add mission 2 program "+brade.getMessage());
    }
    return null;
  }
  public List<Mission> getAllMissionsByProgramId(String programId){
    List<Mission> missions = new ArrayList<Mission>();
    try {
      Node missionHome = this.getOrCreateMissionHome(programId);
      if (null != missionHome){
        NodeIterator nodes =  missionHome.getNodes();
        return this.transferNodes2Objects(Lists.newArrayList(nodes));
      }
    } catch (RepositoryException e) {
      log.error("ERROR cannot get all mission in "+programId);
    }
    return null;
  }
  public Mission getMissionById(String id) {
    if(null == id || "".equals(id)){
      log.error("ERROR cannot get mission by id");
      return null;
    }
    Node aNode = null;
    try {
      aNode = this.getNodeById(id);
      if(aNode != null)
        return this.transferNode2Object(aNode);
    } catch (RepositoryException e) {
      log.error("ERROR cannot get mission by id "+e.getStackTrace());
    }

    return null;
  }
  public Mission updateMission(Mission m){
    try {
      m.checkValid();      
      Node missiondeNode = this.getNodeById(m.getId());
      if (null == missiondeNode) {
      
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_NOT_EXISTS, "cannot update mission not exist "+m.getLabelID());
        
      }
      if (null != missiondeNode && m.getLabelID().equals( missiondeNode.getProperty(node_prop_labelID).getString()) ) {
        this.setPropertiesNode(missiondeNode, m);
        missiondeNode.save();
        return this.transferNode2Object(missiondeNode);
      }

    } catch (BrandAdvocacyServiceException e) {
      log.error("ERROR cannot update mission with empty title");
    } catch (RepositoryException e) {
      log.error("ERROR update mission "+e.getMessage());
    }
    return null;
  }
  public void removeMissionById(String id){
    if (null == id || "".equals(id)) {
      log.error("ERROR cannot remove mission null");
      return;
    }
    try {
        Node aNode = this.getNodeById(id);
        if (aNode != null) {
            Session session = aNode.getSession();
            aNode.remove();
            session.save();
        }
    } catch (RepositoryException e) {
        log.error(" ERROR remove mission "+id+" === Exception "+e.getMessage());
    }
  }
  
  public void setActiveMission(String id,Boolean isActive){
    try{
      if(null == id || "".equals(id))
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"cannot set active for invalid id "+id);
      Node aNode = this.getNodeById(id);
      if(null == aNode)
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_NOT_EXISTS,"cannot set active for mission not exist "+id);
      aNode.setProperty(node_prop_active, isActive);
    }catch(BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }catch(RepositoryException re){
      log.error(" ERROR set active mission "+re.getMessage());
    }
  }

  public int getTotalNumberMissions(Boolean isPublic, Boolean isActive,int priority){
    StringBuilder sql = new StringBuilder("select jcr:uuid from "+ JCRImpl.MISSION_NODE_TYPE +" where ");

    if(isPublic){
      sql.append(node_prop_active).append(" = 'true' ");
    }else{
      sql.append(node_prop_active).append("='").append(isActive).append("'");
    }

    if(priority != 0){
      sql.append(" AND ").append(node_prop_priority).append("=").append(priority);
    }
    return this.getNodesByQuery(sql.toString(),0,0).size();
  }

  public Mission getRandomMission(String username){

    if(this.getTotalNumberMissions(true,true,0) == 0)
      return null;
    List<String> mission_prio1_Ids = this.getMissionIdsByPriority(Priority.PRIORITY_1.getValue());
    if (mission_prio1_Ids.size() == 0)
      mission_prio1_Ids = this.getMissionIdsByPriority(Priority.PRIORITY_2.getValue());
    if (mission_prio1_Ids.size() == 0)
      mission_prio1_Ids = this.getMissionIdsByPriority(Priority.PRIORITY_3.getValue());

    List<String> missionIds = mission_prio1_Ids;
    Map<Integer,List<String>> mission_used_ids = this.getMissionIdsByParticipant(username);
    if(mission_used_ids.size() > 0){

      List<String> mission_used_prio1_ids = mission_used_ids.get(Priority.PRIORITY_1.getValue());
      List<String> mission_used_prio2_ids = mission_used_ids.get(Priority.PRIORITY_2.getValue());
      List<String> mission_used_prio3_ids = mission_used_ids.get(Priority.PRIORITY_3.getValue());
      if(mission_used_prio1_ids.size() == mission_prio1_Ids.size()){

        List<String> mission_prio2_Ids = this.getMissionIdsByPriority(Priority.PRIORITY_2.getValue());
        if(mission_used_prio2_ids.size() == mission_prio2_Ids.size()){
          List<String> mission_prio3_Ids = this.getMissionIdsByPriority(Priority.PRIORITY_3.getValue());
          if(mission_prio3_Ids.size() == mission_used_prio3_ids.size()){
            missionIds = mission_prio1_Ids;
          }else{
            if(mission_prio3_Ids.removeAll(mission_used_prio3_ids)){
              missionIds = mission_prio3_Ids;
            }
          }

        }else{
          if(mission_prio2_Ids.removeAll(mission_used_prio2_ids)){
            missionIds = mission_prio2_Ids;
          }
        }
      }else{
        if(mission_prio1_Ids.removeAll(mission_used_prio1_ids)){
          missionIds = mission_prio1_Ids;
        }
      }
    }
    Random random = new Random();
    int nb = missionIds.size();
    if(nb > 0){
      String mid = missionIds.get(random.nextInt(nb));
      try {
        Node missionNode = this.getNodeById(mid);
        return this.transferNode2Object(missionNode);
      } catch (RepositoryException e) {
        log.error("cannot get random mission for "+username);
      }
    }
    return null;
  }
  public List<Mission> getAllMissionsByParticipant(String username){

    List<Mission> missions = new ArrayList<Mission>();
    ParticipantDAO participantDAO = this.getJcrImplService().getParticipantDAO();
    Node participantNode = participantDAO.getNodeByUserName(username);
    if (null != participantNode){
      Participant participant = null;
      try {
        participant = participantDAO.transferNode2Object(participantNode);
        Set<String> mids = participant.getMission_ids();
        Mission mission;
        for (String mid:mids){
          mission = this.transferNode2Object(this.getNodeById(mid));
          mission.checkValid();
          missions.add(mission);
        }
      } catch (RepositoryException e) {
        log.error("=== ERROR getAllMissionParticipantsByParticipant: cannot transfer node to object "+username);
        e.printStackTrace();
      }

    }
    return missions;
  }
  private List<String> getMissionIdsByPriority(int priority){
    List<String> missionIds = new ArrayList<String>();
    StringBuilder sql = new StringBuilder("select jcr:uuid from "+ JCRImpl.MISSION_NODE_TYPE +" where ");
    sql.append(node_prop_active).append("= 'true' ");
    sql.append(" AND ").append(node_prop_priority).append("=").append(priority);
    List<Node> nodes =  this.getNodesByQuery(sql.toString(),0,0);
    for (Node node:nodes){
      try {
          missionIds.add(node.getUUID());
      } catch (RepositoryException e) {
        log.error("ERROR cannot get mission ids by prio");
      }
    }
    return missionIds;
  }
  private Map<Integer,List<String>> getMissionIdsByParticipant(String username){

    Map<Integer,List<String>> missionIds = new HashMap<Integer, List<String>>();
    ParticipantDAO participantDAO = this.getJcrImplService().getParticipantDAO();
    Node participantNode = participantDAO.getNodeByUserName(username);
    if (null != participantNode){
      Participant participant = null;
      try {
        participant = participantDAO.transferNode2Object(participantNode);
        Set<String> mids = participant.getMission_ids();
        Mission mission;
        List<String> mission_prio1_ids = new ArrayList<String>();
        List<String> mission_prio2_ids = new ArrayList<String>();
        List<String> mission_prio3_ids = new ArrayList<String>();
        for (String mid:mids){
          mission = this.transferNode2Object(this.getNodeById(mid));
          mission.checkValid();
          if (mission.getPriority().equals(Priority.PRIORITY_1)){
            mission_prio1_ids.add(mission.getId());
          }else if (mission.getPriority().equals(Priority.PRIORITY_2)){
            mission_prio2_ids.add(mission.getId());
          }else if (mission.getPriority().equals(Priority.PRIORITY_3)){
            mission_prio3_ids.add(mission.getId());
          }
        }
        missionIds.put(Priority.PRIORITY_1.getValue(),mission_prio1_ids);
        missionIds.put(Priority.PRIORITY_2.getValue(),mission_prio2_ids);
        missionIds.put(Priority.PRIORITY_3.getValue(),mission_prio3_ids);

      } catch (RepositoryException e) {
        log.error("=== ERROR getMissionIdsByParticipant for "+username);
        e.printStackTrace();
      } catch (BrandAdvocacyServiceException brade){
        log.error("ERROR "+brade.getMessage());
      }

    }
    return missionIds;
  }

}
