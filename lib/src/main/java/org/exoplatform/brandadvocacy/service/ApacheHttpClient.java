package org.exoplatform.brandadvocacy.service;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ApacheHttpClient {

  private final static Log log = ExoLogger.getLogger(ApacheHttpClient.class);

  public static void sendRequest(String endpoint, String token, String method, List params){
    if (null == endpoint || "".equals(endpoint) || null == token || "".equals(token))
      return;

    DefaultHttpClient httpClient = new DefaultHttpClient();
    HttpResponse response = null;
    if (method.equals("post")){
      HttpPost httpPost = new HttpPost(endpoint);
      StringEntity input = null;
      try {
        input = new StringEntity(params.toString());
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setEntity(input);
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        response = httpClient.execute(httpPost);
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (ClientProtocolException e) {
        log.error(" error ClientProtocolException request to "+endpoint);
        e.printStackTrace();
      } catch (IOException e) {
        log.error(" cannot send post request to "+endpoint);
        e.printStackTrace();
      }
    }else{
      HttpGet httpGet = new HttpGet(endpoint);
      httpGet.addHeader("accept","application/json");
      try {
        response = httpClient.execute(httpGet);
      } catch (IOException e) {
        log.error(" cannot send get request to "+endpoint);
        e.printStackTrace();

      }
    }
    if(null != response){
      try {
        if (response.getStatusLine().getStatusCode() != 200){
          log.error("error request ");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String output;
        log.info("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
          log.info(output);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    httpClient.getConnectionManager().shutdown();

  }
}
