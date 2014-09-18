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
  private String proposition_id;
  private String participant_username;
  private long address_id;
  private String url_submitted;
  private long status;
  private long createdDate;
  private long modifedDate;
  private long size;
  
  public String getId() {
    return id;
  }
  public String getProposition_id() {
    return proposition_id;
  }
  public void setProposition_id(String proposition_id) {
    this.proposition_id = proposition_id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getParticipant_username() {
    return participant_username;
  }
  public void setParticipant_username(String participant_username) {
    this.participant_username = participant_username;
  }
  public long getAddress() {
    return address_id;
  }
  public void setAddress(long address_id) {
    this.address_id = address_id;
  }
  public String getUrl_submitted() {
    return url_submitted;
  }
  public void setUrl_submitted(String url_submitted) {
    this.url_submitted = url_submitted;
  }
  public long getStatus() {
    return status;
  }
  public void setStatus(int status) {
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
  public long getSize() {
    return size;
  }
  public void setSize(long size) {
    this.size = size;
  }  

}
