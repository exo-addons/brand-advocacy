package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by exoplatform on 10/10/14.
 */
public class LoginController implements Serializable {


  private String currentUserName;

  @Inject
  public LoginController(OrganizationService organizationService){

//    organizationService.get

  }

  public String getCurrentUserName() {
    return currentUserName;
  }

  public void setCurrentUserName(String currentUserName) {
    this.currentUserName = currentUserName;
  }
}
