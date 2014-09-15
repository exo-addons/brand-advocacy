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

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class ParticipantMission {

  private String id;
  private String mission_title;
  private String participant_username;
  private Address address;
  private String url_submitted;
  private Status status;
  private long createdDate;
  private long modifedDate;
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getMission_title() {
    return mission_title;
  }
  public void setMission_title(String mission_title) {
    this.mission_title = mission_title;
  }
  public String getParticipant_username() {
    return participant_username;
  }
  public void setParticipant_username(String participant_username) {
    this.participant_username = participant_username;
  }
  public Address getAddress() {
    return address;
  }
  public void setAddress(Address address) {
    this.address = address;
  }
  public String getUrl_submitted() {
    return url_submitted;
  }
  public void setUrl_submitted(String url_submitted) {
    this.url_submitted = url_submitted;
  }
  public Status getStatus() {
    return status;
  }
  public void setStatus(Status status) {
    this.status = status;
  }
  public long getCreatedDate() {
    return createdDate;
  }
  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }
  public long getModifedDate() {
    return modifedDate;
  }
  public void setModifedDate(long modifedDate) {
    this.modifedDate = modifedDate;
  }
  

}
