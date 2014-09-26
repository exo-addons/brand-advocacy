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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 18, 2014  
 */
public class ManagerDAO extends DAO{

  public static final String node_prop_username = "exo:username";
  public static final String node_prop_role = "exo:role";
  public static final String node_prop_notif = "exo:notification";
  private static final Log log = ExoLogger.getLogger(ManagerDAO.class);
  public ManagerDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }
  public void addManager(Manager m) throws BrandAdvocacyServiceException{
   
  }
  public void setProperties(Node aNode,Manager m) throws RepositoryException{
    aNode.setProperty(node_prop_notif, m.getNotif());
    aNode.setProperty(node_prop_role, m.getRole().getValue());
  }
  public Manager transferNode2Object(Node node) throws RepositoryException{
    Manager manager = new Manager();
    PropertyIterator iter = node.getProperties("exo:*");
    while (iter.hasNext()) {
      Property p = (Property) iter.next(); 
      String name = p.getName();
      if(name.equals(node_prop_username)){
        manager.setUserName(p.getString());
      }
      else if(name.equals(node_prop_role)){
        manager.setRole(Role.getRole((int)p.getLong()));
      }
      else if(name.equals(node_prop_notif)){
        manager.setNotif(p.getBoolean());
      }
    }
    return manager;
  }

  public void addManager2Mission(String mid,List<Manager> managers){
    try {
      if(null == mid || "" == mid)
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID, "cannot add managers to mission id null");
   
      Node missionNode = this.getJcrImplService().getMissionDAO().getNodeById(mid);
      if(null != missionNode){
        try {
          Node managerHomeNode = this.getJcrImplService().getMissionDAO().getOrCreateManagerHome(missionNode);
          List<Value> nodeManagers = new LinkedList<Value>();
          Manager manager;
          Node managerNode;
          int i = 0;
          for(i = 0;i<managers.size();i++){
            manager = managers.get(i);
            managerNode = missionNode.addNode(manager.getUserName(), JCRImpl.MANAGER_NODE_TYPE);
            this.setProperties(managerNode, manager);
          }
          if(i != 0){
            managerHomeNode.getSession().save();
          }
        } catch (UnsupportedRepositoryOperationException e) {
          log.error("=== ERROR add manager to mission "+e.getMessage());
        } catch (RepositoryException e) {
          log.error("=== ERROR add manager to mission "+e.getMessage());
        }
      }

    } catch (BrandAdvocacyServiceException brade) {
      log.error("ERROR cannot add manager "+brade.getMessage());
    }
  }  
  public List<Manager> getAllManagers(String mid){
    List<Manager> managers = new ArrayList<Manager>();
    if(null == mid || "" == mid)
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID, "cannot add managers to mission id null");
    try {
      Node missionNode = this.getJcrImplService().getMissionDAO().getNodeById(mid);
      NodeIterator nodes = missionNode.getNodes();
      Node manager = null;
      Manager aManager = null;
      while (nodes.hasNext()) {
        manager = (Node) nodes.next();
        aManager = this.transferNode2Object(manager);
        if(aManager.checkValid())
          managers.add(aManager);
      }
      return managers;
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return managers;
  }
  
}
