package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.View;
import juzu.request.SecurityContext;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Priority;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 10/10/14.
 */
public class PropositionController {

  IService propositionService;

  @Inject
  LoginController loginController;

  @Inject
  MissionController missionController;
  @Inject
  @Path("proposition/index.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.index indexTpl;

  @Inject
  @Path("proposition/add.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.add addTpl;

  @Inject
  @Path("proposition/edit.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.edit editTpl;

  @Inject
  @Path("proposition/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.list listTpl;
  /*
    @Inject
    @Path("proposition/view.gtmpl")
    org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.view viewTpl;

  */
  @Inject
  public PropositionController(IService iService){
    this.propositionService = iService;
  }

  public Response index(SecurityContext securityContext, String action, String missionId, String propositionId){
    loginController.setCurrentUserName(securityContext.getUserPrincipal().getName());
    if (null != missionId && !"".equals(missionId)){
      if (action.equals("add"))
        return this.addForm(missionId);
      else if (null != propositionId && !"".equals(propositionId)){
        if (action.equals("edit")){
          if (null != propositionId)
            return this.editForm(propositionId);
          else
            return JuZBackEndApplication_.index("Error: cannot find proposition to update");
        }
        else if (action.equals("delete"))
          return this.delete(propositionId);
      }
    }
    return JuZBackEndApplication_.index("Error: cannot find mission to manage proposition");
  }
  public Response addForm(String mid){
    return addTpl.with().set("missionId",mid).ok();
  }

  public Response editForm(String id){

    Proposition proposition =  this.propositionService.getPropositionById(id);
    if(null != proposition)
      return editTpl.with().proposition(proposition).ok();

    return JuZBackEndApplication_.index("cannot find proposition to update");
  }

  public Response list(String mid){
    Mission mission =  this.propositionService.getMissionById(mid);
    if(null != mission){
      List<Proposition> propositions = this.propositionService.getAllPropositions(mission.getId());
      return listTpl.with().set("priorities", Priority.values()).set("mission",mission).set("propositions",propositions).ok();
    }
    return JuZBackEndApplication_.index("cannot find proposition to update");
  }

  @Action
  public Response add(String missionId,String content,String active ){
    Mission mission = this.propositionService.getMissionById(missionId);
    if (null != mission){
      Boolean proposActive = false;
      if (null != active)
        proposActive = active.equals("1") ? true:false;
      Proposition proposition = new Proposition(content);
      proposition.setMission_id(missionId);
      proposition.setActive(proposActive);
      proposition = this.propositionService.addProposition2Mission(proposition);
      if(null != proposition){
        return JuZBackEndApplication_.index("mission_edit",mission.getProgramId(),missionId);
      }
    }
    else{
      return JuZBackEndApplication_.index("mission doesnot exist anymore");
    }

    return Response.ok("something went wrong, cannot add proposition !!!");
  }

  @Action
  public Response delete(String propositionId){
    String missionId = this.propositionService.removeProposition(propositionId);
    if (null != missionId)
      return this.list(missionId);
    return JuZBackEndApplication_.index("mission doesnot exist anymore");
  }
  @Action
  public Response update(String propositionId, String content, String active){
    Proposition proposition = this.propositionService.getPropositionById(propositionId);
    if(null != proposition){
      Boolean proposActive = false;
      if (null != active)
        proposActive = active.equals("1") ? true:false;
      proposition.setContent(content);
      proposition.setActive(proposActive);
      this.propositionService.updateProposition(proposition);
      return this.list(proposition.getMission_id());
    }
    return JuZBackEndApplication_.index(" Proposition does not exist any more to update");

  }

}
