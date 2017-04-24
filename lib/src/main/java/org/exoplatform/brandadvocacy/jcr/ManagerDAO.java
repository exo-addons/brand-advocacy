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
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 18, 2014  
 */
public class ManagerDAO extends DAO{

  public static final String node_prop_parent_id = "exo:parent_id";
  public static final String node_prop_username = "exo:username";
  public static final String node_prop_role = "exo:role";
  public static final String node_prop_notif = "exo:notification";
  private static final Log log = ExoLogger.getLogger(ManagerDAO.class);
  public ManagerDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }
  private Node getOrCreateProgramManagerHome(String programId){
    if(null == programId || "".equals(programId)){
      log.error("ERROR cannot get mission home for an invalid program id ");
      return null;
    }
    try {
      Node node = this.getNodeById(programId);
      return this.getJcrImplService().getProgramDAO().getOrCreateManagerHome(node);
    } catch (RepositoryException e) {
      log.error("ERROR cannot get mission home for an invalid program "+e.getMessage());
    }
    return null;
  }
  private Node getOrCreateMissionManagerHome(String missionId){
    if(null == missionId || "".equals(missionId)){
      log.error("ERROR cannot get mission home for an invalid program id ");
      return null;
    }
    try {
      Node node = this.getNodeById(missionId);
      return this.getJcrImplService().getMissionDAO().getOrCreateManagerHome(node);
    } catch (RepositoryException e) {
      log.error("ERROR cannot get mission home for an invalid program "+e.getMessage());
    }
    return null;
  }

  private void setProperties(Node aNode,Manager m) throws RepositoryException{
    aNode.setProperty(node_prop_parent_id,m.getParentId());
    aNode.setProperty(node_prop_username,m.getUserName());
    aNode.setProperty(node_prop_notif, m.getNotif());
    aNode.setProperty(node_prop_role, m.getRole().getValue());
  }
  public Manager transferNode2Object(Node node) throws RepositoryException{
    if (null == node)
      return null;
    Manager manager = new Manager();
    PropertyIterator iter = node.getProperties("exo:*");
    while (iter.hasNext()) {
      Property p = (Property) iter.next(); 
      String name = p.getName();
      if(name.equals(node_prop_parent_id)){
        manager.setParentId(p.getString());
      }
      else if(name.equals(node_prop_username)){
        manager.setUserName(p.getString());
      }
      else if(name.equals(node_prop_role)){
        manager.setRole(Role.getRole((int)p.getLong()));
      }
      else if(name.equals(node_prop_notif)){
        manager.setNotif(p.getBoolean());
      }
    }
    try {
      manager.checkValid();
      return manager;
    }catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot transfer node to manager object");
    }
    return null;
  }
  public List<Manager> transferNodes2Objects(List<Node> nodes){
    List<Manager> managers = new ArrayList<Manager>(nodes.size());
    Manager manager;
    for (Node node:nodes){
      try {
        manager = this.transferNode2Object(node);
        if (null != manager)
          managers.add(manager);
      } catch (RepositoryException e) {
        log.error("ERROR cannot transfer node to manager object "+e.getMessage());
      }
    }
    return managers;
  }
  private Node getMissionManagerNodeById(String missionId,String username){

    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.MANAGER_NODE_TYPE +" where ");
    sql.append(node_prop_parent_id).append(" = '").append(missionId).append("'");
    sql.append(" AND ").append(node_prop_username).append(" = '").append(Utils.queryEscape(username)).append("'");
    return this.getNodesByQuery(sql.toString(),0,1).get(0);
  }
  private Node addManager(Node homeNode, Manager manager){
    try {
      manager.checkValid();
      if(!homeNode.hasNode(manager.getUserName())){
        Node managerNode = homeNode.addNode(manager.getUserName(),JCRImpl.MANAGER_NODE_TYPE);
        this.setProperties(managerNode,manager);
        homeNode.save();
        return managerNode;
      }else
        return homeNode.getNode(manager.getUserName());
    }catch (RepositoryException re){
      log.error("ERROR cannot add manager "+re.getMessage());
    }catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot add manager "+brade.getMessage());
    }
    return null;
  }

  public Manager addManager2Mission(Manager manager){

    try {
      manager.checkValid();
      String mid = manager.getParentId();
      Node nodeHome = this.getOrCreateMissionManagerHome(mid);
      if (null != nodeHome){
        return this.transferNode2Object(this.addManager(nodeHome, manager));
      }
    } catch (RepositoryException e) {
      log.error("ERROR cannot add manager to mission " + e.getMessage());
    } catch (BrandAdvocacyServiceException brade){
      log.error("ERROR "+brade.getMessage());
    }
    return null;
  }
  public List<Manager> addManagers2Mission(String mid,List<Manager> managers){
    List<Node> nodes = new ArrayList<Node>(managers.size());
    Node nodeHome = this.getOrCreateMissionManagerHome(mid);
    if (null != nodeHome){
      for (Manager manager:managers){
        manager.setParentId(mid);
        Node node = this.addManager(nodeHome,manager);
        if (null != node)
          nodes.add(node);
      }
      if(nodes.size() > 0){
        return this.transferNodes2Objects(nodes);
      }
    }
    return null;
  }  
  public List<Manager> getAllMissionManagers(String mid){

    if(null == mid || "".equals(mid)){
      log.error("ERROR cannot get all managers from mission null");
      return null;
    }
    try {
       Node managerHomeNode = this.getOrCreateMissionManagerHome(mid);
      if (null != managerHomeNode){
        NodeIterator nodes = managerHomeNode.getNodes();
        return this.transferNodes2Objects(Lists.newArrayList(nodes));
      }
    } catch (RepositoryException e) {
        log.error("==== ERROR get all managers "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(" ERROR cannot get all managers "+brade.getMessage());
    }
    return null;
  }

  public Manager updateMissionManager(String missionId,Manager manager){
    try {
      manager.checkValid();
      Node managerNode = this.getMissionManagerNodeById(missionId, manager.getUserName());
      if(null != managerNode){
        this.setProperties(managerNode,manager);
        managerNode.save();
        return this.transferNode2Object(managerNode);
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot update manager "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
    return null;
  }
  public void removeMissionManager(String missionId,String username){
    try {
      Node managerNode = this.getMissionManagerNodeById(missionId, username);
      if(null != managerNode){
        Session session = managerNode.getSession();
        managerNode.remove();
        session.save();
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot remove manager "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }

  }
  public Manager getMissionManagerByUserName(String missionId,String username){
    if(null == username || null == missionId || "".equals(missionId) || "".equals(username))
      return null;
    try {
      return this.transferNode2Object(this.getMissionManagerNodeById(missionId, username));
    } catch (RepositoryException e) {
      log.error(" ERROR cannot get manager from mission");
    }

    return null;

  }
  public Manager addManager2Program(Manager manager){

    try {
      manager.checkValid();
      String programId = manager.getParentId();
      Node nodeHome = this.getOrCreateProgramManagerHome(programId);
      if (null != nodeHome){
        return this.transferNode2Object(this.addManager(nodeHome,manager));
      }
    } catch (RepositoryException e) {
      log.error("ERROR cannot add manager 2 program "+e.getMessage());
    } catch (BrandAdvocacyServiceException brade){
      log.error("ERROR "+brade.getMessage());
    }
    return null;
  }
  public List<Manager> addManagers2Program(String programId,List<Manager> managers){
    List<Node> nodes = new ArrayList<Node>(managers.size());
    Node nodeHome = this.getOrCreateProgramManagerHome(programId);
    if (null != nodeHome){
      for (Manager manager:managers){
        manager.setParentId(programId);
        Node node = this.addManager(nodeHome,manager);
        if (null != node)
          nodes.add(node);
      }
      if(nodes.size() > 0){
        return this.transferNodes2Objects(nodes);
      }
    }
    return null;
  }

  private Node getProgramManagerNodeById(String programId,String username){
    Node homeNode = this.getOrCreateProgramManagerHome(programId);
    if (null != homeNode){
      try {
        if(homeNode.hasNode(username)){
          return homeNode.getNode(username);
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public List<Manager> getAllManagersInProgram(String programId){
    try {
      Node nodeHome = this.getOrCreateProgramManagerHome(programId);
      if (null != nodeHome){
        NodeIterator nodes = nodeHome.getNodes();
        return this.transferNodes2Objects(Lists.newArrayList(nodes));
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR get all managers "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(" ERROR cannot get all managers "+brade.getMessage());
    }
    return null;
  }

  public Manager getProgramManagerByUserName(String programId, String username){
    try {
      return this.transferNode2Object(this.getProgramManagerNodeById(programId,username));
    } catch (RepositoryException e) {
      log.error("ERROR cannot get program manager "+e.getMessage());
    }
    return null;
  }

  public Boolean removeManagerFromProgram(String programId, String username){

    Node managerNode = this.getProgramManagerNodeById(programId,username);
    if (null != managerNode){
      try {
        Session session = managerNode.getSession();
        managerNode.remove();
        session.save();
        return true;
      } catch (RepositoryException e) {
        log.error("ERROR cannot remove manager from program "+e.getMessage());
      }
    }
    return false;
  }

  public Manager updateProgramManager(Manager manager){
    try {
      manager.checkValid();
      String programId = manager.getParentId();
      Node managerNode = this.getProgramManagerNodeById(programId, manager.getUserName());
      if(null != managerNode){
        this.setProperties(managerNode,manager);
        managerNode.save();
        return this.transferNode2Object(managerNode);
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot update program manager"+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot update program manager"+brade.getMessage());
    }
    return null;
  }

}
