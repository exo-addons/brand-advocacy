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
 * Sep 11, 2014  
 */
public class Address {
  private String owner_id;
  private String id;
  private String fName;
  private String lName;
  private String address;
  private String city;
  private String Phone;
  private String country;
  public String getOwner_id() {
    return owner_id;
  }

  public void setOwner_id(String owner_id) {
    this.owner_id = owner_id;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getfName() {
    return fName;
  }
  public void setfName(String fName) {
    this.fName = fName;
  }
  public String getlName() {
    return lName;
  }
  public void setlName(String lName) {
    this.lName = lName;
  }
  public String getAddress() {
    return address;
  }
  public void setAddress(String address) {
    this.address = address;
  }
  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }
  public String getPhone() {
    return Phone;
  }
  public void setPhone(String phone) {
    Phone = phone;
  }
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }  
  public void checkValid() throws BrandAdvocacyServiceException{
  }


}
