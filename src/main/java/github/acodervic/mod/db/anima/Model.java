package github.acodervic.mod.db.anima;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Consumer;

import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.ObjectUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.db.TableMoniter;
import github.acodervic.mod.db.anima.core.AnimaCache;
import github.acodervic.mod.db.anima.core.AnimaQuery;
import github.acodervic.mod.db.anima.core.ResultKey;
import github.acodervic.mod.db.anima.core.functions.TypeFunction;
import github.acodervic.mod.db.anima.event.RowChangeEvent;
import github.acodervic.mod.db.anima.utils.AnimaUtils;


/**
 * 基本 Model,T为id 熟悉类型,一般为String或者integer
 *
 * @author biezhi
 * @date 2018/3/16
 */
public class Model {

    // 用来存储当前对象的动态字段,只要继承了Model就可以操作此map
    private HashMap<String, Object> dynamicField = new HashMap<String, Object>();
    private transient Anima anima;// 数据库连接对象
    /**
     * 当前模型的数据库操作核心对象,通过此对象就可以执行sql等操作 默认使用的是全局连接
     */
    // private transient AnimaQuery<? extends Model> query = new
    // AnimaQuery<>(this.sql2o, this.getClass());
    private transient AnimaQuery<? extends Model> query_Anima;//如果当前有anima会在getQueryAnima时候自动加载
    Class<? extends Model> c = getClass();
    transient boolean startedAutoSyncFromDB = false;
    transient boolean startedAutoSyncListFromDB = false;

    public Object getDynamicField(String name) {
        nullCheck(name);
        return dynamicField.get(name);
    }

    /**
     * @return the startedAutoSyncFromDB
     */
    public boolean isStartedAutoSyncFromDB() {
        return startedAutoSyncFromDB;
    }

    /**
     * @return the startedAutoSyncListFromDB
     */
    public boolean isStartedAutoSyncListFromDB() {
        return startedAutoSyncListFromDB;
    }

    /**
     * @param startedAutoSyncFromDB the startedAutoSyncFromDB to set
     */
    public void setStartedAutoSyncFromDB(boolean startedAutoSyncFromDB) {
        this.startedAutoSyncFromDB = startedAutoSyncFromDB;
    }

    /**
     * @param startedAutoSyncListFromDB the startedAutoSyncListFromDB to set
     */
    public void setStartedAutoSyncListFromDB(boolean startedAutoSyncListFromDB) {
        this.startedAutoSyncListFromDB = startedAutoSyncListFromDB;
    }
    
    /**
     * 将实体和数据源绑定,会自动启动数据同步器
     *
     * @param anima
     * @return
     */
    public Model bindDatabaseSource(Anima anima) {
        this.anima = anima;
        if (anima != null) {
            if (this.anima.enableAutoUpdateModelFromDB) {
                autoSyncFormDB();
            }
            if (this.anima.enableAutoUpdateDBTableToList) {
                if (!isStartedAutoSyncListFromDB()) {
                    autoSyncList();
                    setStartedAutoSyncListFromDB(true);
                }
            }

        }
        return this;
    }

    /**
     * 清空所有动态属性
     */
    public void clearDynamicField() {
        this.dynamicField.clear();
    }

    /**
     * 是否存在某个动态属性?
     * 
     * @param name
     * @return
     */
    public boolean hasDynamicField(String name) {
        nullCheck(name);
        return this.dynamicField.containsKey(name);
    }

    /**
     * 删除某个动态属性
     * 
     * @param name
     */
    public void removeDynamicField(String name) {
        nullCheck(name);
        try {
            this.dynamicField.remove(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置字段,如果name存在则覆盖,否则则添加爱
     * 
     * @param name
     * @param value
     */
    public void setDynamicField(String name, Object value) {
        nullCheck(name, value);
        if (this.dynamicField.containsKey(name)) {
            this.dynamicField.remove(name);
        }
        this.dynamicField.put(name, value);
    }


    /**
     * 保存
     * model到数据库,默认读取从数据库返回的key(returnGeneratedKeys=true),如果modle有主键则尝试使用主键保存,否则则自动生成并填充
     *@param autoClose 是否自动关闭连接
     * @return ResultKey
     */
    public ResultKey save(Boolean autoClose) {
        return query().save(this,autoClose);
    }

        /**
     * 保存
     * model到数据库,默认读取从数据库返回的key(returnGeneratedKeys=true),如果modle有主键则尝试使用主键保存,否则则自动生成并填充
     * 执行完成后自动关闭连接
     *
     * @return ResultKey
     */
    public ResultKey save() {
        return save(true);
    }

    /**
     * 保存 model到数据库,如果modle有主键则尝试使用主键保存,否则则自动生成并填充
     *
     * @param returnGeneratedKeys 是否从数据库中获取生成的key,并刷新
     * @return ResultKey
     */
    public ResultKey save(boolean returnGeneratedKeys) {
        return query().save(this, returnGeneratedKeys);
    }

    /**
     * 更新 model到数据库
     *
     * @return number of rows affected after execution
     */
    public int update() {
        return query().updateByModel(this);
    }

    /**
     * 通过主键 更新数据
     *
     * @param id pk
     * @return 返回执行后受影响的行数
     */
    public int updateById(Serializable id) {
        return new AnimaQuery<>(this.anima, this.getClass()).updateById(this, id);
    }

    /**
     * 删除 model数据
     *
     * @return 返回执行后受影响的行数
     */
    public boolean delete() {
        return query().deleteByModel(this) > 0;
    }

    /**
     * 删除id
     * 
     * @return
     */
    public boolean deleteById() {
        return query().deleteById(query().getPrimaryKeyValue(this)) > 0;
    }

    /**
     * 更新集合语句
     *
     * @param column 表的列名 [sql]
     * @param value  列值
     * @return AnimaQuery
     */
    public AnimaQuery<? extends Model> set(String column, Object value) {
        return query().set(column, value);
    }

    /**
     * Update set statement with lambda
     *
     * @param function table column name with lambda
     * @param value    column value
     * @param <T>
     * @param <R>
     * @return AnimaQuery
     */
    public <T extends Model, R> AnimaQuery<? extends Model> set(TypeFunction<T, R> function, Object value) {
        return query().set(function, value);
    }

    /**
     * Where 语句
     *
     * @param statement conditional clause
     * @param value     column value
     * @return AnimaQuery
     */
    public AnimaQuery<? extends Model> where(String statement, Object value) {
        return query().where(statement, value);
    }

    /**
     * Where statement with lambda
     *
     * @param function column name with lambda
     * @param value    column value
     * @param <T>
     * @param <R>
     * @return AnimaQuery
     */
    public <T extends Model, R> AnimaQuery<? extends Model> where(TypeFunction<T, R> function, Object value) {
        return query().where(function, value);
    }

    /**
     * 默认序列化对象的方法
     */
    public String toJson() {
        return JSONUtil.objToJsonStr(this);
    }

    /**
     * 通过实例获取一个查询对象
     * 
     * @return
     */
    public AnimaQuery<? extends Model> query() {
        if (query_Anima == null) {
            query_Anima = new AnimaQuery<>(this.anima, this.getClass());
        }
        return query_Anima;
    }

    /**
     * @return the dynamicField
     */
    public HashMap<String, Object> getDynamicField() {
        return dynamicField;
    }

    /**
     * @param dynamicField the dynamicField to set
     */
    public void setDynamicField(HashMap<String, Object> dynamicField) {
        this.dynamicField = dynamicField;
    }

    /**
     * 将其它同类对象u覆盖到当前对象
     *
     * @param fromObj
     * @return
     */
    public boolean copyPropertiesToThis(Model fromObj) {
        return ObjectUtil.copyProperties(fromObj, this);
    }


    /**
     * 读取数据库链接
     *
     * @return the anima
     */
    public Anima getAnima() {
        return anima;
    }

    public Model() {
    }

    /**
     * 尝试读取主键值
     * 
     * @return
     */
    public Opt<Object> getPrimaryKeyValue() {
        Opt<Object> keyValue = new Opt<>();
        try {
            keyValue.of(AnimaUtils.getPrimaryKey(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyValue;
    }

    /**
     * 当垃圾回收器释放数据的时候删除注册的自动更新任务
     */
    @Override
    protected void finalize() throws Throwable {
        disableAutoSyncFormDB();
    }

    Consumer<RowChangeEvent> updatedConsumer = new Consumer<RowChangeEvent>() {

        @Override
        public void accept(RowChangeEvent t) {
            Opt<? extends Model> newObj = t.getRowJsonModel(c);
            if (newObj.notNull_()) {
                Opt<Object> nowModelPrimaryKeyValue_ = getPrimaryKeyValue();
                if (nowModelPrimaryKeyValue_.isNull_()) {
                    // 如果当前model没有id则不同步
                    return;
                }
                Model newModel = newObj.get();
                Opt<Object> newModelPrimaryKeyValue_ = newModel.getPrimaryKeyValue();

                if (newModelPrimaryKeyValue_.isNull_()) {
                    // 如果新更新的数据没有id也不同不
                    return;
                }
                // 判断是否和当前id一致
                Object newModelPrimaryKeyValue = newModel.getPrimaryKeyValue().get();
                Object nowModelPrimaryKeyValue = nowModelPrimaryKeyValue_.get();
                // 数据类型一致
                if (newModelPrimaryKeyValue.getClass() == nowModelPrimaryKeyValue.getClass()) {
                    // 暂时只支持Int和String的id同步
                    if (newModelPrimaryKeyValue instanceof Integer) {
                        if (((Integer) newModelPrimaryKeyValue).intValue() == ((Integer) nowModelPrimaryKeyValue)
                                .intValue()) {
                            // 进行更新,覆盖旧的数据
                            copyPropertiesToThis(newModel);
                        }
                    } else if (newModelPrimaryKeyValue instanceof String) {
                        if (newModelPrimaryKeyValue.toString() == nowModelPrimaryKeyValue.toString()) {
                            // 进行更新,覆盖旧的数据
                            copyPropertiesToThis(newModel);
                        }
                    }
                }
            }
        }

    };

    /**
     * 如果数据库条目更改则尝试自动从数据库进行更新复制属性,默认会在对象创建后的100s后自动启用
     */
    public void autoSyncFormDB() {
        // 监控主机表,如果当前对象更改则自动刷新
        if (getAnima() != null && !startedAutoSyncFromDB) {
            Opt<TableMoniter> tableMonitor = getAnima().getTableMonitor();
            if (tableMonitor.notNull_()) {
                String nowTableName = AnimaCache.getTableName(anima, c);
                tableMonitor.get().addOnRowUpdated(nowTableName, updatedConsumer);
                startedAutoSyncFromDB = true;
            }
        }

    }

    /**
     * 此函数用于自动同步同步list的insert和delete,可以在此函数中来维护model的一对多关系
     */
    public void autoSyncList() {

    }

    /**
     * 如果数据库条目更改则尝试自动从数据库进行更新复制属性
     */
    public void disableAutoSyncFormDB() {
        if (getAnima() != null) {
            // 监控主机表,如果当前对象更改则自动刷新
            Opt<TableMoniter> tableMonitor = getAnima().getTableMonitor();
            if (tableMonitor.notNull_()) {
                String nowTableName = AnimaCache.getTableName(anima, c);
                tableMonitor.get().removeListenerTask(nowTableName, updatedConsumer);
                startedAutoSyncFromDB = false;
            }
        }

    }

    /**
     * 判断和另外一个model是否拥有相同的主键tostring值,注意,他们类型必须一致
     * 
     * @param model
     * @return
     */
    public boolean eqPrimaryKeyValueString(Model model) {
        if (model == null) {
            return false;
        }
        if (!model.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
            return false;
        }
        Opt<Object> thisPrimaryKeyValue = getPrimaryKeyValue();
        Opt<Object> primaryKeyValue = getPrimaryKeyValue();

        if (primaryKeyValue.notNull_() && thisPrimaryKeyValue.notNull_()) {
            return primaryKeyValue.get().toString().equals(thisPrimaryKeyValue.toString());
        }
        return false;
    }

}
