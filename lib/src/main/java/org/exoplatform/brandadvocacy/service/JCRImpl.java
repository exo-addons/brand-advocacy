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

import org.exoplatform.brandadvocacy.jcr.*;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.MissionParticipant;
import org.exoplatform.brandadvocacy.model.Participant;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionManager;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionMode;
import org.exoplatform.services.jcr.ext.distribution.DataDistributionType;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.LinkedList;
import java.util.List;



/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class JCRImpl implements IService {

  private OrganizationService orgService;
  private RepositoryService repositoryService;
  private SessionProviderService sessionService;
  private DataDistributionManager dataDistributionManager;  
  private MissionDAO missionDAO;
  private ManagerDAO managerDAO;

  private ParticipantDAO participantDAO;
  private ParticipantMissionDAO participantMissionDAO;
  private PropositionDAO propositionDAO;
  
  private static final Log log = ExoLogger.getLogger(JCRImpl.class);

  public static String workspace = "collaboration";
  
  public static final String EXTENSION_PATH = "/BrandAdvocacys";
  public static final String PROPOSITIONS_PATH = "/Propositions";
  public static final String MANAGERS_PATH = "/Managers";  
  public static final String PARTICIPANTS_PATH = "/Participants";
  public static final String PARTICIPANT_ADDRESSES_PATH = "/Addresses";  
  public static final String MISSION_PARTICIPANTS_PATH = "/Mission_Participants";
  
  public static final String MISSION_NODE_TYPE = "brad:mission";
  public static final String MANAGER_NODE_TYPE = "brad:manager";
  public static final String PROPOSITION_NODE_TYPE = "brad:propostion";
  public static final String PARTICIPANT_NODE_TYPE = "brad:participant";
  public static final String ADDRESS_NODE_TYPE = "brad:address";
  public static final String MISSION_PARTICIPANT_NODE_TYPE = "brad:mission-participant";
  
  public static final String APP_PATH = "ApplicationData/brandAdvocacyExtension";
  
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
    this.orgService = orgService;
    this.sessionService = sessionService;
    this.dataDistributionManager = dataDistributionManager;
    this.repositoryService = repositoryService;
    
    this.getOrCreateExtensionHome();
  }
  public Session getSession() throws RepositoryException {
    ManageableRepository repo = repositoryService.getCurrentRepository();
    SessionProvider sessionProvider = sessionService.getSystemSessionProvider(null); 
    return sessionProvider.getSession(workspace, repo);
  }
  public Node getOrCreateNode(String path) {
    try {
      Session session = getSession();    
      DataDistributionType type = dataDistributionManager.getDataDistributionType(DataDistributionMode.NONE);
      return type.getOrCreateDataNode(session.getRootNode(), path);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
  public List<Node> getNodeByQuery(String strQuery, int offset, int limit) throws RepositoryException {
    StringBuilder sql = new StringBuilder(strQuery);
    Session session = this.getSession();
    QueryImpl jcrQuery = (QueryImpl) session.getWorkspace().getQueryManager().createQuery(sql.toString(), javax.jcr.query.Query.SQL);
    if (limit >= 0) {
        jcrQuery.setOffset(offset);
        jcrQuery.setLimit(limit);
    }
    NodeIterator results = jcrQuery.execute().getNodes();

    List<Node> nodes = new LinkedList<Node>();
    while (results.hasNext()) {
        nodes.add(results.nextNode());
    }
    return nodes;
  }  
  public Node getOrCreateExtensionHome(){
    String path = String.format("%s", EXTENSION_PATH);
    return this.getOrCreateNode(path);
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
  public Mission addMission(Mission m) throws BrandAdvocacyServiceException{
      return this.getMissionDAO().createMission(m);
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
    return this.getMissionDAO().getAllMissions();
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
  public void addParticipantMission(MissionParticipant pm) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public MissionParticipant getParticipantMissionById(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<MissionParticipant> getParticipantMissionsByParticipantId(String pid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateParticipantMission(MissionParticipant pm) {
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
