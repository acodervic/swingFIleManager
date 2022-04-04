package github.acodervic.mod.swing.table;


public class EditData {
    String   columnName;//列下标
    Object editedColumnData;// 编辑后的数据,如果列编辑器为combox则可能绑定的是某个对象

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the editedColumnData
     */
    public Object getEditedColumnData() {
        return editedColumnData;
    }

    /**
     * @param editedColumnData the editedColumnData to set
     */
    public void setEditedColumnData(Object editedColumnData) {
        this.editedColumnData = editedColumnData;
    }

}