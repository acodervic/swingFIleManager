package github.acodervic.mod.db.anima.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.Opt;

/**
 * 此对象是对数据库行更改的事件封装
 */
public class RowChangeEvent {
    static final Logger log = Logger.getLogger(RowChangeEvent.class.getName());
    String action;
    String tableName;
    String rowJson;// 行json数据,insert和delete的时候会封装数据进去
    String oldRowJson;
    String newRowJson;



    /**
     * @return the oldRowJson
     */
    public String getOldRowJson() {
        return oldRowJson;
    }
    /**
     * @param oldRowJson the oldRowJson to set
     */
    public void setOldRowJson(String oldRowJson) {
        this.oldRowJson = oldRowJson;
    }

    /**
     * @return the newRowJson
     */
    public String getNewRowJson() {
        return newRowJson;
    }
    /**
     * @param newRowJson the newRowJson to set
     */
    public void setNewRowJson(String newRowJson) {
        this.newRowJson = newRowJson;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @param rowJson the rowJson to set
     */
    public void setRowJson(String rowJson) {
        this.rowJson = rowJson;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the rowJson
     */
    public String getRowJson() {
        return rowJson;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * 尝试从事件中读取model对象
     *
     * @param <T>
     * @param c
     * @return
     */
    public <T> Opt<T> getRowJsonModel(Class<T> c) {
        Opt<T> ret = new Opt<>();
        if (this.rowJson != null) {
            try {
                ret.of(JSONUtil.jsonToObj(rowJson, c));
            } catch (Exception e) {
                log.log(Level.WARNING, "尝试从 TableChangeEvent 中转换对象出错 " + c.getName() + "  原始json:" + rowJson);
            }
        }
        return ret;
    }


        /**
     * 尝试从updade事件中读取model对象
     *
     * @param <T>
     * @param c
     * @return
     */
    public <T> Opt<UpdatedRowData<T>> getOldAndNewRowJsonModel(Class<T> c) {
        Opt<UpdatedRowData<T>> ret = new Opt<>();
        if (this.rowJson != null) {
            try {
                T newRowObj = JSONUtil.jsonToObj(newRowJson, c);
                T oldRowObj = JSONUtil.jsonToObj(oldRowJson, c);
                UpdatedRowData updateRowData = new UpdatedRowData(oldRowObj, newRowObj);
                ret.of(updateRowData);
            } catch (Exception e) {
                log.log(Level.WARNING, "尝试从 TableChangeEvent 中转换对象出错 " + c.getName() + "  原始json:" + newRowJson);
            }
        }
        return ret;
    }

}
