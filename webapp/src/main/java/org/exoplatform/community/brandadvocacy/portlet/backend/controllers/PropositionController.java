package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.*;
import org.exoplatform.commons.juzu.ajax.Ajax;
import org.exoplatform.brandadvocacy.model.*;
import org.exoplatform.brandadvocacy.service.IService;
import org.exoplatform.community.brandadvocacy.portlet.backend.Flash;
import org.exoplatform.community.brandadvocacy.portlet.backend.JuZBackEndApplication_;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by exoplatform on 10/10/14.
 */
@SessionScoped
public class PropositionController {

  IService propositionService;
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
  LoginController loginController;
  @Inject
  Flash flash;
  @Inject
  public PropositionController(IService iService){
    this.propositionService = iService;
  }

  @View
  public Response addForm(String missionId){
    flash.setStyleMissionMenu("active");
    return addTpl.with().set("missionId",missionId).ok();
  }

  @View
  public Response editForm(String propositionId){
    flash.setStyleMissionMenu("active");
    Proposition proposition =  this.propositionService.getPropositionById(propositionId);
    if(null != proposition)
      return editTpl.with().proposition(proposition).ok();

    return JuZBackEndApplication_.index("cannot find proposition to update");
  }

  public Response list(String mid){
    Mission mission =  this.propositionService.getMissionById(mid);
    if(null != mission){
      List<Proposition> propositions = this.propositionService.getAllPropositions(mission.getId(),null);
      return listTpl.with().set("priorities", Priority.values()).set("mission",mission).set("propositions",propositions).ok();
    }
    return JuZBackEndApplication_.showError("cannot find proposition to update");
  }

  @Action
  public Response add(String missionId,String content,String active ){
    Mission mission = this.propositionService.getMissionById(missionId);
    if (null != mission){
      Boolean proposActive = false;
      if (null != active)
        proposActive = active.equals("true") ? true:false;
      Proposition proposition = new Proposition(content);
      proposition.setMission_id(missionId);
      proposition.setActive(proposActive);
      proposition = this.propositionService.addProposition2Mission(proposition);
      if(null != proposition){
        return Response.ok("ok");
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
      return Response.ok("ok");
    return JuZBackEndApplication_.showError("mission doesnot exist anymore");
  }
  @Action
  public Response update(String propositionId, String content, String active){
    Proposition proposition = this.propositionService.getPropositionById(propositionId);
    if(null != proposition){
      Boolean proposActive = false;
      if (null != active)
        proposActive = active.equals("true") ? true:false;
      proposition.setContent(content);
      proposition.setActive(proposActive);
      proposition = this.propositionService.updateProposition(proposition);
      if (null != proposition)
        return Response.ok("ok");
    }
    return JuZBackEndApplication_.showError(" Proposition does not exist any more to update");

  }

  @Ajax
  @Resource
  public Response ajaxUpdatePropositionInline(String propositionId,String action, String val ){
    Proposition proposition = this.propositionService.getPropositionById(propositionId);
    if(null != proposition){
      if (action.equals("active")){
        Boolean proposActive = false;
        proposActive = val.equals("true") ? true:false;
        proposition.setActive(proposActive);
      }
      proposition = this.propositionService.updateProposition(proposition);
      if (null != proposition)
        return Response.ok("ok");
    }
    return Response.ok("nok");
  }

}
