package org.exoplatform.brandadvocacy.jcr;

import com.google.common.collect.Lists;
import org.exoplatform.brandadvocacy.model.MissionParticipantNote;
import org.exoplatform.brandadvocacy.model.NoteType;
import org.exoplatform.brandadvocacy.model.Program;
import org.exoplatform.brandadvocacy.model.Query;
import org.exoplatform.brandadvocacy.service.BrandAdvocacyServiceException;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.brandadvocacy.service.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by exoplatform on 23/12/14.
 */
public class MissionParticipantNoteDAO extends DAO {

  private static final Log log             = ExoLogger.getLogger(MissionParticipantNoteDAO.class);
  private static final String node_prop_labelID = "exo:labelID";
  private static final String node_prop_type = "exo:type";
  private static final String node_prop_mission_participant_id = "exo:mission_participant_id";
  private static final String node_prop_content = "exo:content";
  private static final String node_prop_author = "exo:author";


  public MissionParticipantNoteDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }
  private Node getOrCreateNoteHome(String missionParticipantId){
    Node missionParticipantNode = null;
    try {
      missionParticipantNode = this.getNodeById(missionParticipantId);
      if (null != missionParticipantNode)
      return this.getJcrImplService().getMissionParticipantDAO().getOrCreateNoteHome(missionParticipantNode);
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return null;
  }
  private void setProperties(Node aNode,MissionParticipantNote missionParticipantNote) throws RepositoryException {
    aNode.setProperty(node_prop_labelID,missionParticipantNote.getLabelID());
    aNode.setProperty(node_prop_type,missionParticipantNote.getType().getValue());
    aNode.setProperty(node_prop_mission_participant_id,missionParticipantNote.getMissionParticipantId());
    aNode.setProperty(node_prop_content,missionParticipantNote.getContent());
    aNode.setProperty(node_prop_author,missionParticipantNote.getAuthor());
  }
  private MissionParticipantNote transfertNode2Object(Node aNode) throws RepositoryException {
    if (null == aNode)
      return null;

    MissionParticipantNote missionParticipantNote = new MissionParticipantNote();
    missionParticipantNote.setId(aNode.getUUID());
    PropertyIterator props = aNode.getProperties("exo:*");
    Property property;
    String name;
    while (props.hasNext()){
      property = props.nextProperty();
      name = property.getName();
      if (name.equals(node_prop_labelID)){
        missionParticipantNote.setLabelID(property.getString());

      }else if (name.equals(node_prop_type)){
        missionParticipantNote.setType(NoteType.getNoteType((int)property.getLong()));
      }else if (name.equals(node_prop_mission_participant_id)){
        missionParticipantNote.setMissionParticipantId(property.getString());
      }else if (name.equals(node_prop_content)){
        missionParticipantNote.setContent(property.getString());
      }else if (name.equals(node_prop_author)){
        missionParticipantNote.setAuthor(property.getString());
      }
    }
    try{
      missionParticipantNote.checkValid();
      return missionParticipantNote;
    }catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot transfer node to object mission participant note");
    }
    return null;
  }
  private List<MissionParticipantNote> transfertNodes2Objects(List<Node> nodes){
    List<MissionParticipantNote> missionParticipantNotes = new ArrayList<MissionParticipantNote>();
    for (Node node:nodes){
      MissionParticipantNote missionParticipantNote = null;
      try {
        missionParticipantNote = this.transfertNode2Object(node);
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
      if (null != missionParticipantNote){
        missionParticipantNotes.add(missionParticipantNote);
      }
    }
    return missionParticipantNotes;
  }
  public MissionParticipantNote addNote2MissionParticipant(MissionParticipantNote missionParticipantNote){
    try{
      missionParticipantNote.checkValid();
      Node noteHome = this.getOrCreateNoteHome(missionParticipantNote.getMissionParticipantId());
      if (null != noteHome){
        Node missionParticipantNoteNode;
        if (noteHome.hasNode(missionParticipantNote.getLabelID())){
          missionParticipantNoteNode = noteHome.getNode(missionParticipantNote.getLabelID());
        }else {
          missionParticipantNoteNode = noteHome.addNode(missionParticipantNote.getLabelID(),JCRImpl.NOTE_NODE_TYPE);
        }
        if (null != missionParticipantNoteNode){
          this.setProperties(missionParticipantNoteNode,missionParticipantNote);
          noteHome.save();
          return this.transfertNode2Object(missionParticipantNoteNode);
        }
      }
    }catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    } catch (RepositoryException e) {
      log.error(e.getMessage());
    }
    return null;
  }
  public List<MissionParticipantNote> getAllByType(String missionParticipantId,int type){
    Node noteHome = this.getOrCreateNoteHome(missionParticipantId);
    List<MissionParticipantNote> result = new ArrayList<MissionParticipantNote>();
    if (null != noteHome){
      try {
        NodeIterator nodeIterator = noteHome.getNodes();
        List<MissionParticipantNote> missionParticipantNotes = this.transfertNodes2Objects(Lists.newArrayList(nodeIterator));
        for (MissionParticipantNote missionParticipantNote:missionParticipantNotes){
          if (type == missionParticipantNote.getType().getValue()){
            result.add(missionParticipantNote);
          }
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return result;
  }
  public MissionParticipantNote getById(String missionParticipantId){
    try {
      Node node = this.getNodeById(missionParticipantId);
      if (null != node){
        return this.transfertNode2Object(node);
      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return null;
  }
}

