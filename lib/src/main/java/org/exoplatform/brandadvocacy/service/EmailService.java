package org.exoplatform.brandadvocacy.service;

import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.mail.Message;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.services.mail.MailService;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by exoplatform on 10/28/14.
 */
public class EmailService {

  IService iService;
  IdentityManager identityManager;
  MailService exoMailService;
  final String email_gift_shipped_template="/html/email_gift_shipped_template.html";
  final String email_thankyou_template="/html/email_thankyou_template.html";
  final String email_mission_failed_template="/html/email_mission_failed_template.html";
  String remoteUrl = "";
  private String senderEmail;
  private static final Log log = ExoLogger.getLogger(EmailService.class);

  public EmailService(IService iService,IdentityManager identityManager,MailService mailService){
    this.iService = iService;
    this.identityManager = identityManager;
    this.exoMailService = mailService;
    this.setSenderEmail("Patrice Lamarque | eXo <mission-control@exoplatform.com>");
    this.remoteUrl = System.getProperty("EXO_DEPLOYMENT_URL");
    if(null == remoteUrl || "".equals(remoteUrl)){
      remoteUrl = "http://community.exoplatform.com";
    }
  }
  public String getSenderEmail() {
    return senderEmail;
  }

  public void setSenderEmail(String senderEmail) {
    this.senderEmail = senderEmail;
  }
  private String getBodyByTemplate(String fileTemplate, Map<String, String> templateProperties) {
    InputStream is = this.getClass().getResourceAsStream(fileTemplate);
    String body = null;
    try {
      body = resolveTemplate(is, templateProperties);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    return body;
  }

  private String resolveTemplate(InputStream is, Map<String, String> properties) throws FileNotFoundException {
    Scanner scanner = new Scanner(is);
    StringBuilder sb = new StringBuilder();
    try {
      while (scanner.hasNextLine()) {
        sb.append(scanner.nextLine()).append(System.getProperty("line.separator"));
      }
    } finally {
      scanner.close();
    }
    String templateContent = sb.toString();
    if (templateContent != null) {
      for (Map.Entry<String, String> property : properties.entrySet()) {
        templateContent = templateContent.replace("${" + property.getKey() + "}", property.getValue());
      }
    }
    return templateContent;
  }
  private String generateMissionParticipantUrl(String mpid){
    return remoteUrl+"/portal/intranet/brand-advocacy/?action=mp_view&id="+mpid;
  }
  private Map<String,String> getCommonMisionInfo(Mission mission, MissionParticipant missionParticipant){
    Map<String,String> infos = null;
    if (null != mission && null != missionParticipant){
      infos = new HashMap<String, String>();
      String username = missionParticipant.getParticipant_username();
      Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,username,true);
      Address address = iService.getAddressById(missionParticipant.getAddress_id());
      infos.put("username",username);
      String fullName = "",email = "";
      if (null != identity){
        fullName = identity.getProfile().getFullName();
        email = identity.getProfile().getEmail();
      }
      infos.put("fullname",fullName);
      infos.put("email",email);
      String strAdrs = "";
      String phone = "";
      if (null != address){
        strAdrs = address.toString();
        phone = address.getPhone();
      }
      infos.put("phone",phone);
      infos.put("address",strAdrs);
      infos.put("mission_title",mission.getTitle());
      infos.put("status",missionParticipant.getStatus().getLabel());
      infos.put("mpid",missionParticipant.getId());
    }
//    log.info(" admin email body "+infos.toString());
    return infos;
  }
  private String getCommonBody(Mission mission, MissionParticipant missionParticipant){
    String body = null;
    Map<String,String> infos = this.getCommonMisionInfo(mission,missionParticipant);
    if(null != infos){
      StringBuilder stringBuilder = new StringBuilder("Mission: ").append(infos.get("mission_title"));
      stringBuilder.append("<br/> Status: ").append(infos.get("status"));
      stringBuilder.append("<br/> Username: ").append(infos.get("username"));
      if(!"".equals(infos.get("email"))){
        stringBuilder.append("<br/> Full name on community: ").append(infos.get("fullname"));
        stringBuilder.append("<br/> Email: ").append(infos.get("email"));
        stringBuilder.append("<br/> Phone: ").append(infos.get("phone"));
        stringBuilder.append("<br/> Shipping address : ").append(infos.get("address"));
      }
      stringBuilder.append("<br/> url: ").append(this.generateMissionParticipantUrl(infos.get("mpid")));
      body = stringBuilder.toString();
    }
    return body;
  }
  private Map<String,String> pushEmailInfo(String subject, String body){
    Map<String,String> emailInfo = new HashMap<String, String>();
    emailInfo.put("subject",subject);
    emailInfo.put("body",body);
    return emailInfo;
  }
  private Map<String,String> getEmailInfoWait4Validation(Mission mission, MissionParticipant missionParticipant){
    String subject = "A new mission is waiting for validation";
    String body = this.getCommonBody(mission,missionParticipant);
    if (null != body){
      return this.pushEmailInfo(subject,body);
    }
    return null;
  }
  private Map<String,String> getEmailInfoWait4Shipping(Mission mission, MissionParticipant missionParticipant){
    String subject = "A mission has been validated and waiting for shipment";
    String body = this.getCommonBody(mission,missionParticipant);
    if (null != body){
      return this.pushEmailInfo(subject,body);
    }
    return null;
  }
  private Map<String,String> getEmailInfoRejected(Mission mission, MissionParticipant missionParticipant){
    String subject = "A mission has been rejected";
    String body = this.getCommonBody(mission,missionParticipant);
    if (null != body){
      return this.pushEmailInfo(subject,body);
    }
    return null;

  }
  private Map<String,String> getEmailInfoAllCompleted(){
    String subject = "All missions have been completed once";
    String body = "You might want to add new mission : link to mission tab ";
    body +=remoteUrl+"/portal/intranet/brand-advocacy/?action=mission";
    return this.pushEmailInfo(subject,body);
  }
  private Map<String,String> generateAdminEmailInfoByStatus(Mission mission, MissionParticipant missionParticipant){

    if (Status.WAITING_FOR_VALIDATE.getLabel().equals(missionParticipant.getStatus().getLabel())){
      return this.getEmailInfoWait4Validation(mission, missionParticipant);
    }else if (Status.VALIDATED.getLabel().equals(missionParticipant.getStatus().getLabel())){
      return this.getEmailInfoWait4Shipping(mission, missionParticipant);
    }else if (Status.REJECTED.getLabel().equals(missionParticipant.getStatus().getLabel())){
      return this.getEmailInfoRejected(mission, missionParticipant);
    }
    return null;
  }

  private Map<String,String> getEmailInfoGiftShipped(Program program,Identity identity){
    String subject = "Your eXo Tshirt has been shipped";
    String remoteImgUrl =  remoteUrl;
    remoteImgUrl+="/brand-advocacy-webapp/img/email";
    Map<String, String> props = new HashMap<String, String>();
    props.put("user.name", identity.getProfile().getFullName());
    props.put("imgUrlBase",remoteImgUrl);
    props.put("program.title",program.getTitle());
    String body = this.getBodyByTemplate(email_gift_shipped_template, props);
    if (null != body){
      return this.pushEmailInfo(subject,body);
    }
    return null;
  }
  private Map<String,String> getEmailInfoThankyou(String fullName){
    String subject = "Mission submitted !";
    String remoteImgUrl =  remoteUrl;
    remoteImgUrl+="/brand-advocacy-webapp/img/email";
    Map<String, String> props = new HashMap<String, String>();
    props.put("user.name", fullName);
    props.put("imgUrlBase",remoteImgUrl);
    String body = this.getBodyByTemplate(email_thankyou_template, props);
    if (null != body){
      return this.pushEmailInfo(subject,body);
    }
    return null;
  }
  private Map<String,String> getEmailInfoMissionFailed(Program program,Identity identity, String note){
    String  subject = program.getTitle()+" - Mission failed";
    String remoteImgUrl =  remoteUrl;
    remoteImgUrl+="/brand-advocacy-webapp/img/email";
    Map<String, String> props = new HashMap<String, String>();
    props.put("user.name", identity.getProfile().getFullName());
    props.put("imgUrlBase",remoteImgUrl);
    props.put("program.title",program.getTitle());
    props.put("reason",note);
    String body = this.getBodyByTemplate(email_mission_failed_template, props);
    if (null != body){
      return this.pushEmailInfo(subject,body);
    }
    return null;
  }

  private Map<String,String> generateParticipantEmailInfoByStatus(Program program, Identity identity,MissionParticipant missionParticipant,String note){
    //log.info( " send to participant with note "+note);
    if(Status.WAITING_FOR_VALIDATE.getLabel().equals(missionParticipant.getStatus().getLabel())){
      return this.getEmailInfoThankyou(identity.getProfile().getFullName());
    }else if (Status.SHIPPED.getLabel().equals(missionParticipant.getStatus().getLabel())){
      return this.getEmailInfoGiftShipped(program,identity);
    } else if (Status.REJECTED.getLabel().equals(missionParticipant.getStatus().getLabel())){
      return this.getEmailInfoMissionFailed(program,identity,note);
    }
    return null;
  }
  public void sendNotif2Managers(MissionParticipant missionParticipant){
    Mission mission = this.iService.getMissionById(missionParticipant.getMission_id());
    if (null != mission){
      Map<String,String> emailInfo = this.generateAdminEmailInfoByStatus(mission, missionParticipant);
      if (null != emailInfo){
        List<Manager> managers = this.iService.getAllManagersInProgram(mission.getProgramId());
        Identity eXoIdentity;
        Message message;
        for (Manager manager:managers){
          if (manager.getNotif() && this.canSend2Manager(manager.getRole(),missionParticipant.getStatus())){
            eXoIdentity = this.identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, manager.getUserName(), true);
            if (null != eXoIdentity){
              log.info("sending email "+eXoIdentity.getProfile().getEmail()+" role "+manager.getRoleLabel()+" subject"+emailInfo.get("subject"));
              message = new Message();
              message.setFrom(this.getSenderEmail());
              message.setTo(eXoIdentity.getProfile().getEmail());
              message.setSubject(emailInfo.get("subject"));
              message.setBody(emailInfo.get("body"));
              message.setMimeType("text/html");
              try {
                this.exoMailService.sendMessage(message);
              } catch (Exception e) {
                log.error("cannot send referral email "+e.getMessage());
              }
            }
          }
        }
      }
    }
  }

  public void sendNotif2Participant(MissionParticipant missionParticipant,String note){
    if (null != missionParticipant){
      if (canSend2Participant(missionParticipant.getStatus())){
        Mission mission = this.iService.getMissionById(missionParticipant.getMission_id());
        if (null != mission){
          Program program = this.iService.getProgramById(mission.getProgramId());
          if (null != program){
            String participantId = missionParticipant.getParticipant_username();
            Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,participantId,true);
            if (null != identity){
              Map<String,String> emailInfo = this.generateParticipantEmailInfoByStatus(program,identity,missionParticipant,note);
              if (null != emailInfo){
                log.info("sending email to participant "+participantId);
                Message message = new Message();
                message.setFrom(this.getSenderEmail());
                message.setTo(identity.getProfile().getEmail());
                message.setSubject(emailInfo.get("subject"));
                message.setBody(emailInfo.get("body"));
                message.setMimeType("text/html");
                try {
                  this.exoMailService.sendMessage(message);
                } catch (Exception e) {
                  log.error("cannot send referral email "+e.getMessage());
                }
              }
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
  private Boolean canSend2Participant(Status status){
    if (Status.OPEN.getValue() == status.getValue() || Status.INPROGRESS.getValue() == status.getValue())
      return false;

    return true;
  }
  public void sendNotifAlmostMissionDone2Managers(String programId){
    List<Manager> managers = this.iService.getAllManagersInProgram(programId);
    Identity eXoIdentity;
    Map<String,String> emailInfo = this.getEmailInfoAllCompleted();
    if (null != emailInfo){
      Message message;
      for (Manager manager:managers){
        if (manager.getNotif() && manager.getRole().getLabel().equals(Role.Admin.getLabel())){
          eXoIdentity = this.identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, manager.getUserName(), true);
          if (null != eXoIdentity){
            log.info("sending email to "+eXoIdentity.getProfile().getEmail()+" role "+manager.getRoleLabel());
            message = new Message();
            message.setFrom(this.getSenderEmail());
            message.setTo(eXoIdentity.getProfile().getEmail());
            message.setSubject(emailInfo.get("subject"));
            message.setBody(emailInfo.get("body"));
            message.setMimeType("text/html");
            try {
              this.exoMailService.sendMessage(message);
            } catch (Exception e) {
              log.error("cannot send referral email "+e.getMessage());
            }
          }
        }
      }
    }
  }
}
