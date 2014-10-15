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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 9, 2014  
 */
public class Utils {

  public static String queryEscape(String s) {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
        char ch = s.charAt(i);
        if (ch == '%' || ch == '"' || ch == '_' || ch == '\\') {
            buffer.append('\\').append(ch);
        } else if (ch == '\'') {
            buffer.append("''");
        } else {
            buffer.append(ch);
        }
    }
    return buffer.toString();
  }

  public static String convertDateFromLong(Long val){
    Date date=new Date(val);
    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
    String dateText = df2.format(date);
    return dateText;
  }
}
