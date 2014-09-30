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

import java.util.*;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class Participant extends User {

  private List<Address> addresses;
  private Set<String> mission_participant_ids = new HashSet<String>();
  public Participant(){

  }

  public Participant(String username){
    this.setUserName(username);
  }

  public Set<String> getMission_participant_ids() {
    return this.mission_participant_ids;
  }

  public void setMission_participant_ids(Set<String> mission_participant_ids) {
    this.mission_participant_ids = mission_participant_ids;
  }

  public List<Address> getAddresses(){
    return this.addresses;
  }
  public void setAddresses(List<Address> addresses){
    this.addresses = addresses;
  }
  public void checkValid() throws BrandAdvocacyServiceException{
    if(null == this.getUserName() || "".equals(this.getUserName()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PARTICIPANT_INVALID,"participant must have username");
    if (0 == this.getMission_participant_ids().size())
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PARTICIPANT_INVALID,"participant must participate to 1 mission");
  }
  public String toString(){
    return getClass().getName()+" - username = "+this.getUserName()+" - number missions "+this.getMission_participant_ids().size();
  }

}
