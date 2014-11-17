package org.exoplatform.community.brandadvocacy.portlet.frontend;

import juzu.*;
import juzu.request.SecurityContext;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.commons.juzu.ajax.Ajax;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by exoplatform on 07/10/14.
 */
@SessionScoped
public class JuZFrontEndApplication {

  IService jcrService;

  String remoteUserName;
  String currentMissionParticipantId;
  String currentMissionParticipantStatus;
  String currentPropositionId;
  String currentMissionId;
  String currentProgramId;
  Boolean isFinished;

  @Inject
  @Path("index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.index indexTpl;

  @Inject
  @Path("stepContainer.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.stepContainer stepContainerTpl;

  @Inject
  @Path("start.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.start startTpl;

  @Inject
  @Path("process.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.process processTpl;

  @Inject
  @Path("terminate.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.terminate terminateTpl;

  @Inject
  @Path("thankyou.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.thankyou thankyouTpl;

  @Inject
  public JuZFrontEndApplication(IService iService){
    this.jcrService = iService;
  }
  private void init(){
    this.currentMissionId = null;
    this.currentMissionParticipantId = null;
    this.currentPropositionId = null;
    this.currentMissionParticipantStatus = null;
    if (null == this.currentProgramId)
      this.loadCurrentProgram();
    if (null != this.currentProgramId) {
//      this.loadCurrentMission();
      this.getRandomMission();
    }
  }
  @View
  public Response.Content index(SecurityContext securityContext){
    this.isFinished = false;
    this.remoteUserName = securityContext.getUserPrincipal().getName();
    if (null != remoteUserName){
      this.init();
      if (null != this.currentMissionId){
        return indexTpl.ok();
      }
    }
    return Response.ok("");
  }

  private Mission getRandomMission(){
    Mission mission = this.jcrService.getRandomMisson(this.currentProgramId,this.remoteUserName);
    if (null != mission){
      this.currentMissionId = mission.getId();
      List<Proposition> propositions = mission.getPropositions();
      if (null != propositions && propositions.size() > 0){
        this.currentPropositionId = propositions.get(0).getId();
        return mission;
      }
    }
    return null;
  }

  private void loadCurrentMission(){
    MissionParticipant missionParticipant = this.jcrService.getCurrentMissionParticipantByUserName(this.currentProgramId,this.remoteUserName);
    if (null != missionParticipant){
      this.currentMissionParticipantId = missionParticipant.getId();
      this.currentMissionId = missionParticipant.getMission_id();
      this.currentMissionParticipantStatus = missionParticipant.getStatus().getLabel();
      this.currentPropositionId = missionParticipant.getProposition_id();
    }
    else {
      if(null == this.currentMissionId && null == this.currentPropositionId){
        Mission mission = this.jcrService.getRandomMisson(this.currentProgramId,this.remoteUserName);
        if (null != mission){
          this.currentMissionId = mission.getId();
          Proposition proposition = this.jcrService.getRandomProposition(this.currentMissionId);
          if (null != proposition){
            this.currentPropositionId = proposition.getId();
          }
        }
      }
    }
  }

  private void loadCurrentProgram(){
    List<Program> programs = this.jcrService.getAllPrograms();
    for (Program program:programs){
      this.currentProgramId = program.getId();
      break;
    }
  }

  @Ajax
  @Resource
  public Response.Content initView(){

    if (null != this.currentMissionId && null != this.currentPropositionId){
      if(null == this.currentMissionParticipantId){
        if(!this.getOrCreateMissionParticipant(this.currentMissionId)){
          return Response.ok("something went wrong, please come back later");
        }else{
          return startTpl.ok();
        }
      }
      else{
        if (Status.OPEN.getLabel().equals(this.currentMissionParticipantStatus)){
          Mission mission = this.getCurrentMission();
          return processTpl.with().set("mission", mission).ok();
        }else if (Status.INPROGRESS.getLabel().equals(this.currentMissionParticipantStatus)){
            return terminateTpl.with().set("sizes", Size.values()).ok();
        }
      }
    }
    return Response.ok("We are preparing next mission, please come back later");
  }
  @Ajax
  @Resource
  public Response.Content loadIndexView(){
    //if (this.isFinished)
    this.init();
    if (null != this.currentProgramId)
      return indexTpl.ok();
    else
      return Response.ok("We are preparing next mission, please come back later");
  }

  @Ajax
  @Resource
  public Response.Content loadStepContainerView(){
    return stepContainerTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadStartView(){
    if(null == this.currentMissionId || null == this.currentPropositionId){
      return Response.ok("We are preparing next mission, please come back later");
    }
    if(!this.getOrCreateMissionParticipant(this.currentMissionId)){
      return Response.ok("something went wrong, please come back later");
    }else{
      return startTpl.ok();
    }
  }

  @Ajax
  @Resource
  public Response loadProcessView(){
    Mission missionRandom = this.getCurrentMission();
    if(null != missionRandom)
     return processTpl.with().set("mission", missionRandom).ok();
    else
      return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response loadTerminateView(){
    if(null != this.currentMissionParticipantId){
      MissionParticipant missionParticipant = this.jcrService.getMissionParticipantById(this.currentMissionParticipantId);
      if(null != missionParticipant){
        missionParticipant.setStatus(Status.INPROGRESS);
        if (null != this.jcrService.updateMissionParticipantInProgram(this.currentProgramId,missionParticipant)){
          this.currentMissionParticipantStatus = Status.INPROGRESS.getLabel();
          return terminateTpl.with().set("sizes", Size.values()).ok();
        }
      }
    }
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response loadThankyouView(String url,String fname, String lname, String address, String city, String phone,String country,String size ){
    if(null != this.currentMissionParticipantId){
      MissionParticipant missionParticipant = this.jcrService.getMissionParticipantById(this.currentMissionParticipantId);
      if(null != missionParticipant){
        missionParticipant.setProposition_id(this.currentPropositionId);
        Address addressObj = new Address(fname,lname,address,city,country,phone);
        addressObj = this.jcrService.addAddress2Participant(this.currentProgramId,this.remoteUserName,addressObj);
        if(null != addressObj ){
          missionParticipant.setUrl_submitted(url);
          missionParticipant.setStatus(Status.WAITING_FOR_VALIDATE);
          missionParticipant.setAddress_id(addressObj.getId());
          missionParticipant.setSize(Size.getSize(Integer.parseInt(size)));
          if (null != this.jcrService.updateMissionParticipantInProgram(this.currentProgramId,missionParticipant) ){
            if (this.completeMission()){
              this.isFinished = true;
              return thankyouTpl.ok();
            }
          }
        }
      }
    }
    this.jcrService.removeMissionParticipant(currentMissionParticipantId);
    return Response.ok("nok");

  }

  // store mission only when user complete his mission
  private Boolean completeMission(){
    if (null != currentMissionId && null != currentPropositionId && null != this.currentMissionParticipantId){
      Participant participant = new Participant(this.remoteUserName);
      participant.setProgramId(this.currentProgramId);
      Set<String> missionIds = new HashSet<String>();
      missionIds.add(currentMissionId);
      participant.setMission_ids(missionIds);
      Set<String> missionParticipantIds = new HashSet<String>();
      missionParticipantIds.add(currentMissionParticipantId);
      participant.setMission_participant_ids(missionParticipantIds);
      if (null != this.jcrService.addParticipant2Program(participant) && null != this.updateCurrentProposition()) {
        return true;
      }
    }
    return false;
  }
  private Boolean getOrCreateMissionParticipant(String missionId){

    MissionParticipant missionParticipant = this.jcrService.getCurrentMissionParticipantByMissionId(currentProgramId,missionId);
    if (null == missionParticipant) {
      missionParticipant = new MissionParticipant();
      missionParticipant.setMission_id(missionId);
      missionParticipant.setParticipant_username(this.remoteUserName);
      missionParticipant = this.jcrService.addMissionParticipant2Program(this.currentProgramId,missionParticipant);
      if(null != missionParticipant){

        Participant participant = new Participant(this.remoteUserName);
        participant.setProgramId(this.currentProgramId);
        Set<String> missionIds = new HashSet<String>();
        participant.setMission_ids(missionIds);
        Set<String> missionParticipantIds = new HashSet<String>();
        missionParticipantIds.add(missionParticipant.getId());
        participant.setMission_participant_ids(missionParticipantIds);

        if (null != this.jcrService.addParticipant2Program(participant)) {
          this.currentMissionParticipantId = missionParticipant.getId();
          this.currentMissionParticipantStatus = Status.OPEN.getLabel();
          return true;
        }
        if (null == this.currentMissionParticipantId){
          this.jcrService.removeMissionParticipant(missionParticipant.getId());
        }
      }
    }
    else{
      this.currentMissionParticipantId = missionParticipant.getId();
      this.currentMissionParticipantStatus = Status.OPEN.getLabel();
      return true;
    }
    return false;
  }

  private Proposition updateCurrentProposition(){
    Proposition proposition = this.jcrService.getPropositionById(this.currentPropositionId);
    if(null != proposition){
      proposition.setNumberUsed(proposition.getNumberUsed()+1);
      return this.jcrService.updateProposition(proposition);
    }
    return null;
  }
  private Mission getCurrentMission(){
    Mission randomMission = null;
    Proposition randomProposition = null;
    if (null != this.currentMissionId){
      List<Proposition>  propositions = new ArrayList<Proposition>(1);
      randomMission = this.jcrService.getMissionById(this.currentMissionId);
      if (null != this.currentPropositionId){
        randomProposition = this.jcrService.getPropositionById(this.currentPropositionId);
        if (null != randomProposition){
          propositions.add(randomProposition);
          randomMission.setPropositions(propositions);
          return randomMission;
        }
      }
    }
    return null;

  }

}
