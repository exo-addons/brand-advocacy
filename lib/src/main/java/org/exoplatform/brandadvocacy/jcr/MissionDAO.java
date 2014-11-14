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
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.*;
import java.util.*;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Sep
 * 9, 2014
 */
public class MissionDAO extends DAO {

  private static final Log   log             = ExoLogger.getLogger(MissionDAO.class);

  public static final String MISSIONS_PATH   = "Missions";

  public static final String node_prop_program_id = "exo:program_id";
  public static final String node_prop_labelID = "exo:labelID";
  public static final String node_prop_title = "exo:title";  
  public static final String node_prop_third_part_link = "exo:third_part_link";
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
    missionNode.setProperty(node_prop_program_id,m.getProgramId());
    if(null != m.getLabelID() && !"".equals(m.getLabelID()))
      missionNode.setProperty(node_prop_labelID,m.getLabelID());
    missionNode.setProperty(node_prop_title, m.getTitle());
    missionNode.setProperty(node_prop_third_part_link, m.getThird_part_link());
    missionNode.setProperty(node_prop_priority, m.getPriority());
    missionNode.setProperty(node_prop_active, m.getActive());
    if (0 != m.getCreatedDate())
      missionNode.setProperty(node_prop_dateCreated, m.getCreatedDate());
    if (0 != m.getModifiedDate())
      missionNode.setProperty(node_prop_modifiedDate,m.getModifiedDate());
  }
  public Mission transferNode2Object(Node node) throws RepositoryException {
    if (null == node)
      return null;
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
      } else if (name.equals(node_prop_program_id)) {
        m.setProgramId(p.getString());
      } else if (name.equals(node_prop_title)) {
        m.setTitle(p.getString());
      } else if (name.equals(node_prop_third_part_link)) {
        m.setThird_part_link(p.getString());
      } else if(name.equals(node_prop_priority)){
        m.setPriority(p.getLong());
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

  public List<Mission> transferNodes2Objects(List<Node> nodes, Boolean isActive) {
    List<Mission> missions = new ArrayList<Mission>(nodes.size());
    Mission mission;
    for (Node node:nodes){
      try {
        mission = this.transferNode2Object(node);
        if (null != mission ) {
          if (null == isActive )
            missions.add(mission);
          else if (mission.getActive() == isActive)
            missions.add(mission);
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return missions;
  }
  public Mission addMission2Program(Mission mission) {
    try{
      mission.checkValid();
      String programId = mission.getProgramId();
      Node homeMissionNode = this.getOrCreateMissionHome(programId);
      if (null != homeMissionNode){
        Node missionNode = homeMissionNode.addNode(mission.getLabelID(),JCRImpl.MISSION_NODE_TYPE);
        this.setPropertiesNode(missionNode,mission);
        homeMissionNode.save();
        return this.transferNode2Object(missionNode);
      }
    }catch (RepositoryException re){
      log.error("ERROR cannot add mission 2 program "+re.getMessage());
    } catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot add mission 2 program "+brade.getMessage());
    }
    return null;
  }
  public List<Mission> getAllMissionsByProgramId(String programId,Boolean isActive){
    List<Mission> missions = new ArrayList<Mission>();
    try {
      Node missionHome = this.getOrCreateMissionHome(programId);
      if (null != missionHome){
        NodeIterator nodes =  missionHome.getNodes();
        return this.transferNodes2Objects(Lists.newArrayList(nodes),isActive);
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
  public List<Mission> search(Query query){
    Program program = this.getJcrImplService().getProgramDAO().getProgramById(query.getProgramId());
    List<Mission> missions = new ArrayList<Mission>();
    if (null != program) {
      StringBuilder sql = new StringBuilder("select * from " + JCRImpl.MISSION_NODE_TYPE + " where ");
      sql.append("jcr:path like '");
      sql.append(JCRImpl.EXTENSION_PATH).append("/").append(Utils.queryEscape(program.getLabelID())).append("/").append(ProgramDAO.node_prop_missions);
      sql.append("/%'");
      if(null != query.getIsActive()){
        sql.append(" AND ").append(node_prop_active).append("='").append(query.getIsActive()).append("'");
      }
      sql.append(" ORDER BY ").append(node_prop_priority).append(" DESC ");
      List<Node> nodes =  this.getNodesByQuery(sql.toString(),query.getOffset(),query.getLimit());
      return this.transferNodes2Objects(nodes,query.getIsActive());
    }
    return missions;
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
  public Boolean removeMissionById(String id){
    if (null == id || "".equals(id)) {
      log.error("ERROR cannot remove mission null");
      return false;
    }
    try {
      Node aNode = this.getNodeById(id);
      Mission mission = this.transferNode2Object(aNode);
      if (null != mission){
        Node homeNode = this.getOrCreateMissionHome(mission.getProgramId());
        if (homeNode.hasNode(mission.getLabelID())){
          aNode.remove();
          homeNode.save();
          return true;
        }
      }
    } catch (RepositoryException e) {
        log.error(" ERROR remove mission "+id+" === Exception "+e.getMessage());
    }
    return false;
  }

  public int getTotalNumberMissions(String programId, Boolean isPublic, Boolean isActive,int priority){
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
  public List<Mission> getAllMissionsInProgramByParticipant(String programId, String username){

    List<Mission> missions = new ArrayList<Mission>();
    ParticipantDAO participantDAO = this.getJcrImplService().getParticipantDAO();
    Node participantNode = participantDAO.getNodeByUserName(programId,username);
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
  public Mission getRandomMission(String programId, String username){
    List<Mission> randomMissions = new LinkedList<Mission>();
    List<Proposition> randomPropositions;
    List<Mission> activeMissions = this.getAllMissionsByProgramId(programId,true);
    List<Mission> missionsUsed = this.getAllMissionsInProgramByParticipant(programId,username);
    List<String> ids = new ArrayList<String>();
    Iterator<Mission> iterator = activeMissions.iterator();
    while (iterator.hasNext()){
      Mission mission = iterator.next();
      Boolean isDiff = true;
      for (Mission missionUsed : missionsUsed){
        if (mission.getId().equals(missionUsed.getId())){
          iterator.remove();
          isDiff = false;
        }
      }
      if (isDiff){
        randomPropositions = new ArrayList<Proposition>();
        Proposition proposition = this.getJcrImplService().getPropositionDAO().getRandomProposition(mission.getId());
        if ( null != proposition){
          randomPropositions.add(proposition);
          mission.setPropositions(randomPropositions);
          randomMissions.add(mission);
          for (int i= 0;i<mission.getPriority();i++){
            ids.add(mission.getId());
          }
        }
      }
    }
    if (ids.size() > 0){
      Collections.shuffle(ids);
      String missionRandomId = ids.get(0);
      for (Mission randomMission:randomMissions){
        if(missionRandomId.equals(randomMission.getId())){
          return randomMission;
        }
      }
//      return this.getMissionById(ids.get(0));
    }
    return null;
  }
}
