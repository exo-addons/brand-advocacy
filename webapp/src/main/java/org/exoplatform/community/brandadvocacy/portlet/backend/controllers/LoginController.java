package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Response;
import juzu.SessionScoped;
import juzu.View;
import juzu.request.SecurityContext;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by exoplatform on 10/10/14.
 */
@Named("login")
@SessionScoped
public class LoginController implements Serializable {

  private String currentProgramId;
  private String currentUserName;

  OrganizationService organizationService;
  @Inject
  public LoginController(OrganizationService organizationService){

    this.organizationService = organizationService;
    this.setCurrentUserName("init");

  }

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
}
