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
package org.exoplatform.community.brandadvocacy.portlet.backend;
import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import org.exoplatform.community.brandadvocacy.portlet.backend.controllers.LoginController;