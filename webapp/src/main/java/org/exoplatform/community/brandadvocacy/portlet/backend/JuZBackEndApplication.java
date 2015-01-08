package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.impl.common.JSON;
import juzu.impl.common.JSONParser;
import juzu.request.HttpContext;
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
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
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

/*  static {
   Utils.readCountriesJSON();
  }*/
  @Inject
  public JuZBackEndApplication(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @View
  public Response index(SecurityContext securityContext) {

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

      String action = WebuiRequestContext.getCurrentInstance().getRequestParameter("action");
      String id = WebuiRequestContext.getCurrentInstance().getRequestParameter("id");

      String[] menus = {"program","mission","participant","mp_view","m_view"};
      if (null != action){
        if (!Arrays.asList(menus).contains(action))
          action = null;
      }
      if (loginController.isAdmin()){
        if (null == action ){
          action = "program";
        }
      }else{
        if (null == action || "program".equals(action) || "mission".equals(action) || "m_view".equals(action)){
          action = "participant";
        }
      }
      String username = "";
      if (null == id)
        id = "";
      if (action.equals("mp_view") ){
        MissionParticipant missionParticipant = this.jcrService.getMissionParticipantById(id);
        if (null != missionParticipant){
          username = missionParticipant.getParticipant_username();
        }
      }
      return indexTpl.with().set("action", action).set("username",username).set("id",id).ok();

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
  public void sendNotifUpdateMissionParticipantEmail(String missionParticipantId,String note){
    JSONObject settings = this.jcrService.getProgramSettings(loginController.getCurrentProgramId());
    this.jcrService.sendNotifMissionParticipantEmail(settings,missionParticipantId,note);
  }
}
