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
    strPagignation.append("<div class=\"pagination uiPageIterator\">");
    if (lastPage > 1){
/*
      <li class="disabled"><a data-placement="bottom" rel="tooltip" data-original-title="Previous Page"><i class="uiIconPrevArrow"></i></a></li>
      <li class="active"><a href="">1</a></li>
      <li><a href="">2</a></li>
      <li><a href="">3</a></li>
      <li class="disabled"><a href="#">...</a></li>
      <li><a href="">20</a></li>
      <li><a data-placement="bottom" rel="tooltip" href="" data-original-title="Next Page"><i class="uiIconNextArrow"></i></a></li>
*/

      strPagignation.append("<ul>");
      for (int i=1;i<=lastPage;i++){
        if (i==this.getCurrentPage()){
          strPagignation.append("<li class=\"active\"><a href=\"#\">").append(i).append("</a>");
        }else {
          strPagignation.append("<li class=\"search-mission-participant-page\" data-page='").append(i).append("'><a href='#'").append("'>").append(i).append("</a>");
        }
        strPagignation.append("</li>");
      }
      strPagignation.append("</ul>");
    }
    strPagignation.append("</ul></div>");
    return strPagignation.toString();
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(double totalPages) {
    this.totalPages = (int)totalPages;
  }
}
