package org.exoplatform.community.brandadvocacy.portlet.backend.models;

/**
 * Created by exoplatform on 10/20/14.
 */
public class AddressDTO {
  private String firstName;
  private String lastName;
  private String address;
  private String city;
  private String Phone;
  private String country;
  private String countryName;

  public AddressDTO(String fName, String lName, String address, String city, String country, String phone){
    this.setFirstName(fName);
    this.setLastName(lName);
    this.setAddress(address);
    this.setCity(city);
    this.setCountry(country);
    this.setCountryName(country);
    this.setPhone(phone);
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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
  public String getFullAddresses(){
    return this.getFirstName()+" "+this.getLastName()+", "+this.getAddress()+" "+this.getCity()+", "+this.getCountryName();
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }
}

