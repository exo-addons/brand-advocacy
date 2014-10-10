package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.template.Template;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.service.IService;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by exoplatform on 10/10/14.
 */
public class MissionController {

  IService missionService;

  public MissionController(IService missionService){
    this.missionService = missionService;
  }

  @Inject
  @Path("mission/index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission.index indexTpl;

  @Inject
  @Path("mission/add.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission.add addTpl;

  @Inject
  @Path("mission/edit.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission.edit editTpl;

  @Inject
  @Path("mission/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission.list listTpl;

  @Inject
  @Path("mission/view.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.mission.view viewTpl;

  @View
  public Response.Content index(){
    return indexTpl.ok();
  }

  @View
  public Response.Content addForm(){
    return addTpl.ok();
  }

  @View
  public Response.Content editForm(String id){

    try {
      Mission mission =  this.missionService.getMissionById(id);
      if(null != mission){
        return editTpl.with().mission(mission).ok();
      }
    } catch (javax.jcr.RepositoryException e) {
      e.printStackTrace();
    }
    return Response.ok("mission not found").withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");

  }

  @View
  public Response.Content list(String keyword, String size, String page){
    int _size = size != null ? Integer.parseInt(size) : 5;
    int _page = page != null ? Integer.parseInt(page) : 0;
    return listTpl.with().missions(this.missionService.getAllMissions()).ok();
  }

  @Action
  public Response add(String title,String third_party_link){
    Mission mission = new Mission(title);
    mission.setThird_party_link(third_party_link);
    this.missionService.addMission(mission);
    return index();
  }

  @Action
  public Response update(String id, String labelID, String title, String link){
    Mission mission = new Mission();
    mission.setId(id);
    mission.setLabelID(labelID);
    mission.setTitle(title);
    mission.setThird_party_link(link);
    this.missionService.updateMission(mission);
    return index();
  }


}

