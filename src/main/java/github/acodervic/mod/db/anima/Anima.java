
package github.acodervic.mod.db.anima;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.db.anima.enums.ErrorCode.IS_NULL;
import static github.acodervic.mod.db.anima.utils.Functions.ifReturnOrThrow;
import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import github.acodervic.mod.data.ArrayUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.db.DataBaseTypeEnum;
import github.acodervic.mod.db.TableMoniter;
import github.acodervic.mod.db.anima.core.AnimaCache;
import github.acodervic.mod.db.anima.core.AnimaQuery;
import github.acodervic.mod.db.anima.core.Atomic;
import github.acodervic.mod.db.anima.core.Connection;
import github.acodervic.mod.db.anima.core.JDBC;
import github.acodervic.mod.db.anima.core.ResultKey;
import github.acodervic.mod.db.anima.core.dml.Delete;
import github.acodervic.mod.db.anima.core.dml.Select;
import github.acodervic.mod.db.anima.core.dml.Update;
import github.acodervic.mod.db.anima.core.functions.TypeFunction;
import github.acodervic.mod.db.anima.dialect.Dialect;
import github.acodervic.mod.db.anima.dialect.MySQLDialect;
import github.acodervic.mod.db.anima.event.EventActionEnum;
import github.acodervic.mod.db.anima.event.RowChangeEvent;
import github.acodervic.mod.db.anima.exception.AnimaException;
import github.acodervic.mod.db.anima.utils.AnimaUtils;

/**
 * Anima
 *
 * @author biezhi
 * @date 2018/3/13
 */

public class Anima {

    static final Logger log = Logger.getLogger(Anima.class.getName());

    /**
     * 数据库操作的真实sql2o对象.
     */
    private JDBC jdbc;

    /**
     * Global table prefix
     */
    private String tablePrefix;

    /**
     * 默认数据库为mysql
     */
    private Dialect dialect = new MySQLDialect();

    /**
     * The type of rollback when an exception occurs, default by RuntimeException
     */
    private Class<? extends Exception> rollbackException = RuntimeException.class;

    /**
     * SQL performance statistics are enabled, which is enabled by default, and
     * outputs the elapsed time required.
     */
    private boolean enableSQLStatistic = false;

    /**
     * use the limit statement of SQL and use "limit ?" when enabled, the way to
     * retrieve a fixed number of rows.
     */
    private boolean useSQLLimit = true;

    /**
     * 全局Anima打开数据库的实例对象,当使用open之后会自动填充
     */
    private static Anima globalConnectionInstance;

    Opt<TableMoniter> tableMonitor = new Opt<TableMoniter>();// 表格监视器
    DataBaseTypeEnum dataBaseType;// 数据库类型枚举
    Boolean enableAutoUpdateDBTableToList = true;// 如果开启,每一个model在初始化的时候都会自动调用 startAutoUpdateListFormDB
    // (默认需要重写)方法,用来更新model绑定的子属性
    Boolean enableAutoUpdateModelFromDB=false;//控制每个model对象是否支持表格监听器,当表格调用update方法之后自动同步数据到内存对象
    boolean enableLog = true;// 默认启动日志
 

    /**
     * 
     * @return
     */
    public static Anima of() {
        return ifReturnOrThrow(null != globalConnectionInstance && null != globalConnectionInstance.jdbc,
                globalConnectionInstance, new AnimaException(IS_NULL));
    }

    /**
     * @return the jdbc
     */
    public JDBC getJdbc() {
        return jdbc;
    }


    /**
     * @param jdbc the jdbc to set
     */
    public void setJdbc(JDBC jdbc) {
        this.jdbc = jdbc;
    }


    /**
     * 对sql2o的封装,打开一个sql2o的连接对象,并返回anima实例
     * 
     * @param sql2o sql2o 对象
     * @return Anima实例
     */
    public static Anima open(JDBC jdbc) {
        Anima anima = new Anima();
        anima.setJdbc(jdbc);
        return anima;
    }

    /**
     * 将当前连接设置为,设置全局数据库连接
     * 
     * @param anima
     */
    public void setGlobalConnectionInstance() {
        // 设置全局数据库连接
        globalConnectionInstance = this;
    }

    /**
     * 获得一个操作连接
     * @return
     */
    public Connection getConn() {
        return getJdbc().newConnection();
    }

    /**
     * 进行一次事物操作,如果执行出现异常则回滚所有操作.
     * 默认异常为rollbackException变量中保存的异常,可以自己设置通过rollbackException函数
     * 
     * @param runnable the code snippet to execute.
     * @return Atomic
     */
    public  Atomic atomic(Consumer<Connection> runnable,boolean isRollback) {
        Connection conn = getConn();
        try {
            conn.executeTransaction(runnable,isRollback);
            return Atomic.ok();
        } catch (Exception e) {
            log.warning(e.getMessage() + e);
            return Atomic.error(e).rollback(isRollback);
        } finally{
            conn.close();
        }
    }

    /**
     * . 社会子回滚异常的类型以触发事物回滚
     * 
     * @param rollbackException roll back exception type
     * @return Anima
     */
    public Anima rollbackException(Class<? extends Exception> rollbackException) {
        this.rollbackException = rollbackException;
        return this;
    }

    /**
     * 读取回滚事务的异常
     * 
     * @return 回滚异常
     */
    public Class<? extends Exception> rollbackException() {
        return this.rollbackException;
    }

    /**
     * 设置全局表前缀, 如 "t_" 在读取modle实例的时候会自动在表名前面加上前缀;
     *
     * @param tablePrefix 表明前缀
     * @return Anima实例
     */
    public Anima tablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        return this;
    }

    public String tablePrefix() {
        return this.tablePrefix;
    }

    /**
     * 指定数据库使用的语法方言.
     *
     * @param dialect 方言 @see Dialect
     * @return Anima实例
     */
    public Anima dialect(Dialect dialect) {
        this.dialect = dialect;
        return this;
    }

    /**
     * 读取当前打开数据库的方言
     * 
     * @return 方言
     */
    public Dialect dialect() {
        return this.dialect;
    }

    /**
     * 设置是否启用sql统计信息.
     *
     * @param enableSQLStatistic sql statistics
     * @return Anima
     */
    public Anima enableSQLStatistic(boolean enableSQLStatistic) {
        this.enableSQLStatistic = enableSQLStatistic;
        return this;
    }

    /**
     * 是否启用sql统计信息.启用后则会统计每次sql操作的时间
     * 
     * @return
     */
    public boolean isEnableSQLStatistic() {
        return this.enableSQLStatistic;
    }

    /**
     * Set the use of SQL limit.
     *
     * @param useSQLLimit use sql limit
     * @return Anima
     */
    public Anima useSQLLimit(boolean useSQLLimit) {
        this.useSQLLimit = useSQLLimit;
        return this;
    }

    /**
     * 是否使用sql限制
     * 
     * @return
     */
    public boolean isUseSQLLimit() {
        return this.useSQLLimit;
    }

 

    /**
     * 通过模型类打开当前连接对象,等同select.from(xx.class)
     *
     * @param <T>
     * @param modelClass
     * @return
     */
    public <T extends Model> AnimaQuery<T> selectFrom(Class<T> modelClass) {
        return select().from(modelClass);
    }

    /**
     * 打开全局连接查询对象,以查询某些指定列
     *
     * @param columns column names
     * @return Select
     */
    public Select select(String columns) {
        return new Select(this, columns);
    }

    /**
     * 使用局部数据库连接
     *
     * @param functions column lambdas
     * @return Select
     */
    public <T extends Model, R> Select select(TypeFunction<T, R>... functions) {
        return select(Arrays.stream(functions).map(AnimaUtils::getLambdaColumnName).collect(joining(", ")));
    }

    /**
     * 使用当前连接,打开更新对象.
     *
     * @return Update
     */
    public Update update() {
        return new Update(this);
    }

    /**
     * 使用当前数据库连接,打开一个更新操作,等同update().From(xx.class)
     *
     * @param <T>
     * @param modelClass
     * @return
     */
    public <T extends Model> AnimaQuery<T> updateFrom(Class<T> modelClass) {
        return update().from(modelClass);
    }

    /**
     * 使用当前数据库连接,打开删除操作.
     *
     * @return Delete
     */
    public Delete delete() {
        return new Delete(this);
    }

    /**
     * 使用当前数据库连接打开删除操作.等同delete().From(xx.class)
     *
     * @return Delete
     */
    public <T extends Model> AnimaQuery<T> deleteFrom(Class<T> modelClass) {
        return new Delete(this).from(modelClass);
    }

    /**
     * 使用当前数据库连接,保存model记录
     *
     * @param model database model
     * @param <T>
     * @return ResultKey
     */
    public <T extends Model> ResultKey save(T model) {
        nullCheck(model);
        return model.bindDatabaseSource(this).save();
    }

    /**
     * 使用当前数据库连接批量保存 model记录
     *
     * @param models model list
     * @param <T>
     */
    public <T extends Model> void saveBatch(List<T> models,boolean isErrorRollback) {
            new AnimaQuery<T>(this).saveBatch(models);
    }

    /**
     * 使用当前数据库连接,批量删除 model 记录
     *
     * @param model model class type
     * @param ids   mode primary id array
     * @param <T>
     * @param <S>
     */
    public <T extends Model, S extends Serializable> void deleteBatch(Class<T> model,boolean isErrorRollback, S... ids) {

                atomic(conn  ->{
                    for (int i = 0; i < ids.length; i++) {
                        deleteById(model, ids[i]);
                    }
                }, isErrorRollback).catchException(e -> log("批量删除失败, 消息: " + e.getMessage()));
    }

    /**
     * 使用全局数据库连接进行批量删除
     *
     * @param model  model class type
     * @param idList mode primary id list
     * @param <T>
     * @param <S>
     */
    public <T extends Model, S extends Serializable> void deleteBatch(Class<T> model,boolean isErrorRollback, List<S> idList) {
        deleteBatch(model, isErrorRollback,AnimaUtils.toArray(idList));
    }

    /**
     * 使用当前数据库连接 通过model的id删除数据库记录
     *
     * @param model model Classs
     * @param id    主键值
     * @param <T>
     * @return
     */
    public <T extends Model> int deleteById(Class<T> model, Serializable id) {
        return new AnimaQuery<>(this, model).deleteById(id);
    }

    /**
     * 使用当前数据库连接执行sql语句,返回受影响的行数
     *
     * @param sql    sql语句
     * @param params 参数
     * @return 返回受影响的行数
     */
    public int execute(String sql, Object... params) {
        return new AnimaQuery<>(this).execute(sql, params);
    }

    /**
     * 基于流的AnimaQuery查询
     *
     * @param <T>
     * @return 
     * @return
     */
    public <T extends Model, S extends Serializable>  List<T> queryList(Class<T> type,String sql, Object[] params) {
        return new AnimaQuery<>(this).queryList(type,sql, params);
    }


        /**
     * 基于流的AnimaQuery查询
     *
     * @param <T>
     * @return
     */
    public List<Map<String, Object>> queryMap(String sql, Object[] params) {
        return new AnimaQuery<>(this).queryListMap(sql, params);
    }
    /**
     * 基于流的AnimaQuery查询
     *
     * @param <T>
     * @return
     */
    public List<Map<String, Object>> queryMap(String sql) {
        Object[] emptyarg = {};
        return new AnimaQuery<>(this).queryListMap(sql, emptyarg);
    }

    /**
     * @return the tableMonitor
     */
    public Opt<TableMoniter> getTableMonitor() {
        return tableMonitor;
    }

    /**
     * @param tableMonitor the tableMonitor to set
     */
    public void setTableMonitor(TableMoniter tableMonitor) {
        this.tableMonitor.of(tableMonitor);
    }

    /**
     * 为一个表格注册一个事件监听器
     * 
     * @param actionEnum
     * @param modelClass
     * @param onEvent
     */
    public void registTableListener(EventActionEnum actionEnum, Class<? extends Model> modelClass,
            Consumer<Opt<? extends Model>> onEvent) {
        if (this.tableMonitor.notNull_() && actionEnum != null) {
            str tableName = new str(AnimaCache.getTableName(this, modelClass));
            Anima db = this;
            Consumer<RowChangeEvent> eventAction = new Consumer<RowChangeEvent>() {
                @Override
                public void accept(RowChangeEvent t) {
                    if (tableName.eqAllIgnoreCase(t.getTableName())) {
                        Opt<? extends Model> model = t.getRowJsonModel(modelClass);
                        if (model.notNull_()) {
                            model.get().bindDatabaseSource(db);
                            onEvent.accept(model);
                        }
                    }
                }
            };
            TableMoniter tableMoniter = this.tableMonitor.get();
            if (EventActionEnum.UPDATE == actionEnum) {
                tableMoniter.addOnRowUpdated(tableName.to_s(), eventAction);
            } else if (EventActionEnum.INSERT == actionEnum) {
                tableMoniter.addOnRowInserted(tableName.to_s(), eventAction);
            } else if (EventActionEnum.DELETE == actionEnum) {
                tableMoniter.addOnRowDeleted(tableName.to_s(), eventAction);
            } else if (EventActionEnum.CHANGE == actionEnum) {
                tableMoniter.addOnRowChanged(tableName.to_s(), eventAction);
            }
        }
    }

    /**
     * 启动用数据库表格监视器
     * 
     * @return
     */
    public boolean startTableMoniter() {
        if (this.tableMonitor.notNull_()) {
            return this.tableMonitor.get().start();
        }
        return false;
    }

    /**
     * @param dataBaseType the dataBaseType to set
     */
    public void setDataBaseType(DataBaseTypeEnum dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    /**
     * @return the dataBaseType
     */
    public DataBaseTypeEnum getDataBaseType() {
        return dataBaseType;
    }

    /**
     * 开启自动绑定子实体列表模式
     */
    public void enableAutoUpdateDBTableToList() {
        this.enableAutoUpdateDBTableToList = true;
    }

    /**
     * 关闭自动绑定子实体列表模式
     */
    public void disenableAutoUpdateDBTableToList() {
        this.enableAutoUpdateDBTableToList = false;
    }

    /**
     *
     * 对表格进行监控,并同步数据到list,注意当每次监控到更改时,都会查询整个表格并返回,需要在 updateFun
     *
     * 对返回的表格进行过滤.(如果表特别大,请勿使用此函数)
     *
     *
     * @param <T>
     * @param dataList
     * @param modelClass 必须有id字段
     * @param updateFun  当检测到更新时候会返回当前数据库监控表的数据作为输入,并
     *                   返回一个新的数据集,作为基础和原始的dataList进行绑定和刷新
     * @param dataBase
     */
    public <T extends Model> void onTableInserted(Class<T> modelClass) {

    }

    public void disableLog() {
        this.enableLog = false;
    }

    public void enableLog() {
        this.enableLog = true;
    }

    /**
     * 启用通过表格监听器自动维护对象属性
     * @return the enableAutoUpdateModelFromDB
     */
    public void   enableAutoUpdateModelFromDB() {
          enableAutoUpdateModelFromDB=true;
    }

    /**
     * 禁用通过表格监听器自动维护对象属性
     * @return the enableAutoUpdateModelFromDB
     */
    public void  disableAutoUpdateModelFromDB() {
        enableAutoUpdateModelFromDB=false;
    }
    /**
     * 输出日志
     * 
     * @param msg
     */
    public void log(String... msg) {
        if (enableLog) {
            log.info(ArrayUtil.toStr(msg, null));
        }
    }

    /**
     * 
     */
    public Anima() {
    }

}