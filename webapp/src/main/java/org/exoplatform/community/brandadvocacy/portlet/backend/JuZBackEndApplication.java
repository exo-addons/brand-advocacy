package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by exoplatform on 01/10/14.
 */
@SessionScoped
public class JuZBackEndApplication {

  @Inject
  @Path("index.gtmpl")
  Template index;
/*
  @Inject
  OrganizationService organizationService;
  */
  @Inject
  IService brandAdvocacyService;

  @Inject
  //OrganizationService organizationService,
  public JuZBackEndApplication(IService brandAdvocacyService){
    //this.organizationService = organizationService;
    this.brandAdvocacyService = brandAdvocacyService;
  }

  @View
  public Response.Content index(){
    return index.ok();
  }


  @Action
  @Route("/addMission")
  public Response.View addMission(String title){
    Mission mission = new Mission(title);
    this.brandAdvocacyService.addMission(mission);
    return JuZBackEndApplication_.index();
  }

}
