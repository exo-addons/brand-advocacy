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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 22, 2014  
 */

public class TestBradService extends AbstractTest {

  public void testProgram(){

    Program program = new Program("brand advocacy program");
    program = this.service.addProgram(program);
    int nbPrograms = 0;
    List<Program> programs = this.service.getAllPrograms();
    if (null != programs){
      nbPrograms = programs.size();
    }
    assertEquals("should have 1 program",1,nbPrograms);

    Manager manager1 = new Manager(this.username);
    manager1.setParentId(program.getId());
    manager1 = this.service.addManager2Program(manager1);
    int nbManagers =     this.service.getAllManagersInProgram(program.getId()).size();
    assertEquals("should 1 manager in program",1,nbManagers);
    assertEquals("manager should have name like ","anhvt",manager1.getUserName());

    List<Manager> managers = new ArrayList<Manager>(2);
    Manager manager2 = new Manager("manage 1");
    manager2.setRole(Role.Validator);
    managers.add(manager2);
    manager2 = new Manager("manager 2");
    manager2.setRole(Role.Shipping_Manager);
    managers.add(manager2);

    this.service.addManagers2Program(program.getId(),managers);
    assertEquals("should have 2 more managers ",nbManagers+2,this.service.getAllManagersInProgram(program.getId()).size());

  }

  public void testMission(){
    Program currentProgram = null;
    List<Program> programs = this.service.getAllPrograms();
    for (Program program:programs){
      currentProgram = program;
    }
    int nbMission = this.service.getAllMissionsByProgramId(currentProgram.getId(),null).size();
    Mission mission1 = new Mission(currentProgram.getId()," mission 1 - prio 1");
    mission1.setActive(true);
    this.service.addMission2Program(mission1);
    assertEquals("should have 1 mission",nbMission+1,this.service.getAllMissionsByProgramId(currentProgram.getId(),null).size());

    nbMission = this.service.getAllMissionsByProgramId(currentProgram.getId(),null).size();
    Mission mission2 = new Mission(currentProgram.getId()," mission 2 - prio 1");
    mission2.setActive(true);
    mission2 = this.service.addMission2Program(mission2);

    assertEquals("should have 1 more mission ",nbMission+1,this.service.getAllMissionsByProgramId(currentProgram.getId(),null).size());

    List<Manager> mission2Managers = this.service.getAllMissionManagers(mission2.getId());
    int nbMissionManagers = 0;
    if (null != mission2Managers){
      nbMissionManagers = mission2Managers.size();
    }

    Manager missionManager1 = new Manager("i_am_mission_manager");
    missionManager1.setRole(Role.Admin);
    missionManager1.setParentId(mission2.getId());
    missionManager1 = this.service.addManager2Mission(missionManager1);
    assertEquals("should have 1 more manager in mission ",nbMissionManagers+1,this.service.getAllMissionManagers(mission2.getId()).size());
    assertEquals(" manager should have username like ","i_am_mission_manager",missionManager1.getUserName());

    mission2.setTitle("edit title");
    mission2.setThird_part_link("google.com");
    mission2 = this.service.updateMission(mission2);
    assertEquals("should have new title","edit title",mission2.getTitle());
    assertEquals("should have third part link","google.com",mission2.getThird_part_link());

    nbMission = this.service.getAllMissionsByProgramId(currentProgram.getId(),null).size();
    this.service.removeMissionById(mission2.getId());
    assertEquals("shoule reduce 1 mission",nbMission-1,this.service.getAllMissionsByProgramId(currentProgram.getId(),null).size());
  }
  public void testProposition(){
    Program currentProgram = null;
    List<Program> programs = this.service.getAllPrograms();
    for (Program program:programs){
      currentProgram = program;
    }
    Mission currentMission = null;
    List<Mission> missions = this.service.getAllMissionsByProgramId(currentProgram.getId(),null);
    for (Mission mission:missions){
      currentMission = mission;
    }
    int nbPropositions = this.service.getAllPropositions(currentMission.getId(),null).size();
    Proposition proposition1 = new Proposition();
    proposition1.setContent(" i am proposition 1");
    proposition1.setMission_id(currentMission.getId());
    proposition1 = this.service.addProposition2Mission(proposition1);
    assertEquals("should have 1 more proposition",nbPropositions+1,this.service.getAllPropositions(currentMission.getId(),null).size());

    proposition1.setContent("new content");
    proposition1 = this.service.updateProposition(proposition1);
    assertEquals("new content should be new content ","new content",proposition1.getContent());
    nbPropositions = this.service.getAllPropositions(currentMission.getId(),null).size();
    this.service.removeProposition(proposition1.getId());
    assertEquals("should reduce 1 proposition",nbPropositions-1,this.service.getAllPropositions(currentMission.getId(),null).size());

    Proposition proposition2 = new Proposition();
    proposition2.setContent(" i am proposition 2");
    proposition2.setMission_id(currentMission.getId());
    this.service.addProposition2Mission(proposition2);
  }

  public void testMissionParticipant(){
    Program currentProgram = null;
    List<Program> programs = this.service.getAllPrograms();
    for (Program program:programs){
      currentProgram = program;
    }

    Mission currentMission = new Mission(currentProgram.getId()," mission 1 - prio 1");
    currentMission.setActive(true);
    currentMission = this.service.addMission2Program(currentMission);

    Proposition currentProposition = new Proposition();
    currentProposition.setContent(" i am proposition 1");
    currentProposition.setMission_id(currentMission.getId());
    currentProposition = this.service.addProposition2Mission(currentProposition);


    int nbMP = this.service.getAllMissionParticipantsInProgram(currentProgram.getId()).size();
    MissionParticipant missionParticipant = new MissionParticipant();
    missionParticipant.setMission_id(currentMission.getId());
    missionParticipant.setProposition_id(currentProposition.getId());
    missionParticipant.setParticipant_username(this.username);
    missionParticipant = this.service.addMissionParticipant2Program(currentProgram.getId(),missionParticipant);
    assertEquals("should have 1 more MP",nbMP+1,this.service.getAllMissionParticipantsInProgram(currentProgram.getId()).size());

    int nbUsedOld = currentProposition.getNumberUsed();
    currentProposition.setNumberUsed(nbUsedOld+1);
    currentProposition = this.service.updateProposition(currentProposition);
    assertEquals("number proposition used should be added to 1",nbUsedOld+1,currentProposition.getNumberUsed());

    missionParticipant.setStatus(Status.INPROGRESS);
    missionParticipant.setUrl_submitted("twitter.com");
    Long date_submitted = System.currentTimeMillis();
    missionParticipant.setDate_submitted(date_submitted);
    missionParticipant= this.service.updateMissionParticipantInProgram(currentProgram.getId(),missionParticipant);
    assertEquals("should update url submitted","twitter.com",missionParticipant.getUrl_submitted());

    missionParticipant.setStatus(Status.WAITING_FOR_VALIDATE);
    this.service.updateMissionParticipantInProgram(currentProgram.getId(),missionParticipant);
    assertEquals("should update status",Status.WAITING_FOR_VALIDATE,missionParticipant.getStatus());
    assertEquals("should keep date submitted",(Long)date_submitted,(Long)missionParticipant.getDate_submitted());
    assertEquals("should keep the url submitted","twitter.com",missionParticipant.getUrl_submitted());

    int nbParticipants = this.service.getAllParticipantsInProgram(currentProgram.getId()).size();
    Participant participant = this.service.getParticipantInProgramByUserName(currentProgram.getId(),this.username);
    if (null == participant){
      participant = new Participant(this.username);
    }
    Set<String> mpIds = new HashSet<String>(1);
    mpIds.add(missionParticipant.getId());
    Set<String> mIds = new HashSet<String>(1);
    mIds.add(currentMission.getId());
    participant.setMission_participant_ids(mpIds);
    participant.setMission_ids(mIds);
    participant.setProgramId(currentProgram.getId());
    participant = this.service.addParticipant2Program(participant);
    assertEquals("should have 1 more participant",nbParticipants+1,this.service.getAllParticipantsInProgram(currentProgram.getId()).size());

    Mission newMission = new Mission(currentProgram.getId(),"mission 2: review on twitter");
    newMission.setActive(true);
    newMission.setThird_part_link("google.com");
    newMission = this.service.addMission2Program(newMission);

    Proposition proposition1 = new Proposition("i am proposition 1 in new mission");
    proposition1.setMission_id(newMission.getId());
    proposition1 = this.service.addProposition2Mission(proposition1);

    MissionParticipant missionParticipant1 = new MissionParticipant();
    missionParticipant1.setMission_id(newMission.getId());
    missionParticipant1.setProposition_id(proposition1.getId());
    missionParticipant1.setParticipant_username(participant.getUserName());
    missionParticipant1.setStatus(Status.OPEN);
    missionParticipant1 = this.service.addMissionParticipant2Program(currentProgram.getId(),missionParticipant1);

    participant = this.service.getParticipantInProgramByUserName(currentProgram.getId(),this.username);
    int nbMPIdsOld = participant.getMission_participant_ids().size();
    int nbMIdsOld = participant.getMission_ids().size();

    Set<String> newMPIds = new HashSet<String>(1);
    newMPIds.add(missionParticipant1.getId());
    participant.setMission_participant_ids(newMPIds);

    Set<String> newMIds = new HashSet<String>(1);
    newMIds.add(newMission.getId());
    participant.setMission_ids(newMIds);

    participant = this.service.addParticipant2Program(participant);
    assertEquals("should participate 1 more mission",nbMIdsOld+1,participant.getMission_ids().size());
    assertEquals("should have 1 more mission participant",nbMPIdsOld+1,participant.getMission_participant_ids().size());

    Proposition proposition2 = new Proposition(" i am proposition 2 in new mission");
    proposition2.setMission_id(newMission.getId());
    proposition2 = this.service.addProposition2Mission(proposition2);

    participant = this.service.getParticipantInProgramByUserName(currentProgram.getId(),this.username);
    MissionParticipant missionParticipant2 = new MissionParticipant();
    missionParticipant2.setMission_id(newMission.getId());
    missionParticipant2.setProposition_id(proposition2.getId());
    missionParticipant2.setParticipant_username(participant.getUserName());
    missionParticipant2 = this.service.addMissionParticipant2Program(currentProgram.getId(),missionParticipant2);

    nbMPIdsOld = participant.getMission_participant_ids().size();
    nbMIdsOld = participant.getMission_ids().size();

    newMPIds = new HashSet<String>(1);
    newMPIds.add(missionParticipant2.getId());
    participant.setMission_participant_ids(newMPIds);

    newMIds = new HashSet<String>(1);
    newMIds.add(newMission.getId());
    participant.setMission_ids(newMIds);

    participant = this.service.addParticipant2Program(participant);
    assertEquals("should participate only 2 missions",nbMIdsOld,participant.getMission_ids().size());
    assertEquals("should have 1 more mission participant",nbMPIdsOld+1,participant.getMission_participant_ids().size());


  }

  public void testRandom(){

    Program currentProgram = null;
    List<Program> programs = this.service.getAllPrograms();
    for (Program program:programs){
      currentProgram = program;
    }

    Participant participant = new Participant("toto");

    List<Manager> managers = new ArrayList<Manager>();
    Manager manager = new Manager("toto");
    manager.setRole(Role.Validator);
    managers.add(manager);

    this.service.addManagers2Program(currentProgram.getId(),managers);

    Mission m_prio1 = new Mission(" mission prio 60% ");
    m_prio1.setProgramId(currentProgram.getId());
    m_prio1.setPriority(60);
    m_prio1 = this.service.addMission2Program(m_prio1);
    this.service.addManagers2Mission(m_prio1.getId(),managers);

    Proposition proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 1 a");
    proposition.setMission_id(m_prio1.getId());
    this.service.addProposition2Mission(proposition);

    Proposition proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 1 a");
    proposition2.setMission_id(m_prio1.getId());
    this.service.addProposition2Mission(proposition2);

    Proposition proposition3 = new Proposition();
    proposition3.setContent("proposition 3 for mission prio 1 a");
    proposition3.setMission_id(m_prio1.getId());
    this.service.addProposition2Mission(proposition3);

    Mission randomMission = this.service.getRandomMisson(currentProgram.getId(),participant.getUserName());
    assertEquals("should have no random mission",null,randomMission);

    m_prio1.setActive(true);
    m_prio1 = this.service.updateMission(m_prio1);
    randomMission = this.service.getRandomMisson(currentProgram.getId(),"toto");
    assertEquals("should found a random mission",m_prio1.getId(),randomMission.getId());

    Proposition randomProposition = this.service.getRandomProposition(randomMission.getId());
    assertNotNull("should have a random proposition", randomProposition);


    int nbParticipants = this.service.getAllParticipantsInProgram(currentProgram.getId()).size();

    MissionParticipant missionParticipant = new MissionParticipant();
    missionParticipant.setMission_id(randomMission.getId());
    missionParticipant.setProposition_id(proposition.getId());
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant = this.service.addMissionParticipant2Program(currentProgram.getId(),missionParticipant);
    missionParticipant.setStatus(Status.INPROGRESS);
    missionParticipant = this.service.updateMissionParticipantInProgram(currentProgram.getId(),missionParticipant);
    assertEquals("mp should have in progress status",Status.INPROGRESS,missionParticipant.getStatus());

    participant.setProgramId(currentProgram.getId());
    Set<String> mpIds = new HashSet<String>();
    mpIds.add(missionParticipant.getId());
    participant.setMission_participant_ids(mpIds);

    Set<String> mIds = new HashSet<String>();
    mIds.add(m_prio1.getId());
    participant.setMission_ids(mIds);

    participant =  this.service.addParticipant2Program(participant);

    assertEquals("should have 1 more participant",nbParticipants+1,this.service.getAllParticipantsInProgram(currentProgram.getId()).size());

    randomMission = this.service.getRandomMisson(currentProgram.getId(),participant.getUserName());
    assertEquals("should have no random mission",null,randomMission);

    Mission m_prio2 = new Mission(" mission prio 20% ");
    m_prio2.setPriority(20);
    m_prio2.setProgramId(currentProgram.getId());
    m_prio2.setActive(true);
    m_prio2 = this.service.addMission2Program(m_prio2);

    randomMission = this.service.getRandomMisson(currentProgram.getId(), participant.getUserName());
    assertEquals("should have 1 random mission", m_prio2.getId(), randomMission.getId());

    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 1 b");
    proposition.setMission_id(m_prio2.getId());
    this.service.addProposition2Mission(proposition);

    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 1 b");
    proposition2.setMission_id(m_prio2.getId());
    this.service.addProposition2Mission(proposition2);

    Mission m_prio3 = new Mission(" mission prio 10 % ");
    m_prio3.setPriority(10);
    m_prio3.setActive(true);
    m_prio3.setProgramId(currentProgram.getId());
    m_prio3 = this.service.addMission2Program(m_prio3);



    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 2");
    proposition.setMission_id(m_prio3.getId());
    this.service.addProposition2Mission(proposition);

    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 2");
    proposition2.setMission_id(m_prio3.getId());
    this.service.addProposition2Mission(proposition2);


    randomMission = this.service.getRandomMisson(currentProgram.getId(),participant.getUserName());
    randomProposition = this.service.getRandomProposition(randomMission.getId());
    missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(randomMission.getId());
    missionParticipant.setProposition_id(randomProposition.getId());
    missionParticipant = this.service.addMissionParticipant2Program(currentProgram.getId(),missionParticipant);

    missionParticipant.setStatus(Status.OPEN);
    missionParticipant = this.service.updateMissionParticipantInProgram(currentProgram.getId(),missionParticipant);

    Mission m_prio4 = new Mission(" mission prio 5% ");
    m_prio4.setPriority(5);
    m_prio4.setActive(true);
    m_prio4.setProgramId(currentProgram.getId());
    m_prio4 = this.service.addMission2Program(m_prio4);

    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 3");
    proposition.setMission_id(m_prio4.getId());
    this.service.addProposition2Mission(proposition);

    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 3");
    proposition2.setMission_id(m_prio4.getId());
    this.service.addProposition2Mission(proposition2);

    randomMission = this.service.getRandomMisson(currentProgram.getId(),participant.getUserName());
    randomProposition = this.service.getRandomProposition(randomMission.getId());
    missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(randomMission.getId());
    missionParticipant.setProposition_id(randomProposition.getId());
    missionParticipant = this.service.addMissionParticipant2Program(currentProgram.getId(),missionParticipant);

    missionParticipant.setStatus(Status.WAITING_FOR_VALIDATE);
    missionParticipant = this.service.updateMissionParticipantInProgram(currentProgram.getId(),missionParticipant);

    Mission m_prio5 = new Mission(" mission prio 5% false ");
    m_prio5.setPriority(5);
    m_prio5.setActive(true);
    m_prio5.setProgramId(currentProgram.getId());
    m_prio5 = this.service.addMission2Program(m_prio5);

    proposition = new Proposition();
    proposition.setContent("proposition 1 for mission prio 4");
    proposition.setMission_id(m_prio5.getId());
    this.service.addProposition2Mission(proposition);

    proposition2 = new Proposition();
    proposition2.setContent("proposition 2 for mission prio 4");
    proposition2.setMission_id(m_prio5.getId());
    this.service.addProposition2Mission(proposition2);

    int nbMPs = this.service.getAllMissionParticipantsInProgram(currentProgram.getId()).size();
    randomMission = this.service.getRandomMisson(currentProgram.getId(),participant.getUserName());
    randomProposition = this.service.getRandomProposition(randomMission.getId());
    missionParticipant = new MissionParticipant();
    missionParticipant.setParticipant_username(participant.getUserName());
    missionParticipant.setMission_id(randomMission.getId());
    missionParticipant.setProposition_id(randomProposition.getId());
    missionParticipant = this.service.addMissionParticipant2Program(currentProgram.getId(),missionParticipant);
    assertEquals("shoule have 1 more mps",nbMPs+1,this.service.getAllMissionParticipantsInProgram(currentProgram.getId()).size());
    missionParticipant.setStatus(Status.SHIPPED);
    missionParticipant = this.service.updateMissionParticipantInProgram(currentProgram.getId(),missionParticipant);
    assertEquals("mp should have shipped status",Status.SHIPPED,missionParticipant.getStatus());
//    assertEquals("should have 1 mp shipped",1,this.service.searchMissionParticipants(currentProgram.getId(),"",Status.SHIPPED,0,0).size());
    assertEquals("should have 8 mission ",8,this.service.getAllMissionsByProgramId(currentProgram.getId(),null).size());

//    this.showInfo();
    this.showInfoRandom();
  }
  public void showInfoRandom(){

    Program currentProgram = null;
    List<Program> programs = this.service.getAllPrograms();
    for (Program program:programs){
      currentProgram = program;
    }

    Mission mission = this.service.getRandomMisson(currentProgram.getId(),"toto");
    if(null != mission){
      debug(" random mission "+mission.toString());
      debug("========= list propositions  ================");
      Proposition proposition = this.service.getRandomProposition(mission.getId());
      if(null != proposition)
        debug(" random proposition "+proposition.getContent());
    }

  }
  /*
  public void testAll(){
    int nbPrograms = 0;
    List<Program> programs = this.service.getAllPrograms();
    if (null != programs){
      nbPrograms = programs.size();
    }
    Program program = new Program("brand advocacy program");
    program = this.service.addProgram(program);
    assertEquals("should have 1 program",1,nbPrograms);

    Mission mission1 = new Mission(program.getId(),"facebook !!!! priority 1 ");
    mission1.setActive(true);

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
    m.setThird_part_link("exo.com");
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
  */

}
