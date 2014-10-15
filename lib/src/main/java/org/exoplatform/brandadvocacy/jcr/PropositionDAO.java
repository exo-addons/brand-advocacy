/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.brandadvocacy.jcr;

import org.exoplatform.brandadvocacy.model.Mission;
import org.exoplatform.brandadvocacy.model.Proposition;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 15, 2014  
 */
public class PropositionDAO extends DAO {

  private static final String node_prop_labelID = "exo:labelID";
  public static final String node_prop_mission_id = "exo:mission_id";
  public static final String node_prop_content = "exo:content";
  public static final String node_prop_active = "exo:active";
  public static final String node_prop_numberUsed = "exo:numberUsed";
  private static final Log log = ExoLogger.getLogger(PropositionDAO.class);

  public PropositionDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
    // TODO Auto-generated constructor stub
  }

  public void setProperties(Node aNode, Proposition p) throws RepositoryException {
    aNode.setProperty(node_prop_labelID,p.getLabelID());
    aNode.setProperty(node_prop_mission_id, p.getMission_id());
    aNode.setProperty(node_prop_content, p.getContent());
    aNode.setProperty(node_prop_active, p.getActive());
    aNode.setProperty(node_prop_numberUsed, p.getNumberUsed());
  }

  private Proposition transferNode2Object(Node node) throws RepositoryException {
    Proposition proposition = new Proposition();
    proposition.setId(node.getUUID());
    PropertyIterator iter = node.getProperties("exo:*");
    Property p;
    String name;
    while (iter.hasNext()) {
      p = iter.nextProperty();
      name = p.getName();
      if (name.equals(node_prop_labelID)){
        proposition.setLabelID(p.getString());
      } else if (name.equals(node_prop_mission_id)) {
        proposition.setMission_id(p.getString());
      } else if (name.equals(node_prop_content)) {
        proposition.setContent(p.getString());
      } else if (name.equals(node_prop_active)) {
        proposition.setActive(p.getBoolean());
      } else if (name.equals(node_prop_numberUsed)) {
        proposition.setNumberUsed((int) p.getLong());
      }
    }
    return proposition;
  }
  private List<Proposition> transferNodes2Objects(List<Node> nodes){
    List<Proposition> propositions = new ArrayList<Proposition>(nodes.size());
    Proposition aPropostion;
    for (Node node:nodes){
      try {
        aPropostion = this.transferNode2Object(node);
        if(aPropostion.checkValid()){
          propositions.add(aPropostion);
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return propositions;
  }
  public Node getNodeById(String id) throws RepositoryException{
    return this.getJcrImplService().getSession().getNodeByUUID(id);
  }
  public Node getNodeByLabelID(String mid, String pid){
    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.PROPOSITION_NODE_TYPE +" where jcr:path like '");
    sql.append(JCRImpl.EXTENSION_PATH).append("/").append(JCRImpl.MISSIONS_PATH);
    sql.append("/").append(Utils.queryEscape(mid)).append("/").append(MissionDAO.node_prop_propositions);
    sql.append("/").append(Utils.queryEscape(pid));
    sql.append("'");
    Session session;
    try {
      session = this.getJcrImplService().getSession();
      Query query = session.getWorkspace().getQueryManager().createQuery(sql.toString(), Query.SQL);
      QueryResult result = query.execute();
      NodeIterator nodes = result.getNodes();
      if (nodes.hasNext()) {
        return nodes.nextNode();
      }
    } catch (RepositoryException e) {
      log.error("ERROR cannot get proposition  "+pid +" from mission "+mid+" Exeption "+e.getMessage());
    }
    return null;
  }

  public List<Proposition> searchPropositions(String keyword){
    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.PROPOSITION_NODE_TYPE +" where ");
    sql.append(node_prop_labelID).append(" like '%"+keyword+"%'");
    sql.append(" OR "+node_prop_content).append(" like '%"+keyword+"%'");
    List<Node> nodes =  this.getNodesByQuery(sql.toString(),0,0);
    return this.transferNodes2Objects(nodes);
  }

  public Mission addProposition2Mission(String mid,List<Proposition> propositions){
    try {
      if(null == mid || "".equals(mid))
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID, "cannot add proposition to mission id null");
      try {
        Node missionNode = this.getJcrImplService().getMissionDAO().getNodeById(mid);
        Node propositionHomeNode = this.getJcrImplService().getMissionDAO().getOrCreatePropositionHome(missionNode);
        if(null != propositionHomeNode){
          Proposition proposition;
          Node propositionNode;
          int i = 0;
          for(i = 0;i<propositions.size();i++){
            proposition = propositions.get(i);
            proposition.setMission_id(mid);
            if(proposition.checkValid()) {
              propositionNode = propositionHomeNode.addNode(proposition.getLabelID(), JCRImpl.PROPOSITION_NODE_TYPE);
              this.setProperties(propositionNode, proposition);
            }
          }
          if(0 != i) {
            propositionHomeNode.save();
            return this.getJcrImplService().getMissionDAO().transferNode2Object(missionNode);
          }
        }
      }
      catch (ItemExistsException ie){
        log.error(" === ERROR cannot add existing item "+ie.getMessage());
      }
      catch (UnsupportedRepositoryOperationException e) {
        log.error("=== ERROR cannot add proposition to mission "+e.getMessage());
      } catch (RepositoryException e) {
        log.error("=== ERROR cannot add proposition to mission "+e.getMessage());
      }

    } catch (BrandAdvocacyServiceException brade) {
      log.error("=== ERROR cannot add proposition "+brade.getMessage());
    }
    return null;
  }
  public List<Proposition> getAllPropositions(String mid){
    List<Proposition> propositions = new ArrayList<Proposition>();

    if(null == mid || "".equals(mid))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID, "cannot get all propositions from mission id null");
    try {
      Node missionNode = this.getJcrImplService().getMissionDAO().getNodeById(mid);
      if(null != missionNode){
        Node propositionHomeNode = this.getJcrImplService().getMissionDAO().getOrCreatePropositionHome(missionNode);
        NodeIterator nodes = propositionHomeNode.getNodes();
        Node propositionNode = null;
        Proposition aProposition = null;
        while (nodes.hasNext()) {
          propositionNode = (Node) nodes.next();
          aProposition = this.transferNode2Object(propositionNode);
          if(aProposition.checkValid())
            propositions.add(aProposition);
          else
            log.error("cannot get invalid proposition "+propositionNode.toString());
        }
        return propositions;
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR get all propositions "+e.getMessage() );
    }
    return propositions;
  }
  public Proposition updateProposition(Proposition proposition){
    if(!proposition.checkValid())
      return null;
    try {
      Node propositionNode = this.getNodeById(proposition.getId());//this.getNodeByLabelID(proposition.getMission_id(), proposition.getId());
      if(null != propositionNode && proposition.getId().equals(propositionNode.getUUID())){
        this.setProperties(propositionNode,proposition);
        propositionNode.getSession().save();
        return this.transferNode2Object(propositionNode);
      }else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PROPOSITION_NOT_EXISTS," cannot update proposition not exists");
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot update proposition "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
    return null;
  }
  public void removeProposition(String id){
    try {
      Node propositionNode = this.getNodeById(id); //this.getNodeByLabelID(proposition.getMission_id(), proposition.getId());
      if(null != propositionNode){
        Session session = propositionNode.getSession();
        propositionNode.remove();
        session.save();
      }else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.PROPOSITION_NOT_EXISTS," cannot remove proposition not exists");
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot remove proposition "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
  }
  public Proposition getPropositionById(String id){
    try {
      return this.transferNode2Object(this.getNodeById(id));
    } catch (RepositoryException e) {
      log.error(" cannot get proposition ");
    }
    return null;
  }

  public int getTotalNumberPropositions(Boolean isPublic, Boolean isActive,String mid){
    StringBuilder sql = new StringBuilder("select jcr:uuid from "+ JCRImpl.PROPOSITION_NODE_TYPE +" where ");
    if(isPublic){
      sql.append(node_prop_active).append("= 'true' ");
    }else{
      sql.append(node_prop_active).append("='").append(isActive).append("'");
    }
    if (null != mid && !"".equals(mid)){
      sql.append(" AND ").append(node_prop_mission_id).append(" = ").append(mid);
    }
    return this.getNodesByQuery(sql.toString(),0,0).size();
  }
  public Proposition getRandomProposition(String mid){

    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.PROPOSITION_NODE_TYPE +" where ");
    sql.append(node_prop_active).append("= 'true'");
    sql.append(" AND ").append(node_prop_mission_id).append("='").append(mid).append("'");
    sql.append(" ORDER BY ").append(node_prop_numberUsed);
    List<Node> nodes =  this.getNodesByQuery(sql.toString(),0,1);
    try {
      if (nodes.size() > 0)
        return this.transferNode2Object(nodes.get(0));
    } catch (RepositoryException e) {
      log.error("ERROR cannot get random proposition");
    }
    return null;
  }

}

