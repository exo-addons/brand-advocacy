package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.MissionController;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;

/**
 * Created by exoplatform on 01/10/14.
 */
@SessionScoped
public class JuZBackEndApplication {

  @Inject
  @Path("index.gtmpl")
  Template index;

  OrganizationService organizationService;
  IService brandAdvocacyService;

  @Inject
  MissionController missionController;

  @Inject
  public JuZBackEndApplication(OrganizationService organizationService,IService brandAdvocacyService){
    this.organizationService = organizationService;
    this.brandAdvocacyService = brandAdvocacyService;
  }

  @View
  public Response.Content index(){
    return index.ok();
  }

}
