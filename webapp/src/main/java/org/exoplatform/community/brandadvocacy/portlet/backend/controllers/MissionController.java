package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.model.Priority;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by exoplatform on 10/10/14.
 */
@SessionScoped
public class MissionController {

  IService missionService;
  @Inject
  LoginController loginController;

  @Inject
  PropositionController propositionController;

  @Inject
  ManagerController managerController;

  String currentProgramId;
  String currentMissionId;
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

  public Response index(){
    if (null != loginController.getCurrentProgramId())
      return this.list("",null,null);
    else
      return indexTpl.ok();
  }
  @View
  public Response addForm(){

    return addTpl.with().set("programId",loginController.getCurrentProgramId()).set("priorities", Priority.values()).ok();
  }
  @View
  public Response editForm(String missionId){

    Mission mission =  this.missionService.getMissionById(missionId);
    if(null != mission){
      List<Proposition> propositions = this.missionService.getAllPropositions(mission.getId());
      return editTpl.with().set("priorities", Priority.values()).set("mission",mission).set("propositions",propositions).ok();
    }
    return Response.ok("mission not found").withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");
  }

  public Response list(String keyword, String size, String page){
    int _size = size != null ? Integer.parseInt(size) : 5;
    int _page = page != null ? Integer.parseInt(page) : 0;
    String programId = loginController.getCurrentProgramId();
    List<Mission> missions = this.missionService.getAllMissionsByProgramId(programId);

      return listTpl.with().set("priorities", Priority.values()).set("programId",programId).set("missions",missions).ok();
  }

  @Action
  public Response add(String title,String third_part_link,String priority,String active){

    Boolean mActive = false;
    if (null != active)
      mActive = active.equals("1") ? true:false;
    Mission mission = new Mission(loginController.getCurrentProgramId(),title);
    mission.setThird_part_link(third_part_link);
    mission.setActive(mActive);
    mission.setPriority(Priority.getPriority(Integer.parseInt(priority)));
    mission = this.missionService.addMission2Program(mission);
    if(null != mission)
      return JuZBackEndApplication_.index("mission_index");
    else
      return JuZBackEndApplication_.showError("can not add mission to program, please try later");
  }

  @Action
  public Response update(String id, String title, String third_part_link, String priority, String active){
    Mission mission  = this.missionService.getMissionById(id);
    if (null != mission){
      mission.setTitle(title);
      mission.setThird_part_link(third_part_link);
      mission.setCreatedDate(0);
      mission.setPriority(Priority.getPriority(Integer.parseInt(priority)));
      Boolean mActive = false;
      if (null != active)
        mActive = active.equals("1") ? true:false;
      mission.setActive(mActive);
      mission = this.missionService.updateMission(mission);
      if(null != mission)
        return JuZBackEndApplication_.index("mission_index");
      else
        return JuZBackEndApplication_.showError("can not update mission, please try later");
    }
    return JuZBackEndApplication_.showError("something went wrong, cannot update mission not existing");


  }
  @Action
  public Response delete(String missionId){
    this.missionService.removeMissionById(missionId);
    return JuZBackEndApplication_.index("mission_index");
  }


}

