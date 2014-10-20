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

import java.util.List;
import java.util.UUID;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class Mission {
  
  private String id;
  private String programId;
  private String title;
  private String third_part_link;
  private Priority priority;
  private Boolean active;
  List<Proposition> propositions;
  private long createdDate;
  private long modifiedDate;
  private List<Manager> managers;
  private String labelID;

  public Mission(){
    this.init();
  }
  public Mission(String title){
    this.setTitle(title);
    this.init();
  }
  public Mission(String programId, String title){
    this.setProgramId(programId);
    this.setTitle(title);
    this.init();
   
  }
  public void init(){
    this.setPriority(Priority.PRIORITY_1);
    this.setLabelID(UUID.randomUUID().toString());
    this.setCreatedDate(System.currentTimeMillis());
    this.setModifiedDate(System.currentTimeMillis());
    this.setActive(false);
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
  public void setId(String id) {
    this.id = id;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getThird_part_link() {
    return third_part_link;
  }
  public void setThird_part_link(String third_part_link) {
    this.third_part_link = third_part_link;
  }
  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }
  public Boolean getActive() {
    return active;
  }
  public void setActive(Boolean active) {
    this.active = active;
  }
  public List<Proposition> getPropositions() {
    return propositions;
  }
  public void setPropositions(List<Proposition> propositions) {
    this.propositions = propositions;
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
  public List<Manager> getManagers() {
    return managers;
  }
  public void setManagers(List<Manager> managers) {
    this.managers = managers;
  }
  public void checkValid() throws BrandAdvocacyServiceException {
    if (null == this.getProgramId() || "".equals(this.getProgramId())){
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_INVALID,"mission must belong to a program");
    }
    if(null == this.getLabelID() || "".equals(this.getLabelID()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"mission must have label id");
    if (null == this.getTitle() || "".equals(this.getTitle()) || this.getTitle().trim().isEmpty()) {
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_INVALID,"mission must have title");
    }
  }
  public String toString(){
    return this.getTitle()+" - "+this.getActive()+" - "+this.getPriority().getLabel();
  }


  public String getProgramId() {
    return programId;
  }

  public void setProgramId(String programId) {
    this.programId = programId;
  }
}
