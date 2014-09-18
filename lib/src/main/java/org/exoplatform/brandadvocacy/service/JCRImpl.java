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

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.brandadvocacy.jcr.ManagerDAO;
import org.exoplatform.brandadvocacy.jcr.MissionDAO;
import org.exoplatform.brandadvocacy.jcr.ParticipantDAO;
import org.exoplatform.brandadvocacy.jcr.ParticipantMissionDAO;
import org.exoplatform.brandadvocacy.jcr.PropositionDAO;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Participant;
import org.exoplatform.brandadvocacy.model.ParticipantMission;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;



/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class JCRImpl implements IService {

  
  private RepositoryService repositoryService;
  private SessionProviderService sessionService;
  private DataDistributionManager dataDistributionManager;  
  public String workspace = "brandadvocacy";
  private MissionDAO missionDAO;
  private ManagerDAO managerDAO;

  private ParticipantDAO participantDAO;
  private ParticipantMissionDAO participantMissionDAO;
  private PropositionDAO propositionDAO;
  
  private static final Log log = ExoLogger.getLogger(JCRImpl.class);
  
  public JCRImpl(InitParams params, OrganizationService orgService, SessionProviderService sessionService, RepositoryService repositoryService, DataDistributionManager dataDistributionManager){
    
    if(params != null){
      ValueParam param = params.getValueParam("workspace");
      if(param != null){
        workspace = param.getValue();
      }
    }
    this.setMissionDAO(new MissionDAO(this));
    this.setManagerDAO(new ManagerDAO(this));
    this.setParticipantDAO(new ParticipantDAO(this));
    this.setParticipantMissionDAO(new ParticipantMissionDAO(this));
    this.setPropositionDAO(new PropositionDAO(this));
    
  }
  public Session getSession() throws RepositoryException {
    ManageableRepository repo = repositoryService.getCurrentRepository();
    SessionProvider sessionProvider = sessionService.getSystemSessionProvider(null); 
    return sessionProvider.getSession(workspace, repo);
  }

  public MissionDAO getMissionDAO() {
    return missionDAO;
  }
  public void setMissionDAO(MissionDAO missionDAO) {
    this.missionDAO = missionDAO;
  }
  public ManagerDAO getManagerDAO() {
    return managerDAO;
  }
  public void setManagerDAO(ManagerDAO managerDAO) {
    this.managerDAO = managerDAO;
  }
  public ParticipantDAO getParticipantDAO() {
    return participantDAO;
  }
  public void setParticipantDAO(ParticipantDAO participantDAO) {
    this.participantDAO = participantDAO;
  }
  public ParticipantMissionDAO getParticipantMissionDAO() {
    return participantMissionDAO;
  }
  public void setParticipantMissionDAO(ParticipantMissionDAO participantMissionDAO) {
    this.participantMissionDAO = participantMissionDAO;
  }
  public PropositionDAO getPropositionDAO() {
    return propositionDAO;
  }
  public void setPropositionDAO(PropositionDAO propositionDAO) {
    this.propositionDAO = propositionDAO;
  }

  
  @Override
  public void addMission(Mission m) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeMission(String id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Mission getMissionById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Mission> getAllMissions() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateMission(Mission m) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addParticipant(Participant p) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeParticipant(String id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Participant getParticipantById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Participant> getAllParticipants() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Participant> getParticipantsByMissionId(String mid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void addParticipantMission(ParticipantMission pm) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public ParticipantMission getParticipantMissionById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ParticipantMission> getParticipantMissionsByParticipantId(String pid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateParticipantMission(ParticipantMission pm) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addProposition(Proposition p) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeProposition(String id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Proposition getPropositionById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Proposition> getPropositionsByMissionId(String mid) {
    // TODO Auto-generated method stub
    return null;
  }

}
