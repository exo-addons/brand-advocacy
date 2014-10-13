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
public class Proposition {
  
  private String id;
  private String mission_id;
  private String content;
  private Boolean active;
  private int numberUsed;
  private String labelID;

  public Proposition(){
    this.init();
  }
  public Proposition(String content){
    this.setContent(content);
    this.init();

  }
  private void init(){
    this.setLabelID(UUID.randomUUID().toString());
    this.setNumberUsed(0);
    this.setActive(true);

  }
  public String getLabelID() {
    return labelID;
  }

  public void setLabelID(String labelID) {
    this.labelID = labelID;
  }

  public String getId(){
    return this.id;
  }
  public void setId(String id){
    this.id = id;
  }
  public String getMission_id() {
    return mission_id;
  }
  public void setMission_id(String mission_id) {
    this.mission_id = mission_id;
  }
  public String getContent(){
    return this.content;
  }
  public void setContent(String content){
    this.content =content;
  }
  public Boolean getActive(){
    return this.active;
  }
  public void setActive(Boolean active){
    this.active = active;
  }
  public int getNumberUsed(){
    return this.numberUsed;
  }
  public void setNumberUsed(int nb){
    this.numberUsed = nb;
  }
  public Boolean checkValid(){


    if(null == this.getContent() || "".equals(this.getContent())){
      return false;
    }else if(null == this.getMission_id() || "".equals(this.getMission_id())){
      return false;
    }
    return true;

  }
  public String getShortContent(int length){
    int maxLeng = this.getContent().length();
    if(maxLeng < length)
      return this.getContent();
    return this.getContent().substring(0,length);

  }
  public String toString(){
    return getClass().getName()+" - "+this.getId()+" - mission id = "+this.getMission_id()+" - content= "+this.getContent();
  }
}
