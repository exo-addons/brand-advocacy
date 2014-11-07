/**
 * Created by exoplatform on 07/10/14.
 */
@Application(defaultController = JuZFrontEndApplication.class)
@Portlet(name="FrontendPortlet")
@Bindings(
        {
                @Binding(value = org.exoplatform.services.organization.OrganizationService.class),
                @Binding(value = org.exoplatform.brandadvocacy.service.IService.class)
        }
)
package org.exoplatform.community.brandadvocacy.portlet.frontend;

import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;