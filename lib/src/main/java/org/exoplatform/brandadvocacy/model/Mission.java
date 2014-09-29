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
  private String title;
  private String third_party_link;
  private Long priority;
  private Boolean active;
  List<Proposition> propositions;
  private long createdDate;
  private long modifiedDate;
  private List<Manager> managers;
  
  public Mission(){
  }
  public Mission(String title){
   this.setTitle(title);
   this.init();
   
  }
  public void init(){

      this.setId(UUID.randomUUID().toString());
      this.setPriority((long) Priority.PRIORITY_2.priority());
      this.setCreatedDate(System.currentTimeMillis());
      this.setActive(true);
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
  public String getThird_party_link() {
    return third_party_link;
  }
  public void setThird_party_link(String third_party_link) {
    this.third_party_link = third_party_link;
  }
  public Long getPriority() {
    return priority;
  }
  public void setPriority(Long priority) {
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
    if(null == this.getId() || "".equals(this.getId()))
      throw new IllegalArgumentException("mission id is invalid ");
    
    if (null == this.getTitle() || "".equals(this.getTitle()) || this.getTitle().trim().isEmpty()) {
      throw new IllegalArgumentException("Mission title is invalid");    
   }
  }
  public String toString(){
    return this.getTitle()+" - "+this.getThird_party_link();
  }
}
