package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.templates.addMissionFrm;
import org.exoplatform.community.brandadvocacy.portlet.backend.templates.missionsList;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by exoplatform on 01/10/14.
 */
@SessionScoped
public class JuZBackEndApplication {

  @Inject
  @Path("index.gtmpl")
  Template index;

  @Inject
  @Path("missionsList.gtmpl")
  missionsList missionsListTpl;

  @Inject
  @Path("addMissionFrm.gtmpl")
  addMissionFrm addMissionFrmTpl;

  OrganizationService organizationService;
  IService brandAdvocacyService;

  @Inject
  public JuZBackEndApplication(OrganizationService organizationService,IService brandAdvocacyService){
    this.organizationService = organizationService;
    this.brandAdvocacyService = brandAdvocacyService;
  }

  @View
  public Response.Content index(){
    return index.ok();
  }

  @View
  public Response.Content missionsList(){
    return missionsListTpl.with().missions(this.brandAdvocacyService.getAllMissions()).ok();
  }

  @View
  public Response.Content addMissionFrm(){
    return addMissionFrmTpl.ok();
  }

  @View
  public Response.Content show(String id){
    return Response.ok("string id = "+id).withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");
  }

  @View
  public Response.Content editMission(String id){
    String res = "mission not found";
    try {
      Mission mission =  this.brandAdvocacyService.getMissionById(id);
      if(null != mission){
        return addMissionFrmTpl.with().mission(mission).ok();
      }
    } catch (javax.jcr.RepositoryException e) {
      e.printStackTrace();
    }
    return Response.ok(res).withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");
  }

  @Action
  public Response.View addMission(String title){
    Mission mission = new Mission(title);
    this.brandAdvocacyService.addMission(mission);
    return JuZBackEndApplication_.index();
  }



}
