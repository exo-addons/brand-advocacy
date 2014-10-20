package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.View;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Priority;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.service.IService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 10/10/14.
 */
public class MissionController {

  IService missionService;
  @Inject
  LoginController loginController;

  @Inject
  PropositionController propositionController;

  @Inject
  ManagerController managerController;

  @Inject
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

  public Response index(){
    return Response.ok(" mission index page");
  }

  @View
  public Response.Content addForm(){
    return addTpl.with().set("priorities", Priority.values()).ok();
  }

  @View
  public Response.Content editForm(String id){

    Mission mission =  this.missionService.getMissionById(id);
    if(null != mission){
      return editTpl.with().set("priorities", Priority.values()).set("mission",mission).ok();
    }
    return Response.ok("mission not found").withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");
  }

  @View
  public Response.Content list(String keyword, String size, String page){
    int _size = size != null ? Integer.parseInt(size) : 5;
    int _page = page != null ? Integer.parseInt(page) : 0;
    return listTpl.with().set("priorities", Priority.values()).set("missions", this.missionService.getAllMissions()).ok();
  }

  @View
  public Response.Content view(String id){

      Mission mission = this.missionService.getMissionById(id);
      if(null != mission){
        List<Proposition> propositions = this.missionService.getPropositionsByMissionId(id);
        mission.setPropositions(propositions);
        return viewTpl.with().set("priorities", Priority.values()).set("mission",mission).ok();
      }
    return Response.ok("something went wrong");
  }

  @Action
  public Response add(String title,String third_party_link,String priority,String active){
    Boolean mActive = false;
    if (null != active)
      mActive = active.equals("1") ? true:false;
    Mission mission = new Mission(title);
    mission.setThird_part_link(third_party_link);
    mission.setActive(mActive);
    mission.setPriority(Priority.getPriority(Integer.parseInt(priority)));
    List<Manager> managers = new ArrayList<Manager>();
    Manager manager = new Manager(loginController.getCurrentUserName());
    managers.add(manager);
    mission.setManagers(managers);
    this.missionService.addMission(mission);
    return MissionController_.list("",null,null);
  }

  @Action
  public Response update(String id, String labelID, String title, String third_party_link, String priority, String active){
    Mission mission  = this.missionService.getMissionById(id);
    if (null != mission){
      mission.setLabelID(labelID);
      mission.setTitle(title);
      mission.setThird_part_link(third_party_link);
      mission.setCreatedDate(0);
      mission.setPriority(Priority.getPriority(Integer.parseInt(priority)));
      Boolean mActive = false;
      if (null != active)
        mActive = active.equals("1") ? true:false;
      mission.setActive(mActive);
      this.missionService.updateMission(mission);
      return MissionController_.list("",null,null);
    }
    return Response.ok("something went wrong, cannot update mission not existing");


  }

  @Action
  public Response delete(String id){
    this.missionService.removeMission(id);
    return MissionController_.list(null,null,null);
  }


}

