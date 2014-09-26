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
public enum Role {
  
  Admin(1),
  Validator(2),
  Shipping_Manager(3),
  Participant(4);

  private final int role;

  Role(int role) {
      this.role = role;
  }

  public int getValue() {
      return this.role;
  }

  public String getLabel() {
      switch (this.role) {
          case 3:
              return "Shipping Manager";
          case 2:
              return "Validator";
          case 1:
              return "Admin";
          default:
              return "Participant";
      }
  }

  public static Role getRole(int role) {
      for (Role type : Role.values()) {
          if (type.getValue() == role) {
              return type;
          }
      }

      return Participant;
  }
}

