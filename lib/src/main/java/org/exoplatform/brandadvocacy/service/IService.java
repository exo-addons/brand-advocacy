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
package org.exoplatform.brandadvocacy.service;

import org.exoplatform.brandadvocacy.model.*;

import javax.jcr.RepositoryException;
import java.util.List;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public interface IService {

  public Program addProgram(Program program);
  public Program updateProgram(Program program);
  public Program getProgramById(String programId);

  public Mission addMission(String programId,Mission m);
  public void removeMission(String id);
  public Mission getMissionById(String id);
  public List<Mission> getAllMissions();
  public Mission updateMission(Mission m);
  public Mission getRandomMisson(String username);
  public List<Mission> getAllMissionsByParticipant(String username);

  public Participant addParticipant(Participant p);
  public void removeParticipant(String id);
  public Participant getParticipantByUserName(String id);
  public List<Participant> getAllParticipants();
  public List<Participant> getParticipantsByMissionId(String mid);

  public Address addAddress(String username,Address address);
  public Address updateAddress(Address address);
  public void removeAddress(String id);
  public List<Address> getAllAddressesByParticipant(String username);
  public Address getAddressById(String id);

  public Manager addManager2Mission(String missionId,Manager manager);
  public List<Manager> addManagers2Mission(String missionId,List<Manager> managers);
  public Manager updateMissionManager(Manager manager);
  public List<Manager> getAllMissionManagers(String missionId);
  public void removeMissionManager(String missionId, String username);
  public Manager getMissionManager(String missionId,String username);

  public Manager addManager2Program(String programId,Manager manager);
  public List<Manager> addManagers2Program(String programId,List<Manager> managers);
  public Manager updateProgramManager(String programId,Manager manager);
  public void removeProgramManager(String programId,String username);
  public Manager getProgramManager(String programId,String username);

  public Proposition updateProposition(Proposition proposition);
  public List<Proposition> getPropositionsByMissionId(String mid);
  public Proposition getPropositionById(String id);
  public Mission addProposition2Mission(String mid,List<Proposition> propositions);
  public void removeProposition(String id);
  public List<Proposition> searchPropositions(String sql);
  public Proposition getRandomProposition(String mid);

  public MissionParticipant addMissionParticipant(MissionParticipant missionParticipant);
  public MissionParticipant updateMissionParticipant(MissionParticipant missionParticipant);
  public List<MissionParticipant> getAllMissionParticipants();
  public List<MissionParticipant> getMissionParticipantsByParticipant(String username);
  public MissionParticipant getMissionParticipantById(String mpId);
  public void removeMissionParticipant(String id);
}
