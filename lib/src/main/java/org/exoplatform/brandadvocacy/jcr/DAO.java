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

import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.exoplatform.brandadvocacy.service.JCRImpl;
import org.exoplatform.services.jcr.impl.core.query.QueryImpl;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 18, 2014  
 */
public abstract class DAO {

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

}
