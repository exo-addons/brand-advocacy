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

  public void testAll(){
    Mission m = new Mission("facebook !!!! priority 1 ");
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


    Proposition proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission facebook");
    propositions.add(proposition2);

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

    int nbMPsAction = 0;

    Participant participant = new Participant("participant_1");

    MissionParticipant missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(m.getId());
    missionParticipant.setProposition_id(proposition.getId());
    missionParticipant = this.service.addMissionParticipant(missionParticipant);

    Set<String> missionIds = new HashSet<String>();
    missionIds.add(m.getId());
    participant.setMission_ids(missionIds);

    Set<String> missionParticipantIds = new HashSet<String>();
    missionParticipantIds.add(missionParticipant.getId());
    participant.setMission_participant_ids(missionParticipantIds);

    participant = this.service.addParticipant(participant);
    nbMPsAction++;

    MissionParticipant missionParticipant2 = new MissionParticipant();
    missionParticipant2.setParticipant_username(participant.getUserName());
    missionParticipant2.setMission_id(m.getId());
    missionParticipant2.setProposition_id(proposition2.getId());
    missionParticipant2 = this.service.addMissionParticipant(missionParticipant2);

    missionIds = new HashSet<String>();
    missionIds.add(m.getId());
    participant.setMission_ids(missionIds);

    missionParticipantIds = new HashSet<String>();
    missionParticipantIds.add(missionParticipant2.getId());
    participant.setMission_participant_ids(missionParticipantIds);

    participant = this.service.addParticipant(participant);

    nbMPsAction ++;

    List<Mission> missions = this.service.getAllMissionsByParticipant(participant.getUserName());
    assertEquals(participant.getUserName() + " should participate to 1 mission ",1,missions.size());

    List<MissionParticipant> missionParticipants = this.service.getMissionParticipantsByParticipant(participant.getUserName());
    assertEquals(participant.getUserName() + " should participate to 1 mission twice ",2,missionParticipants.size());

    Mission m2 = new Mission("twitter !!!!");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m2.setManagers(managers);
    m2 = this.service.addMission(m2);

    assertEquals("should have 2 missions ", 2 , this.service.getAllMissions().size());


    participant = this.service.getParticipantByUserName("participant_1");
    missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(m2.getId());

    missionParticipant = this.service.addMissionParticipant(missionParticipant);
    missionIds = new HashSet<String>();
    missionIds.add(m2.getId());
    participant.setMission_ids(missionIds);

    missionParticipantIds = new HashSet<String>();
    missionParticipantIds.add(missionParticipant.getId());
    participant.setMission_participant_ids(missionParticipantIds);

    this.service.addParticipant(participant);
    nbMPsAction ++;

    int nbMPs = this.service.getAllMissionParticipants().size();
    assertEquals("total mission participants shoule be  "+nbMPsAction, nbMPs , nbMPs);

    int nbParticipants = this.service.getAllParticipants().size();
    assertEquals("should have 1 participants ", 1 , nbParticipants);

    Address address = new Address("tuan","vu","100 hqv","hn","vn","09123");
    this.service.addAddress(participant.getUserName(),address);
    int nbAdrs = this.service.getAllAddressesByParticipant(participant.getUserName()).size();
    assertEquals("should have 1 adrs",1,nbAdrs);

    Participant participant2 = new Participant("participant_2");

    missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(m.getId());

    missionParticipant = this.service.addMissionParticipant(missionParticipant);
    missionIds = new HashSet<String>();
    missionIds.add(m.getId());
    participant2.setMission_ids(missionIds);

    missionParticipantIds = new HashSet<String>();
    missionParticipantIds.add(missionParticipant.getId());
    participant2.setMission_participant_ids(missionParticipantIds);

    this.service.addParticipant(participant2);

    assertEquals("should have 1 more mission participant ",nbMPs+1,this.service.getAllMissionParticipants().size());

    assertEquals("should have 1 more participant ", nbParticipants + 1 , this.service.getAllParticipants().size());

//    showInfo();
  }

  public void testRandom(){

    Participant participant = new Participant("toto");

    List<Manager> managers = new ArrayList<Manager>();
    Manager manager = new Manager("toto");
    manager.setRole(Role.Validator);
    managers.add(manager);

    Mission m_prio1 = new Mission(" mission prio 1 a");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m_prio1.setManagers(managers);
    m_prio1.setPriority(Priority.PRIORITY_1);
    m_prio1 = this.service.addMission(m_prio1);
    this.service.addManagers2Mission(m_prio1.getId(),managers);

    List<Proposition> propositions = new ArrayList<Proposition>();
    Proposition proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 1 a");
    propositions.add(proposition);

    Proposition proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 1 a");
    propositions.add(proposition2);

    Proposition proposition3 = new Proposition();
    proposition3.setContent("proposition 3 for mission prio 1 a");
    propositions.add(proposition3);
    proposition3.setNumberUsed(1);
    this.service.addProposition2Mission(m_prio1.getId(), propositions);



    Mission m_prio2 = new Mission(" mission prio 1 b ");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m_prio2.setManagers(managers);
    m_prio2.setPriority(Priority.PRIORITY_1);
    m_prio2 = this.service.addMission(m_prio2);
    this.service.addManagers2Mission(m_prio2.getId(),managers);

    propositions = new ArrayList<Proposition>();
    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 1 b");
    propositions.add(proposition);

    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 1 b");
    propositions.add(proposition2);
    this.service.addProposition2Mission(m_prio2.getId(), propositions);

    Mission m_prio3 = new Mission(" mission prio 2 ");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m_prio3.setManagers(managers);
    m_prio3.setPriority(Priority.PRIORITY_2);
    m_prio3 = this.service.addMission(m_prio3);
    this.service.addManagers2Mission(m_prio3.getId(),managers);

    propositions = new ArrayList<Proposition>();
    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 2");
    propositions.add(proposition);
    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 2");
    propositions.add(proposition2);
    this.service.addProposition2Mission(m_prio3.getId(), propositions);

    Mission m_prio4 = new Mission(" mission prio 3 ");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m_prio4.setManagers(managers);
    m_prio4.setPriority(Priority.PRIORITY_3);
    m_prio4 = this.service.addMission(m_prio4);
    this.service.addManagers2Mission(m_prio4.getId(),managers);

    propositions = new ArrayList<Proposition>();
    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 3");
    propositions.add(proposition);
    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 3");
    propositions.add(proposition2);
    this.service.addProposition2Mission(m_prio4.getId(), propositions);

    Mission m_prio5 = new Mission(" mission prio 4 ");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m_prio5.setManagers(managers);
    m_prio5.setPriority(Priority.PRIORITY_1);
    m_prio5 = this.service.addMission(m_prio5);
    this.service.addManagers2Mission(m_prio5.getId(),managers);

    propositions = new ArrayList<Proposition>();
    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 4");
    propositions.add(proposition);
    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 4");
    propositions.add(proposition2);
    this.service.addProposition2Mission(m_prio5.getId(), propositions);

    Mission m_prio6 = new Mission(" mission prio 5 ");
    managers = new ArrayList<Manager>();
    manager = new Manager("manager2");
    managers.add(manager);
    m_prio6.setManagers(managers);
    m_prio6.setPriority(Priority.PRIORITY_1);
    m_prio6 = this.service.addMission(m_prio6);
    this.service.addManagers2Mission(m_prio6.getId(),managers);

    propositions = new ArrayList<Proposition>();
    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 5");
    propositions.add(proposition);
    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 5");
    propositions.add(proposition2);
    this.service.addProposition2Mission(m_prio6.getId(), propositions);

    assertEquals("should have 8 mission ",8,this.service.getAllMissions().size());

//    this.showInfo();
    this.showInfoRandom();
  }
  public void showInfoRandom(){

    Mission mission = this.service.getRandomMisson("participant_1");
    if(null != mission){
      debug(" random mission "+mission.getTitle() + " - "+mission.getPriority().getLabel());
      debug("========= list propositions  ================");
      Proposition proposition = this.service.getRandomProposition(mission.getId());
      if(null != proposition)
        debug(" random proposition "+proposition.getContent());
    }

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

      for (MissionParticipant mp : this.service.getMissionParticipantsByParticipant(participant.getUserName())){

        debug( mp.toString());
      }

    }

    List<MissionParticipant> missionParticipants = this.service.getAllMissionParticipants();
    debug("========= list mission participants ================");
    for (MissionParticipant missionParticipant:missionParticipants){
      debug(missionParticipant.toString());
    }


  }

}
