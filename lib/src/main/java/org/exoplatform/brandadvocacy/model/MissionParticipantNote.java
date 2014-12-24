package org.exoplatform.brandadvocacy.model;

import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;

import java.util.UUID;

/**
 * Created by exoplatform on 23/12/14.
 */
public class MissionParticipantNote {
  private String id;
  private String missionParticipantId;
  private NoteType type;
  private String author;
  private String content;
  private String labelID;

  public MissionParticipantNote(){

  }
  public MissionParticipantNote(String missionParticipantId){
    this.setLabelID(UUID.randomUUID().toString());
    this.setMissionParticipantId(missionParticipantId);
    this.setType(NoteType.AdminComment);
  }

  public String getMissionParticipantId() {
    return missionParticipantId;
  }

  public void setMissionParticipantId(String missionParticipantId) {
    this.missionParticipantId = missionParticipantId;
  }


  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
  public void checkValid() throws BrandAdvocacyServiceException{
    if (null == this.getMissionParticipantId() || "".equals(this.getMissionParticipantId()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"mission participant note must have mp id");
    if (null == this.getContent() || "".equals(this.getContent()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.NOTE_MP_INVALID,"mp note must have message");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabelID() {
    return labelID;
  }

  public void setLabelID(String labelID) {
    this.labelID = labelID;
  }

  public NoteType getType() {
    return type;
  }

  public void setType(NoteType type) {
    this.type = type;
  }
}

