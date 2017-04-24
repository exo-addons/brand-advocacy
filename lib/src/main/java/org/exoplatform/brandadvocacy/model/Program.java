package org.exoplatform.brandadvocacy.model;

import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.json.JSONObject;

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
  private JSONObject settings;
  public static final String banner_url_setting_key = "banner_url";
  public static final String email_sender_setting_key = "email_sender";
  public static final String MANAGER_NAME_KEY = "manager_name";
  public static final String MANAGER_TITLE_KEY = "manager_title";
  public static final String size_out_of_stock_setting_key = "size_out_of_stock";
  public static final String save_user_data_endpoint_setting_key = "save_user_data_endpoint";
  public static final String save_user_data_request_method_setting_key = "save_user_data_request_method";
  public static final String save_user_data_endpoint_token_setting_key = "save_user_data_endpoint_token";

  public static final String FACEBOOK_OAUTH_URL_SETTING_KEY = "facebook_oauth_url";
  public static final String GOOGLE_OAUTH_URL_SETTING_KEY = "google_oauth_url";
  public static final String LINKEDIN_OAUTH_URL_SETTING_KEY = "linkedin_oauth_url";

  public static final String FACEBOOK_SHARE_URL_SETTING_KEY = "facebook_share_url";
  public static final String GOOGLE_SHARE_URL_SETTING_KEY = "google_share_url";
  public static final String LINKEDIN_SHARE_URL_SETTING_KEY = "linkedin_share_url";

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

  public JSONObject getSettings() {
    return settings;
  }

  public void setSettings(JSONObject settings) {
    this.settings = settings;
  }
}
