package org.exoplatform.brandadvocacy.jcr;

import com.google.common.collect.Lists;
import org.exoplatform.brandadvocacy.model.Address;
import org.exoplatform.brandadvocacy.model.Manager;
import org.exoplatform.brandadvocacy.model.Mission;
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

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 30, 2014
 */
public class AddressDAO extends DAO{
  private static final Log log             = ExoLogger.getLogger(AddressDAO.class);
  private static final String node_prop_labelID = "exo:labelID";
  private static final String node_prop_fname = "exo:fname";
  private static final String node_prop_lname = "exo:lname";
  private static final String node_prop_address = "exo:address";
  private static final String node_prop_city = "exo:city";
  private static final String node_prop_country = "exo:country";
  private static final String node_prop_phone = "exo:phone";


  public AddressDAO(JCRImpl jcrImpl) {
    super(jcrImpl);
  }
  private Node getOrCreateAddressHome(String prgoramId, String username){

    if (null == username || "".equals(username)){
      log.error("ERROR cannot get or create address home in participant null");
      return null;
    }
    Node participant = this.getJcrImplService().getParticipantDAO().getNodeByUserName(prgoramId,username);
    if (null != participant){
      return this.getJcrImplService().getParticipantDAO().getOrCreateAddressHome(participant);
    }
    return null;
  }
  public void setProperties(Node aNode, Address adrs) throws RepositoryException {
    aNode.setProperty(node_prop_labelID,adrs.getLabelID());
    aNode.setProperty(node_prop_fname,adrs.getfName());
    aNode.setProperty(node_prop_lname,adrs.getlName());
    aNode.setProperty(node_prop_address,adrs.getAddress());
    aNode.setProperty(node_prop_city,adrs.getCity());
    aNode.setProperty(node_prop_country,adrs.getCountry());
    aNode.setProperty(node_prop_phone,adrs.getPhone());
  }

  public Address transferNode2Object(Node node) throws RepositoryException {
    if (null == node)
      return null;
    Address address = new Address();
    address.setId(node.getUUID());
    PropertyIterator iter = node.getProperties("exo:*");
    Property p;
    String name;
    while (iter.hasNext()) {
      p = iter.nextProperty();
      name = p.getName();
      if (name.equals(node_prop_labelID)){
        address.setLabelID(p.getString());
      } else if (name.equals(node_prop_fname)) {
        address.setfName(p.getString());
      } else if (name.equals(node_prop_lname)) {
        address.setlName(p.getString());
      } else if (name.equals(node_prop_address)) {
        address.setAddress(p.getString());
      } else if (name.equals(node_prop_city)) {
        address.setCity(p.getString());
      } else if (name.equals(node_prop_country)){
        address.setCountry(p.getString());
      } else if (name.equals(node_prop_phone)){
        address.setPhone(p.getString());
      }
    }
    try {
      address.checkValid();
      return address;
    }catch (BrandAdvocacyServiceException brade){
      log.error("ERROR cannot transfer node to object");
    }
    return null;
  }
  public List<Address> transferNodes2Objects(List<Node> nodes) {
    List<Address> addresses  = new ArrayList<Address>(nodes.size());
    for (Node node:nodes){
      try {
        Address address = this.transferNode2Object(node);
        if (null != address){
          addresses.add(address);
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    return addresses;
  }
  public Address addAddress2Participant(String programId, String username,Address address) {

    try {
      Node addressHomeNode = this.getOrCreateAddressHome(programId,username);
      address.checkValid();
      if (null != addressHomeNode) {
        Node addressNode = null;
        if(!addressHomeNode.hasNode(address.getLabelID()))
          addressNode = addressHomeNode.addNode(address.getLabelID(), JCRImpl.ADDRESS_NODE_TYPE);
        else
          addressNode = addressHomeNode.getNode(address.getLabelID());
        if (null != addressNode) {
          this.setProperties(addressNode, address);
          addressHomeNode.save();
          return this.transferNode2Object(addressNode);
        }
      }
    } catch (ItemExistsException ie) {
      log.error(" === ERROR cannot add existing item " + ie.getMessage());
    } catch (UnsupportedRepositoryOperationException e) {
      log.error("=== ERROR cannot add address to participant " + e.getMessage());
    } catch (RepositoryException e) {
      log.error("=== ERROR cannot add address to participant " + e.getMessage());
    } catch (BrandAdvocacyServiceException brade) {
      log.error("=== ERROR cannot add address " + brade.getMessage());
    }
    return null;
  }

  public List<Address> getAllAddressesByParticipantInProgram(String programId, String username){
    try {
        Node addressHomeNode = this.getOrCreateAddressHome(programId,username);
        if(null != addressHomeNode){
          NodeIterator nodes = addressHomeNode.getNodes();
          return this.transferNodes2Objects(Lists.newArrayList(nodes));
        }
    } catch (RepositoryException e) {
      log.error("==== ERROR get all propositions "+e.getMessage() );
    }
    return null;
  }
  public Address updateAddress(Address address){
    try {
      address.checkValid();
      Node addressNode = this.getNodeById(address.getId());
      if(null != addressNode){
        this.setProperties(addressNode,address);
        addressNode.save();
        return this.transferNode2Object(addressNode);
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot update address "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
    return null;
  }
  public void removeAddress(String id){
    if (null == id || "".equals(id)){
      log.error("ERROR cannot remove address null");
      return;
    }
    try {
      Node addressNode = this.getNodeById(id);
      if(null != addressNode){
        Session session = addressNode.getSession();
        addressNode.remove();
        session.save();
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot remove address "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
  }

  public Address getAddressById(String id){
    if (null == id || "".equals(id))
      return null;

    try {
      Node node = this.getNodeById(id);
      if (null != node){
        return this.transferNode2Object(node);
      }
    } catch (RepositoryException e) {
      log.error("ERROR getAddressById cannot get address node");
    }
    return null;
  }
}
