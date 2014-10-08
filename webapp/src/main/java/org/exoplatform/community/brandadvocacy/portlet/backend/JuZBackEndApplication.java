package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.service.IService;
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

  OrganizationService organizationService;
  IService brandAdvocacyService;

  @Inject
  public JuZBackEndApplication(OrganizationService organizationService,IService brandAdvocacyService){
    this.organizationService = organizationService;
    this.brandAdvocacyService = brandAdvocacyService;
  }

  @View
  public Response.Content missionsList(){
    return missionsListTpl.with().missions(this.brandAdvocacyService.getAllMissions()).ok();
  }

  @View
  @Route("/listMissions")
  public Response.Content index(){
    return this.missionsList();
  }
  @Action
  @Route("/addMission")
  public Response.View addMission(String title){
    Mission mission = new Mission(title);
    this.brandAdvocacyService.addMission(mission);
    return JuZBackEndApplication_.missionsList();
  }



}
