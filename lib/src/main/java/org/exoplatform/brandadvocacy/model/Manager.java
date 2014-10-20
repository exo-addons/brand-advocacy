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
package org.exoplatform.brandadvocacy.model;

import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class Manager extends User{

  private String parentId;
  private Role role;  
  private Boolean notif;


  public Manager(){
    
  }
  public Manager(String username){
    this.setUserName(username);
    this.setRole(Role.Admin);
    this.setNotif(true);
  }
  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getParentId() {

    return parentId;
  }
  public Role getRole(){
    return this.role;
  }
  public void setRole(Role role){
    this.role = role;
  }
  public Boolean getNotif(){
    return notif;
  }
  public void setNotif(Boolean notif){
    this.notif = notif;
  }
  public String getRoleLabel(){
    return this.getRole().getLabel();
  }
  public void checkValid() throws BrandAdvocacyServiceException{
    if (null == this.getParentId() || "".equals(this.getParentId()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MANAGER_INVALID,"manager must manage a program or mission");
    if(null == this.getUserName() || "".equals(this.getUserName()) )
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MANAGER_INVALID," username manager is invalid");
  }

  public String toString(){
    return this.getParentId()+" - " + this.getUserName()+" - "+this.getRole()+" - "+this.getNotif();
  }
}
