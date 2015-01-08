package org.exoplatform.community.brandadvocacy.portlet.backend;


import juzu.impl.common.JSON;
import juzu.impl.common.JSONParser;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by exoplatform on 23/12/14.
 */
public class Utils {
  private static  ArrayList<JSON> countriesList;
  public static void readCountriesJSON(){
    try {
      countriesList = (ArrayList)new JSONParser(new FileReader(Utils.class.getResource("/json/countries.json").getFile())).parse();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static String getCountryNameByCode(String code){
    if (null == countriesList || countriesList.size() == 0 || null == code || "".equals(code))
      return "";
    JSON json;
    String country = null;
    for (int i =0;i< countriesList.size();i++){
      json = (JSON)countriesList.get(i);
      if(code.equals(json.get("code"))){
        country = (String) json.get("name");
        break;
      }
    }
    return country;
  }
  public static String convertDateFromLong(Long val){
    Date date=new Date(val);
    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
    String dateText = df2.format(date);
    return dateText;
  }

}
