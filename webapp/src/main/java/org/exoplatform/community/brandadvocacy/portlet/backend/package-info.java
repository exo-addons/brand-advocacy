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
    @Binding(Flash.class)
  }
)
@Assets(
        scripts = {
                @Script(id = "jquery", src = "js/jquery-1.7.1.min.js"),
                @Script(src = "js/ckeditor/ckeditor.js", depends = "jquery"),
                @Script(src = "js/ckeditor/adapters/jquery.js", depends = "jquery"),
                @Script(src = "js/iphone-style-checkboxes.js", depends = "jquery"),
                @Script(src = "js/brad-backend.js", depends = "juzu.ajax")
        },
        stylesheets = {
                @Stylesheet(src = "css/brad-backend.css")
        },
        location = AssetLocation.SERVER
)
package org.exoplatform.community.brandadvocacy.portlet.backend;
import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.*;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.LoginController;