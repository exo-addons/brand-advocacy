package org.exoplatform.community.brandadvocacy.portlet.backend.models;


/**
 * Created by exoplatform on 14/10/14.
 */
public class MissionParticipantDTO {
  private String id;
  private String mission_id;
  private String participant_fullName;
  private String url_submitted;
  private String status;
  private String size;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMission_id() {
    return mission_id;
  }

  public void setMission_id(String mission_id) {
    this.mission_id = mission_id;
  }

  public String getParticipant_fullName() {
    return participant_fullName;
  }

  public void setParticipant_fullName(String participant_fullName) {
    this.participant_fullName = participant_fullName;
  }

  public String getUrl_submitted() {
    return url_submitted;
  }

  public void setUrl_submitted(String url_submitted) {
    this.url_submitted = url_submitted;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }



}
