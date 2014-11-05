package org.exoplatform.community.brandadvocacy.portlet.backend;

import juzu.FlashScoped;

import javax.inject.Named;

/**
 * Created by anhvt on 05/11/14.
 */
@Named("flash")
@FlashScoped
public class Flash {

  private String styleGeneralMenu="";
  private String styleMissionMenu="";
  private String styleMissionParticipantMenu="";

  public String getStyleGeneralMenu() {
    return styleGeneralMenu;
  }

  public void setStyleGeneralMenu(String styleGeneralMenu) {
    this.styleGeneralMenu = styleGeneralMenu;
  }

  public String getStyleMissionMenu() {
    return styleMissionMenu;
  }

  public void setStyleMissionMenu(String styleMissionMenu) {
    this.styleMissionMenu = styleMissionMenu;
  }

  public String getStyleMissionParticipantMenu() {
    return styleMissionParticipantMenu;
  }

  public void setStyleMissionParticipantMenu(String styleMissionParticipantMenu) {
    this.styleMissionParticipantMenu = styleMissionParticipantMenu;
  }
}
