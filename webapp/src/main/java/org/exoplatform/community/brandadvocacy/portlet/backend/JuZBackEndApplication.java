package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.request.SecurityContext;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.LoginController;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.MissionController;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.ProgramController;
import org.exoplatform.community.brandadvocacy.portlet.backend.templates.index;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.manager.IdentityManager;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by exoplatform on 01/10/14.
 */
@SessionScoped
public class JuZBackEndApplication {

  @Inject
  @Path("index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.index indexTpl;

  OrganizationService organizationService;
  IService jcrService;

  @Inject
  MissionController missionController;
  @Inject
  LoginController loginController;
  @Inject
  ProgramController programController;

  String currentProgramId = null;
  @Inject
  public JuZBackEndApplication(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @View
  public Response index(SecurityContext securityContext) {
    loginController.setCurrentUserName(securityContext.getUserPrincipal().getName());
    List<Program> programs = this.jcrService.getAllPrograms();
    Program program = null;
    if (programs.size() > 0){
      program = programs.get(0);
      this.currentProgramId = program.getId();
    }
    return programController.index(program);
  }
  @View
  public Response index(SecurityContext securityContext,String action,String programId){
    if (null == programId){
      programId = this.currentProgramId;
    }
    loginController.setCurrentUserName(securityContext.getUserPrincipal().getName());
    if (action.equals("mission_index")){
      return missionController.index(securityContext,"index",programId,null);
    }
    return missionController.index(securityContext,"add",programId,null);


  }
  @View
  public Response index(SecurityContext securityContext,String action,String programId,String missionId){
    loginController.setCurrentUserName(securityContext.getUserPrincipal().getName());
    if (action.equals("mission_delete")){
      return missionController.index(securityContext,"delete",programId,missionId);
    }
    else if (action.equals("mission_edit")){
      return missionController.index(securityContext,"edit",programId,missionId);
    }
    return Response.ok("ok");
  }
}
