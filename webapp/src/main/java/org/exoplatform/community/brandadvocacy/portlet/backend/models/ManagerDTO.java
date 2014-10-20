package org.exoplatform.community.brandadvocacy.portlet.backend.models;

/**
 * Created by exoplatform on 10/20/14.
 */
public class ManagerDTO {
  private String username;
  private String fullName;
  private boolean isNotif;

  public ManagerDTO(String username){
    this.setUsername(username);
  }
  public boolean isNotif() {
    return isNotif;
  }

  public void setNotif(boolean isNotif) {
    this.isNotif = isNotif;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
