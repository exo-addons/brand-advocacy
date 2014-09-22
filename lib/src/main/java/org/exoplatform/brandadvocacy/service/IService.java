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

import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.MissionParticipant;
import org.exoplatform.brandadvocacy.model.Participant;
import org.exoplatform.brandadvocacy.model.Proposition;

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
  public Mission getMissionById(String id);
  public List<Mission> getAllMissions();
  public void updateMission(Mission m);
  
  public void addParticipant(Participant p);
  public void removeParticipant(String id);
  public Participant getParticipantById(String id);
  public List<Participant> getAllParticipants();
  public List<Participant> getParticipantsByMissionId(String mid);

  public void addParticipantMission(MissionParticipant pm);
  public MissionParticipant getParticipantMissionById(String id);
  public List<MissionParticipant> getParticipantMissionsByParticipantId(String pid);
  public void updateParticipantMission(MissionParticipant pm);
  
  public void addProposition(Proposition p);
  public void removeProposition(String id);
  public Proposition getPropositionById(String id);
  public List<Proposition> getPropositionsByMissionId(String mid);
}
