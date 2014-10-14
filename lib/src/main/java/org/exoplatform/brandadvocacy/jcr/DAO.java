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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 18, 2014  
 */
public abstract class DAO {
  private static final Log log = ExoLogger.getLogger(DAO.class);
  private JCRImpl jcrImplService;
  
  public JCRImpl getJcrImplService() {
    return jcrImplService;
  }

  public void setJcrImplService(JCRImpl jcrImplService) {
    this.jcrImplService = jcrImplService;
  }

  public DAO(JCRImpl jcrImpl){
    this.jcrImplService = jcrImpl;
  }

  public List<Node> getNodesByQuery(String sql, int offset, int limit){

    List<Node> list = new ArrayList<Node>();
    try {
      Session session = this.getJcrImplService().getSession();
      QueryImpl query = (QueryImpl) session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
      if(0 != offset)
        query.setOffset(offset);
      if (0 != limit)
        query.setLimit(limit);

      QueryResult result = query.execute();
      NodeIterator nodes = result.getNodes();
      if (nodes.hasNext()) {
        list.add(nodes.nextNode()) ;
      }
    } catch (RepositoryException e) {
      log.error("ERROR cannot get nodes by query  "+sql);
      e.printStackTrace();
    }
    return list;
  }
}
