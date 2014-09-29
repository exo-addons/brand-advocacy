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
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class ParticipantDAO extends DAO {

  public static final String node_prop_username = "exo:username";
  public static final String node_prop_mission_ids = "exo:mission_ids";
  private static final Log log = ExoLogger.getLogger(ParticipantDAO.class);
  public ParticipantDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }

  public void setProperties(Node aNode,Participant participant) throws RepositoryException {
    aNode.setProperty(node_prop_mission_ids,participant.getMission_id());
    aNode.setProperty(node_prop_username,participant.getUserName());
  }
  public Participant transferNode2Object(Node node) throws RepositoryException{
    Participant participant = new Participant();
    PropertyIterator iter = node.getProperties("exo:*");
    while (iter.hasNext()) {
      Property p = (Property) iter.next();
      String name = p.getName();
      if(name.equals(node_prop_mission_ids)){
        participant.setMission_id(p.getString());
      }
      else if(name.equals(node_prop_username)){
        participant.setUserName(p.getString());
      }
    }
    return participant;
  }

}
