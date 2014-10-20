package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Path;
import juzu.Response;
import juzu.SessionScoped;
import juzu.View;
import juzu.request.SecurityContext;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;

/**
 * Created by exoplatform on 20/10/14.
 */
@SessionScoped
public class ProgramController {


  @Inject
  @Path("program/index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.program.index indexTpl;

/*
  OrganizationService organizationService;
  IService iService;

  @Inject
  LoginController loginController;


  public ProgramController(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.iService = iService;
  }
*/
  @View
  public Response.Content index(SecurityContext securityContext,String action){
  //  loginController.setCurrentUserName(securityContext.getUserPrincipal().getName());
    if (action.equals("index"))
      return Response.ok(" i am program");
    else
      return Response.ok("no index");
  }

  public Response.Content detail(){
    return indexTpl.ok();
  }
}
