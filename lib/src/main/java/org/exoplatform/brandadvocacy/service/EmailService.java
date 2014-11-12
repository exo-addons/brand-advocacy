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
  String remoteUrl = "";
  private static final Log log = ExoLogger.getLogger(EmailService.class);

  public EmailService(IService iService,IdentityManager identityManager,MailService mailService){
    this.iService = iService;
    this.identityManager = identityManager;
    this.exoMailService = mailService;
    this.remoteUrl = System.getProperty("EXO_DEPLOYMENT_URL");
    if(null == remoteUrl || "".equals(remoteUrl)){
      remoteUrl = "http://community.exoplatform.com";
    }
    remoteUrl +="/brand-advocacy-webapp/img/email";
  }

  private String getBodyByTemplate(String fileTemplate, Map<String, String> templateProperties) {
    InputStream is = this.getClass().getResourceAsStream(fileTemplate);
    String body;
    try {
      body = resolveTemplate(is, templateProperties);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
      body = "";
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
  private String generateBodyMissionSubmitted(String missionId){
    String body = null;
    Mission mission = this.iService.getMissionById(missionId);
    if (null != mission){
      body = "Mission " + mission.getTitle();
      body +=" has been submitted ";
    }

    return body;
  }
  private String generateBodyGiftShipped(String fullName){
    Map<String, String> props = new HashMap<String, String>();
    props.put("user.name", fullName);
    props.put("imgUrlBase",remoteUrl);
    return this.getBodyByTemplate(email_gift_shipped_template, props);
  }
  private String generateBodyThankyou(String fullName){
    Map<String, String> props = new HashMap<String, String>();
    props.put("user.name", fullName);
    props.put("imgUrlBase",remoteUrl);
    return this.getBodyByTemplate(email_thankyou_template, props);
  }
  private String generateBodyMissionParticipantByStatus(Mission mission,MissionParticipant missionParticipant) {
    String body = null;

    body = "there is new change on referral program ";
    body +="view on <b>?action=mp_view&id="+missionParticipant.getId();
    return body;
  }
  public void sendNotif2Managers(MissionParticipant missionParticipant){

    if (null != missionParticipant){
      Mission mission = this.iService.getMissionById(missionParticipant.getMission_id());
      if (null != mission && mission.getActive()){
        List<Manager> managers = this.iService.getAllManagersInProgram(mission.getProgramId());
        Identity eXoIdentity;
        String body = this.generateBodyMissionParticipantByStatus(mission,missionParticipant);
        if (null != body){
          Message message;
          for (Manager manager:managers){
            if (manager.getNotif() && this.canSend2Manager(manager.getRole(),missionParticipant.getStatus())){
              eXoIdentity = this.identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, manager.getUserName(), true);
              if (null != eXoIdentity){
                log.info("sending email to "+eXoIdentity.getProfile().getEmail()+" role "+manager.getRoleLabel());
                message = new Message();
                message.setTo(eXoIdentity.getProfile().getEmail());
                message.setSubject("new message from referral program");
                message.setBody(body);
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

  public void sendNotif2Participant(MissionParticipant missionParticipant){
    if (null != missionParticipant){
      String subjectSending = this.canSend2Participant(missionParticipant.getStatus().getValue());
      if (null != subjectSending){
        String participantId = missionParticipant.getParticipant_username();
        Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME,participantId,true);
        if (null != identity){
          String subject="Referral Program";
          String body = "";
          if (subjectSending.equals(Status.VALIDATED.getLabel())){
            subject = "Your mission has beed validated";
            body = this.generateBodyThankyou(identity.getProfile().getFullName());
          }else if (subjectSending.equals(Status.SHIPPED.getLabel())){
            subject= "Your eXo t-shirt has been shipped !";
            body = this.generateBodyGiftShipped(identity.getProfile().getFullName());
          }
          log.info("sending email to participant "+participantId);
          Message message = new Message();
          message.setTo(identity.getProfile().getEmail());
          message.setSubject(subject);
          message.setBody(body);
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
  private String canSend2Participant(int missionParticipantStatus){
    if (missionParticipantStatus == Status.SHIPPED.getValue()){
      return Status.SHIPPED.getLabel();
    }else if (missionParticipantStatus == Status.VALIDATED.getValue()){
      return Status.VALIDATED.getLabel();
    }
    return null;
  }
}
