package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Role;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.community.brandadvocacy.portlet.backend.models.ManagerDTO;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.social.core.identity.model.Profile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by exoplatform on 10/12/14.
 */
@SessionScoped
public class ManagerController {


  IService jcrService;
  OrganizationService organizationService;

  @Inject
  LoginController loginController;
  @Inject
  @Path("manager/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.manager.list listTpl;
  @Inject
  @Path("manager/search.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.manager.search searchTpl;

  @Inject
  public ManagerController(OrganizationService organizationService,IService iService){
    this.organizationService = organizationService;
    this.jcrService = iService;
  }

  @Ajax
  @Resource
  public Response listProgramManagers(){
    String programId = loginController.getCurrentProgramId();
    if (null != programId){
      List<Manager> managers = this.jcrService.getAllManagersInProgram(programId);
      User exoUser;
      List<ManagerDTO> managerDTOs = new ArrayList<ManagerDTO>(managers.size());
      ManagerDTO managerDTO;
      for (Manager manager:managers){
        try {
          exoUser = this.organizationService.getUserHandler().findUserByName(manager.getUserName());
          if(null != exoUser){
            managerDTO = new ManagerDTO(manager.getUserName());
            managerDTO.setFullName(exoUser.getFirstName() + " "+exoUser.getLastName());
            managerDTO.setRole(manager.getRole());
            managerDTO.setNotif(manager.getNotif());
            managerDTOs.add(managerDTO);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return listTpl.with().set("roles", Role.values()).set("managers",managerDTOs).set("currentUser",loginController.getCurrentUserName()).ok();
    }
    else
      return Response.ok("nok");


  }

  @Ajax
  @Resource
  public Response add2Program(String username, String role, String notif){
    try {
      String progamId = loginController.getCurrentProgramId();
      User exoUser = this.organizationService.getUserHandler().findUserByName(username);
      if(null != exoUser){
        Boolean mNotif = false;
        if(null != notif){
          mNotif = "true".equals(notif)? true:false;
        }
        Program program = this.jcrService.getProgramById(progamId);
        if (null != program){
          Manager manager = new Manager(username);
          manager.setParentId(program.getId());
          manager.setRole(Role.getRole(Integer.parseInt(role)));
          manager.setNotif(mNotif);
          manager = this.jcrService.addManager2Program(manager);
          if (null != manager)
            return Response.ok("ok");
        }
      }
      else
        return Response.ok("cannot find this user");

    } catch (Exception e) {
      e.printStackTrace();
    }
    return Response.ok("something went wrong, cannot add user like a manager");

  }

  @Ajax
  @Resource
  public Response addManagers2Program(String[] usernames, String role, String notif){

    try {
      String progamId = loginController.getCurrentProgramId();
      if (null == progamId || null == usernames || usernames.length <= 0)
        return Response.ok("something went wrong, cannot add user like a manager");
      Boolean mNotif = false;
      mNotif = false;
      if(null != notif){
        mNotif = "true".equals(notif)? true:false;
      }
      List<Manager> managers = new ArrayList<Manager>();
      for (String username:usernames){
        try {
          User exoUser = this.organizationService.getUserHandler().findUserByName(username);
          if(null != exoUser){
            Manager manager = new Manager(username);
            manager.setParentId(progamId);
            manager.setRole(Role.getRole(Integer.parseInt(role)));
            manager.setNotif(mNotif);
            manager.checkValid();
            managers.add(manager);
          }
        }catch (Exception e){}
      }
      if (managers.size() > 0){
        if (this.jcrService.addManagers2Program(progamId,managers).size() > 0){
          return Response.ok("ok");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Response.ok("something went wrong, cannot add user like a manager");

  }

  @Ajax
  @Resource
  public Response updateAjaxProgramManagerInLine(String username, String action, String val){
    String programId = loginController.getCurrentProgramId();
    Manager manager = this.jcrService.getProgramManagerByUserName(programId,username);
    if(null != manager){
      if (action.equals("notif")){
        Boolean mNotif = false;
        mNotif = "true".equals(val)? true:false;
        manager.setNotif(mNotif);
      }else if (action.equals("role")){
        manager.setRole(Role.getRole(Integer.parseInt(val)));
      }
      manager = this.jcrService.updateProgramManager(manager);
      if (null != manager){
        if (manager.getUserName().equals(loginController.getCurrentUserName())){
          loginController.setRights(manager.getRole().getLabel());
        }
        return Response.ok("ok");
      }
    }
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response removeProgramManager(String username){
    String programId = loginController.getCurrentProgramId();
    if (this.jcrService.removeManagerFromProgram(programId,username))
      return Response.ok("ok");
    else
      return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response searchEXOUsers(String keyword){
    ListAccess<User> userListAccess = this.jcrService.searchEXOUsers(keyword);
    if (null != userListAccess) {
      List<ManagerDTO> managerDTOs = new LinkedList<ManagerDTO>();
      try {
        for (int i = 0; i < userListAccess.getSize(); i++) {
          try{
            User[] users = userListAccess.load(i, 1);
            User user = users[0];
            if (null != user) {
              ManagerDTO managerDTO = new ManagerDTO(user.getUserName());
              managerDTO.setUserName(user.getUserName());
              managerDTO.setFullName(user.getFirstName() + " " + user.getLastName());
              managerDTOs.add(managerDTO);
            }
          }catch (Exception e){
          }
        }
        if (managerDTOs.size() > 0)
          return searchTpl.with().set("managers", managerDTOs).ok();
      } catch (Exception e) {
      }
    }
    return Response.ok("");
  }
  @Ajax
  @Resource
  public Response searchEXOProfiles(String keyword){
    List<Profile> profiles = this.jcrService.searchEXOProfiles(keyword);
    if (null != profiles) {
      List<ManagerDTO> managerDTOs = new LinkedList<ManagerDTO>();
      try {
        Profile profile;
        for (int i = 0; i < profiles.size(); i++) {
          try{
            profile = profiles.get(i);
            if (null != profile) {
              ManagerDTO managerDTO = new ManagerDTO(profile.getIdentity().getRemoteId());
              managerDTO.setFullName(profile.getFullName());
              managerDTOs.add(managerDTO);
            }
          }catch (Exception e){
          }
        }
        if (managerDTOs.size() > 0)
          return searchTpl.with().set("managers", managerDTOs).ok();
      } catch (Exception e) {
      }
    }
    return Response.ok("");
  }
}
