package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Path;
import juzu.Resource;
import juzu.Response;
import juzu.SessionScoped;
import juzu.plugin.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Priority;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.model.Query;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.MissionDTO;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
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

  @Ajax
  @Resource
  public Response indexMission(){
    if (null != loginController.getCurrentProgramId())
      return this.list("",null,null);
    else
      return Response.ok("There is no program yet");
  }
  @Ajax
  @Resource
  public Response addForm(){
    return addTpl.with().ok();
  }

  @Ajax
  @Resource
  public Response editForm(String missionId){
    Mission mission =  this.missionService.getMissionById(missionId);
    if(null != mission){
      MissionDTO missionDTO = new MissionDTO(mission.getProgramId(),mission.getId(),mission.getTitle(),mission.getPriority(),mission.getThird_part_link(),mission.getActive());
      List<Proposition> propositions = this.missionService.getAllPropositions(mission.getId(),null);
      return editTpl.with().set("priorities", Priority.values()).set("mission",missionDTO).set("propositions",propositions).ok();
    }
    return Response.ok("nok");
  }

  public Response list(String keyword, String size, String page){
    String alertPriority = "";
    String programId = loginController.getCurrentProgramId();
    List<Mission> missions = this.missionService.searchMission(new Query(programId));
    List<MissionDTO> missionDTOs = new LinkedList<MissionDTO>();
    MissionDTO missionDTO;
    int totalPriority = 0;
    for (Mission mission:missions){
      missionDTO = new MissionDTO(mission.getProgramId(),mission.getId(),mission.getTitle(),mission.getPriority(),mission.getThird_part_link(),mission.getActive());
      missionDTO.setPropositions(this.missionService.getAllPropositions(mission.getId(),true));
      if (mission.getActive()){
        totalPriority +=(int)mission.getPriority();
      }
      missionDTOs.add(missionDTO);
    }
/*    if (totalPriority > 100){
      alertPriority = "attention, the total priority is exceeded";
    }*/
    return listTpl.with().set("priorities", Priority.values()).set("missions",missionDTOs).set("alertPriority",alertPriority).set("totalPriority",totalPriority).ok();
  }

  @Ajax
  @Resource
  public Response addMission(String title,String third_part_link,String priority){

    Boolean mActive = false;
    Mission mission = new Mission(loginController.getCurrentProgramId(),title);
    mission.setThird_part_link(third_part_link);
    mission.setActive(mActive);
    mission.setPriority(Integer.parseInt(priority));
    mission = this.missionService.addMission2Program(mission);
    if(null != mission)
      return Response.ok(mission.getId());
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response updateMission(String id, String title, String third_part_link, String mission_active){
    Mission mission  = this.missionService.getMissionById(id);
    if (null != mission){
      mission.setTitle(title);
      mission.setThird_part_link(third_part_link);
      mission.setCreatedDate(0);
      Boolean mActive = false;
      if (null != mission_active)
        mActive = mission_active.equals("true") ? true:false;
      mission.setActive(mActive);
      mission = this.missionService.updateMission(mission);
      if(null != mission)
        return Response.ok("ok");
    }
    return Response.ok("nok");
  }
  @Ajax
  @Resource
  public Response deleteMission(String missionId){
    if(this.missionService.removeMissionById(missionId))
      return Response.ok("ok");
    return Response.ok("nok");
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
        if (mActive && this.missionService.getAllPropositions(missionId,true).size() == 0){
          doUpdate = false;
        }
      }
      if (doUpdate){
        mission = this.missionService.updateMission(mission);
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
      jsonObject.put("mid",missionId);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return Response.ok(jsonObject.toString());
  }

  @Ajax
  @Resource
  public Response updateMissionPriority(String missionId, String priority){
    Mission mission  = this.missionService.getMissionById(missionId);
    if (null != mission){
      int prio = Integer.parseInt(priority);
      if (mission.getPriority() != prio){
          mission.setPriority(prio);
          if(null != this.missionService.updateMission(mission)){
              return Response.ok("Priority has been updated");
          }else{
            return Response.ok("Something went wrong, cannot update priority");
          }
      }else
        return Response.ok("Priority has been updated");
    }else{
      return Response.ok("Mission no longer exist");
    }
  }
}

