package org.exoplatform.community.brandadvocacy.portlet.frontend;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import juzu.template.Template;

import javax.inject.Inject;

/**
 * Created by exoplatform on 07/10/14.
 */
public class JuZFrontEndApplication {

  @Inject
  @Path("index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.index indexTpl;

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


  @View
  public Response.Content index(){
    return indexTpl.ok();
  }

/*  @Ajax
  @Resource
  public Response.Content loadStartContent(){
    return startTpl.ok();
  }*/
}
