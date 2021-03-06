/**
 * Created by exoplatform on 21/07/15.
 */
@Application(defaultController = JuZLogoutApplication.class)
@Portlet(name="LogoutPortlet")
@Bindings(
        {
                @Binding(value = org.exoplatform.services.organization.OrganizationService.class),
                @Binding(value = org.exoplatform.brandadvocacy.service.IService.class)
        }
)
@Scripts({
        @Script(id="jquery",value = "js/jquery-3.2.1.min.js", location = AssetLocation.SERVER),
        @Script(value = "js/slider/jquery.easing.1.3.js",depends = "jquery", location = AssetLocation.SERVER),
        @Script(value = "js/slider/jquery.animate-enhanced.min.js",depends = "jquery", location = AssetLocation.SERVER),
        @Script(value = "js/slider/jquery.superslides.js",depends = "jquery", location = AssetLocation.SERVER),
        @Script(value = "js/brad-logout.js",depends = "jquery", location = AssetLocation.SERVER)
})
@Stylesheets({
        @Stylesheet(value = "skin/css/brad-logout.css", location = AssetLocation.SERVER),
        @Stylesheet(value = "skin/css/slider/superslides.css", location = AssetLocation.SERVER)
})
@Assets("*")

package org.exoplatform.community.brandadvocacy.portlet.logout;

import juzu.Application;
import juzu.asset.AssetLocation;
import juzu.plugin.asset.*;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;
import juzu.plugin.portlet.Portlet;

