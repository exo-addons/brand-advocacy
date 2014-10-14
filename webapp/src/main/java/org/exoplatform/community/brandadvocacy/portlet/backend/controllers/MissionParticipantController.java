package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Path;
import juzu.Response;
import juzu.View;
import org.exoplatform.brandadvocacy.model.MissionParticipant;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.MissionParticipantDTO;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 10/12/14.
 */
public class MissionParticipantController {

  OrganizationService organizationService;
  IService missionParticipantService;

  @Inject
  @Path("mission_participant/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission_participant.list listTpl;

  @Inject
  @Path("mission_participant/view.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission_participant.view viewTpl;


  @Inject
  public MissionParticipantController(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.missionParticipantService = iService;
  }

  @View
  public Response.Content index(){

    List<MissionParticipantDTO> missionParticipantDTOs = new ArrayList<MissionParticipantDTO>();

    List<MissionParticipant>  missionParticipants = this.missionParticipantService.getAllMissionParticipants();
    User exoUser;
    for (MissionParticipant missionParticipant : missionParticipants){
      try {
        exoUser = this.organizationService.getUserHandler().findUserByName(missionParticipant.getParticipant_username());
        if(null != exoUser){
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return listTpl.with().set("missionParticipants",missionParticipants).ok();
  }

  @View
  public Response.Content loadDetailView(){
    return viewTpl.ok();
  }

}
