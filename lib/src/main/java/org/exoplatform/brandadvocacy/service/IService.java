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
  public List<Program> getAllPrograms();

  public Mission addMission2Program(Mission mission);
  public void removeMissionById(String missionId);
  public Mission getMissionById(String missionId);
  public List<Mission> getAllMissionsByProgramId(String programId);
  public Mission updateMission(Mission mission);
  public Mission getRandomMisson(String programId,String username);
  public List<Mission> getAllMissionsByParticipant(String programId, String username);

  public Participant addParticipant2Program(Participant participant);
  public Participant getParticipantInProgramByUserName(String programId, String username);
  public List<Participant> getAllParticipantsInProgram(String programId);

  public Address addAddress2Participant(String programId, String username,Address address);
  public Address updateAddress(Address address);
  public void removeAddress(String addressId);
  public List<Address> getAllAddressesByParticipantInProgram(String programId, String username);
  public Address getAddressById(String id);

  public Manager addManager2Mission(Manager manager);
  public List<Manager> addManagers2Mission(String missionId,List<Manager> managers);
  public Manager updateMissionManager(String missionId,Manager manager);
  public List<Manager> getAllMissionManagers(String missionId);
  public void removeMissionManager(String missionId, String username);
  public Manager getMissionManagerByUserName(String missionId,String username);

  public Manager addManager2Program(Manager manager);
  public List<Manager> addManagers2Program(String programId,List<Manager> managers);
  public Manager updateProgramManager(Manager manager);
  public void removeManagerFromProgram(String programId, String username);
  public Manager getProgramManagerByUserName(String programId, String username);
  public List<Manager> getAllManagersInProgram(String programId);

  public Proposition addProposition2Mission(Proposition proposition);
  public List<Proposition> getAllPropositions(String missionId,Boolean isActive);
  public Proposition getPropositionById(String id);
  public Proposition getRandomProposition(String missionId);
  public String removeProposition(String propositionId);
  public List<Proposition> searchPropositions(String keyword, int offset, int limit);
  public Proposition updateProposition(Proposition proposition);

  public MissionParticipant addMissionParticipant2Program(String programId, MissionParticipant missionParticipant);
  public List<MissionParticipant> getAllMissionParticipantsInProgram(String programId);
  public List<MissionParticipant> getAllMissionParticipantsInProgramByParticipant(String programId,String username);
  public void removeMissionParticipant(String id);
  public MissionParticipant getMissionParticipantById(String mpId);
  public List<MissionParticipant> searchMissionParticipants(String programId, String keyword,Status status, int offset, int limit);
  public MissionParticipant updateMissionParticipantInProgram(String programId, MissionParticipant missionParticipant);
}
