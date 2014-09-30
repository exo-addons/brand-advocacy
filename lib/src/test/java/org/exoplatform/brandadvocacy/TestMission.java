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
import java.util.*;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 22, 2014  
 */
public class TestMission extends AbstractTest {

/*
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
    this.service.addProposition2Mission(m.getId(), propositions);
    assertEquals("should have 1 proposition ", 1, this.service.getAllPropositions(m.getId()).size());
  }
  public void testManager(Mission m){
    debug(" ============ test manager =================");
    Manager manager = new Manager(this.username);
    manager.setRole(Role.Validator);
    manager.setMission_id(m.getId());
    this.service.updateManager(manager);
    this.service.removeManager(manager);
    assertEquals("should have 1 manager after removing 1",1,this.service.getAllManagers(m.getId()).size());
  }
  public void testProposition(Proposition proposition){
    debug("================ test proposition");
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
*/
  public void testAll(){
    Mission m = new Mission("facebook !!!!");
    List<Manager> managers = new ArrayList<Manager>();
    Manager manager = new Manager(this.username);
    manager.setRole(Role.Shipping_Manager);
    managers.add(manager);
    m.setManagers(managers);
    m = this.service.addMission(m);
    for(Manager manager1:this.service.getAllManagers(m.getId())){
      assertEquals(Role.Shipping_Manager.getLabel(), manager1.getRoleLabel());
    }
    managers = new ArrayList<Manager>();
    manager = new Manager("toto");
    manager.setRole(Role.Validator);
    managers.add(manager);
    this.service.addManagers2Mission(m.getId(),managers);
    assertEquals("should have 2 managers", 2, this.service.getAllManagers(m.getId()).size());

    List<Proposition> propositions = new ArrayList<Proposition>();
    Proposition proposition = new Proposition();
    proposition.setContent("proposition 1 for mission facebook");
    propositions.add(proposition);
    proposition = new Proposition();
    proposition.setContent("proposition 2 for mission facebook");
    propositions.add(proposition);
    this.service.addProposition2Mission(m.getId(), propositions);

    assertEquals("should have 2 propositions in mission "+m.getTitle(),2,this.service.getPropositionsByMissionId(m.getId()).size());

    m.setTitle("fb");
    m.setThird_party_link("exo.com");
    m = this.service.updateMission(m);
    assertEquals("fb",m.getTitle());

    String propositionLabelID = proposition.getLabelID();
    String mid = m.getId();
    debug("====== search proposition "+propositionLabelID+ "=============");
    List<Proposition> resultPropositions = this.service.searchPropositions(propositionLabelID);
    for (Proposition prop:resultPropositions){
      prop.setContent(" EDIT CONTENT");
      this.service.updateProposition(prop);
    }


    Mission m2 = new Mission("twitter !!!!");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m2.setManagers(managers);
    m2 = this.service.addMission(m2);

    assertEquals("should have 2 missions ", 2 , this.service.getAllMissions().size());

    Participant participant = new Participant("participant_1");

    MissionParticipant missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(m.getId());
    try {
      missionParticipant = this.service.addMissionParticipant(missionParticipant);
      Set<String> missionParticipantIds = new HashSet<String>();
      missionParticipantIds.add(missionParticipant.getId());
      participant.setMission_participant_ids(missionParticipantIds);
      this.service.addParticipant(participant);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }

    missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(m2.getId());
    try {
      missionParticipant = this.service.addMissionParticipant(missionParticipant);
      Set<String> missionParticipantIds = new HashSet<String>();
      missionParticipantIds.add(missionParticipant.getId());
      participant.setMission_participant_ids(missionParticipantIds);
      this.service.addParticipant(participant);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    int nbMPs = 0;
    try {
      nbMPs = this.service.getAllMissionParticipants().size();
      assertEquals("1 participant should have 2 mission participants ", 2 , nbMPs);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    int nbParticipants = this.service.getAllParticipants().size();
    assertEquals("should have 1 participant ", 1 , nbParticipants);

    Address address = new Address("tuan","vu","100 hqv","hn","vn","09123");
    this.service.addAddress(participant.getUserName(),address);
    int nbAdrs = this.service.getAllAddressesByParticipant(participant.getUserName()).size();
    assertEquals("should have 1 adrs",1,nbAdrs);


    participant = new Participant("participant_2");

    missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(m.getId());
    try {
      missionParticipant = this.service.addMissionParticipant(missionParticipant);
      Set<String> missionParticipantIds = new HashSet<String>();
      missionParticipantIds.add(missionParticipant.getId());
      participant.setMission_participant_ids(missionParticipantIds);
      this.service.addParticipant(participant);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    try {
      assertEquals("should have 1 more mission participant ",nbMPs+1,this.service.getAllMissionParticipants().size());
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    assertEquals("should have 1 more participant ", nbParticipants + 1 , this.service.getAllParticipants().size());
    showInfo();
  }
  public void showInfo(){
    List<Mission> missions = this.service.getAllMissions();
    debug("========= list missions ================");
    for (Mission m:missions){
      debug("========== info ================="+m.toString());
      List<Manager> managers = this.service.getAllManagers(m.getId());
      debug("========= list managers  ================");
      for (Manager manager:managers){
        debug(manager.toString());
      }
      debug("========= list propositions  ================");
      List<Proposition> propositions = this.service.getPropositionsByMissionId(m.getId());
      for (Proposition proposition:propositions){
        debug(proposition.toString());
      }
    }
    debug("========= list participants ================");
    List<Participant> participants = this.service.getAllParticipants();
    for (Participant participant:participants){
      debug(participant.toString());
      debug(" ================== list mission/participant ==================== for "+participant.getUserName());
      try {
        for (MissionParticipant mp : this.service.getMissionParticipantsByParticipant(participant.getUserName())){

          debug( mp.toString());
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    try {
      List<MissionParticipant> missionParticipants = this.service.getAllMissionParticipants();
      debug("========= list mission participants ================");
      for (MissionParticipant missionParticipant:missionParticipants){
        debug(missionParticipant.toString());
      }
    } catch (RepositoryException e) {
      log.error("cannot get all mission participants");
    }

  }

}
