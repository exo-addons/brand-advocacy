package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;

import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.model.Priority;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.Flash;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.MissionDTO;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
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
  Flash flash;
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

    return addTpl.with().set("programId",loginController.getCurrentProgramId()).ok();
  }
  @View
  public Response editForm(String missionId){
    this.flash.setStyleMissionMenu("active");
    Mission mission =  this.missionService.getMissionById(missionId);
    if(null != mission){
      MissionDTO missionDTO = new MissionDTO(mission.getProgramId(),mission.getId(),mission.getTitle(),mission.getPriority(),mission.getThird_part_link(),mission.getActive());
      List<Proposition> propositions = this.missionService.getAllPropositions(mission.getId(),null);
      return editTpl.with().set("priorities", Priority.values()).set("mission",missionDTO).set("propositions",propositions).ok();
    }
    return Response.ok("mission not found").withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");
  }

  public Response list(String keyword, String size, String page){
    String alertPriority = "";
    int _size = size != null ? Integer.parseInt(size) : 5;
    int _page = page != null ? Integer.parseInt(page) : 0;
    String programId = loginController.getCurrentProgramId();
    List<Mission> missions = this.missionService.searchMission(new Query(programId));
    List<MissionDTO> missionDTOs = new LinkedList<MissionDTO>();
    MissionDTO missionDTO;
    int totalPriority = 0;
    for (Mission mission:missions){
      missionDTO = new MissionDTO(mission.getProgramId(),mission.getId(),mission.getTitle(),mission.getPriority(),mission.getThird_part_link(),mission.getActive());
      missionDTO.setPropositions(this.missionService.getAllPropositions(mission.getId(),true));
      missionDTOs.add(missionDTO);
      if (mission.getActive()){
        totalPriority +=(int)mission.getPriority();
      }
    }
    if (totalPriority > 100){
      alertPriority = "attention, the total priority is exceeded";
    }
    return listTpl.with().set("priorities", Priority.values()).set("programId",programId).set("missions",missionDTOs).set("alertPriority",alertPriority).ok();
  }

  @Action
  public Response add(String title,String third_part_link,String priority,String active){

    Boolean mActive = false;
    if (null != active)
      mActive = active.equals("true") ? true:false;
    Mission mission = new Mission(loginController.getCurrentProgramId(),title);
    mission.setThird_part_link(third_part_link);
    mission.setActive(mActive);
    mission.setPriority(Integer.parseInt(priority));
    mission = this.missionService.addMission2Program(mission);
    if(null != mission)
      return JuZBackEndApplication_.index("mission_index");
    else
      return JuZBackEndApplication_.showError("can not add mission to program, please try later");
  }

  @Action
  public Response update(String id, String title, String third_part_link, String priority, String mission_active){
    Mission mission  = this.missionService.getMissionById(id);
    if (null != mission){
      mission.setTitle(title);
      mission.setThird_part_link(third_part_link);
      mission.setCreatedDate(0);
      mission.setPriority(Integer.parseInt(priority));
      Boolean mActive = false;
      if (null != mission_active)
        mActive = mission_active.equals("true") ? true:false;
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

  @Ajax
  @Resource
  public Response ajaxUpdateInline(String missionId,String action,String val){
    Boolean error = false;
    Mission mission  = this.missionService.getMissionById(missionId);
    JSONObject jsonObject = new JSONObject();
    boolean doUpdate = true;
    String errorMsg = "";
    if (null != mission){
      if (action.equals("priority")){
        mission.setPriority(Integer.parseInt(val));
      }else if (action.equals("active") ){
        Boolean mActive = false;
        mActive = val.equals("true") ? true : false;
        mission.setActive(mActive);
        if (this.missionService.getAllPropositions(missionId,true).size() == 0){
          doUpdate = false;
        }
      }
      if (doUpdate){
        mission = this.missionService.updateMission(mission);
        List<Mission> missions = this.missionService.getAllMissionsByProgramId(this.loginController.getCurrentProgramId(),true);
        if (null != missions){
          int totalPrio = 0;
          for (Mission m:missions){
            totalPrio +=(int)m.getPriority();
          }
          if (totalPrio > 100){
            error = true;
            errorMsg = "mission-prio-exceeded";
          }
        }
      }else {
        errorMsg = "You cannot activate this mission, as there is no proposition";
        error = true;
      }
    }
    if (null == mission){
      errorMsg = "Something went wrong, cannot update mission";
      error = true;
    }
    try {
      jsonObject.put("error",error);
      jsonObject.put("msg",errorMsg);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return Response.ok(jsonObject.toString());
  }
}

