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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.exoplatform.brandadvocacy.model.Manager;
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
    aNode.setProperty(node_prop_role, m.getRole());
  }
  public void transferNode2Object(){
    
  }

}
