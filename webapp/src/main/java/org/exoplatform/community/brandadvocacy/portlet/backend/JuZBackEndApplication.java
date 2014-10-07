package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.*;
import juzu.template.Template;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by exoplatform on 01/10/14.
 */

public class JuZBackEndApplication {

  static Set<String> locations = new HashSet<String>();

  static {
    locations.add("hanoi");
    locations.add("paris");
  }
  @Inject
  @Path("index.gtmpl")
  Template index;

  @View
  public Response.Content index(){
    return index("i_am_urlparams");
  }
  @Inject
  Service service;

  @View
  @Route("/show/{location}")
  public Response.Content index(String location){
    Map<String,Object> parameters = new HashMap<String, Object>();
    String serviceString = service.show();
    parameters.put("urlParams",location);
    parameters.put("serviceResult",serviceString);
    parameters.put("location", "marseille");
    parameters.put("temperature", serviceString);
    parameters.put("locations", locations);
    return index.with(parameters).ok();
  }

  @Action
  @Route("/add")
  public Response.View add(String location){
    locations.add(location);
    return JuZBackEndApplication_.index(location);
  }
}
