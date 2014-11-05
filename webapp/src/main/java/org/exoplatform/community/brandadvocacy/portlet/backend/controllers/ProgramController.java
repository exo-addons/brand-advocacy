package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.ManagerDTO;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 20/10/14.
 */
@SessionScoped
public class ProgramController {


  @Inject
  @Path("program/index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.program.index indexTpl;

  @Inject
  @Path("program/add.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.program.add addTpl;


  OrganizationService organizationService;
  IService jcrService;

  @Inject
  LoginController loginController;

  @Inject
  public ProgramController(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  public Response index(){
    Program program = null;
    String programId = loginController.getCurrentProgramId();
    if (null != programId){
      program = this.jcrService.getProgramById(programId);
    }

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
            managerDTO.setRole(manager.getRole());
            managerDTO.setNotif(manager.getNotif());
            managerDTOs.add(managerDTO);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return indexTpl.with().set("roles", Role.values()).set("program", program).set("managers",managerDTOs).set("currentUser",loginController.getCurrentUserName()).ok();
    }
    else
      return addTpl.ok();
  }
  @Action
  public Response add(String title){
    Program program = new Program(title);
    program = this.jcrService.addProgram(program);
    if (null != program){
      Manager manager = new Manager(loginController.getCurrentUserName());
      manager.setParentId(program.getId());
      this.jcrService.addManager2Program(manager);
      return JuZBackEndApplication_.index("program_index");
    }else
      return JuZBackEndApplication_.showError("cannot add program, please retry later");

  }

  @Action
  public Response update(String programId,String title){
    Program program = this.jcrService.getProgramById(programId);
    if (null != program){
      program.setTitle(title);
      this.jcrService.updateProgram(program);
    }
    return JuZBackEndApplication_.index("program_index");
  }

}
