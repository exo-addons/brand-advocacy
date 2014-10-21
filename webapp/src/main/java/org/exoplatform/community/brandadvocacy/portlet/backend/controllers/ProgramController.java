package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import juzu.request.SecurityContext;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;
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

  public Response index(@Mapped Program program){
    if (null != program){
      return indexTpl.with().set("program",program).ok();
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
      return JuZBackEndApplication_.index();
    }else
      return JuZBackEndApplication_.index("cannot add program, please retry later");

  }

  @Action
  public Response update(String programId,String title){
    Program program = this.jcrService.getProgramById(programId);
    if (null != program){
      program.setTitle(title);
      this.jcrService.updateProgram(program);
    }
    return JuZBackEndApplication_.index();
  }

}
