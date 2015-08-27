package org.exoplatform.community.brandadvocacy.portlet.logout;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import juzu.request.HttpContext;
import juzu.request.SecurityContext;
import org.apache.http.message.BasicNameValuePair;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.service.ApacheHttpClient;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.brandadvocacy.service.Utils;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@SessionScoped
public class JuZLogoutApplication {

  @Inject
  IService jcrService;

  String remoteUserName;
  String currentMissionParticipantId;
  String currentMissionParticipantStatus;
  String currentPropositionId;
  String currentMissionId;
  String currentProgramId;
  String currentProgramTitle;
  Boolean isFinished;
  String bannerUrl;
  String sizeOutOfStock;
  String save_user_data_endpoint;
  String save_user_data_endpoint_token;
  String save_user_data_request_method;
  JSONObject currentSettings;


  @Inject
  @Path("index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.logout.templates.index indexTpl;


  @Inject
  @Path("stepContainer.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.logout.templates.stepContainer stepContainerTpl;

  @Inject
  @Path("process.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.logout.templates.process processTpl;

  @Inject
  @Path("terminate.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.logout.templates.terminate terminateTpl;

  @Inject
  @Path("thankyou.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.logout.templates.thankyou thankyouTpl;

  public JuZLogoutApplication(){
  }
  private void init(){
    this.remoteUserName = null;
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
    this.bannerUrl = "";
    this.isFinished = false;
    this.remoteUserName = null;
    if (null == securityContext.getUserPrincipal()){
      this.init();
      if (null != this.currentMissionId){

        this.currentSettings = this.jcrService.getProgramSettings(currentProgramId);
        if (null != currentSettings){
          String banner_url = Utils.getAttrFromJson(currentSettings,Program.banner_url_setting_key);
          if (null != banner_url && !"".equals(banner_url))
            this.bannerUrl = banner_url;
          sizeOutOfStock = Utils.getAttrFromJson(currentSettings,Program.size_out_of_stock_setting_key);
          save_user_data_endpoint = Utils.getAttrFromJson(currentSettings,Program.save_user_data_endpoint_setting_key);
          save_user_data_endpoint_token = Utils.getAttrFromJson(currentSettings,Program.save_user_data_endpoint_token_setting_key);
          save_user_data_request_method = Utils.getAttrFromJson(currentSettings,Program.save_user_data_request_method_setting_key);

        }

        if(!"".equals(bannerUrl) && !this.checkBannerUrl(bannerUrl))
          this.bannerUrl = "";

        return indexTpl.with().set("bannerUrl",bannerUrl).set("programTitle",currentProgramTitle).ok();
      }
    }
    return Response.ok("");
  }

  private Boolean checkBannerUrl(String bannerUrl){

    try {
      URL url = new URL(bannerUrl);
      HttpURLConnection huc = (HttpURLConnection) url.openConnection();
      huc.setRequestMethod("HEAD");
      int responseCode = huc.getResponseCode();
      if (responseCode == 200)
        return true;
    } catch (MalformedURLException e) {
    } catch (IOException e) {
    }
    return false;
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
      this.currentProgramTitle = program.getTitle();
      break;
    }
  }

  public String checkSession(){
    return "";
  }

  @Ajax
  @Resource
  public Response.Content initView(){

    if (null != this.currentMissionId && null != this.currentPropositionId){
      if(null == this.currentMissionParticipantId){
        if(!this.getOrCreateMissionParticipant(this.currentMissionId)){
          return Response.ok("something went wrong, please come back later");
        }else{
          return indexTpl.ok();
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
  public Response.Content loadStepContainerView(){
    return stepContainerTpl.ok();
  }

  @Ajax
  @Resource
  public Response processOpenMission(){
    String session = this.checkSession();
    if ("".equals(session)){
      if(!this.getOrCreateMissionParticipant(this.currentMissionId)){
        return Response.ok("nok");
      }else{
        return Response.ok("ok");
      }
    }else{
      return Response.ok(session);
    }

  }

  @Ajax
  @Resource
  public Response loadProcessView(){
    Mission missionRandom = this.getCurrentMission();
    if(null != missionRandom){
      return processTpl.with().set("mission", missionRandom).ok();
    }
    else
      return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response loadTerminateView(){
    String[] out_of_stock = {};
    if(null != sizeOutOfStock && !"".equals(sizeOutOfStock))
      out_of_stock = sizeOutOfStock.split(",");
    return terminateTpl.with().set("sizes", Size.values()).set("size_out_of_stock",out_of_stock).ok();
  }

  @Ajax
  @Resource
  public Response executeMission(){
    String session = this.checkSession();
    if ("".equals(session)){
      if(null != this.currentMissionParticipantId){
        MissionParticipant missionParticipant = this.jcrService.getMissionParticipantById(this.currentMissionParticipantId);
        if(null != missionParticipant){
          missionParticipant.setStatus(Status.INPROGRESS);
          if (null != this.jcrService.updateMissionParticipantInProgram(this.currentProgramId,missionParticipant)){
            this.currentMissionParticipantStatus = Status.INPROGRESS.getLabel();
            return Response.ok("ok");
          }
        }
      }
      return Response.ok("nok");
    }
    else
      return Response.ok(session);

  }

  @Ajax
  @Resource
  // store mission only when user complete his mission
  public Response completeMission(String url){
    if(null != this.currentMissionParticipantId){
      MissionParticipant missionParticipant = this.jcrService.getMissionParticipantById(this.currentMissionParticipantId);
      if(null != missionParticipant){
        missionParticipant.setStatus(Status.COMPLETE);
        missionParticipant.setUrl_submitted(url);
        if (null != this.jcrService.updateMissionParticipantInProgram(this.currentProgramId,missionParticipant)){
          this.currentMissionParticipantStatus = Status.COMPLETE.getLabel();
          return Response.ok("ok");
        }
      }
    }
    if (this.jcrService.removeMissionParticipantInParticipant(currentProgramId,remoteUserName,currentMissionParticipantId)) {
      this.jcrService.removeMissionParticipant(currentMissionParticipantId);
    }
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  // store mission only when user complete his mission
  public Response terminate(String url,String fname, String lname,String email, String address, String city, String phone,String country,String size ){
    this.remoteUserName = email;
    String session = this.checkSession();
    this.getOrCreateMissionParticipant(currentMissionId);
    if ("".equals(session)){
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
              Participant participant = new Participant(this.remoteUserName);
              participant.setProgramId(this.currentProgramId);
              Set<String> missionIds = new HashSet<String>();
              missionIds.add(currentMissionId);
              participant.setMission_ids(missionIds);
              Set<String> missionParticipantIds = new HashSet<String>();
              missionParticipantIds.add(currentMissionParticipantId);
              participant.setMission_participant_ids(missionParticipantIds);
              if (null != this.jcrService.addParticipant2Program(participant) && null != this.updateCurrentProposition()) {
                if (null == save_user_data_endpoint || "".equals(save_user_data_endpoint) || null == save_user_data_endpoint_token || "".equals(save_user_data_endpoint_token)){
                  List params = new ArrayList();
                  params.add(new BasicNameValuePair("email",email));
                  params.add(new BasicNameValuePair("firstname", fname));
                  params.add(new BasicNameValuePair("lastname", lname));
                  ApacheHttpClient.sendRequest(save_user_data_endpoint,save_user_data_endpoint_token,save_user_data_request_method,params);
                }
                return Response.ok("ok");
              }
            }
          }
        }
      }
      if (this.jcrService.removeMissionParticipantInParticipant(currentProgramId,remoteUserName,currentMissionParticipantId)) {
        this.jcrService.removeMissionParticipant(currentMissionParticipantId);
      }
      return Response.ok("nok");
    }
    else
      return Response.ok(session);
  }
  @Ajax
  @Resource
  public Response loadThankyouView(){
    return thankyouTpl.ok();
  }
  private Boolean getOrCreateMissionParticipant(String missionId){

    MissionParticipant missionParticipant = this.jcrService.getCurrentMissionParticipantByMissionId(currentProgramId,missionId,remoteUserName);
    if (null == missionParticipant) {
      missionParticipant = new MissionParticipant();
      missionParticipant.setMission_id(missionId);
      missionParticipant.setParticipant_username(remoteUserName);
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
  @Ajax
  @Resource
  public Response sendNotifEmail(){
    if (this.jcrService.sendNotifMissionParticipantEmail(this.currentSettings,this.currentMissionParticipantId,"")) {
      return Response.ok("ok");
    }
    return Response.ok("nok");
  }
  @Ajax
  @Resource
  public Response sendNotifAlmostMissionDoneEmail(){
    if (this.jcrService.sendNotifAlmostMissionDoneEmail(this.currentProgramId,this.remoteUserName)) {
      return Response.ok("ok");
    }
    return Response.ok("nok");
  }
  @Ajax
  @Resource
  public Response generateNewMission(){
    String session = checkSession();
    if("".equals(session)){
      this.init();
      if(null != this.currentMissionId)
        return Response.ok("ok");
      return Response.ok("nok");
    }
    return Response.ok(session);
  }

}
