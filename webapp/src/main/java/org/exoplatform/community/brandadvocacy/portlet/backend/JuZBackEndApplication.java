package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import juzu.request.SecurityContext;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.*;
import org.exoplatform.community.brandadvocacy.portlet.backend.templates.index;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.manager.IdentityManager;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by exoplatform on 01/10/14.
 */
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
  @Inject
  PropositionController propositionController;

  @Inject
  MissionParticipantController missionParticipantController;

  @Inject
  public JuZBackEndApplication(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @View
  public Response index(SecurityContext securityContext,String action) {

    if (null == loginController.getCurrentProgramId()){
      List<Program> programs = this.jcrService.getAllPrograms();
      Program program = null;
      loginController.setCurrentUserName(securityContext.getUserPrincipal().getName());
      if (programs.size() > 0){
        program = programs.get(0);
        loginController.setCurrentProgramId(program.getId());
      }
      this.generateRights(loginController.getCurrentProgramId(),loginController.getCurrentUserName());
    }
    if (null != action){
      if (action.equals("mission_participant_index"))
        return missionParticipantController.index();
      else if (action.equals("mission_index"))
        return missionController.index();
    }
    if (loginController.isAdmin()){
      return programController.index();
    }else{
      return missionParticipantController.index();
    }

  }
  @View
  public Response showError(String msg){
    return indexTpl.with().set("msg",msg).ok();
  }

  private void generateRights(String programId,String username){
    if (null != programId && null != username){
      Manager manager = this.jcrService.getProgramManagerByUserName(programId,username);
      if (null != manager){
        loginController.setRights(manager.getRole().getLabel());
      }
    }
    else
      loginController.setRights(Role.Admin.getLabel());

  }

  @Ajax
  @Resource
  public Response ta(){
    return Response.ok("tao");
  }

}
