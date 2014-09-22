package org.exoplatform.brandadvocacy;

import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.component.test.AbstractKernelTest;
import org.exoplatform.component.test.ConfigurationUnit;
import org.exoplatform.component.test.ConfiguredBy;
import org.exoplatform.component.test.ContainerScope;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;

import javax.jcr.Session;

@ConfiguredBy({
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal/brandadvocacy/brandadvocacy_configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/exo.portal.component.test.jcr-configuration.xml"),
    @ConfigurationUnit(scope = ContainerScope.PORTAL, path = "conf/portal/brandadvocacy/jcr_configuration.xml")
  })
public abstract class AbstractTest extends AbstractKernelTest {
    protected final String username = "root";
    protected IService service;
    protected Mission testMission;
    protected Proposition proposition;

    @Override
    protected void setUp() throws Exception {        
        super.setUp();
        this.service = getService(IService.class);
        Mission m = new Mission("");
        testMission = this.service.addMission(m);
    }

    protected Session getSession() throws Exception {
        SessionProviderService providerService = getService(SessionProviderService.class);
        RepositoryService repoService = getService(RepositoryService.class);
        ManageableRepository currentRepo = repoService.getDefaultRepository();
        Session session = providerService.getSystemSessionProvider(null).getSession(JCRImpl.workspace, currentRepo);
        return session;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getService(Class<T> t) {
        return (T)getContainer().getComponentInstanceOfType(t);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
