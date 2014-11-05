package org.exoplatform.community.brandadvocacy.portlet.backend.models;

/**
 * Created by exoplatform on 16/10/14.
 */
public class ParticipantDTO {

  private String userName;
  private String fullName;
  private String urlProfile;
  private String urlAvatar;
  private String email;
  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getUrlProfile() {
    return urlProfile;
  }

  public void setUrlProfile(String urlProfile) {
    this.urlProfile = urlProfile;
  }

  public String getUrlAvatar() {
    return urlAvatar;
  }

  public void setUrlAvatar(String urlAvatar) {
    this.urlAvatar = urlAvatar;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}

