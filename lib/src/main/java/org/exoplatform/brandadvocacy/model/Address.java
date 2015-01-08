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
 * Sep 11, 2014  
 */
public class Address {

  private String id;
  private String fName;
  private String lName;
  private String address;
  private String city;
  private String Phone;
  private String country;
  private String labelID;

  public Address(){
    this.setLabelID(UUID.randomUUID().toString());
  }
  public Address(String fName, String lName, String address, String city, String country, String phone){
    this.setLabelID(UUID.randomUUID().toString());
    this.setfName(fName);
    this.setlName(lName);
    this.setAddress(address);
    this.setCity(city);
    this.setCountry(country);
    this.setPhone(phone);
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
    if (null == this.getLabelID() || "".equals(this.getLabelID()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"label address invalid");
    if (null == this.getlName() || null == this.getlName() || null == this.getAddress() || null == this.getCity() || null == this.getCountry() || null == this.getPhone())
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_NOT_EXISTS," address invalid");
    if ("".equals( this.getlName() ) || "".equals( this.getlName() ) || "".equals( this.getAddress() ) || "".equals( this.getCity() ) || "".equals( this.getCountry() )|| "".equals( this.getPhone()) )
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.MISSION_NOT_EXISTS," address invalid");
  }
  public String toString(){
    return this.getfName()+" "+this.getlName()+", "+this.getAddress()+", "+this.getCity()+" "+this.getCountry();
  }

}
