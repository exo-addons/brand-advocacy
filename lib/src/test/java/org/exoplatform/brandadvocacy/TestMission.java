/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.brandadvocacy;

import org.exoplatform.brandadvocacy.model.*;
import org.junit.Test;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 22, 2014  
 */
public class TestMission extends AbstractTest {

  public void testCreate(){
    int nb = this.service.getAllMissions().size();
    Mission m = new Mission("my second mission");
    List<Manager> managers = new ArrayList<Manager>();

    Manager manager = new Manager(this.username);
    managers.add(manager);

    m.setManagers(managers);
    m = this.service.addMission(m);


    assertEquals("should have 1 more missions ", nb+1, this.service.getAllMissions().size());
    assertEquals("should have 1 managers ",1, this.service.getAllManagers(m.getId()).size());
    ShowMissionInfo();

    managers = new ArrayList<Manager>();
    manager = new Manager("toto");
    manager.setRole(Role.Shipping_Manager);
    managers.add(manager);
    this.service.addManagers2Mission(m.getId(),managers);
    assertEquals("should have 2 managers ",2, this.service.getAllManagers(m.getId()).size());

    List<Proposition> propositions = new ArrayList<Proposition>();
    Proposition proposition = new Proposition();
    proposition.setContent("proposition 1");
    propositions.add(proposition);
    this.service.addProposition2Mission(m.getId(),propositions);
    assertEquals("should have 1 proposition ",1, this.service.getAllPropositions(m.getId()).size());
    ShowMissionInfo();
    proposition.setMission_id(m.getId());
    this.testProposition(proposition);
    this.testManager(m);
    ShowMissionInfo();
  }
  public void testManager(Mission m){
    debug("test manager");
    Manager manager = new Manager(this.username);
    manager.setRole(Role.Validator);
    manager.setMission_id(m.getId());
    this.service.updateManager(manager);
    ShowMissionInfo();
    this.service.removeManager(manager);
    assertEquals("should have 1 manager after removing 1",1,this.service.getAllManagers(m.getId()).size());
  }
  public void testProposition(Proposition proposition){
    debug("test proposition");
    proposition.setContent("new content");
    this.service.updateProposition(proposition);
    ShowMissionInfo();
    this.service.removeProposition(proposition);
    assertEquals("should have no propostion after removing",0,this.service.getAllPropositions(proposition.getMission_id()).size());
  }
  public void testMissionParticipant() throws RepositoryException {
    Mission m = new Mission("mission twitter");
    m.setThird_party_link("http:google.com");
    m = this.service.addMission(m);
    Manager manager = new Manager(this.username);
    List<Manager> managers = new ArrayList<Manager>();
    managers.add(manager);
    this.service.addManagers2Mission(m.getId(),managers);
    Proposition proposition = new Proposition("twitte it !!! ");
    List<Proposition> propositions = new ArrayList<Proposition>();
    propositions.add(proposition);
    this.service.addProposition2Mission(m.getId(),propositions);
    ShowMissionInfo();
    MissionParticipant missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username("participant 1");
    missionParticipant.setMission_id(m.getId());
    this.service.addMissionParticipant(missionParticipant);
    showMissionParticipantInfo();
  }
  public void ShowMissionInfo(){
    List<Mission> missions = this.service.getAllMissions();
    for (Mission m:missions){
      debug(m.toString());
      List<Manager> managers = this.service.getAllManagers(m.getId());
      for (Manager manager:managers){
        debug(manager.toString());
      }
      List<Proposition> propositions = this.service.getAllPropositions(m.getId());
      for (Proposition proposition:propositions){
        debug(proposition.toString());
      }
    }
  }
  public void showMissionParticipantInfo(){
    try {
      List<MissionParticipant> missionParticipants = this.service.getAllMissionParticipants();
      for (MissionParticipant missionParticipant:missionParticipants){
        debug(missionParticipant.toString());
      }
    } catch (RepositoryException e) {
      log.error("cannot get all mission participants");
    }
  }
  public void testDuplicateManager(){
    Mission mission = new Mission("mission for manager");
    List<Manager> managers = new ArrayList<Manager>();
    Manager manager = new Manager(this.username);
    managers.add(manager);
    manager = new Manager(this.username);
    managers.add(manager);
    mission.setManagers(managers);
    mission = this.service.addMission(mission);
    ShowMissionInfo();
  }

}
