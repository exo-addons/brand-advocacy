package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.View;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 10/12/14.
 */
public class ManagerController {

  IService managerService;
  OrganizationService organizationService;
  @Inject
  MissionController missionController;

  @Inject
  @Path("manager/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.manager.list listTpl;

  @Inject
  @Path("manager/add.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.manager.add addTpl;

  @Inject
  public ManagerController(OrganizationService organizationService,IService managerService){
    this.organizationService = organizationService;
    this.managerService = managerService;
  }

  @View
  public Response.Content list(String mid){

    List<Manager> managers = this.managerService.getAllManagers(mid);
    User exoUser;
    for (Manager manager:managers){
      try {
        exoUser = this.organizationService.getUserHandler().findUserByName(manager.getUserName());
        if(null != exoUser){
          manager.setFullName(exoUser.getFirstName() + " "+exoUser.getLastName());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return listTpl.with().set("roles",Role.values()).set("managers", managers).set("missionId",mid).ok();

  }

  @View
  public Response.Content addForm(String mid){
    return addTpl.with().set("missionId",mid).set("roles",Role.values()).ok();
  }
  @Action
  public Response add(String mid, String username, String role, String notif){
    try {
      User exoUser = this.organizationService.getUserHandler().findUserByName(username);
      if(null != exoUser){
        Boolean mNotif = false;
        if(null != notif){
          mNotif = "1".equals(notif)? true:false;
        }
        Mission mission  = this.managerService.getMissionById(mid);
        if (null != mission){
          List<Manager> managers = new ArrayList<Manager>(1);
          Manager manager = new Manager(username);
          manager.setMission_id(mission.getId());
          manager.setMissionLabelId(mission.getLabelID());
          manager.setRole(Role.getRole(Integer.parseInt(role)));
          manager.setNotif(mNotif);
          managers.add(manager);
          this.managerService.addManagers2Mission(mission.getId(),managers);
          return missionController.view(mid);
        }
      }
      else
        return Response.ok("cannot find this user");

    } catch (Exception e) {
      e.printStackTrace();
    }

    return Response.ok("something went wrong, cannot add user like a manager");

  }

  @Action
  public void update(String mid, String username, String role, String notif){
    Boolean mNotif = false;
    if(null != notif){
      mNotif = "1".equals(notif)? true:false;
    }
    Mission mission =  this.managerService.getMissionById(mid);
    if(null != mission) {

      Manager manager = this.managerService.getManager(mission.getLabelID(),username);
      if(null != manager){
        manager.setMission_id(mission.getId());
        manager.setMissionLabelId(mission.getLabelID());
        manager.setRole(Role.getRole(Integer.parseInt(role)));
        manager.setNotif(mNotif);
        this.managerService.updateManager(manager);
      }

    }
  }

  @Action
  public void delete(String mid,String username){
    Mission mission =  this.managerService.getMissionById(mid);
    if (null != mission){
      this.managerService.removeManager(mission.getLabelID(),username);
    }
  }

}
