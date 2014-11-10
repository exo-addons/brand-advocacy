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
  @Path("proposition/add.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.add addTpl;

  @Inject
  @Path("proposition/edit.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.edit editTpl;

  @Inject
  @Path("proposition/list.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.list listTpl;

  @Inject
  LoginController loginController;
  @Inject
  Flash flash;
  @Inject
  public PropositionController(IService iService){
    this.propositionService = iService;
  }

  @Ajax
  @Resource
  public Response indexProposition(String missionId){
    return this.list(missionId);
  }

  @Ajax
  @Resource
  public Response loadAddPropositionForm(String missionId){
    return addTpl.with().set("missionId",missionId).ok();
  }

  @Ajax
  @Resource
  public Response loadEditPropositionForm(String propositionId){
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
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response addProposition(String missionId,String content,String active ){
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
    return Response.ok("nok");
  }

  @Ajax
  @Resource
  public Response deleteProposition(String propositionId){
    if (this.propositionService.removeProposition(propositionId))
      return Response.ok("ok");
    return Response.ok("nok");
  }
  @Ajax
  @Resource
  public Response updateProposition(String propositionId, String content, String active){
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
    return Response.ok("nok");
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
