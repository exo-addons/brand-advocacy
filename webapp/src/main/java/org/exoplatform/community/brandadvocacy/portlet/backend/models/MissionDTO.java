package org.exoplatform.community.brandadvocacy.portlet.backend.models;

import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Proposition;

import java.util.List;

/**
 * Created by exoplatform on 31/10/14.
 */
public class MissionDTO {

  private String id;
  private String programId;
  private String title;
  private String third_part_link;
  private long priority;
  private Boolean active;
  List<Proposition> propositions;
  private long createdDate;
  private long modifiedDate;
  public MissionDTO(String programId,String id,String title,long priority,String third_part_link,Boolean isActive){
    this.setProgramId(programId);
    this.setId(id);
    this.setTitle(title);
    this.setPriority(priority);
    this.setThird_part_link(third_part_link);
    this.setActive(isActive);
  }
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProgramId() {
    return programId;
  }

  public void setProgramId(String programId) {
    this.programId = programId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getThird_part_link() {
    return third_part_link;
  }

  public void setThird_part_link(String third_part_link) {
    this.third_part_link = third_part_link;
  }

  public long getPriority() {
    return priority;
  }

  public void setPriority(long priority) {
    this.priority = priority;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public List<Proposition> getPropositions() {
    return propositions;
  }

  public void setPropositions(List<Proposition> propositions) {
    this.propositions = propositions;
  }

  public long getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(long createdDate) {
    this.createdDate = createdDate;
  }

  public long getModifiedDate() {
    return modifiedDate;
  }

  public void setModifiedDate(long modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public boolean canActivate(){
    if (null != this.getPropositions() && this.getPropositions().size() > 0)
      return true;
    return false;
  }


}
