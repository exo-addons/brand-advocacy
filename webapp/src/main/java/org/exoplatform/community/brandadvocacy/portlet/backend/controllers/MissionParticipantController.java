package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.community.brandadvocacy.portlet.backend.Flash;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.MissionParticipantDTO;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.Pagination;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.ParticipantDTO;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 10/12/14.
 */
@SessionScoped
public class MissionParticipantController {

  final static int NUMBER_RECORDS = 10;
  OrganizationService organizationService;
  IdentityManager identityManager;
  IService missionParticipantService;

  @Inject
  LoginController loginController;
  @Inject
  @Path("mission_participant/index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission_participant.index indexTpl;

  @Inject
  @Path("mission_participant/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission_participant.list listTpl;

  @Inject
  @Path("mission_participant/viewajax.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission_participant.viewajax viewAjaxTpl;

  @Inject
  @Path("mission_participant/previous.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission_participant.previous previousTPL;


  @Inject
  public MissionParticipantController(OrganizationService organizationService,IdentityManager identityManager ,IService iService){
    this.organizationService = organizationService;
    this.identityManager = identityManager;
    this.missionParticipantService = iService;

  }

  @Ajax
  @Resource
  public Response indexMP(){
    return indexTpl.with().set("states", Status.values()).ok();
  }

  public Response list(){
    String programId = loginController.getCurrentProgramId();
    List<MissionParticipant>  missionParticipants = this.missionParticipantService.getAllMissionParticipantsInProgram(programId);
    List<MissionParticipantDTO> missionParticipantDTOs = this.transfers2DTOs(missionParticipants);
    return listTpl.with().set("missionParticipantDTOs",missionParticipantDTOs).set("states", Status.values()).ok();
  }

  @View
  public Response view(String missionParticipantId){
    return this.detail(missionParticipantId,false);
  }
  @Ajax
  @Resource
  public Response loadDetail(String missionParticipantId){
    return this.detail(missionParticipantId,true);
  }
  private Response detail(String missionParticipantId,Boolean isAjax){
    MissionParticipant missionParticipant = this.missionParticipantService.getMissionParticipantById(missionParticipantId);
    if(null != missionParticipant){
      try {
        Mission mission = this.missionParticipantService.getMissionById(missionParticipant.getMission_id());
        if(null != mission){
          Identity identity = this.identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,missionParticipant.getParticipant_username(),true);
          if (null != identity){
            MissionParticipantDTO missionParticipantDTO = new MissionParticipantDTO();
            missionParticipantDTO.setId(missionParticipantId);
            missionParticipantDTO.setMission_title(mission.getTitle()+" on "+mission.getThird_part_link());
            missionParticipantDTO.setSize(missionParticipant.getSize().getLabel());
            missionParticipantDTO.setDate_submitted(Utils.convertDateFromLong(missionParticipant.getModifiedDate()));
            missionParticipantDTO.setStatus(missionParticipant.getStatus());
            missionParticipantDTO.setUrl_submitted(missionParticipant.getUrl_submitted());

            ParticipantDTO participantDTO = new ParticipantDTO();
            participantDTO.setUserName(missionParticipant.getParticipant_username());
            participantDTO.setFullName(identity.getProfile().getFullName());
            participantDTO.setUrlAvatar(identity.getProfile().getAvatarUrl());
            participantDTO.setUrlProfile(identity.getProfile().getUrl());
            participantDTO.setEmail(identity.getProfile().getEmail());
            Address address = this.missionParticipantService.getAddressById(missionParticipant.getAddress_id());
            if (null == address){
              address = new Address("","","","","","");
            }
            if (isAjax){
              return viewAjaxTpl.with().set("missionParticipantDTO",missionParticipantDTO).set("address",address).set("participantDTO",participantDTO).set("states",Status.values()).ok();
            }
          }
        }

      } catch (Exception e) {
        return  Response.ok("nok");
      }
    }
    return  Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response.Content search(String keyword,String status,String page){
    String programId = loginController.getCurrentProgramId();
    Query query = new Query(programId);
    query.setKeyword(keyword);
    query.setStatus(status);
    query.setOffset(page);
    query.setLimit(NUMBER_RECORDS);
    List<MissionParticipant>  missionParticipants = this.missionParticipantService.searchMissionParticipants(query);
    List<MissionParticipantDTO> missionParticipantDTOs = this.transfers2DTOs(missionParticipants);
    Pagination pagination = new Pagination(this.missionParticipantService.getTotalMissionParticipants(query),NUMBER_RECORDS,page);
    return listTpl.with().set("missionParticipantDTOs",missionParticipantDTOs).set("states", Status.values()).set("pagination",pagination).ok();
  }

  @Ajax
  @Resource
  public Response ajaxUpdateMPInline(String missionParticipantId,String action,String val){
    Boolean hasError = false;
    String msg = "";
    if (loginController.isShippingManager()){

      if (Status.SHIPPED.getValue() != Integer.parseInt(val)) {
        hasError = true;
        msg = "you have no rights for this change";
      }
    }else if (loginController.isValidator()){
      if (Status.SHIPPED.getValue() == Integer.parseInt(val)){
        hasError = true;
        msg = "you have no rights for this change";
      }
    }
    if (hasError){
      return Response.ok(msg);
    }
    MissionParticipant missionParticipant = this.missionParticipantService.getMissionParticipantById(missionParticipantId);
    if (null != missionParticipant){
      if (action.equals("status"))
        missionParticipant.setStatus(Status.getStatus(Integer.parseInt(val)));

      missionParticipant = this.missionParticipantService.updateMissionParticipantInProgram(loginController.getCurrentProgramId(),missionParticipant);
      if (null != missionParticipant)
        return Response.ok("ok");
    }
    return Response.ok("something went wrong");
  }

  private List<MissionParticipantDTO> transfers2DTOs(List<MissionParticipant> missionParticipants){
    List<MissionParticipantDTO> missionParticipantDTOs = new ArrayList<MissionParticipantDTO>();
    MissionParticipantDTO missionParticipantDTO;
    Mission mission;
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
            missionParticipantDTO.setParticipant_id(exoUser.getUserName());
            missionParticipantDTO.setStatus(missionParticipant.getStatus());
            missionParticipantDTO.setUrl_submitted(missionParticipant.getUrl_submitted());
            missionParticipantDTO.setDate_submitted(Utils.convertDateFromLong(missionParticipant.getModifiedDate()));
            missionParticipantDTOs.add(missionParticipantDTO);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return missionParticipantDTOs;
  }

  @Ajax
  @Resource
  public Response getPreviousMissionParticipant(String username){
    String programId = loginController.getCurrentProgramId();
    List<MissionParticipant> missionParticipants = this.missionParticipantService.getAllMissionParticipantsInProgramByParticipant(programId,username);
    List<MissionParticipantDTO> missionParticipantDTOs = new ArrayList<MissionParticipantDTO>();
    MissionParticipantDTO missionParticipantDTO;
    Mission mission;
    for (MissionParticipant missionParticipant : missionParticipants){
      mission = this.missionParticipantService.getMissionById(missionParticipant.getMission_id());
      if (null != mission){
        missionParticipantDTO = new MissionParticipantDTO();
        missionParticipantDTO.setParticipant_id(username);
        missionParticipantDTO.setId(missionParticipant.getId());
        missionParticipantDTO.setMission_title(mission.getTitle());
        missionParticipantDTO.setDate_submitted(Utils.convertDateFromLong(missionParticipant.getModifiedDate()));
        missionParticipantDTOs.add(missionParticipantDTO);
      }
    }
    return previousTPL.with().set("missionParticipantDTOs",missionParticipantDTOs).ok();
  }

  @Ajax
  @Resource
  public Response removeMissionParticipant(String username,String missionParticipantId){
    if(loginController.isAdmin()){
      if (this.missionParticipantService.removeMissionParticipantInParticipant(loginController.getCurrentProgramId(),username,missionParticipantId)){
        if (this.missionParticipantService.removeMissionParticipant(missionParticipantId)){
          return Response.ok("ok");
        }else {
          return Response.ok("Something went wrong, cannot remove this mission participant");
        }
      }else {
        return Response.ok("Something went wrong, cannot remove this mission participant in participant");
      }
    }else
      return Response.ok("you have no rights to do this task");
  }
}
