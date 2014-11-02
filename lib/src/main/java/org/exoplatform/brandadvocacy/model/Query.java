package org.exoplatform.brandadvocacy.model;

/**
 * Created by exoplatform on 11/1/14.
 */
public class Query {

  private String programId;
  private String username;
  private String title;
  private String content;
  private int status;
  private String keyword;
  private int offset;
  private int limit;

  public Query(String programId){
    this.setProgramId(programId);
    this.setStatus("0");
    this.setOffset("0");
    this.setLimit(0);
  }
  public String getProgramId() {
    return programId;
  }

  public void setProgramId(String programId) {
    this.programId = programId;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(String status) {
    int stt = 0;
    if (null != status && !"".equals(status)){
      try {
        stt = Integer.parseInt(status);
      }catch (Exception e){
        stt = 0;
      }
    }
    this.status = stt;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    if (null == keyword)
      keyword = "";
    this.keyword = keyword;
  }

  public int getOffset() {
    if (offset > 0)
      offset--;
    return (offset)*this.getLimit();

  }

  public void setOffset(String offset) {
    int st = 0;
    try {
      if (null != offset && !"".equals(offset))
      st = Integer.parseInt(offset);
    }catch (Exception e){
      st = 0;
    }
    this.offset = st;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
