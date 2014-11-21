package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;

import juzu.request.SecurityContext;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.MissionParticipant;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.*;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.webui.application.WebuiRequestContext;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by exoplatform on 01/10/14.
 */
public class JuZBackEndApplication {

  @Inject
  @Path("index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.index indexTpl;

  @Inject
  @Path("error.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.error errorTpl;

  @Inject
  @Path("confirmpopup.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.confirmpopup confirmpopupTpl;

  OrganizationService organizationService;
  IService jcrService;

  @Inject
  LoginController loginController;
  @Inject
  MissionParticipantController missionParticipantController;


  @Inject
  public JuZBackEndApplication(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @View
  public Response index(SecurityContext securityContext) {

    String action = WebuiRequestContext.getCurrentInstance().getRequestParameter("action");
    String missionParticipantId = WebuiRequestContext.getCurrentInstance().getRequestParameter("id");


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
    if (null != loginController.getRights()){
      if (null != action && action.equals("mp_view") && null != missionParticipantId && !"".equals(missionParticipantId) ){
        MissionParticipant missionParticipant = this.jcrService.getMissionParticipantById(missionParticipantId);
        if (null != missionParticipant){
          return indexTpl.with().set("mp_view",true).set("username",missionParticipant.getParticipant_username()).set("missionParticipantId",missionParticipantId).ok();
        }
      }
      return indexTpl.with().set("mp_view",false).set("username","").set("missionParticipantId","").ok();

    }
    return this.showError("alert-info","Info","You have no rights");
  }

  public Response showError(String type,String icon,String msg){
    return errorTpl.with().set("type",type).set("icon",icon).set("msg",msg).ok();
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
  public Response loadConfirmPopupContent(String action,String id,String msg){
    return confirmpopupTpl.with().set("action",action).set("id",id).set("msg",msg).ok();
  }
  @Ajax
  @Resource
  public void sendNotifUpdateMissionParticipantEmail(String missionParticipantId){
    this.jcrService.sendNotifMissionParticipantEmail(missionParticipantId);
  }
}
