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
 
  public Mission addMission(Mission m) throws BrandAdvocacyServiceException;
  public void removeMission(String id);
  public Mission getMissionById(String id) throws RepositoryException;
  public List<Mission> getAllMissions();
  public Mission updateMission(Mission m);

  public Participant addParticipant(Participant p) throws RepositoryException;
  public void removeParticipant(String id);
  public Participant getParticipantById(String id);
  public List<Participant> getAllParticipants();
  public List<Participant> getParticipantsByMissionId(String mid);

  public Address addAddress(String username,Address address);
  public Address updateAddress(Address address);
  public Address removeAddress(Address address);
  public List<Address> getAllAddressesByParticipant(String username);

  public Mission addManagers2Mission(String mid,List<Manager> managers);
  public Manager updateManager(Manager manager);
  public List<Manager> getAllManagers(String mid);
  public void removeManager(String missionLabelId, String username);
  public Manager getManager(String missionLabelId,String username);

  public Proposition updateProposition(Proposition proposition);
  public List<Proposition> getPropositionsByMissionId(String mid);
  public Proposition getPropositionById(String id);
  public Mission addProposition2Mission(String mid,List<Proposition> propositions);
  public void removeProposition(String id);
  public List<Proposition> searchPropositions(String sql);

  public MissionParticipant addMissionParticipant(MissionParticipant missionParticipant) throws RepositoryException;
  public MissionParticipant updateMissionParticipant(MissionParticipant missionParticipant) throws RepositoryException;
  public List<MissionParticipant> getAllMissionParticipants() throws RepositoryException;
  public List<MissionParticipant> getMissionParticipantsByParticipant(String username) throws RepositoryException;


}
