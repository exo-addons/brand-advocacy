package org.exoplatform.brandadvocacy.model;

import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;

import java.util.List;
import java.util.UUID;

/**
 * Created by exoplatform on 17/10/14.
 */
public class Program {

  private String id;
  private String labelID;
  private String title;
  private Boolean active;
  private List<Manager> managers;

  public Program(String title){
    this.setLabelID(UUID.randomUUID().toString());
    this.setTitle(title);
    this.setActive(true);
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
  public String getTitle() {
    return title;
  }
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public List<Manager> getManagers() {
    return managers;
  }

  public void setManagers(List<Manager> managers) {

    this.managers = managers;
  }
  public void checkValid() throws BrandAdvocacyServiceException{
    if (null == this.getTitle() || "".equals(this.getTitle()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PROGRAM_INVALID," progam must have title");
    else if (null == this.getLabelID() || "".equals(this.getLabelID()))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"program must have label id");
  }
  public String toString(){
    return this.getTitle();
  }
}
