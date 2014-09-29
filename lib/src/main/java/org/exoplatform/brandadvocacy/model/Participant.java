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

import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class Participant extends User {

  private String mission_id;
  private List<Address> addresses;
  private List<String> mission_ids;
  
  public List<String> getMission_ids() {
    return mission_ids;
  }
  public void setMission_ids(List<String> mission_ids) {
    this.mission_ids = mission_ids;
  }
  public String getMission_id() {
    return mission_id;
  }
  public void setMission_id(String mission_id) {
    this.mission_id = mission_id;
  }
  public List<Address> getAddresses(){
    return this.addresses;
  }
  public void setAddresses(List<Address> addresses){
    this.addresses = addresses;
  }
  public String toString(){
    return getClass().getName();
  }

}
