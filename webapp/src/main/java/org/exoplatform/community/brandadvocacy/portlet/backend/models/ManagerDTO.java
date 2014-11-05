package org.exoplatform.community.brandadvocacy.portlet.backend.models;

import org.exoplatform.brandadvocacy.model.Role;

/**
 * Created by exoplatform on 10/20/14.
 */
public class ManagerDTO {
  private String username;
  private String fullName;
  private boolean isNotif;
  private Role role;
  public ManagerDTO(String username){
    this.setUserName(username);
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

  public String getUserName() {
    return username;
  }

  public void setUserName(String username) {
    this.username = username;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public boolean getNotif() {
    return isNotif;
  }
}
