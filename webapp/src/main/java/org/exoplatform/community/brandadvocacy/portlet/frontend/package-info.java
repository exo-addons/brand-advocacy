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
@Scripts({
        @Script(id="jquery",value = "js/jquery-1.7.1.min.js", location = AssetLocation.SERVER),
        @Script(value = "js/brad-frontend.js",depends = "jquery", location = AssetLocation.SERVER),
})
@Stylesheets(@Stylesheet(value = "skin/css/brandadvocacy.css", location = AssetLocation.SERVER))
@Assets("*")
package org.exoplatform.community.brandadvocacy.portlet.frontend;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.*;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;