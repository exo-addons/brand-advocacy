package org.exoplatform.brandavocacy;

import org.exoplatform.brandadvocacy.AbstractTest;
import org.exoplatform.brandadvocacy.model.Mission;

public class TestMission extends AbstractTest {
 public void testCreate() throws Exception{
     Mission m = new Mission(null);
     this.service.addMission(m);
 }
}
