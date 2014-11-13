package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.community.brandadvocacy.portlet.backend.Flash;
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

  @Ajax
  @Resource
  public Response index(){
    Program program = null;
    String programId = loginController.getCurrentProgramId();
    if (null != programId){
      program = this.jcrService.getProgramById(programId);
    }

    if (null != program){
      return indexTpl.with().set("program", program).ok();
    }
    else
      return addTpl.ok();
  }
  @Ajax
  @Resource
  public Response add(String title){
    Program program = new Program(title);
    program = this.jcrService.addProgram(program);
    if (null != program){
      Manager manager = new Manager(loginController.getCurrentUserName());
      manager.setParentId(program.getId());
      if (null != this.jcrService.addManager2Program(manager)) {
        loginController.setCurrentProgramId(program.getId());
        return Response.ok(program.getId());
      }
    }
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response update(String title){
    String programId = loginController.getCurrentProgramId();
    Program program = this.jcrService.getProgramById(programId);
    if (null != program){
      program.setTitle(title);
      if (null != this.jcrService.updateProgram(program)){
        return Response.ok("ok");
      }
    }
    return Response.ok("nok");
  }


}
