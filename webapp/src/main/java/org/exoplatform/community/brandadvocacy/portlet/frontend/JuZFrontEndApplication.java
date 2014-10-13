package org.exoplatform.community.brandadvocacy.portlet.frontend;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 07/10/14.
 */
public class JuZFrontEndApplication {

  OrganizationService organizationService;
  IService jcrService;

  @Inject
  @Path("index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.index indexTpl;

  @Inject
  @Path("discovery.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.discovery discoveryTpl;

  @Inject
  @Path("start.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.start startTpl;

  @Inject
  @Path("process.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.process processTpl;

  @Inject
  @Path("terminate.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.terminate terminateTpl;

  @Inject
  @Path("thankyou.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.thankyou thankyouTpl;

  @Inject
  public JuZFrontEndApplication(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @View
  public Response.Content index(){
    return indexTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadIndexView(){
    return indexTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadDiscoveryView(){
    return discoveryTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadStartView(){
    return startTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadProcessView(){
    String title = "Write a review of eXo Platform<br>on <a href=\"#\">GetApp.com</a>";
    Mission mission = new Mission(title);
    List<Proposition> propositions = new ArrayList<Proposition>(1);
    String content = "I’ve been using eXo Platform since few years now. What I like the most with eXo is...";
    Proposition proposition = new Proposition(content);
    propositions.add(proposition);
    mission.setPropositions(propositions);
    return processTpl.with().set("mission",mission).ok();
  }

  @Ajax
  @Resource
  public Response.Content loadTerminateView(){
    return terminateTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadThankyouView(){
    return thankyouTpl.ok();
  }
}
