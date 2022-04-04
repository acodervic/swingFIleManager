
package github.acodervic.mod.db.anima.page;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Page<T> {

    /**
     * current pageNum number
     */
    private int pageNum = 1;

    /**
     * How many pages per pageNum
     */
    private int limit = 10;

    /**
     * prev pageNum number
     */
    private int prevPage = 1;

    /**
     * next pageNum number
     */
    private int nextPage = 1;

    /**
     * total pageNum count
     */
    private int totalPages = 1;

    /**
     * total row count
     */
    private long totalRows = 0L;

    /**
     * row list
     */
    private List<T> rows=new ArrayList<>();

    /**
     * is first pageNum
     */
    private boolean isFirstPage = false;

    /**
     * is last pageNum
     */
    private boolean isLastPage = false;

    /**
     * has prev pageNum
     */
    private boolean hasPrevPage = false;

    /**
     * has next pageNum
     */
    private boolean hasNextPage = false;

    /**
     * navigation pageNum number
     */
    private int navPages = 8;

    /**
     * all navigation pageNum number
     */
    private int[] navPageNums;

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @return the navPageNums
     */
    public int[] getNavPageNums() {
        return navPageNums;
    }

    /**
     * @return the navPages
     */
    public int getNavPages() {
        return navPages;
    }

    /**
     * @return the nextPage
     */
    public int getNextPage() {
        return nextPage;
    }

    /**
     * @return the pageNum
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * @return the prevPage
     */
    public int getPrevPage() {
        return prevPage;
    }

    /**
     * @return the rows
     */
    public List<T> getRows() {
        return rows;
    }

    /**
     * @return the totalPages
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @return the totalRows
     */
    public long getTotalRows() {
        return totalRows;
    }

    /**
     * @param isFirstPage the isFirstPage to set
     */
    public void setFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    /**
     * @param hasPrevPage the hasPrevPage to set
     */
    public void setHasPrevPage(boolean hasPrevPage) {
        this.hasPrevPage = hasPrevPage;
    }

    /**
     * @param hasNextPage the hasNextPage to set
     */
    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    /**
     * @param isLastPage the isLastPage to set
     */
    public void setLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @param navPageNums the navPageNums to set
     */
    public void setNavPageNums(int[] navPageNums) {
        this.navPageNums = navPageNums;
    }

    /**
     * @param navPages the navPages to set
     */
    public void setNavPages(int navPages) {
        this.navPages = navPages;
    }

    /**
     * @param nextPage the nextPage to set
     */
    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    /**
     * @param pageNum the pageNum to set
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    /**
     * @param prevPage the prevPage to set
     */
    public void setPrevPage(int prevPage) {
        this.prevPage = prevPage;
    }

    /**
     * @param totalPages the totalPages to set
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @param totalRows the totalRows to set
     */
    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }
    public <R> Page<R> map(Function<? super T, ? extends R> mapper) {
        Page<R> page = new Page<>(this.totalRows, this.pageNum, this.limit);
        if (null != rows) {
            page.setRows(rows.stream().map(mapper).collect(Collectors.toList()));
        }
        return page;
    }

    public Page<T> peek(Consumer<T> consumer) {
        if (null != rows) {
            this.rows = rows.stream().peek(consumer).collect(Collectors.toList());
        }
        return this;
    }

    public Page<T> navPages(int navPages) {
        // calculation of navigation pageNum after basic parameter setting
        this.calcNavigatePageNumbers(navPages);
        return this;
    }

    public Page() {
    }

    public Page(long total, int page, int limit) {
        init(total, page, limit);
    }


    public Page(long total, PageRow pageRow_opt) {
        if (pageRow_opt!=null) {
            init(total, pageRow_opt.getPageNum(), pageRow_opt.getPageSize());
        }else{
            init(total, 1,99999999);
        }
    }

    private void init(long total, int pageNum, int limit) {
        // set basic params
        this.totalRows = total;
        this.limit = limit;
        this.totalPages = (int) ((this.totalRows - 1) / this.limit + 1);

        // automatic correction based on the current number of the wrong input
        if (pageNum < 1) {
            this.pageNum = 1;
        } else if (pageNum > this.totalPages) {
            this.pageNum = this.totalPages;
        } else {
            this.pageNum = pageNum;
        }

        // calculation of navigation pageNum after basic parameter setting
        this.calcNavigatePageNumbers(this.navPages);

        // and the determination of pageNum boundaries
        judgePageBoudary();
    }

    private void calcNavigatePageNumbers(int navPages) {
        // when the total number of pages is less than or equal to the number of navigation pages
        if (this.totalPages <= navPages) {
            navPageNums = new int[totalPages];
            for (int i = 0; i < totalPages; i++) {
                navPageNums[i] = i + 1;
            }
        } else {
            // when the total number of pages is greater than the number of navigation pages
            navPageNums = new int[navPages];
            int startNum = pageNum - navPages / 2;
            int endNum   = pageNum + navPages / 2;
            if (startNum < 1) {
                startNum = 1;
                for (int i = 0; i < navPages; i++) {
                    navPageNums[i] = startNum++;
                }
            } else if (endNum > totalPages) {
                endNum = totalPages;
                for (int i = navPages - 1; i >= 0; i--) {
                    navPageNums[i] = endNum--;
                }
            } else {
                for (int i = 0; i < navPages; i++) {
                    navPageNums[i] = startNum++;
                }
            }
        }
    }

    private void judgePageBoudary() {
        isFirstPage = pageNum == 1;
        isLastPage = pageNum == totalPages && pageNum != 1;
        hasPrevPage = pageNum != 1;
        hasNextPage = pageNum != totalPages;
        if (hasNextPage) {
            nextPage = pageNum + 1;
        }
        if (hasPrevPage) {
            prevPage = pageNum - 1;
        }
    }

    /**
     * 开始手动分页
     * @param pageNum 页码
     * @param limit 每页多少条数据
     */
    public Page<T> startPage(List<T> list, Integer pageNum,Integer limit) {

        if (list==null||list.size()==0) {
            return null;
        }

        Integer count = list.size(); // 记录总数
        this.setTotalRows(count);
        this.setLimit(limit);
        this.setPageNum(pageNum);
        int pageCount = 0; // 页数
        if (count % limit == 0) {
            pageCount = count / limit;
        } else {
            pageCount = count / limit + 1;
        }
        this.setTotalPages(pageCount);

        int fromIndex = 0; // 开始索引
        int toIndex = 0; // 结束索引

        if (!pageNum.equals(pageCount)) {
            fromIndex = (pageNum - 1) * limit;
            toIndex = fromIndex + limit;
        } else {
            fromIndex = (pageNum - 1) * limit;
            toIndex = count;
        }
        this.setRows(list.subList(fromIndex, toIndex));
        return this;
    }

}
