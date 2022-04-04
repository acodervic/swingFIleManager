/**
 * Copyright (c) 2018, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package github.acodervic.mod.db.anima.core;

import java.util.List;
import java.util.Map;

import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.page.PageRow;

/**
 * SQLParams
 * <p>
 * This class is used to store the parameters of the generated SQL.
 *
 * @author biezhi
 * @date 2018/3/17
 */
public class SQLParams {

    private Class<? extends Model> modelClass;
    private Object                 model;
    private String                 selectColumns;
    private String                 tableName;
    private String                 pkName;
    private StringBuilder          conditionSQL;
    private List<Object>           columnValues;
    private Map<String, Object>    updateColumns;
    private List<String>           excludedColumns;
    private PageRow                pageRow;
    private String                 orderBy;
    private boolean                isSQLLimit;

    private String customSQL;

    /**
     * @param customSQL the customSQL to set
     */
    public void setCustomSQL(String customSQL) {
        this.customSQL = customSQL;
    }

    /**
     * @param selectColumns the selectColumns to set
     */
    public void setSelectColumns(String selectColumns) {
        this.selectColumns = selectColumns;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the isSQLLimit
     */
    public boolean isSQLLimit() {
        return isSQLLimit;
    }

    /**
     * @param isSQLLimit the isSQLLimit to set
     */
    public void setSQLLimit(boolean isSQLLimit) {
        this.isSQLLimit = isSQLLimit;
    }

    /**
     * @return the columnValues
     */
    public List<Object> getColumnValues() {
        return columnValues;
    }

    /**
     * @param columnValues the columnValues to set
     */
    public void setColumnValues(List<Object> columnValues) {
        this.columnValues = columnValues;
    }

    /**
     * @return the pkName
     */
    public String getPkName() {
        return pkName;
    }

    /**
     * @param pkName the pkName to set
     */
    public void setPkName(String pkName) {
        this.pkName = pkName;
    }

    /**
     * @return the conditionSQL
     */
    public StringBuilder getConditionSQL() {
        return conditionSQL;
    }

    /**
     * @param conditionSQL the conditionSQL to set
     */
    public void setConditionSQL(StringBuilder conditionSQL) {
        this.conditionSQL = conditionSQL;
    }

    /**
     * @return the model
     */
    public Object getModel() {
        return model;
    }

    /**
     * @param model the model to set
     */
    public void setModel(Object model) {
        this.model = model;
    }

    /**
     * @return the modelClass
     */
    public Class<? extends Model> getModelClass() {
        return modelClass;
    }

    /**
     * @param modelClass the modelClass to set
     */
    public void setModelClass(Class<? extends Model> modelClass) {
        this.modelClass = modelClass;
    }

    /**
     * @return the updateColumns
     */
    public Map<String, Object> getUpdateColumns() {
        return updateColumns;
    }

    /**
     * @param updateColumns the updateColumns to set
     */
    public void setUpdateColumns(Map<String, Object> updateColumns) {
        this.updateColumns = updateColumns;
    }

    /**
     * @return the pageRow
     */
    public PageRow getPageRow() {
        return pageRow;
    }

    /**
     * @param pageRow the pageRow to set
     */
    public void setPageRow(PageRow pageRow) {
        this.pageRow = pageRow;
    }

    /**
     * @return the orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * @param orderBy the orderBy to set
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * @return the excludedColumns
     */
    public List<String> getExcludedColumns() {
        return excludedColumns;
    }

    /**
     * @param excludedColumns the excludedColumns to set
     */
    public void setExcludedColumns(List<String> excludedColumns) {
        this.excludedColumns = excludedColumns;
    }

    /**
     * @return the customSQL
     */
    public String getCustomSQL() {
        return customSQL;
    }

    /**
     * @return the selectColumns
     */
    public String getSelectColumns() {
        return selectColumns;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

}
