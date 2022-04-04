package github.acodervic.mod.swing.table;



/**
 * 实体,列名,显示列值的包装,在getTableCellRendererComponent时候会被传递
 */
public class ColumnRenderObj<T> {
    T rowObj;
    int cloumnIndex = -1;// 当前渲染的列下表
    int rowIndex = -1;// 当前渲染的行下表
    boolean isSelected = false;
    String header;
    String columnText;

    /**
     * @return the rowObj
     */
    public T getRowObj() {
        return rowObj;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @return the columnText
     */
    public String getColumnText() {
        return columnText;
    }

    /**
     * @param rowObj the rowObj to set
     */
    public void setRowObj(T rowObj) {
        this.rowObj = rowObj;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @param columnText the columnText to set
     */
    public void setColumnText(String columnText) {
        this.columnText = columnText;
    }

    /**
     * @param isSelected the isSelected to set
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * @return the isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @return the cloumnIndex
     */
    public int getCloumnIndex() {
        return cloumnIndex;
    }

    /**
     * @return the rowIndex
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param cloumnIndex the cloumnIndex to set
     */
    public void setCloumnIndex(int cloumnIndex) {
        this.cloumnIndex = cloumnIndex;
    }

    /**
     * @param rowIndex the rowIndex to set
     */
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * @param rowObj
     * @param header
     * @param columnText
     * 
     */
    public ColumnRenderObj(T rowObj, String header, String columnText, int cloumnIndex, int rowIndex,
            boolean isSelected) {
        this.rowObj = rowObj;
        this.header = header;
        this.isSelected = isSelected;
        this.cloumnIndex = cloumnIndex;
        this.rowIndex = rowIndex;
        this.columnText = columnText;
    }

}