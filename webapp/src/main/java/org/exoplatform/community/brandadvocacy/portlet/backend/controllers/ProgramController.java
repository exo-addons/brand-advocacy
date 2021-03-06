package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Size;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.Settings;
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
    String manager_name = "";
    String manager_title = "";
    String  size_out_of_stock = "";
    String save_user_data_endpoint = "";
    String save_user_data_endpoint_token = "";
    String save_user_data_request_method = "";
    String facebook_oauth_url="",google_oauth_url="",linkedin_oauth_url = "";
    String facebook_share_url="",google_share_url="",linkedin_share_url = "";

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
        manager_name = Utils.getAttrFromJson(settings,Program.MANAGER_NAME_KEY);
        manager_title = Utils.getAttrFromJson(settings,Program.MANAGER_TITLE_KEY);
        size_out_of_stock = Utils.getAttrFromJson(settings,Program.size_out_of_stock_setting_key);
        save_user_data_endpoint = Utils.getAttrFromJson(settings,Program.save_user_data_endpoint_setting_key);
        save_user_data_endpoint_token = Utils.getAttrFromJson(settings,Program.save_user_data_endpoint_token_setting_key);
        save_user_data_request_method = Utils.getAttrFromJson(settings,Program.save_user_data_request_method_setting_key);


        facebook_oauth_url = Utils.getAttrFromJson(settings,Program.FACEBOOK_OAUTH_URL_SETTING_KEY);
        google_oauth_url = Utils.getAttrFromJson(settings,Program.GOOGLE_OAUTH_URL_SETTING_KEY);
        linkedin_oauth_url = Utils.getAttrFromJson(settings,Program.LINKEDIN_OAUTH_URL_SETTING_KEY);
        facebook_share_url = Utils.getAttrFromJson(settings,Program.FACEBOOK_SHARE_URL_SETTING_KEY);
        google_share_url = Utils.getAttrFromJson(settings,Program.GOOGLE_SHARE_URL_SETTING_KEY);
        linkedin_share_url = Utils.getAttrFromJson(settings,Program.LINKEDIN_SHARE_URL_SETTING_KEY);

      }

      String[] out_of_stock = size_out_of_stock.split(",");
      return indexTpl.with().set("program", program).set("banner_url",banner_url)
              .set("email_sender",email_sender).set("sizes", Size.values())
              .set("size_out_of_stock",out_of_stock)
              .set("save_user_data_endpoint",save_user_data_endpoint)
              .set("save_user_data_endpoint_token",save_user_data_endpoint_token)
              .set("save_user_data_request_method",save_user_data_request_method)
              .set("manager_name",manager_name).set("manager_title",manager_title)
              .set("facebook_oauth_url",facebook_oauth_url)
              .set("google_oauth_url",google_oauth_url)
              .set("linkedin_oauth_url",linkedin_oauth_url)
              .set("facebook_share_url",facebook_share_url)
              .set("google_share_url",google_share_url)
              .set("linkedin_share_url",linkedin_share_url)
              .ok();
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
  public Response update(@Mapped Settings settingsBean){
    String title = settingsBean.title;
    String banner_url= settingsBean.banner_url;
    String email_sender = settingsBean.email_sender;
    String manager_name = settingsBean.manager_name;
    String manager_title = settingsBean.manager_title;
    String size_out_of_stock = settingsBean.size_out_of_stock;
    String save_user_data_endpoint = settingsBean.save_user_data_endpoint;
    String save_user_data_endpoint_token = settingsBean.save_user_data_endpoint_token;
    String save_user_data_request_method = settingsBean.save_user_data_request_method;
    String facebook_oauth_url = settingsBean.facebook_oauth_url;
    String google_oauth_url = settingsBean.google_oauth_url;
    String linkedin_oauth_url = settingsBean.linkedin_oauth_url;
    String facebook_share_url = settingsBean.facebook_share_url;
    String google_share_url = settingsBean.google_share_url;
    String linkedin_share_url = settingsBean.linkedin_share_url;

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
          if (null == manager_name)
            manager_name = "";
          if (null == manager_title)
            manager_title = "";

          if (null == facebook_oauth_url)
            facebook_oauth_url = "";
          if (null == google_oauth_url)
            google_oauth_url = "";
          if (null == linkedin_oauth_url)
            linkedin_oauth_url = "";
          if (null == facebook_share_url)
            facebook_share_url = "";
          if (null == google_share_url)
            google_oauth_url = "";
          if (null == linkedin_share_url)
            linkedin_share_url = "";
            
          settings.put(Program.banner_url_setting_key,banner_url);
          settings.put(Program.email_sender_setting_key,email_sender);
          settings.put(Program.MANAGER_NAME_KEY,manager_name);
          settings.put(Program.MANAGER_TITLE_KEY,manager_title);
          if(null == size_out_of_stock)
            size_out_of_stock = "";
          settings.put(Program.size_out_of_stock_setting_key,size_out_of_stock);
          settings.put(Program.save_user_data_endpoint_setting_key,save_user_data_endpoint);
          settings.put(Program.save_user_data_endpoint_token_setting_key,save_user_data_endpoint_token);
          settings.put(Program.save_user_data_request_method_setting_key,save_user_data_request_method);
          settings.put(Program.FACEBOOK_OAUTH_URL_SETTING_KEY,facebook_oauth_url);
          settings.put(Program.GOOGLE_OAUTH_URL_SETTING_KEY,google_oauth_url);
          settings.put(Program.LINKEDIN_OAUTH_URL_SETTING_KEY,linkedin_oauth_url);
          settings.put(Program.FACEBOOK_SHARE_URL_SETTING_KEY,facebook_share_url);
          settings.put(Program.GOOGLE_SHARE_URL_SETTING_KEY,google_share_url);
          settings.put(Program.LINKEDIN_SHARE_URL_SETTING_KEY,linkedin_share_url);
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
