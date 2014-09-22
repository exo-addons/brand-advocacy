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
  private int active;
  private int numberUsed;
  
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
  public int getActive(){
    return this.active;
  }
  public void setActive(int active){
    this.active = active;
  }
  public int getNumberUsed(){
    return this.numberUsed;
  }
  public void setNumberUsed(int nb){
    this.numberUsed = nb;
  }
  public void checkValid() throws BrandAdvocacyServiceException{
      if(null == this.getContent() || "".equals(this.getContent()))
          throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PROPOSITION_INVALID,"proposition cannot have empty content");
  }
}
