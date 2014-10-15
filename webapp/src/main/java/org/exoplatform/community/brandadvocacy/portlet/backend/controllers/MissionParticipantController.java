package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Path;
import juzu.Response;
import juzu.View;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.MissionParticipant;
import org.exoplatform.brandadvocacy.model.Status;
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
    MissionParticipantDTO missionParticipantDTO;
    Mission mission;
    List<MissionParticipant>  missionParticipants = this.missionParticipantService.getAllMissionParticipants();
    User exoUser;
    for (MissionParticipant missionParticipant : missionParticipants){
      try {
        exoUser = this.organizationService.getUserHandler().findUserByName(missionParticipant.getParticipant_username());
        if(null != exoUser){
          mission = this.missionParticipantService.getMissionById(missionParticipant.getMission_id());
          if (null != mission){
            missionParticipantDTO = new MissionParticipantDTO();
            missionParticipantDTO.setId(missionParticipant.getId());
            missionParticipantDTO.setMission_title(mission.getTitle());
            missionParticipantDTO.setParticipant_fullName(exoUser.getFirstName()+ " "+exoUser.getLastName());
            missionParticipantDTO.setStatus(missionParticipant.getStatus());
            missionParticipantDTO.setUrl_submitted(missionParticipant.getUrl_submitted());
            missionParticipantDTO.setDate_submitted("");
            missionParticipantDTOs.add(missionParticipantDTO);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return listTpl.with().set("missionParticipantDTOs",missionParticipantDTOs).set("states", Status.values()).ok();
  }

  @View
  public Response.Content view(String id){
    MissionParticipant missionParticipant = this.missionParticipantService.getMissionParticipantById(id);
    if(null != missionParticipant){
      return viewTpl.with().set("missionParticipant",missionParticipant).ok();
    }
    return Response.ok("cannot get mission participant");
  }


}
