package org.exoplatform.community.brandadvocacy.portlet.backend.models;

/**
 * Created by exoplatform on 11/2/14.
 */
public class Pagination {
  private int total;
  private int currentPage;
  private int firstPage;
  private int nbRecordsPerPage;
  private int totalPages;
  public Pagination(int total,int nbRecordsPerPage,String currentPage){
    this.setTotal(total);
    this.setCurrentPage(currentPage);
    this.setNbRecordsPerPage(nbRecordsPerPage);
    this.setTotalPages(Math.ceil(this.getTotal()/this.getNbRecordsPerPage()));
  }
  public double getTotal() {
    return (double)total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(String currentPage) {
    int current = 1;
    try {
      if (null != currentPage && !"".equals(currentPage))
      current = Integer.parseInt(currentPage);
    }catch (Exception e){
      current = 1;
    }
    this.currentPage = current;
  }

  public int getFirstPage() {
    return firstPage;
  }

  public void setFirstPage(int firstPage) {
    this.firstPage = firstPage;
  }

  public int getNbRecordsPerPage() {
    return nbRecordsPerPage;
  }

  public void setNbRecordsPerPage(int nbRecordsPerPage) {
    this.nbRecordsPerPage = nbRecordsPerPage;
  }
  public String generatePagination(String params){
    StringBuilder strPagignation = new StringBuilder("<div class='pagination uiPageIterator clearfix'>");
    double lastPage = this.getTotalPages();
    if (lastPage > 1){
      strPagignation.append("<ul>");
      for (int i=1;i<=lastPage;i++){
        strPagignation.append("<li>");
        if (i==this.getCurrentPage()){
          strPagignation.append(i);
        }else {
          strPagignation.append("<a href='").append(params).append("&page=").append(i).append("'>").append(i).append("</a>");
        }
        strPagignation.append("</li>");
      }
      strPagignation.append("</ul>");
    }
    strPagignation.append("</div>");
    return strPagignation.toString();
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(double totalPages) {
    this.totalPages = (int)totalPages;
  }
}
