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

import java.util.Date;
import java.util.List;

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
  private Priority priority;
  private int active;
  List<Proposition> propositions;
  private long createdDate;
  private long modifedDate;
  private List<Manager> managers;

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
  public Priority getPriority() {
    return priority;
  }
  public void setPriority(Priority priority) {
    this.priority = priority;
  }
  public int getActive() {
    return active;
  }
  public void setActive(int active) {
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
  public long getModifedDate() {
    return modifedDate;
  }
  public void setModifedDate(long modifedDate) {
    this.modifedDate = modifedDate;
  }
  public List<Manager> getManagers() {
    return managers;
  }
  public void setManagers(List<Manager> managers) {
    this.managers = managers;
  }

  
}
