package org.exoplatform.community.brandadvocacy.portlet.frontend;

import juzu.*;
import juzu.plugin.ajax.Ajax;
import juzu.request.SecurityContext;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.services.organization.OrganizationService;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by exoplatform on 07/10/14.
 */
@SessionScoped
public class JuZFrontEndApplication {

  OrganizationService organizationService;
  IService jcrService;

  String remoteUserName;
  String currentMissionParticipantId;
  @Inject
  @Path("index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.index indexTpl;

  @Inject
  @Path("discovery.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.frontend.templates.discovery discoveryTpl;

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
  public JuZFrontEndApplication(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @View
  public Response.Content index(SecurityContext securityContext){
    this.remoteUserName = securityContext.getUserPrincipal().getName();
    this.currentMissionParticipantId = null;
    return indexTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadIndexView(){
    return indexTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadDiscoveryView(){
    return discoveryTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadStartView(){
    return startTpl.ok();
  }

  @Ajax
  @Resource
  public Response.Content loadProcessView(){
    Mission missionRandom = this.jcrService.getMissionRandom("Write a review of eXo","been using eXo Platform");
    return processTpl.with().set("mission",missionRandom).set("status", Status.INPROGRESS).ok();
  }

  @Ajax
  @Resource
  public Response.Content loadTerminateView(){
    if(null != this.currentMissionParticipantId){
      return terminateTpl.with().set("sizes", Size.values()).ok();
    }
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response.Content loadThankyouView(String fname, String lname, String address, String city, String phone,String country,String size ){
    if(null != this.currentMissionParticipantId){
      MissionParticipant missionParticipant = this.jcrService.getMissionParticipantById(this.currentMissionParticipantId);
      if(null != missionParticipant){
        Address addressObj = new Address(fname,lname,address,city,country,phone);
        addressObj = this.jcrService.addAddress(this.remoteUserName,addressObj);
        if(null != addressObj ){
          missionParticipant.setStatus(Status.WAITING_FOR_VALIDATE);
          missionParticipant.setAddress_id(addressObj.getId());
          missionParticipant.setSize(Size.getSize(Integer.parseInt(size)));
          if (null != this.jcrService.updateMissionParticipant(missionParticipant) ){
            this.currentMissionParticipantId = null;
            return thankyouTpl.ok();
          }
        }
      }
    }
    return Response.ok("nok");

  }

  @Ajax
  @Resource
  public Response addMissionParticipant(String missionId, String propositionId, String status){

    MissionParticipant missionParticipant = new MissionParticipant();
    missionParticipant.setMission_id(missionId);
    missionParticipant.setProposition_id(propositionId);
    missionParticipant.setParticipant_username(this.remoteUserName);
    if(null != status)
      missionParticipant.setStatus(Status.getStatus(Integer.parseInt(status)));

    try {
      missionParticipant = this.jcrService.addMissionParticipant(missionParticipant);
      if(null != missionParticipant){
        this.currentMissionParticipantId = missionParticipant.getId();
        Participant participant = new Participant(this.remoteUserName);
        Set<String> missionParticipantIds = new HashSet<String>();
        missionParticipantIds.add(this.currentMissionParticipantId);
        participant.setMission_participant_ids(missionParticipantIds);
        if (null != this.jcrService.addParticipant(participant));
          return Response.ok("ok");
      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return Response.ok("nok");
  }

}
