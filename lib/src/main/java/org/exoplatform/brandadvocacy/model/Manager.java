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

import javax.jcr.Value;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class Manager extends User{

  private String mission_id;
  private String missionLabelId;
  private Role role;  
  private Boolean notif;


  public Manager(){
    
  }
  public Manager(String username){
    this.setUserName(username);
    this.setRole(Role.Admin);
    this.setNotif(true);
  }
  public void setMission_id(String mission_id) {
    this.mission_id = mission_id;
  }

  public String getMission_id() {

    return mission_id;
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
  public Boolean checkValid(){
    if(null == this.getUserName() || "".equals(this.getUserName()) || null == this.getMission_id() || "".equals(this.getMission_id()) )
      return false;
    return true;
  }

  public String toString(){
    return getClass().getName()+" - " + this.getUserName()+" - "+this.getRole()+" - "+this.getNotif();
  }

  public String getMissionLabelId() {
    return missionLabelId;
  }

  public void setMissionLabelId(String missionLabelId) {
    this.missionLabelId = missionLabelId;
  }
}
