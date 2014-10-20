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
import org.exoplatform.brandadvocacy.model.*;
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
  private ProgramDAO programDAO;
  private MissionDAO missionDAO;
  private ManagerDAO managerDAO;
  private AddressDAO addressDAO;
  private ParticipantDAO participantDAO;
  private MissionParticipantDAO missionParticipantDAO;
  private PropositionDAO propositionDAO;
  
  private static final Log log = ExoLogger.getLogger(JCRImpl.class);

  public static String workspace = "collaboration";
  
  public static final String EXTENSION_PATH = "/BrandAdvocacys";
  public static final String MISSIONS_PATH   = "Missions";
  public static final String MISSION_PARTICIPANT_PATH = "MissionParticipants";
  public static final String PARTICIPANT_PATH = "Participants";

  public static final String PROGRAM_NODE_TYPE = "brad:program";
  public static final String MISSION_NODE_TYPE = "brad:mission";
  public static final String MISSION_LIST_NODE_TYPE = "brad:missionslist";
  public static final String MANAGER_LIST_NODE_TYPE = "brad:managerslist";
  public static final String MISSION_PARTICIPANT_LIST_NODE_TYPE = "brad:missionparticipantslist";
  public static final String PARTICIPANT_LIST_NODE_TYPE = "brad:participantslist";
  public static final String PROPOSITION_LIST_NODE_TYPE = "brad:propositionslist";
  public static final String MANAGER_NODE_TYPE = "brad:manager";
  public static final String PROPOSITION_NODE_TYPE = "brad:proposition";
  public static final String PARTICIPANT_NODE_TYPE = "brad:participant";
  public static final String ADDRESS_LIST_NODE_TYPE = "brad:addresseslist";
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
    this.setProgramDAO(new ProgramDAO(this));
    this.setMissionDAO(new MissionDAO(this));
    this.setManagerDAO(new ManagerDAO(this));
    this.setParticipantDAO(new ParticipantDAO(this));
    this.setMissionParticipantDAO(new MissionParticipantDAO(this));
    this.setPropositionDAO(new PropositionDAO(this));
    this.setAddressDAO(new AddressDAO(this));
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
  public ProgramDAO getProgramDAO() {
    return programDAO;
  }

  public void setProgramDAO(ProgramDAO programDAO) {
    this.programDAO = programDAO;
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
  public AddressDAO getAddressDAO() {
    return addressDAO;
  }

  public void setAddressDAO(AddressDAO addressDAO) {
    this.addressDAO = addressDAO;
  }
  public MissionParticipantDAO getMissionParticipantDAO() {
    return missionParticipantDAO;
  }
  public void setMissionParticipantDAO(MissionParticipantDAO missionParticipantDAO) {
    this.missionParticipantDAO = missionParticipantDAO;
  }
  public PropositionDAO getPropositionDAO() {
    return propositionDAO;
  }
  public void setPropositionDAO(PropositionDAO propositionDAO) {
    this.propositionDAO = propositionDAO;
  }


  @Override
  public Program addProgram(Program program) {
    return this.getProgramDAO().addProgram(program);
  }

  @Override
  public Program updateProgram(Program program) {
    return this.getProgramDAO().updateProgram(program);
  }

  @Override
  public Program getProgramById(String programId) {
    return this.getProgramDAO().getProgramById(programId);
  }

  @Override
  public List<Program> getAllPrograms() {
    return this.getProgramDAO().getAllPrograms();
  }

  @Override
  public Mission addMission2Program(Mission mission) {
    return this.getMissionDAO().addMission2Program(mission);
  }

  @Override
  public void removeMissionById(String missionId) {
    this.getMissionDAO().removeMissionById(missionId);
  }

  @Override
  public Mission getMissionById(String missionId) {
    return this.getMissionDAO().getMissionById(missionId);
  }

  @Override
  public List<Mission> getAllMissionsByProgramId(String programId) {
    return this.getMissionDAO().getAllMissionsByProgramId(programId);
  }

  @Override
  public Mission updateMission(Mission mission) {
    return this.getMissionDAO().updateMission(mission);
  }

  @Override
  public Mission getRandomMisson(String programId, String username) {
    return this.getMissionDAO().getRandomMission(programId,username);
  }

  @Override
  public List<Mission> getAllMissionsByParticipant(String programId, String username) {
    return this.getMissionDAO().getAllMissionsInProgramByParticipant(programId,username);
  }

  @Override
  public Participant addParticipant2Program(Participant participant) {
    return this.getParticipantDAO().addParticipant2Program(participant);
  }

  @Override
  public Participant getParticipantInProgramByUserName(String programId, String username) {
    return this.getParticipantDAO().getParticipantInProgramByUserName(programId,username);
  }

  @Override
  public List<Participant> getAllParticipantsInProgram(String programId) {
    return this.getParticipantDAO().getAllParticipantsInProgram(programId);
  }

  @Override
  public Address addAddress2Participant(String programId, String username, Address address) {
    return this.getAddressDAO().addAddress2Participant(programId,username,address);
  }

  @Override
  public Address updateAddress(Address address) {
    return this.getAddressDAO().updateAddress(address);
  }

  @Override
  public void removeAddress(String addressId) {
    this.getAddressDAO().removeAddress(addressId);
  }

  @Override
  public List<Address> getAllAddressesByParticipantInProgram(String programId, String username) {
    return this.getAddressDAO().getAllAddressesByParticipantInProgram(programId,username);
  }

  @Override
  public Address getAddressById(String id) {
    return this.getAddressDAO().getAddressById(id);
  }

  @Override
  public Manager addManager2Mission(Manager manager) {
    return this.getManagerDAO().addManager2Mission(manager);
  }

  @Override
  public List<Manager> addManagers2Mission(String missionId, List<Manager> managers) {
    return this.getManagerDAO().addManagers2Mission(missionId,managers);
  }

  @Override
  public Manager updateMissionManager(String missionId,Manager manager) {
    return this.getManagerDAO().updateMissionManager(missionId,manager);
  }

  @Override
  public List<Manager> getAllMissionManagers(String missionId) {
    return this.getManagerDAO().getAllMissionManagers(missionId);
  }

  @Override
  public void removeMissionManager(String missionId, String username) {
    this.getManagerDAO().removeMissionManager(missionId,username);
  }

  @Override
  public Manager getMissionManagerByUserName(String missionId, String username) {
    return this.getManagerDAO().getMissionManagerByUserName(missionId,username);
  }

  @Override
  public Manager addManager2Program(Manager manager) {
    return this.getManagerDAO().addManager2Program(manager);
  }

  @Override
  public List<Manager> addManagers2Program(String programId, List<Manager> managers) {
    return this.getManagerDAO().addManagers2Program(programId,managers);
  }

  @Override
  public Manager updateProgramManager(String programId, Manager manager) {
    return this.getManagerDAO().updateProgramManager(programId,manager);
  }

  @Override
  public void removeManagerFromProgram(String programId, String username) {
    this.getManagerDAO().removeManagerFromProgram(programId,username);
  }

  @Override
  public Manager getProgramManagerByUserName(String programId, String username) {
    return this.getManagerDAO().getProgramManagerByUserName(programId,username);
  }

  @Override
  public List<Manager> getAllManagersInProgram(String programId) {
    return this.getManagerDAO().getAllManagersInProgram(programId);
  }

  @Override
  public Proposition addProposition2Mission(Proposition proposition) {
    return this.getPropositionDAO().addProposition2Mission(proposition);
  }

  @Override
  public List<Proposition> getAllPropositions(String missionId) {
    return this.getPropositionDAO().getAllPropositions(missionId);
  }

  @Override
  public Proposition getPropositionById(String id) {
    return this.getPropositionDAO().getPropositionById(id);
  }

  @Override
  public Proposition getRandomProposition(String missionId) {
    return this.getPropositionDAO().getRandomProposition(missionId);
  }

  @Override
  public void removeProposition(String propositionId) {
    this.getPropositionDAO().removeProposition(propositionId);
  }

  @Override
  public List<Proposition> searchPropositions(String keyword, int offset, int limit) {
    return this.getPropositionDAO().searchPropositions(keyword,offset,limit);
  }

  @Override
  public Proposition updateProposition(Proposition proposition) {
    return this.getPropositionDAO().updateProposition(proposition);
  }

  @Override
  public MissionParticipant addMissionParticipant2Program(String programId, MissionParticipant missionParticipant) {
    return this.getMissionParticipantDAO().addMissionParticipant2Program(programId,missionParticipant);
  }

  @Override
  public List<MissionParticipant> getAllMissionParticipantsInProgram(String programId) {
    return this.getMissionParticipantDAO().getAllMissionParticipantsInProgram(programId);
  }

  @Override
  public List<MissionParticipant> getAllMissionParticipantsInProgramByParticipant(String programId, String username) {
    return this.getMissionParticipantDAO().getAllMissionParticipantsInProgramByParticipant(programId,username);
  }

  @Override
  public MissionParticipant getMissionParticipantById(String mpId) {
    return this.getMissionParticipantDAO().getMissionParticipantById(mpId);
  }

  @Override
  public MissionParticipant updateMissionParticipantInProgram(String programId, MissionParticipant missionParticipant) {
    return this.getMissionParticipantDAO().updateMissionParticipantInProgram(programId,missionParticipant);
  }

  @Override
  public void removeMissionParticipant(String id) {
    this.getMissionParticipantDAO().removeMissionParticipant(id);
  }

  @Override
  public List<MissionParticipant> searchMissionParticipants(String programId, String keyword, int offset, int limit) {
    return this.getMissionParticipantDAO().searchMissionParticipants(programId,keyword,offset,limit);
  }

}
