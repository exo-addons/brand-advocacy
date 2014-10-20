package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import juzu.request.SecurityContext;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;

import javax.inject.Inject;
import java.util.ArrayList;
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
    this.currentProgramId = null;
    this.currentMissionId = null;
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


  public Response index(SecurityContext securityContext,String action,String programId, String missionId){
    loginController.setCurrentUserName(securityContext.getUserPrincipal().getName());

    if (null != programId){
      this.currentProgramId = programId;
      if(null == missionId){
        if (action.equals("index")){
          return this.list("",null,null);
        }else if (action.equals("add")){
          return this.addForm();
        }
      }else{
        this.currentMissionId = missionId;
        if (action.equals("edit")){
          return this.editForm();
        }
        else
          MissionController_.delete();
      }
    }
    return indexTpl.ok();

  }
  public Response addForm(){
    return addTpl.with().set("programId",this.currentProgramId).set("priorities", Priority.values()).ok();
  }
  public Response editForm(){

    Mission mission =  this.missionService.getMissionById(this.currentMissionId);
    if(null != mission){
      return editTpl.with().set("priorities", Priority.values()).set("mission",mission).ok();
    }
    return Response.ok("mission not found").withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");
  }

  public Response list(String keyword, String size, String page){
    int _size = size != null ? Integer.parseInt(size) : 5;
    int _page = page != null ? Integer.parseInt(page) : 0;
    return listTpl.with().set("priorities", Priority.values()).set("programId",this.currentProgramId).set("missions", this.missionService.getAllMissionsByProgramId(this.currentProgramId)).ok();
  }

  @Action
  public Response add(String title,String third_party_link,String priority,String active){

    Boolean mActive = false;
    if (null != active)
      mActive = active.equals("1") ? true:false;
    Mission mission = new Mission(this.currentProgramId,title);
    mission.setThird_part_link(third_party_link);
    mission.setActive(mActive);
    mission.setPriority(Priority.getPriority(Integer.parseInt(priority)));
    List<Manager> managers = new ArrayList<Manager>();
    Manager manager = new Manager(loginController.getCurrentUserName());
    managers.add(manager);
    mission.setManagers(managers);
    mission = this.missionService.addMission2Program(mission);
    if(null != mission)
      return JuZBackEndApplication_.index("index",this.currentProgramId);
    else
      return Response.ok("ERROR");
  }

  @Action
  public Response update(String id, String title, String third_party_link, String priority, String active){
    Mission mission  = this.missionService.getMissionById(id);
    if (null != mission){
      mission.setTitle(title);
      mission.setThird_part_link(third_party_link);
      mission.setCreatedDate(0);
      mission.setPriority(Priority.getPriority(Integer.parseInt(priority)));
      Boolean mActive = false;
      if (null != active)
        mActive = active.equals("1") ? true:false;
      mission.setActive(mActive);
      mission = this.missionService.updateMission(mission);
      if(null != mission)
        return JuZBackEndApplication_.index("index",this.currentProgramId,null);
      else
        return Response.ok("ERROR");
    }
    return Response.ok("something went wrong, cannot update mission not existing");


  }

  @Action
  public Response delete(){
    this.missionService.removeMissionById(this.currentMissionId);
    return JuZBackEndApplication_.index("index",this.currentProgramId,null);
  }


}

