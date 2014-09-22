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
package org.exoplatform.brandadvocacy.model;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */

public enum Status {
  
  OPEN(1),
  INPROGRESS(2),
  WAITING_FOR_VALIDATE(3),
  VALIDATED(4),
  SHIPPED(5),
  REJECTED(6);
  
  private final int status;

  Status(int status) {
      this.status = status;
  }

  public int status() {
      return this.status;
  }

  public String getLabel() {
      switch (this.status) {
          
          case 6:
            return "Rejected";
          case 5:
              return "Shipped";
          case 4:
              return "Validated";
          case 3:
              return "Waiting For Validate";
          case 2:
              return "In Progress";
          case 1:
              return "Open";
          default:
              return "No name";
      }
  }

  public static Status getStatus(int status) {
      for (Status type : Status.values()) {
          if (type.status() == status) {
              return type;
          }
      }

      return OPEN;
  }
}
