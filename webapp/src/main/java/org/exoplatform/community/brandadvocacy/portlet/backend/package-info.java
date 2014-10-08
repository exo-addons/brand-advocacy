/**
 * Created by exoplatform on 01/10/14.
 */
@Application(defaultController = JuZBackEndApplication.class)
@Portlet(name="BackendPortlet")
@Bindings(
  {
//    @Binding(value = org.exoplatform.services.organization.OrganizationService.class),
    @Binding(value = org.exoplatform.brandadvocacy.service.IService.class,implementation = org.exoplatform.brandadvocacy.service.JCRImpl.class)
  }
)
@Servlet("/")
package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;
import juzu.plugin.servlet.Servlet;
import org.exoplatform.brandadvocacy.service.IService;