package org.exoplatform.community.brandadvocacy.portlet.frontend;

import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.template.Template;

import javax.inject.Inject;

/**
 * Created by exoplatform on 07/10/14.
 */
public class JuZFrontEndApplication {

  @Inject
  @Path("index.gtmpl")
  Template index;

  @View
  public Response.Content index(){
    return index.ok();
  }
}
