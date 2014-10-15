package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.request.SecurityContext;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;
import java.io.Serializable;

/**
 * Created by exoplatform on 10/10/14.
 */
public class LoginController implements Serializable {


  private String currentUserName;
  OrganizationService organizationService;

  @Inject
  public LoginController(OrganizationService organizationService, SecurityContext securityContext){

    this.organizationService = organizationService;
    this.setCurrentUserName(securityContext.getUserPrincipal().getName());

  }

  public String getCurrentUserName() {
    return currentUserName;
  }

  public void setCurrentUserName(String currentUserName) {
    this.currentUserName = currentUserName;
  }
}
