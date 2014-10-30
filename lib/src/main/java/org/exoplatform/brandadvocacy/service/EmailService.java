package org.exoplatform.brandadvocacy.service;

import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;

import java.util.List;

/**
 * Created by exoplatform on 10/28/14.
 */
public class EmailService {

  IService iService;
  IdentityManager identityManager;
  private static final Log log = ExoLogger.getLogger(EmailService.class);
  public EmailService(IService iService,IdentityManager identityManager){
    this.iService = iService;
    this.identityManager = identityManager;
  }

  private String generateBodyMissionSubmitted(String missionId){
    String body = null;
    Mission mission = this.iService.getMissionById(missionId);
    if (null != mission){
      body = "Mission " + mission.getTitle();
      body +=" has been submitted ";

    }

    return body;
  }
  private String generateBodyMissionParticipantByStatus(Mission mission,MissionParticipant missionParticipant) {
    String body = null;
    Status status = missionParticipant.getStatus();

    return body;
  }
  public void sendNotif2Manager(String missionParticipantId){

    MissionParticipant missionParticipant = this.iService.getMissionParticipantById(missionParticipantId);
    if (null != missionParticipant){
      Mission mission = this.iService.getMissionById(missionParticipant.getMission_id());
      if (null != mission && mission.getActive()){
        List<Manager> managers = this.iService.getAllManagersInProgram(mission.getProgramId());
        Identity eXoIdentity;
        String body = this.generateBodyMissionParticipantByStatus(mission,missionParticipant);
        if (null != body){
          for (Manager manager:managers){
            if (manager.getNotif() && this.canSend2Manager(manager.getRole(),missionParticipant.getStatus())){
              eXoIdentity = this.identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, manager.getUserName(), true);

            }
          }
        }
      }

    }

  }

  private Boolean canSend2Manager(Role role,Status status){
    if (role.getValue() == Role.Admin.getValue())
      return true;
    else if (role.getValue() == Role.Validator.getValue()){
      if (status.getValue() == Status.WAITING_FOR_VALIDATE.getValue())
        return true;
    }
    else if (role.getValue() == Role.Shipping_Manager.getValue()){
      if (status.getValue() == Status.VALIDATED.getValue())
        return true;
    }
    return false;
  }
}
