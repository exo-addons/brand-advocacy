/**
 * Created by exoplatform on 01/10/14.
 */
@Application(defaultController = org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication.class)
@Portlet(name="BackendPortlet")
@Bindings(
  {
    @Binding(value = org.exoplatform.services.organization.OrganizationService.class),
    @Binding(value = org.exoplatform.social.core.manager.IdentityManager.class),
    @Binding(value = org.exoplatform.brandadvocacy.service.IService.class),
    @Binding(LoginController.class),
  }
)
@Scripts({
  @Script(id="jquery",value = "js/jquery-1.7.1.min.js", location = AssetLocation.SERVER),
  @Script(value = "js/brad-backend.js",depends = "jquery", location = AssetLocation.SERVER),
  @Script(value = "js/iphone-style-checkboxes.js", location = AssetLocation.SERVER, depends = "jquery")
})
@Stylesheets(@Stylesheet(value = "skin/css/brad-backend.css", location = AssetLocation.SERVER))
@Assets("*")
package org.exoplatform.community.brandadvocacy.portlet.backend;
import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.*;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.LoginController;