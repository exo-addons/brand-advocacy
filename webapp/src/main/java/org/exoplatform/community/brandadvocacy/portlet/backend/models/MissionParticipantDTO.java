package org.exoplatform.community.brandadvocacy.portlet.backend.models;


import org.exoplatform.brandadvocacy.model.Status;

/**
 * Created by exoplatform on 14/10/14.
 */
public class MissionParticipantDTO {
  private String id;
  private String mission_id;
  private String mission_title;
  private String participant_fullName;
  private String participant_id;
  private String url_submitted;
  private Status status;
  private String size;
  private String date_submitted;
  private AddressDTO addressDTO;

  public MissionParticipantDTO(){
    this.setAddressDTO(null);
    this.setMission_id("");
  }
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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }


  public String getMission_title() {
    return mission_title;
  }

  public void setMission_title(String mission_title) {
    this.mission_title = mission_title;
  }

  public String getDate_submitted() {
    return date_submitted;
  }

  public void setDate_submitted(String date_submitted) {
    this.date_submitted = date_submitted;
  }

  public String getParticipant_id() {
    return participant_id;
  }

  public void setParticipant_id(String participant_id) {
    this.participant_id = participant_id;
  }

  public AddressDTO getAddressDTO() {
    return addressDTO;
  }

  public void setAddressDTO(AddressDTO addressDTO) {
    this.addressDTO = addressDTO;
  }
  public String getFullAddresses(){
    if (null != this.getAddressDTO()){
      return this.getAddressDTO().getFullAddresses();
    }
    return "not provided yet";
  }
}
