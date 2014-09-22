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

import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class MissionDAO extends DAO {
  
  private static final Log log = ExoLogger.getLogger(MissionDAO.class);
  public MissionDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }
  public Mission createMission(Mission m){
    try{
     m.checkValid();
     m = this.getJcrImplService().addMission(m);
     return m;
    }catch (BrandAdvocacyServiceException brade){
     log.error("cannot create mission with null title");
    }catch (Exception e){
     log.error("cannot create mission");
    }
    return null;
  }
  public Node getMissionNode(String id){
    return null;
  }
  public Mission getMissionById(String id){
    
    return null; 
  }
  
}
