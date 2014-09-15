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
 * Sep 11, 2014  
 */
public enum Size {
  
  Large(1),
  Medium(2),
  Small(3);

  private final int size;

  Size(int size) {
      this.size = size;
  }

  public int size() {
      return this.size;
  }

  public String getLabel() {
      switch (this.size) {
          case 3:
              return "S";
          case 2:
              return "M";
          case 1:
              return "L";
          default:
              return "No size";
      }
  }

  public static Size getSize(int size) {
      for (Size type : Size.values()) {
          if (type.size() == size) {
              return type;
          }
      }

      return Large;
  }
}

