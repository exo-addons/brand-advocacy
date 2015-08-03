package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Size;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.services.organization.OrganizationService;
import org.json.JSONException;
import org.json.JSONObject;

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
    String banner_url = "";
    String email_sender = "";
    String  size_out_of_stock = "";
    Program program = null;
    String programId = loginController.getCurrentProgramId();
    if (null != programId){
      program = this.jcrService.getProgramById(programId);
    }
    if (null != program){
      JSONObject settings = this.jcrService.getProgramSettings(programId);
      if (null != settings){
        banner_url = Utils.getAttrFromJson(settings,Program.banner_url_setting_key);
        email_sender = Utils.getAttrFromJson(settings,Program.email_sender_setting_key);
        size_out_of_stock = Utils.getAttrFromJson(settings,Program.size_out_of_stock_setting_key);
      }
      String[] out_of_stock = size_out_of_stock.split(",");
      return indexTpl.with().set("program", program).set("banner_url",banner_url).set("email_sender",email_sender).set("sizes", Size.values()).set("size_out_of_stock",out_of_stock).ok();
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
  public Response update(String title,String banner_url,String email_sender,String size_out_of_stock){
    String programId = loginController.getCurrentProgramId();
    Program program = this.jcrService.getProgramById(programId);
    if (null != program){
      program.setTitle(title);
      if (null != this.jcrService.updateProgram(program)){
        JSONObject settings = new JSONObject();
        try {
          if (null == banner_url)
            banner_url = "";
          if (null == email_sender)
            email_sender = "";
          if(null == size_out_of_stock)
            size_out_of_stock = "";
          settings.put(Program.banner_url_setting_key,banner_url);
          settings.put(Program.email_sender_setting_key,email_sender);
          settings.put(Program.size_out_of_stock_setting_key,size_out_of_stock);
          program.setSettings(settings);
          this.jcrService.setProgramSettings(program);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        return Response.ok("The program has been updated");
      }
    }
    return Response.ok("nok");
  }


}
