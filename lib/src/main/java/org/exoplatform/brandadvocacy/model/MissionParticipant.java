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

import java.util.UUID;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class MissionParticipant {

  private String id;
  private String mission_id;
  private String proposition_id;
  private String participant_username;
  private String address_id;
  private String url_submitted;
  private Status status;
  private long createdDate;
  private long modifiedDate;
  private Size size;
  private String labelID;

  public MissionParticipant(){
    this.setLabelID(UUID.randomUUID().toString());
    this.setStatus(Status.OPEN);
    this.setSize(Size.Medium);
    this.setCreatedDate(System.currentTimeMillis());

  }
  public String getLabelID() {
    return labelID;
  }

  public void setLabelID(String labelID) {
    this.labelID = labelID;
  }

  public String getId() {
    return id;
  }
  public String getMission_id() {
    return mission_id;
  }

  public void setMission_id(String mission_id) {
    this.mission_id = mission_id;
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
  public String getUrl_submitted() {
    return url_submitted;
  }
  public void setUrl_submitted(String url_submitted) {
    this.url_submitted = url_submitted;
  }

  public String getAddress_id() {
    return address_id;
  }
  public void setAddress_id(String address_id) {
    this.address_id = address_id;
  }
  public long getCreatedDate() {
    return createdDate;
  }
  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }
  public long getModifiedDate() {
    return modifiedDate;
  }
  public void setModifiedDate(long modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }
  public void checkValid() throws BrandAdvocacyServiceException{
    if(null == this.getLabelID() || "".equals(this.getLabelID()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"mission participant must have label ID");
    if(null == this.getParticipant_username() || "".equals(this.getParticipant_username()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_PARTICIPANT_INVALID,"mission participant must be assigned to a participant");
    if (null == this.getMission_id() || "".equals(this.getMission_id()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_PARTICIPANT_INVALID,"mission participant must belong to a mission");
  }
  public String toString(){
    return getClass().getName()+" - username = "+this.getParticipant_username()+" - id = " + this.getId()+" - missionid= "+this.getMission_id()+" propoid= "+this.getProposition_id();
  }
}
