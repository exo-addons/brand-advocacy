package org.exoplatform.brandadvocacy.jcr;

import org.exoplatform.brandadvocacy.model.Address;
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
  public Node getNodeByLabelID(String pid,String labelID){
    StringBuilder sql = new StringBuilder("select * from "+ JCRImpl.ADDRESS_NODE_TYPE +" where jcr:path like '");
    sql.append(JCRImpl.EXTENSION_PATH).append("/").append(JCRImpl.PARTICIPANT_PATH);
    sql.append("/").append(Utils.queryEscape(pid)).append("/").append(ParticipantDAO.node_prop_addresses);
    sql.append("/").append(Utils.queryEscape(labelID));
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
      log.error("ERROR cannot get address  "+labelID +" from participant "+pid+" Exeption "+e.getMessage());
    }
    return null;
  }

  public Address addAddress2Participant(String username,Address address){
    try {
      address.checkValid();
      if(null == username || "".equals(username))
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID, "cannot add address to participant id null");
      try {
        Node participantNode = this.getJcrImplService().getParticipantDAO().getNodeByUserName(username);
        Node addressHomeNode = this.getJcrImplService().getParticipantDAO().getOrCreateAddressHome(participantNode);
        if(null != addressHomeNode){
          Node addressNode = addressHomeNode.addNode(address.getLabelID(),JCRImpl.ADDRESS_NODE_TYPE);
          this.setProperties(addressNode, address);
          addressHomeNode.save();
          return this.transferNode2Object(addressNode);

        }
      }
      catch (ItemExistsException ie){
        log.error(" === ERROR cannot add existing item "+ie.getMessage());
      }
      catch (UnsupportedRepositoryOperationException e) {
        log.error("=== ERROR cannot add address to participant "+e.getMessage());
      } catch (RepositoryException e) {
        log.error("=== ERROR cannot add address to participant "+e.getMessage());
      }

    } catch (BrandAdvocacyServiceException brade) {
      log.error("=== ERROR cannot add address "+brade.getMessage());
    }
    return null;
  }
  public List<Address> getAllAddressesByParticipant(String username){
    List<Address> addresses = new ArrayList<Address>();

    if(null == username || "".equals(username))
      throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID, "cannot get all addresses from participant id null");
    try {
      Node participantNode = this.getJcrImplService().getParticipantDAO().getNodeByUserName(username);
      if(null != participantNode){
        Node addressHomeNode = this.getJcrImplService().getParticipantDAO().getOrCreateAddressHome(participantNode);
        if(null != addressHomeNode){
          NodeIterator nodes = addressHomeNode.getNodes();
          Address address;
          while (nodes.hasNext()){
            try {
              address = this.transferNode2Object(nodes.nextNode());
              address.checkValid();
              addresses.add(address);
            } catch (RepositoryException re){
              log.error("=== ERROR get all addresses ");
              re.printStackTrace();
            } catch (BrandAdvocacyServiceException brade){
              log.error("==== ERROR get all addresses "+brade.getMessage());
            }

          }
        }
      }
    } catch (RepositoryException e) {
      log.error("==== ERROR get all propositions "+e.getMessage() );
    }
    return addresses;
  }
  public Address updateAddress(Address address){
    try {
      address.checkValid();
      Node addressNode = this.getNodeById(address.getId());
      if(null != addressNode && address.getId().equals(addressNode.getUUID())){
        this.setProperties(addressNode,address);
        addressNode.getSession().save();
        return address;
      }else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ADDRESS_NOT_EXISTS," cannot update address not exists");
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot update address "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }
    return null;
  }
  public Address removeAddress(Address address){
    try {
      if(null == address.getId() || "".equals(address.getId()))
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ID_INVALID,"cannot remove invalid address");
      Node addressNode = this.getNodeById(address.getId());
      if(null != addressNode && address.getId().equals(addressNode.getUUID())){
        Session session = addressNode.getSession();
        addressNode.remove();
        session.save();
        return address;
      }else
        throw new BrandAdvocacyServiceException(BrandAdvocacyServiceException.ADDRESS_NOT_EXISTS," cannot remove address not exists");
    } catch (RepositoryException e) {
      log.error("==== ERROR cannot remove address "+e.getMessage() );
    } catch (BrandAdvocacyServiceException brade){
      log.error(brade.getMessage());
    }

    return null;
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
