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
 // @Script(id = "jquery", value = "javascripts/jquery.1.7.2.min.js"),
  @Script(
    id = "bradJS", value = "js/test.js",location = AssetLocation.SERVER
         // ,   depends = {"jquery"}
  )
})
package org.exoplatform.community.brandadvocacy.portlet.backend;
import juzu.Route;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Scripts;
import juzu.plugin.webjars.WebJar;
import juzu.plugin.webjars.WebJars;
import org.exoplatform.commons.juzu.KernelProviderFactory;
import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import juzu.plugin.servlet.Servlet;
import org.exoplatform.brandadvocacy.service.IService;