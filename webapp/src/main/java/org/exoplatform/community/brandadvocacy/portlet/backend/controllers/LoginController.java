package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;


import juzu.SessionScoped;
import org.exoplatform.brandadvocacy.model.Role;

import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by exoplatform on 10/10/14.
 */
@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

  private String currentProgramId;
  private String currentUserName;
  private String rights;
  public String getCurrentUserName() {
    return currentUserName;
  }

  public void setCurrentUserName(String currentUserName) {
    this.currentUserName = currentUserName;
  }

  public String getCurrentProgramId() {
    return currentProgramId;
  }

  public void setCurrentProgramId(String currentProgramId) {
    this.currentProgramId = currentProgramId;
  }


  public String getRights() {
    return rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }
  public Boolean isAdmin(){
    return this.getRights().equals(Role.Admin.getLabel());
  }
  public Boolean isValidator(){
    return this.getRights().equals(Role.Validator.getLabel());
  }
  public Boolean isShippingManager(){
    return this.getRights().equals(Role.Shipping_Manager.getLabel());
  }
}
