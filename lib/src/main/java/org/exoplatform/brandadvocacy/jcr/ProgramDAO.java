package org.exoplatform.brandadvocacy.jcr;

import com.google.common.collect.Lists;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.JSONException;
import org.json.JSONObject;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 17/10/14.
 */
public class ProgramDAO extends DAO {

  private static final Log log             = ExoLogger.getLogger(ProgramDAO.class);

  public static final String node_prop_labelID = "exo:labelID";
  public static final String node_prop_title = "exo:title";
  public static final String node_prop_active = "exo:active";
  public static final String node_prop_managers = "exo:managerslist";
  public static final String node_prop_missions = "exo:missionslist";
  public static final String node_prop_participants = "exo:participantslist";
  public static final String node_prop_missionparticipants = "exo:missionparticipantslist";
  public static final String node_prop_settings = "exo:settings";
  public static final String node_settings = "exo:program-settings";

  public ProgramDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }

  public Node getOrSettingsHome() throws RepositoryException {
    Node extensionHome = this.getJcrImplService().getOrCreateExtensionHome();
    if (null != extensionHome){
      if (extensionHome.hasNode(node_settings)){
        return extensionHome.getNode(node_settings);
      }else{
        return extensionHome.addNode(node_settings,JCRImpl.PROGRAM_SETTINGS_NODE_TYPE);
      }
    }
    return null;
  }

  public Node getOrCreateManagerHome(Node node) throws RepositoryException {
    return this.getOrCreateNodeCommon(node,node_prop_managers,JCRImpl.MANAGER_LIST_NODE_TYPE);
  }

  public Node   getOrCreateMissionHome(Node node) throws RepositoryException {
    return this.getOrCreateNodeCommon(node,node_prop_missions,JCRImpl.MISSION_LIST_NODE_TYPE);
  }

  public Node getOrCreateParticipantHome(Node node) throws RepositoryException {
    return this.getOrCreateNodeCommon(node,node_prop_participants,JCRImpl.PARTICIPANT_LIST_NODE_TYPE);
  }
  public Node getOrCreateMissionParticipantHome(Node node) throws RepositoryException {
    return this.getOrCreateNodeCommon(node,node_prop_missionparticipants,JCRImpl.MISSION_PARTICIPANT_LIST_NODE_TYPE);
  }

  private void setSettingsPropertiesNode(Node node, Program program) throws RepositoryException {
    node.setProperty(node_prop_settings,program.getSettings().toString());
  }
  private JSONObject transferNode2SettingsObject(Node node) throws RepositoryException {
    if (node.hasProperty(node_prop_settings)){
      String settings = node.getProperty(node_prop_settings).getString();
      try {
        if (null != settings && !"".equals(settings))
          return new JSONObject(settings);
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
  private void setPropertiesNode(Node node, Program program) throws RepositoryException {

    node.setProperty(node_prop_labelID,program.getLabelID());
    node.setProperty(node_prop_title, program.getTitle());
    node.setProperty(node_prop_active,program.getActive());
  }
  private Program transferNode2Object(Node node) throws RepositoryException {
    Program program = new Program("");
    program.setId(node.getUUID());
    PropertyIterator iter = node.getProperties("exo:*");
    iter =  node.getProperties();
    Property p;
    String name;
    while (iter.hasNext()) {
      p = iter.nextProperty();
      name = p.getName();
      if (name.equals(node_prop_labelID)) {
        program.setLabelID(p.getString());
      } else if (name.equals(node_prop_title)) {
        program.setTitle(p.getString());
      } else if(name.equals(node_prop_active)){
        program.setActive(p.getBoolean());
      }
    }
    try {
      program.checkValid();
      return program;
    }
    catch (BrandAdvocacyServiceException brade){
      log.error(" ERROR cannot tranfert node to program object "+brade.getMessage());
    }
    return null;
  }
  private List<Program> transferNodes2Objects(List<Node> nodes){
    List<Program> programs = new ArrayList<Program>(nodes.size());
    for (Node node:nodes){
      Program program = null;
      try {
        program = this.transferNode2Object(node);
        if(null != program){
          programs.add(program);
        }
      } catch (RepositoryException e) {
        log.error("ERROR cannot transfert nodes to program list");
      }
    }
    return programs;
  }

  public Program addProgram(Program program){
    try{
      program.checkValid();
      Node extensionHome = this.getJcrImplService().getOrCreateExtensionHome();
      if (null != extensionHome){
        Node node = extensionHome.addNode(program.getLabelID(),JCRImpl.PROGRAM_NODE_TYPE);
        setPropertiesNode(node,program);
        extensionHome.getSession().save();
        return this.transferNode2Object(node);
      }

    }catch (RepositoryException re){
      log.error("ERROR cannot add program "+re.getMessage());
    }
    catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot add program "+brade.getMessage());
    }
    return null;
  }
  public Program updateProgram(Program program) {
    try {
      program.checkValid();
      Node node = this.getNodeById(program.getId());
      setPropertiesNode(node, program);
      node.save();
      return this.transferNode2Object(node);
    } catch (RepositoryException re) {
      log.error("ERROR cannot update program " + re.getMessage());
    } catch (BrandAdvocacyServiceException brade) {
      log.error("ERROR cannot update program " + brade.getMessage());
    }
    return null;
  }
  public Program getProgramById(String id){

    if (null == id || "".equals(id))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"cannot get program by invalid id ");
    try {
      Node node = this.getNodeById(id);
      if(null != node)
        return this.transferNode2Object(node);
    }catch (RepositoryException re){
      log.error("ERROR cannot get program by id "+id);
    }
    return null;
  }
  public List<Program> getAllPrograms(){
    Node node = this.getJcrImplService().getOrCreateExtensionHome();
    try {
      NodeIterator nodeIterator = node.getNodes();
      return this.transferNodes2Objects(Lists.newArrayList(nodeIterator));
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return null;
  }
  public JSONObject saveSettings(Program program){
    if (null != program.getSettings() && !"".equals(program.getSettings().toString())){
      try {
        Node settingsHome = this.getOrSettingsHome();
        Node settingsNode = null;
        if (null != settingsHome){
          if (!settingsHome.hasNode(program.getId())){
            settingsNode = settingsHome.addNode(program.getId(),JCRImpl.PROGRAM_SETTINGS_NODE_TYPE);
          }
          else{
            settingsNode = settingsHome.getNode(program.getId());
          }
          if (null != settingsNode){
            this.setSettingsPropertiesNode(settingsNode,program);
            settingsHome.getParent().save();
            return this.transferNode2SettingsObject(settingsNode);
          }
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }

    }
    return null;
  }
  public JSONObject getSettings(String programId){
    Node programNode = null;
    try {
      Node settingsHome = this.getOrSettingsHome();
      if (settingsHome.hasNode(programId)){
        return this.transferNode2SettingsObject(settingsHome.getNode(programId));
      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return null;
  }
}

