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

import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Sep
 * 9, 2014
 */
public class MissionDAO extends DAO {

  private static final Log   log             = ExoLogger.getLogger(MissionDAO.class);

  public static final String MISSIONS_PATH   = "Missions";
  public static final String MANAGERS_PATH = "Managers";
  public static final String PROPOSITIONS_PATH = "Propositions";
  
  public static final String node_prop_id = "exo:id";
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

  public Node getOrCreateMissionHome() {
    String path = String.format("%s/%s",JCRImpl.EXTENSION_PATH,MISSIONS_PATH);
    return this.getJcrImplService().getOrCreateNode(path);
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

  public void setPropertiesNode(Node missionNode, Mission m) throws RepositoryException {
    missionNode.setProperty(node_prop_id, m.getId());
    missionNode.setProperty(node_prop_title, m.getTitle());
    missionNode.setProperty(node_prop_third_party_link, m.getThird_party_link());
    missionNode.setProperty(node_prop_priority, m.getPriority());
    missionNode.setProperty(node_prop_active, m.getActive());
    missionNode.setProperty(node_prop_dateCreated, m.getCreatedDate());
  }
  public Mission transferNode2Object(Node node) throws RepositoryException {
    Mission m = new Mission();
    PropertyIterator iter = node.getProperties("exo:*");
    Property p;
    String name;
    while (iter.hasNext()) {
      p = iter.nextProperty();
      name = p.getName();
      if (name.equals(node_prop_id)) {
        m.setId(p.getString());
      } else if (name.equals(node_prop_title)) {
        m.setTitle(p.getString());
      } else if (name.equals(node_prop_third_party_link)) {
        m.setThird_party_link(p.getString());
      } else if(name.equals(node_prop_priority)){
        m.setPriority(p.getLong());
      } else if(name.equals(node_prop_active)){
        m.setActive(p.getBoolean());
      } else if (name.equals(node_prop_dateCreated)) {
        m.setCreatedDate(p.getLong());
      }
    }
    return m;
  }
  public Mission createMission(Mission m) {
    try {
      m.checkValid();
      try {
        Node homeMissionNode = this.getOrCreateMissionHome();
        Node missionNode = homeMissionNode.addNode(m.getId(),JCRImpl.MISSION_NODE_TYPE);
        this.setPropertiesNode(missionNode,m);
        homeMissionNode.getSession().save();
        if(null != m.getManagers() && m.getManagers().size() > 0)
          this.getJcrImplService().getManagerDAO().addManager2Mission(m.getId(), m.getManagers());
        return this.transferNode2Object(missionNode);
      } catch (RepositoryException e) {
        log.error(" repo exception "+e.getMessage());
      }
    } catch (BrandAdvocacyServiceException brade) {
      log.error("cannot create mission with null title");
    }
    return null;
  }
  public List<Mission> getAllMissions(){
    List<Mission> missions = new ArrayList<Mission>();
    Node missionHome = this.getOrCreateMissionHome();
    try {
      NodeIterator nodes =  missionHome.getNodes();
      while (nodes.hasNext()) {
        missions.add(this.transferNode2Object(nodes.nextNode()));
      }
      return missions;
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  public Node getNodeById(String id) {
    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.MISSION_NODE_TYPE +" where jcr:path like '");
    sql.append(JCRImpl.EXTENSION_PATH).append("/").append(MISSIONS_PATH);
    sql.append("/").append(Utils.queryEscape(id)).append("'");
    Session session;
    try {
      session = this.getJcrImplService().getSession();
      Query query = session.getWorkspace().getQueryManager().createQuery(sql.toString(), Query.SQL);

      QueryResult result = query.execute();
      NodeIterator nodes = result.getNodes();
      if (nodes.hasNext()) {
        return nodes.nextNode();
      }   
    } catch (RepositoryException e) {
      log.error("ERROR get mission id "+id +" Exeption "+e.getMessage());
    }
    return null;        
  }

  public Mission getMissionById(String id) throws RepositoryException {
    Node aNode = this.getNodeById(id);
    if(aNode != null)
      return this.transferNode2Object(aNode);
    return null;
  }
  public void UpdateMission(Mission m){
    try {
      m.checkValid();      
      Node missionHome = this.getOrCreateMissionHome();
      if (!missionHome.hasNode(m.getId())) {
      
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_NOT_EXISTS, "cannot update mission not exist "+m.getId());
        
      }
      Node aNode = missionHome.getNode(m.getId());
      if ( m.getId().equals( aNode.getProperty(node_prop_id).getString()) ) {
        this.setPropertiesNode(aNode, m);        
        aNode.save();
      }

    } catch (BrandAdvocacyServiceException e) {
      log.error("ERROR cannot update mission with empty title");
    } catch (RepositoryException e) {
      log.error("ERROR update mission "+e.getMessage());
    }              
  }
  public void removeMissionById(String id){
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

}
