package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.View;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.IService;
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

  public Response.Content listProgramManagers(String programId){

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

  public Response.Content addProgramManagerForm(String programId){
    return addTpl.with().set("programId",programId).set("roles",Role.values()).ok();
  }
  @Action
  public Response add2Program(String progamId, String username, String role, String notif){
    try {
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
          this.jcrService.addManager2Program(manager);
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
  public void updateProgramManager(String programId, String username, String role, String notif){
    Boolean mNotif = false;
    if(null != notif){
      mNotif = "1".equals(notif)? true:false;
    }
    Manager manager = this.jcrService.getProgramManagerByUserName(programId,username);
    if(null != manager){
      manager.setRole(Role.getRole(Integer.parseInt(role)));
      manager.setNotif(mNotif);
      this.jcrService.updateProgramManager(manager);
    }
  }

  @Action
  public void deleteProgramManager(String programId, String username){
    this.jcrService.removeManagerFromProgram(programId,username);
  }

}
