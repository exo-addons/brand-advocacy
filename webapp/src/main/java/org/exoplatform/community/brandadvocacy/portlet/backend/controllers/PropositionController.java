package org.exoplatform.community.brandadvocacy.portlet.backend.controllers;

import juzu.Action;
import juzu.Path;
import juzu.Response;
import juzu.View;
import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.service.IService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 10/10/14.
 */
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

  @Inject
  @Path("proposition/view.gtmpl")
  org.exoplatform.community.brandadvocacy.portlet.backend.templates.proposition.view viewTpl;


  @Inject
  public PropositionController(IService iService){
    this.propositionService = iService;
  }

  @View
  public Response.Content addForm(String mid){
    return addTpl.with().set("missionId",mid).ok();
  }
  @View
  public Response.Content editForm(String id){

    Proposition proposition =  this.propositionService.getPropositionById(id);
    if(null != proposition)
      return editTpl.with().proposition(proposition).ok();

    return Response.ok("mission not found").withMimeType("text/html; charset=UTF-8").withHeader("Cache-Control", "no-cache");
  }

  @View
  public Response.Content list(String mid){
    return listTpl.with().set("missionId",mid).set("propositions",this.propositionService.getPropositionsByMissionId(mid)).ok();
  }
  @Action
  public Response add(String missionId,String content,String active ){
    List<Proposition> propositions = new ArrayList<Proposition>(1);
    Boolean proposActive = false;
    if (null != active)
      proposActive = active.equals("1") ? true:false;
    Proposition proposition = new Proposition(content);
    proposition.setMission_id(missionId);
    proposition.setActive(proposActive);
    propositions.add(proposition);
    Mission mission = this.propositionService.addProposition2Mission(missionId,propositions);
    if(null != mission){
      return PropositionController_.list(mission.getId());
    }
    return Response.ok("something went wrong, cannot add proposition !!!");
  }
  @Action
  public Response delete(String mid, String id){
    this.propositionService.removeProposition(id);
    return PropositionController_.list(mid);
  }
  @Action
  public Response update(String id, String content, String active){
    Proposition proposition = this.propositionService.getPropositionById(id);
    if(null != proposition){
      Boolean proposActive = false;
      if (null != active)
        proposActive = active.equals("1") ? true:false;
      proposition.setContent(content);
      proposition.setActive(proposActive);

      this.propositionService.updateProposition(proposition);
      return PropositionController_.list(proposition.getMission_id());
    }
    return Response.ok(" cannot update proposition not exist");



  }

}
