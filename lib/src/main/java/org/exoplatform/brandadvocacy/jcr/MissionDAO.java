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
import java.util.List;

import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Sep
 * 9, 2014
 */
public class MissionDAO extends DAO {

  private static final Log   log             = ExoLogger.getLogger(MissionDAO.class);

  public static final String MISSIONS_PATH   = "/Missions/";

  public static final String NodeType = "brad:mission";
  public static final String node_prop_id = "exo:id";
  public static final String node_prop_title = "exo:title";  
  public static final String node_prop_third_party_link = "exo:third_party_link";
  public static final String node_prop_priority = "exo:priority";
  public static final String node_prop_active = "exo:active";
  public static final String node_prop_dateCreated = "exo:dateCreated";
  public static final String node_prop_modifiedDate = "exo:modifiedDate";

  public MissionDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }

  public Node getOrCreateMissionHome() {
    String path = String.format("%s",JCRImpl.EXTENSION_PATH+MISSIONS_PATH);
    return this.getJcrImplService().getOrCreateNode(path);
  }
  private void setPropertiesNode(Node missionNode, Mission m) throws RepositoryException {
    missionNode.setProperty(node_prop_id, m.getId());
    missionNode.setProperty(node_prop_title, m.getTitle());
    missionNode.setProperty(node_prop_third_party_link, m.getThird_party_link());
    missionNode.setProperty(node_prop_priority, m.getPriority());
    missionNode.setProperty(node_prop_active, m.getActive());
    missionNode.setProperty(node_prop_dateCreated, m.getCreatedDate());
  }
  private Mission transferNode2Mission(Node node) throws RepositoryException {
    Mission m = new Mission();
    PropertyIterator iter = node.getProperties("exo:*");
    while (iter.hasNext()) {
        Property p = iter.nextProperty();
        String name = p.getName();
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
      //Node extensionNode = this.getJcrImplService().getOrCreateExtensionHome();
      try {
        Node missionNode = this.getOrCreateMissionHome();//extensionNode.addNode(MISSIONS_PATH+m.getId(),NodeType);
        this.setPropertiesNode(missionNode, m);
        missionNode.getSession().save();
        return this.transferNode2Mission(missionNode);
      } catch (ItemExistsException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (PathNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (NoSuchNodeTypeException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (LockException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (VersionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ConstraintViolationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (RepositoryException e) {
        log.error(" repo exception "+e.getMessage());
        // TODO Auto-generated catch block
//        e.printStackTrace();
      }
      
      return m;
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
        missions.add(this.transferNode2Mission(nodes.nextNode()));
      }
      return missions;
    } catch (RepositoryException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  public Node getMissionNode(String id) {
    return null;
  }

  public Mission getMissionById(String id) {

    return null;
  }

}
