
package github.acodervic.mod.db.anima.page;


public class PageRow {

    private Integer pageNum=0;
    private Integer pageSize;


    /**
     * @return the pageNum
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum the pageNum to set
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return the pageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public PageRow(Integer pageNum, Integer pageSize) {
    if (pageNum!=null&&pageNum>0) {
        this.pageNum = pageNum;
    }
        this.pageSize = pageSize;
    }



}