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
package org.exoplatform.brandadvocacy.service;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 19, 2014  
 */
public class BrandAdvocacyServiceException extends RuntimeException {
  private static final long serialVersionUID = 42L;
  private int code;
  
  public static final int DUPLICATED = 1;
  public static final int MISSION_NOT_EXISTS = 2;
  public static final int PARTICIPANT_NOT_EXISTS = 3;
  public static final int ADDRESS_NOT_EXISTS = 4;
  public static final int MISSION_PARTICIPANT_NOT_EXISTS = 5;
  public static final int MISSION_INVALID = 6;
  public static final int PROPOSITION_INVALID = 7;
  public static final int ID_INVALID = 8;
  public static final int MISSION_PARTICIPANT_INVALID = 9;
  public static final int MANAGER_NOT_EXISTS = 10;
  public static final int PROPOSITION_NOT_EXISTS = 11;

  public BrandAdvocacyServiceException(String message, Throwable throwable) {
      this(0, message, throwable);
  }
  
  public BrandAdvocacyServiceException(int code, String message) {
      this(code, message, null);
  }
  
  public BrandAdvocacyServiceException(int code, String message, Throwable throwable) {
      super(message, throwable);
      this.code = code;
  }

  public int getCode() {
      return code;
  }

}
