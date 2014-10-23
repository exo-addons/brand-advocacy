package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.ManagerDTO;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 10/12/14.
 */
public class ManagerController {


  IService jcrService;
  OrganizationService organizationService;
  @Inject
  MissionController missionController;

  @Inject
  LoginController loginController;
  @Inject
  @Path("manager/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.manager.list listTpl;

  @Inject
  @Path("manager/add.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.manager.add addTpl;

  @Inject
  public ManagerController(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @Ajax
  @Resource
  public Response listProgramManagers(){
    String programId = loginController.getCurrentProgramId();
    Program program = this.jcrService.getProgramById(programId);
    if (null != program){
      List<Manager> managers = this.jcrService.getAllManagersInProgram(programId);
      User exoUser;
      List<ManagerDTO> managerDTOs = new ArrayList<ManagerDTO>(managers.size());
      ManagerDTO managerDTO;
      for (Manager manager:managers){
        try {
          exoUser = this.organizationService.getUserHandler().findUserByName(manager.getUserName());
          if(null != exoUser){
            managerDTO = new ManagerDTO(manager.getUserName());
            managerDTO.setFullName(exoUser.getFirstName() + " "+exoUser.getLastName());
            managerDTOs.add(managerDTO);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return listTpl.with().set("roles",Role.values()).set("managers", managerDTOs).set("program",program).ok();
    }
    else
      return Response.ok("nok");


  }
  @View
  public Response.Content addProgramManagerForm(){
    return addTpl.with().set("roles",Role.values()).ok();
  }
  @Action
  public Response add2Program(String username, String role, String notif){
    try {
      String progamId = loginController.getCurrentProgramId();
      User exoUser = this.organizationService.getUserHandler().findUserByName(username);
      if(null != exoUser){
        Boolean mNotif = false;
        if(null != notif){
          mNotif = "1".equals(notif)? true:false;
        }
        Program program = this.jcrService.getProgramById(progamId);
        if (null != program){
          Manager manager = new Manager(username);
          manager.setParentId(program.getId());
          manager.setRole(Role.getRole(Integer.parseInt(role)));
          manager.setNotif(mNotif);
          manager = this.jcrService.addManager2Program(manager);
          if (null != manager)
            return JuZBackEndApplication_.index("program_index");
        }
      }
      else
        return Response.ok("cannot find this user");

    } catch (Exception e) {
      e.printStackTrace();
    }

    return Response.ok("something went wrong, cannot add user like a manager");

  }

  @Action
  public void updateProgramManager(String username, String role, String notif){
    String programId = loginController.getCurrentProgramId();
    Boolean mNotif = false;
    if(null != notif){
      mNotif = "yes".equals(notif)? true:false;
    }
    Manager manager = this.jcrService.getProgramManagerByUserName(programId,username);
    if(null != manager){
      manager.setRole(Role.getRole(Integer.parseInt(role)));
      manager.setNotif(mNotif);
      manager = this.jcrService.updateProgramManager(manager);
      if (null != manager){
        if (manager.getUserName().equals(loginController.getCurrentUserName())){
          loginController.setRights(manager.getRole().getLabel());
        }
      }
    }
  }

  @Action
  public Response deleteProgramManager(String programId, String username){
    this.jcrService.removeManagerFromProgram(programId,username);
    return JuZBackEndApplication_.index("program_index");
  }
}
