/**
 * Created by exoplatform on 01/10/14.
 */

@Application(defaultController = JuZBackEndApplication.class)
@WebJars(@WebJar("jquery"))
@Portlet(name="BackendPortlet")
@Bindings(
  {
    @Binding(value = org.exoplatform.services.organization.OrganizationService.class),
    @Binding(value = org.exoplatform.brandadvocacy.service.IService.class)
  }
)
@Scripts({
   @Script(id = "jquery", value = "jquery/1.10.2/jquery.js"),
   @Script(
    id = "bradBKJS", value = "js/brad-backend.js",location = AssetLocation.SERVER,depends = {"jquery"}
  )
})
package org.exoplatform.community.brandadvocacy.portlet.backend;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Scripts;
import juzu.plugin.webjars.WebJar;
import juzu.plugin.webjars.WebJars;
import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;