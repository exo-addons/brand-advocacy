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
public enum Priority {

  PRIORITY_1(1),
  PRIORITY_2(2),  
  PRIORITY_3(3);  

  private final int priority;

  Priority(int priority) {
      this.priority = priority;
  }

  public int getValue() {
      return this.priority;
  }

  public static Priority getPriority(int priority) {
      for (Priority type : Priority.values()) {
          if (type.getValue() == priority) {
              return type;
          }
      }

      return PRIORITY_1;
  }

  public String getLabel() {
      switch (this.priority) {
          case 3:
              return "3";
          case 2:
              return "2";
          case 1:
          default:
              return "1";
      }
  }
}
