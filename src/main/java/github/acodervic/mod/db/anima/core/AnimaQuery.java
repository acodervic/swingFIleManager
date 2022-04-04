
package github.acodervic.mod.db.anima.core;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.SqlStatement;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.db.anima.Anima;
import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.core.functions.TypeFunction;
import github.acodervic.mod.db.anima.enums.DMLType;
import github.acodervic.mod.db.anima.enums.ErrorCode;
import github.acodervic.mod.db.anima.enums.OrderBy;
import github.acodervic.mod.db.anima.exception.AnimaException;
import github.acodervic.mod.db.anima.page.Page;
import github.acodervic.mod.db.anima.page.PageRow;
import github.acodervic.mod.db.anima.utils.AnimaUtils;

import static github.acodervic.mod.db.anima.core.AnimaCache.*;
import static github.acodervic.mod.db.anima.utils.Functions.*;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;

/**
 * 数据库操作核心类,主要是对sql2o的封装,通过modelClass等属性和方便的操作sql2o
 *
 * @author biezhi
 */
public class AnimaQuery<T extends Model> {
    static final Logger log = Logger.getLogger(AnimaQuery.class.getName());
    /**
     * 当前查询对象使用的数据库对象,
     */
    private Anima anima;

    /**
     * java模型,对应数据库表名.
     */
    private Class<T> modelClass;



    /**
     * 储存条件子句.
     */
    private StringBuilder conditionSQL = new StringBuilder();

    /**
     * 存储ORDER BY子句.
     */
    private StringBuilder orderBySQL = new StringBuilder();

    /**
     * Store the 列名s to be excluded.
     */
    private List<String> excludedColumns = new ArrayList<>(8);

    /**
     * 存储参数的list在执行的时候会填充?
     */
    private List<Object> paramValues = new ArrayList<>(8);

    /**
     * 存储更新的列.
     */
    private Map<String, Object> updateColumns = new LinkedHashMap<>(8);

    /**
     * 您是否使用SQL进行限制操作并使用“限制”？ 如果启用。
     *默认情况下，查询数据的方法是打开的，并且部分数据库不支持此操作。
     */
    private boolean isSQLLimit;

    /**
     * 当前查询是否是子定义sql语句
     */
    private boolean useSQL;

    /**
     * 一些指定的查询列, 比如 “uid, name, age”
     */
    private String selectColumns;

    /**
     * 主键列名
     */
    private String primaryKeyColumn;

    /**
     * 模型表名称
     */
    private String tableName;

    /**
     * @see 当前sql流的前置查询操作,select,update,delete
     */
    private DMLType dmlType;

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }
    /**
     * 联表参数
     */
    private List<JoinParam> joinParams = new ArrayList<>();

    private Consumer<T> allOrOneProcessFun_opt;// 此函数可以对all()和one()函数后查询到的结果进行处理,可能为null

    /**
     * 设置all函数和one函数之前的处理函数
     *
     * @param allOrOneProcessFun_opt the allOrOneProcessFun_opt to set
     * @return
     */
    public AnimaQuery<T> setAllOrOneProcessFun_opt(Consumer<T> allOrOneProcessFun_opt) {
        this.allOrOneProcessFun_opt = allOrOneProcessFun_opt;
        return this;
    }

    public AnimaQuery(Anima anima, DMLType dmlType) {
        this.dmlType = dmlType;
        this.anima = anima;
    }

    /**
     * 构建i一个查询对象基于model,和指定数据库
     *
     * @param sql2o      指定数据库连接
     * @param modelClass
     */
    public AnimaQuery(Anima anima, Class<T> modelClass) {
        this.anima = anima;
        this.parse(modelClass);
    }

    /**
     * 构建i一个查询对象基于model,和指定数据库
     *
     * @param sql2o 指定数据库连接,如果为null会尝试使用全局连接
     */
    public AnimaQuery(Anima anima) {
        this.anima = anima;
        if (modelClass != null) {
            this.parse(modelClass);
        }
    }

    /**
     * 通过modelClass来构建查询对象
     * @param modelClass
     * @return
     */
    public AnimaQuery<T> parse(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.tableName = AnimaCache.getTableName(getAnima(), modelClass);
        this.primaryKeyColumn = AnimaCache.getPKColumn(modelClass);
        return this;
    }

    /**
     * 排除一些列
     *
     * @param columnNames 表列名
     * @return AnimaQuery
     */
    public AnimaQuery<T> exclude(String... columnNames) {
        Collections.addAll(excludedColumns, columnNames);
        return this;
    }

    /**
     * 按lamba表达式排除列
     *
     * @param functions lambda 列
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> exclude(TypeFunction<T, R>... functions) {
        String[] columnNames = Arrays.stream(functions)
                .map(AnimaUtils::getLambdaColumnName)
                .collect(toList())
                .toArray(new String[functions.length]);
        return this.exclude(columnNames);
    }

    /**
     * 设置查询指定列.
     *
     * @param columns 表列名
     * @return AnimaQuery
     */
    public AnimaQuery<T> select(String columns) {
        if (null != this.selectColumns) {
            throw new AnimaException("Select method can only be called once.");
        }
        this.selectColumns = columns;
        return this;
    }

    /**
     * where 条件,指定sql,并追加AND语句到当前操作流sql的后面
     *
     * @param statement like "age > ?" "name = ?"
     * @return AnimaQuery
     */
    public AnimaQuery<T> where(String statement) {
        conditionSQL.append(" AND ").append(statement);
        return this;
    }

    /**
     * where 条件, 设置sql同时设置一个值并追加AND语句到当前操作流sql的后面
     *
     * @param statement 如 "age > ?" "name = ?"
     * @param value     值
     * @return AnimaQuery
     */
    public AnimaQuery<T> where(String statement, Object value) {
        conditionSQL.append(" AND ").append(statement);
        if (!statement.contains("?")) {
            conditionSQL.append(" = ?");
        }
        paramValues.add(value);
        return this;
    }



    /**
     * where 条件, 设置sql同时设置一个值并追加AND语句到当前操作流sql的后面
     *
     * @param statementSql 如 "age > ?" "name = ?"
     * @param value     值
     * @return AnimaQuery
     */
    public AnimaQuery<T> whereSql(String statementSql, Object... parms) {
        conditionSQL.append(" AND ").append(statementSql);
        if (!statementSql.contains("?")) {
            conditionSQL.append(" = ?");
            for (Object parm : parms) {
                paramValues.add(parm);
            }
        }
        return this;
    }

    /**
     * 使用lamba设置列名并追加AND语句到当前操作流sql的后面
     *
     * @param function lambda 表达式, 使用 Model::getXXX
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> where(TypeFunction<T, R> function) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        conditionSQL.append(" AND ").append(columnName);
        return this;
    }

    /**
     *使用lambda设置列名,在同时设置值,sql生成为column = ?并追加到当前操作流sql的后面
     * @param function lambda表达式, 如 Model::getXXX
     * @param value    列值
     * @param <S>
     * @param <R>
     * @return AnimaQuery
     */
    public <S extends Model, R> AnimaQuery<T> where(TypeFunction<S, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        conditionSQL.append(" AND ").append(columnName).append(" = ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 根据模型设置where参数，并生成类似于where的sql: age = ? and name = ?并追加到当前操作流sql的后面
     *
     * @param model
     * @return AnimaQuery
     */
    public AnimaQuery<T> where(T model) {
        Field[] declaredFields = model.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            //根据模型通过反射get读取内部属性值
            Object value = AnimaUtils.invokeMethod(model, getGetterName(declaredField.getName()), AnimaUtils.EMPTY_ARG);
            if (null == value) {
                continue;
            }
            if (declaredField.getType().equals(String.class) && AnimaUtils.isEmpty(value.toString())) {
                continue;
            }
            String columnName = AnimaCache.getColumnName(declaredField);
            this.where(columnName, value);
        }
        return this;
    }

    /**
     * Equals 语句
     *
     * @param value 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> eq(Object value) {
        conditionSQL.append(" = ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 生成 "IS NOT NULL" 语句,并追加到当前操作流sql的后面
     *
     * @return AnimaQuery
     */
    public AnimaQuery<T> notNull() {
        conditionSQL.append(" IS NOT NULL");
        return this;
    }

    /**
     * 根据列名生成 " WHERE xxx IS NULL" 语句,并追加到当前操作流sql的后面
     * 
     * @param <R>
     * @param function
     * @return
     */
    public <R> AnimaQuery<T> isNull(TypeFunction<T, R> function) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        ifNullThrow(columnName, new AnimaException(ErrorCode.FUNCTION_COLUMN_NAME_INVALID+function.toString()) );
        conditionSQL.append("WHERE ").append(columnName).append(" IS  NULL");
        return this;
    }



    /**
     * * 根据列名生成 " AND xxx IS NULL" 语句,并追加到当前操作流sql的后面
     * 
     * @param <R>
     * @param function
     * @return
     */
    public <R> AnimaQuery<T> andIsNull(TypeFunction<T, R> function) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        ifNullThrow(columnName, new AnimaException(ErrorCode.FUNCTION_COLUMN_NAME_INVALID+function.toString()) );
        conditionSQL.append(" AND ").append(columnName).append("  IS  NULL");
        return this;
    }

    /**
     * 根据列名生成 "IS NOT NULL" 语句,并追加到当前操作流sql的后面
     * @param <R>
     * @param function
     * @return
     */
    public <R> AnimaQuery<T> notNull(TypeFunction<T, R> function) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        ifNullThrow(columnName, new AnimaException(ErrorCode.FUNCTION_COLUMN_NAME_INVALID+function.toString()) );
        conditionSQL.append("WHERE ").append(columnName).append("  IS NOT NULL");
        return this;
    }
    
    /**
     * 生成 AND 语句, 同时设置值,并追加到当前操作流sql的后面
     *
     * @param statement 条件
     * @param value     列值
     * @return
     */
    public AnimaQuery<T> and(String statement, Object value) {
        return this.where(statement, value);
    }

    /**
     * 使用lambda生产and语句,并追加到当前操作流sql的后面
     *
     * @param function lambda表示的别名
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> and(TypeFunction<T, R> function) {
        return this.where(function);
    }

    /**
     * 通过lambda生成AND语句, 同时设置值,并追加到当前操作流sql的后面
     *
     * @param function 由lmbda表达的列名
     * @param value    列值
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> and(TypeFunction<T, R> function, Object value) {
        return this.where(function, value);
    }

    /**
     * 生成 OR 语句, 同时设置值,并追加语句到当前操作流sql的后面
     *
     * @param statement like "name = ?" "age > ?"
     * @param value     列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> or(String statement, Object value) {
        conditionSQL.append(" OR (").append(statement);
        if (!statement.contains("?")) {
            conditionSQL.append(" = ?");
        }
        conditionSQL.append(')');
        paramValues.add(value);
        return this;
    }
    
    /**
     * 生成"or = ?"语句,可以通过方法引用传递列名
     * @param <R>
     * @param function 方法引用
     * @param value 参数值
     * @return
     */
        public <R> AnimaQuery<T> orEq(TypeFunction<T, R> function, Object value) {

        String columnName = AnimaUtils.getLambdaColumnName(function);
        ifNullThrow(columnName, new AnimaException(ErrorCode.FUNCTION_COLUMN_NAME_INVALID+function.toString()) );
        conditionSQL.append(" OR ").append(columnName).append(" = ?");
        paramValues.add(value);
        return this;
    }

        /**
     * 生成"or != ?"语句,可以通过方法引用传递列名
     * @param <R>
     * @param function 方法引用
     * @param value 参数值
     * @return
     */
    public <R> AnimaQuery<T> orNotEq(TypeFunction<T, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        ifNullThrow(columnName, new AnimaException(ErrorCode.FUNCTION_COLUMN_NAME_INVALID+function.toString()) );
        conditionSQL.append(" OR ").append(columnName).append(" != ?");
        paramValues.add(value);
        return this;
    }



        /**
     * 生成 OR 语句, 同时设置值,并追加语句到当前操作流sql的后面
     *
     * @param statement like "name = ?" "age > ?"
     * @param value     列值
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> orLike(TypeFunction<T, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        ifNullThrow(columnName, new AnimaException(ErrorCode.FUNCTION_COLUMN_NAME_INVALID+function.toString()) );
        conditionSQL.append(" OR ").append(columnName).append(" LIKE ?");
        paramValues.add(value);
        return this;
    }
    
 

        /**
     * 生成 OR 语句, 同时设置值,并追加语句到当前操作流sql的后面
     *
     * @param statement like "name = ?" "age > ?"
     * @param value     列值
     * @return AnimaQuery
     */
        public <R> AnimaQuery<T> orNotLike(TypeFunction<T, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        ifNullThrow(columnName, new AnimaException("orLike传递的function没有提供列名"+function.toString()) );
        conditionSQL.append(" OR ").append(columnName).append(" NOT LIKE ?");
        paramValues.add(value);
        return this;
    }
    

    /**
     * 生成 "AND !=" 语句, 同时设置值 value,并追加语句到当前操作流sql的后面
     *
     * @param columnName 列名 [sql]
     * @param value      列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> notEq(String columnName, Object value) {
        conditionSQL.append(" AND ").append(columnName).append(" != ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 使用lambda生成 "!=" 语句, 同时设置值,并追加到当前操作流sql的后面
     *
     * @param function 列名使用lambda
     * @param value    列值
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> notEq(TypeFunction<T, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.notEq(columnName, value);
    }

    /**
     * 生成"!=" 语句, 同时设置值,并追加到当前操作流sql的后面
     *
     * @param value 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> notEq(Object value) {
        conditionSQL.append(" != ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 生成"AND != ''" 语句,并追加到当前操作流sql的后面
     *
     * @param columnName 列名
     * @return AnimaQuery
     */
    public AnimaQuery<T> notEmpty(String columnName) {
        conditionSQL.append(" AND ").append(columnName).append(" != ''");
        return this;
    }

    /**
     * 生成"!= ''" 语句 使用lambda,并追加语句到当前操作流sql的后面
     *
     * @param function 列名使用lambda
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> notEmpty(TypeFunction<T, R> function) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.notEmpty(columnName);
    }

    /**
     * 生成"!= ''" 语句,并追加语句到当前操作流sql的后面
     *
     * @return AnimaQuery
     */
    public AnimaQuery<T> notEmpty() {
        conditionSQL.append(" != ''");
        return this;
    }

    /**
     * 生成"AND 列名 IS NOT NULL" 语句并追加语句到当前操作流sql的后面
     *
     * @param columnName 列名
     * @return
     */
    public AnimaQuery<T> notNull(String columnName) {
        conditionSQL.append(" AND ").append(columnName).append(" IS NOT NULL");
        return this;
    }

    /**
     * 生成"AND  列名 like ? "语句, 同时设置值并追加语句到当前操作流sql的后面
     *
     * @param columnName 列名
     * @param value      列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> like_(String columnName, Object value) {
        conditionSQL.append(" AND ").append(columnName).append(" LIKE ?");
        paramValues.add(value);
        return this;
    }

/**
 *  生成like statement使用lambda, 同时设置值并追加语句到当前操作流sql的后面
 * @param <R>
 * @param function
 * @param value
 * @return
 */
    public <R> AnimaQuery<T> like(TypeFunction<T, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.like_(columnName, value);
    }

    /**
     * 生成"LIKE ?" 语句, 同时设置值,并追加语句到当前操作流sql的后面
     *
     * @param value 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> like(Object value) {
        conditionSQL.append(" LIKE ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 生成between(AND 列名 BETWEEN ? and ?)语句, 同时设置值,并追加语句到当前操作流sql的后面
     *
     * @param columnName 列名
     * @param a          first range value
     * @param b          second range value
     * @return AnimaQuery
     */
    public AnimaQuery<T> between(String columnName, Object a, Object b) {
        conditionSQL.append(" AND ").append(columnName).append(" BETWEEN ? and ?");
        paramValues.add(a);
        paramValues.add(b);
        return this;
    }

    /**
     * 使用lambda生成between 语句, 同时设置值
     *
     * @param function 使用lambda生成列
     * @param a        first range value
     * @param b        second range value
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> between(TypeFunction<T, R> function, Object a, Object b) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.between(columnName, a, b);
    }

    /**
     * 生成between 语句(" BETWEEN ? and ?") ,同时设置值并追加到当前操作流sql的后面
     *
     * @param a first range value
     * @param b second range value
     * @return AnimaQuery
     */
    public AnimaQuery<T> between(Object a, Object b) {
        conditionSQL.append(" BETWEEN ? and ?");
        paramValues.add(a);
        paramValues.add(b);
        return this;
    }

    /**
     * 生成">" 语句(" AND 列名 > ?"), 同时设置值并追加到当前操作流sql的后面
     *
     * @param columnName table 列名 [sql]
     * @param value      列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> gt(String columnName, Object value) {
        conditionSQL.append(" AND ").append(columnName).append(" > ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 使用lambda生成">" 语句, 同时设置值并追加到当前操作流sql的后面
     *
     * @param function 列名使用lambda
     * @param value    列值
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> gt(TypeFunction<T, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.gt(columnName, value);
    }

    /**
     * 生成">" 语句(" > ?"),同时设置值并追加到当前操作流sql的后面
     *
     * @param value 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> gt(Object value) {
        conditionSQL.append(" > ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 生成">=" 语句(" >= ?")同时设置值并追加到当前操作流sql的后面
     *
     * @param value 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> gte(Object value) {
        conditionSQL.append(" >= ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 使用lambda生成">=" 语句, 同时设置值并追加到当前操作流sql的后面
     *
     * @param function 列名使用lambda
     * @param value    列值
     * @param <R>
     * @return AnimaQuery
     */
    public <S extends Model, R> AnimaQuery<T> gte(TypeFunction<S, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.gte(columnName, value);
    }

    /**
     * 生成"<"语句(" < ?")同时设置值并追加到当前操作流sql的后面
     *
     * @param value 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> lt(Object value) {
        conditionSQL.append(" < ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 使用lambda生成"<" 语句, 同时设置值并追加到当前操作流sql的后面
     *
     * @param function 列名使用lambda
     * @param value    列值
     * @param <R>
     * @return AnimaQuery
     */
    public <S extends Model, R> AnimaQuery<T> lt(TypeFunction<S, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.lt(columnName, value);
    }

    /**
     * 生成"<=" 语句("<= ?")同时设置值并追加到当前操作流sql的后面
     *
     * @param value 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> lte(Object value) {
        conditionSQL.append(" <= ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 使用lambda生成"<=" 语句, 同时设置值并追加到当前操作流sql的后面
     *
     * @param function 使用lambda生成列名
     * @param value    列值
     * @param <R>
     * @return AnimaQuery
     */
    public <S extends Model, R> AnimaQuery<T> lte(TypeFunction<S, R> function, Object value) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.lte(columnName, value);
    }

    /**
     * 生成">=" 语句("AND  列名>= ?"), 同时设置值并追加到当前操作流sql的后面
     *
     * @param column  列名 [sql]
     * @param value  列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> gte(String column, Object value) {
        conditionSQL.append(" AND ").append(column).append(" >= ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 生成"<" 语句(" AND 列名 < ?"), 同时设置值并追加到当前操作流sql的后面
     *
     * @param column  列名 [sql]
     * @param value  列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> lt(String column, Object value) {
        conditionSQL.append(" AND ").append(column).append(" < ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 生成"<=" 语句, 同时设置值并追加到当前操作流sql的后面
     *
     * @param column  列名 [sql]
     * @param value  列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> lte(String column, Object value) {
        conditionSQL.append(" AND ").append(column).append(" <= ?");
        paramValues.add(value);
        return this;
    }

    /**
     * 生成"in" 语句, 同时设置值并追加到当前操作流sql的后面
     *
     * @param column  列名 [sql]
     * @param args   列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> in(String column, Object... args) {
        if (null == args || args.length == 0) {
            log.warning("Column: {}, query params is empty.");
            return this;
        }
        conditionSQL.append(" AND ").append(column).append(" IN (");
        this.setArguments(args);
        conditionSQL.append(")");
        return this;
    }

    /**
     * 生成"in" ,并追加到当前操作流sql的后面
     *
     * @param args 列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> in(Object... args) {
        if (null == args || args.length == 0) {
            log.warning("Column: {}, query params is empty.");
            return this;
        }
        conditionSQL.append(" IN (");
        this.setArguments(args);
        conditionSQL.append(")");
        return this;
    }

    /**
     * 设置参数,并追加到当前操作流sql的后面
     *
     * @param list in 多个参数值
     * @param <S>
     * @return AnimaQuery
     */
    public <S> AnimaQuery<T> in(List<S> list) {
        return this.in(list.toArray());
    }

    /**
     * 生成"in" 语句, 同时设置值并追加到当前操作流sql的后面
     *
     * @param column 列名
     * @param args   in 多个参数值
     * @param <S>
     * @return AnimaQuery
     */
    public <S> AnimaQuery<T> in(String column, List<S> args) {
        return this.in(column, args.toArray());
    }

    /**
     * 使用lambda生成"in"语句 , 同时设置值并追加到当前操作流sql的后面
     *
     * @param function 列名使用lambda
     * @多个参数值   in 多个参数值
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> in(TypeFunction<T, R> function, Object... values) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.in(columnName, values);
    }

    /**
     * 使用lambda生成"in"语句 , 同时设置值并追加到当前操作流sql的后面
     *
     * @param function 列名使用lambda
     * @多个参数值   in 多个参数值
     * @param <R>
     * @return AnimaQuery
     */
    public <S, R> AnimaQuery<T> in(TypeFunction<T, R> function, List<S> values) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return this.in(columnName, values);
    }

    /**
     * 生成order by 语句并追加到当前操作流sql的后面
     *
     * @param order like "id desc"
     * @return AnimaQuery
     */
    public AnimaQuery<T> order(String order) {
        if (this.orderBySQL.length() > 0) {
            this.orderBySQL.append(',');
        }
        this.orderBySQL.append(' ').append(order);
        return this;
    }

    /**
     * 生成order by 语句
     *
     * @param columnName 列名
     * @param orderBy    order by @see OrderBy
     * @return AnimaQuery
     */
    public AnimaQuery<T> order(String columnName, OrderBy orderBy) {
        if (this.orderBySQL.length() > 0) {
            this.orderBySQL.append(',');
        }
        this.orderBySQL.append(' ').append(columnName).append(' ').append(orderBy.toString());
        return this;
    }

    /**
     * 使用lambda生成order by语句 
     *
     * @param function 列名使用lambda
     * @param orderBy  order by @see OrderBy
     * @param <R>
     * @return AnimaQuery
     */
    public <R> AnimaQuery<T> order(TypeFunction<T, R> function, OrderBy orderBy) {
        String columnName = AnimaUtils.getLambdaColumnName(function);
        return order(columnName, orderBy);
    }

    /**
     * 通过主键查询模型 
     *
     * @param id 主键值
     * @return model 实例
     */
    public T byId(Object id) {
        this.beforeCheck();
        this.where(primaryKeyColumn, id);

        String sql = this.buildSelectSQL(false);

        T model = this.queryOne(modelClass, sql, paramValues);

        ifNotNullThen(model, () -> this.setJoin(Collections.singletonList(model)));
        // 绑定数据源
        if (model != null && allOrOneProcessFun_opt != null) {
            // 处理数据
            allOrOneProcessFun_opt.accept(model);
            model.bindDatabaseSource(getAnima());
        }
        return model;
    }

    /**
     * 通过多个主键查询模型
     *
     * @param ids 主键值
     * @return models实例集合
     */
    public List<T> byIds(Object... ids) {
        this.in(this.primaryKeyColumn, ids);
        return this.all();
    }

    /**
     * 查询单个模型实例
     *
     * @return 一个model实例
     */
    public T one() {
        this.beforeCheck();

        String sql = this.buildSelectSQL(true);

        T model = this.queryOne(modelClass, sql, paramValues);
        if (model != null && allOrOneProcessFun_opt != null) {
            // 处理数据
            allOrOneProcessFun_opt.accept(model);
        }
        if (model != null) {
            model.bindDatabaseSource(getAnima());
        }
        ifThen(null != model && null != joinParams,
                () -> this.setJoin(Collections.singletonList(model)));

        return model;
    }

    /**
     * 查询所有模型实例
     *
     * @return model 集合
     */
    public List<T> all() {
        this.beforeCheck();
        String  sql    = this.buildSelectSQL(true);
        List<T> models = this.queryList(modelClass, sql, paramValues);
        // 绑定数据源
        models.forEach((m) -> {
            if (m != null && allOrOneProcessFun_opt != null) {
                // 处理数据
                allOrOneProcessFun_opt.accept(m);
            }
            m.bindDatabaseSource(getAnima());
        });

        this.setJoin(models);
        return models;
    }

    /**
     * 查询所有模型实例,输出为一个map key=idString value等于实际实体
     *
     * @return model 集合
     */
    public Hashtable<String, T> allToMap() {
        List<T> all = all();
        Hashtable hashtable = new Hashtable<String, T>();
        for (int i = 0; i < all.size(); i++) {
            T t = all.get(i);
            String idString = getPrimaryKeyValue(t).toString();
            hashtable.put(idString, t);

        }
        return hashtable;
    }

    /**
     * 将当前组装语句的sql,执行查询结果返回为一个 List<Map>,有时候查询会跨越多个联接以及聚合函数.在这种情况下,可能无法将其映射到单个Java模型.将数据库结果作为Map对象的List返回
     *
     * @return List<Map> 一个maplist一个list元素代表一条记录,一个记录中有一个map,列名->列值
     */
    public List<Map<String, Object>> maps() {
        this.beforeCheck();
        String sql = this.buildSelectSQL(true);
        return this.queryListMap(sql, paramValues);
    }

    /**
     * @return 执行all()函数,查询当前sql返回所有记录,并将返回的list转换为Stream流
     */
    public Stream<T> stream() {
        List<T> all = all();

        return ifReturn(null == all || all.isEmpty(),
                Stream.empty(),
                Objects.requireNonNull(all).stream());
    }

    /**
     * 执行all()函数,查询当前sql返回所有记录,并将返回的list转换为并行Stream流方便操作
     *
     * @return parallel stream
     */
    public Stream<T> parallel() {
        return stream().parallel();
    }

    /**
     * 执行all()函数,查询当前sql返回所有记录,并将返回的list转换为并行Stream流(非并行)之后直接使用map函数进行修改
     *
     * @param function stream流map函数的处理函数
     * @param <R>
     * @return Stream 处理后的流
     */
    public <R> Stream<R> streamMap(Function<T, R> function) {
        return stream().map(function);
    }
    /**
     * 执行all()函数,查询当前sql返回所有记录,并将返回的list转换为并行Stream流(并行)之后直接使用map函数进行修改
     *
     * @param function stream流map函数的处理函数
     * @param <R>
     * @return Stream 处理后的流
     */
    public <R> Stream<R> parallelStreamMap(Function<T, R> function) {
        return parallel().map(function);
    }
    

    /**
     * 执行all()函数,查询当前sql返回所有记录,并将返回的list转换为并行Stream流(非并行)之后直接使用filter函数对流中的元素进行删除过滤操作 .
     *
     * @param predicate stream流filter函数的处理函数
     * @return Stream 删除过滤后的流
     */
    public Stream<T> streamFilter(Predicate<T> predicate) {
        return stream().filter(predicate);
    }


    /**
     * 从all函数返回的,结果中取固定数量的数据.
     *
     * @param limit model size
     * @return model list
     */
    public List<T> limit(int limit) {
        return ifReturn(getAnima().isUseSQLLimit(),
                () -> {
                    isSQLLimit = true;
                    paramValues.add(limit);
                    return all();
                },
                () -> {
                    List<T> all = all();
                    return ifReturn(all.size() > limit,
                            all.stream().limit(limit).collect(toList()),
                            all);
                });
    }

    /**
     * 对查询结果进行分页
     *
     * @param page  page number
     * @param limit number each page
     * @return Page
     */
    public Page<T> page(int page, int limit) {
        return this.page(new PageRow(page, limit));
    }

    /**
     * 使用sql对查询结果进行分页 
     *
     * @param sql     sql语句
     * @param pageRow 分页参数
     * @return Page
     */
    public Page<T> page(String sql, PageRow pageRow) {
        return this.page(sql, paramValues, pageRow);
    }

    /**
     * 使用sql对查询结果进行分页 
     *
     * @param sql         sql 语句
     * @param paramValues 多个参数值
     * @param pageRow     分页参数
     * @return Page
     */
    public Page<T> page(String sql, List<Object> paramValues, PageRow pageRow) {
        return this.page(sql, paramValues.toArray(), pageRow);
    }


    /**
     * 使用sql对查询结果进行分页 
     *
     * @param sql     sql语句
     * @param params  多个参数值
     * @param pageRow_opt 分页参数
     * @return Page
     */
    public Page<T> page(String sql, Object[] params, PageRow pageRow_opt) {
        this.beforeCheck();
            //在进行分页之前进行count查询
            boolean hasCount=false;//代表有无数据
            long count=0;
            if (pageRow_opt != null) {
                String countSql = useSQL ? "SELECT COUNT(*) FROM (" + sql + ") tmp" : buildCountSQL(sql);
                Connection coon = getCoon();
                try {
                    count =coon .queryCount(countSql, params);
                } catch (Exception e) {
                    //TODO: log error
                }finally{
                    coon.close();
                }
                if (count > 0) {
                    hasCount = true;
                } else {
                    hasCount = false;
                }
            } else {
                // 当pageRow==null代表不分页,默认认为数据库有数据
                hasCount = true;
            }

            Page<T> pageBean = new Page<>(count, pageRow_opt);

            //构建分页sql.并查询
            if (hasCount) {
                if (pageRow_opt!=null) {
                    //指定分页查询数据
                    String pageSQL = this.buildPageSQL(sql, pageRow_opt);
                    Connection coon = getCoon();
                    try {
                        List<T> list = coon.queryList(modelClass, pageSQL, params);
                        this.setJoin(list);
                        pageBean.setRows(list);
                    } catch (Exception e) {
                        //TODO: log error
                    }finally{
                        coon.close();
                    }
                }else{
                     //代表分页条件为null
                     //默认不分页查询全部
                    List<T> list = all();
                    this.setJoin(list);
                    pageBean.setRows(list);
                }
            }
                    // 绑定数据源
        pageBean.getRows().forEach((m) -> {
            if (m != null && allOrOneProcessFun_opt != null) {
                // 处理数据
                allOrOneProcessFun_opt.accept(m);
            }
            m.bindDatabaseSource(getAnima());
        });
            return pageBean;
    }

    private String buildCountSQL(String sql) {
        return "SELECT COUNT(*) " + sql.substring(sql.indexOf("FROM"));
    }

    /**
     * 对查询结果进行分页
     *
     * @param pageRow 分页参数s
     * @return Page
     */
    public Page<T> page(PageRow pageRow) {
        String sql = this.buildSelectSQL(false);
        return this.page(sql, pageRow);
    }

    /**
     * 根据当前的sql流的where条件来进行count查询.
     *
     * @return 匹配当前where条件的记录数
     */
    public long count() {
        this.beforeCheck();
        String sql = this.buildCountSQL();
        return this.queryOne(Long.class, sql, paramValues);
    }

    /**
     * 设置一些更新列,在update数据库的时候使用本质是增加一个set的列
     *
     * @param column 列名
     * @param value  列值
     * @return AnimaQuery
     */
    public AnimaQuery<T> set(String column, Object value) {
        updateColumns.put(column, value);
        return this;
    }

    /**
     * 设置一些更新列,在update数据库的时候使用本质是增加一个set的列
     *
     * @param function 使用lambda生成的列名
     * @param value    列值
     * @param <S>
     * @param <R>
     * @return
     */
    public <S extends Model, R> AnimaQuery<T> set(TypeFunction<S, R> function, Object value) {
        return this.set(AnimaUtils.getLambdaColumnName(function), value);
    }

    /**
     * 添加一个联合查询.
     *
     * @param joinParam Join 的多个参数
     * @return AnimaQuery
     */
    public AnimaQuery<T> join(JoinParam joinParam) {
        ifNullThrow(joinParam,
                new AnimaException("Join param not null"));

        ifNullThrow(joinParam.getJoinModel(),
                new AnimaException("Join param [model] not null"));

        ifNullThrow(AnimaUtils.isEmpty(joinParam.getFieldName()),
                new AnimaException("Join param [as] not empty"));

        ifNullThrow(AnimaUtils.isEmpty(joinParam.getOnLeft()),
                new AnimaException("Join param [onLeft] not empty"));

        ifNullThrow(AnimaUtils.isEmpty(joinParam.getOnRight()),
                new AnimaException("Join param [onRight] not empty"));

        this.joinParams.add(joinParam);
        return this;
    }

    /**
     * 查询一个模型,通过指定模型和sql,还有参数,本质是在sql最后增加"LIMIT 1"
     *
     * @param type   模型类型
     * @param sql    sql语句
     * @param params 填充到sql 的?的参数
     * @param <S>
     * @return S
     */
    public <S> S queryOne(Class<S> type, String sql, Object[] params) {
        Connection coon = this.getCoon();
        try {
            return coon.queryOne(type, sql, params);
        } catch (Exception e) {
            //TODO: log error
        }finally{
            coon.close();
        }
        return null;
    }

    /**
     * 查询一个模型,通过指定模型和sql,还有参数, 本质是在sql最后增加"LIMIT 1"
     *
     * @param type   模型类型
     * @param sql    sql语句
     * @param params 填充到sql 的?的参数
     * @param <S>
     * @return S
     */
    public <S> S queryOne(Class<S> type, String sql, List<Object> params) {
        if (getAnima().isUseSQLLimit()) {
            sql += " LIMIT 1";
        }
        List<S> list = queryList(type, sql, params);
        return AnimaUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    /**
     * 查询一个模型集合,通过指定模型和sql,还有参数
     *
     * @param type   模型类型
     * @param sql    sql语句
     * @param params 填充到sql 的?的参数
     * @param <S>
     * @return List<S>
     */
    public <S> List<S> queryList(Class<S> type, String sql, Object[] params) {
            Connection coon = this.getCoon();
            try {
                return coon.queryList(type, sql, params);
            } catch (Exception e) {
                //TODO: log error
            }finally{
                coon.close();
            }
            return  null;
    }

    /**
     * 查询一个模型集合,通过指定模型和sql,还有参数
     *
     * @param type   模型类型
     * @param sql    sql语句
     * @param params 填充到sql 的?的参数
     * @param <S>
     * @return List<S>
     */
    public <S> List<S> queryList(Class<S> type, String sql, List<Object> params) {
        return this.queryList(type, sql, params.toArray());
    }

    /**
     *  通过指定sql,还有参数,将查询结果返回为一个 List<Map>,有时候查询会跨越多个联接以及聚合函数.在这种情况下,可能无法将其映射到单个Java模型.将数据库结果作为Map对象的List返回
     *
     * @param sql    sql 语句
     * @param params 填充到sql 的?的参数
     * @return List<Map> 一个maplist一个list元素代表一条记录,一个记录中有一个map,列名->列值
     */
    public List<Map<String, Object>> queryListMap(String sql, Object[] params) {
        Connection coon = getCoon();
        try {
            coon.queryListMap(sql, params);
        } catch (Exception e) {
            //TODO: log error

        }finally{
            coon.close();
        }
         return  null;
    }

    /**
     * 通过指定sql,和参数,将查询结果返回为一个 List<Map>,有时候查询会跨越多个联接以及聚合函数.在这种情况下,可能无法将其映射到单个Java模型.将数据库结果作为Map对象的List返回
     *
     * @param sql    sql 语句
     * @param params 填充到sql 的?的参数
     * @return List<Map>一个maplist一个list元素代表一条记录,一个记录中有一个map,列名->列值
     */
    public List<Map<String, Object>> queryListMap(String sql, List<Object> params) {
        return this.queryListMap(sql, params.toArray());
    }

    /**
     * 执行 当前已经组装的sql流语句,执行更新或者删除,注意如果此sql流前置逻辑为select则不支持!
     *
     * @return 受影响的行数
     */
    public int execute() {
        switch (dmlType) {
            case UPDATE:
                return this.update();
            case DELETE:
                return this.delete();
            default:
                throw new AnimaException("只支持update和delete逻辑!.当前逻辑为"+dmlType.toString());
        }
    }

    /**
     * 执行更新或者删除的sql语句,不能为查询sql
     *
     * @param sql    sql语句
     * @param params 填充到sql 的?的参数
     * @return 受影响的行数
     */
    public int execute(String sql, Object... params) {
        Connection coon = getCoon();
        try {
            return coon.execute(sql, params);
        } catch (Exception e) {
            //TODO: logerror
        }finally{
            coon.close();
        }
        return -1;
    }

    /**
     *执行更新或者删除的sql语句,不能为查询sql
     *
     * @param sql    sql语句
     * @param params 填充到sql 的?的参数
     * @return 受影响的行数
     */
    public int execute(String sql, List<Object> params) {
        return this.execute(sql, params.toArray());
    }

    /**
     * 通过模型实例,保存一个模型,当保存的时候,会自动判断模型的主键是否为null,如果为null则默认自动生成主键,否则则按照设置的主键进行保存
     *
     * @param model 模型实例
     * @param <S>
     * @return ResultKey
     */
    public <S extends Model> ResultKey save(S model ) {
        //通过模型是否设置主键值来判断是否自动生成主键
        return save(model, getPrimaryKeyValue(model)==null?true:false);
    }

    /**
     * 通过模型实例读取主键值,如果模型为注解@pk则返回null
     * @param <S>
     * @param model
     * @return
     */
    public <S> Object getPrimaryKeyValue(S model) {
            try {
                String pkFieldName= AnimaCache.getPKColumn(modelClass);
        return  ifNotNullReturn(pkFieldName, AnimaUtils.invokeMethod(model, getGetterName(pkFieldName), AnimaUtils.EMPTY_ARG), null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
    }

    /**
     * 设置模型主键
     * 
     * @param <S>
     * @param model
     * @return
     */
    public <S> Object setPrimaryKeyValue(S model, Object pk) {
        if (pk == null || model == null) {
            return null;
        }
        try {
            String pkFieldName = AnimaCache.getPKColumn(modelClass);

            return ifNotNullReturn(pkFieldName,
                    AnimaUtils.invokeMethod(model, getSetterName(pkFieldName), new Object[] { pk }), null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过模型实例,保存一个模型
     *
     * @param model 模型实例
     * @param returnGeneratedKeys 是否设置返回自动生成的key到model
     * @param <S>
     * @return ResultKey
     */
    public <S extends Model> ResultKey save(S model,boolean returnGeneratedKeys ) {
        Connection coon = getCoon();
        try {
            List<Object> columnValues = AnimaUtils.toColumnValues(model, true);
            String sql = this.buildInsertSQL(model, columnValues);
    
            List<Object> params = columnValues.stream().filter(Objects::nonNull).collect(toList());
    
            Object returnKey = coon.insert(sql, AnimaCache.getPKColumn(model.getClass()), params.toArray());
            // 判断是否保存记录时候是指定的自生成主键
            // 如果不是则返回模型中已经预先指定好的主键
            if (returnGeneratedKeys) {
                // 自动设置已经保存的主键
                setPrimaryKeyValue(model, returnKey);
            }
            return new ResultKey(returnKey);
        } catch (Exception e) {
            //TODO: log error
        }finally{
            coon.close();
        }
        return null;
    }

    /**
     * 通过当前sql流删除模型
     *
     * @return 受影响的行数
     */
    public int delete() {
        String sql = this.buildDeleteSQL(null);
        return this.execute(sql, paramValues);
    }

    /**
     * 通过主键值和当前sql流来删除模型
     *
     * @param id  主键值
     * @param <S>
     * @return 受影响的行数, 一般正常删除后返回 1.
     */
    public <S extends Serializable> int deleteById(Object id) {
        if (id == null) {
            return -1;
        }
        this.where(primaryKeyColumn, id);
        return this.delete();
    }

    /**
     * 根据模型实例删除模型,其本质是通过模型内部指定的主键值删除
     *
     * @param model 模型实例
     * @param <S>
     * @return 受影响的行数
     */
    public <S extends Model> int deleteByModel(S model) {
        this.beforeCheck();
        String       sql             = this.buildDeleteSQL(model);
        List<Object> columnValueList = AnimaUtils.toColumnValues(model, false);
        return this.execute(sql, columnValueList);
    }

    /**
     * 通过当前sql六执行更新操作
     *
     * @return 受影响的行数
     */
    public int update() {
        this.beforeCheck();
        String       sql             = this.buildUpdateSQL(null, updateColumns);
        List<Object> columnValueList = new ArrayList<>();
        updateColumns.forEach((key, value) -> columnValueList.add(value));
        columnValueList.addAll(paramValues);
        return this.execute(sql, columnValueList);
    }

    /**
     * 通过主键更新当前sql流中的模型,将无视模型中设置的主键值
     *
     * @param id 主键值
     * @return 受影响的行数, 正常情况是 1.
     */
    public int updateById(Serializable id) {
        this.where(primaryKeyColumn, id);
        return this.update();
    }

    /**
     * 通过主键和模型实例来更新数据,将无视模型中设置的主键值
     *
     * @param model 模型实例
     * @param id    主键值
     * @param <S>
     * @return 受影响的行数, 正常情况是 1.
     */
    public <S extends Model> int updateById(S model, Serializable id) {
        this.where(primaryKeyColumn, id);
        String       sql             = this.buildUpdateSQL(model, null);
        List<Object> columnValueList = AnimaUtils.toColumnValues(model, true);
        columnValueList.add(id);
        return this.execute(sql, columnValueList);
    }

    /**
     * 通过模型来跟更新操作,主键从模型实例中读取
     *
     * @param model 模型实例
     * @param <S>
     * @return 受影响的行数
     */
    public <S extends Model> int updateByModel(S model) {
        this.beforeCheck();

        Object primaryKey = AnimaUtils.getPrimaryKey(model);

        StringBuilder sql = new StringBuilder(this.buildUpdateSQL(model, null));

        List<Object> columnValueList = AnimaUtils.toColumnValues(model, false);

        ifNotNullThen(primaryKey, () -> {
            sql.append(" WHERE ").append(this.primaryKeyColumn).append(" = ?");
            columnValueList.add(primaryKey);
        });

        return this.execute(sql.toString(), columnValueList);
    }

    /**
     * 根据args参数个数追加设置'?''
     */
    private void setArguments(Object[] args) {
        for (int i = 0; i < args.length; i++) {

            ifThen(i == args.length - 1,
                    () -> conditionSQL.append("?"),
                    () -> conditionSQL.append("?, "));

            paramValues.add(args[i]);
        }
    }

    /**
     * 通过当前sql流,构建一个查询sql.
     *
     * @param addOrderBy add the order by clause.
     * @return select sql
     */
    private String buildSelectSQL(boolean addOrderBy) {
        SQLParams sqlParams = new SQLParams();
        sqlParams.setModelClass(modelClass);
        sqlParams.setTableName(tableName);
        sqlParams.setPkName(primaryKeyColumn);
        sqlParams.setSelectColumns(selectColumns);
        sqlParams.setConditionSQL(conditionSQL);
        sqlParams.setExcludedColumns(excludedColumns);
        sqlParams.setSQLLimit(isSQLLimit);
        ifThen(addOrderBy, () -> sqlParams.setOrderBy(this.orderBySQL.toString()));

        return getAnima().dialect().select(sqlParams);
    }

    /**
     * @return the anima
     */
    public Anima getAnima() {
        return this.anima != null ? this.anima : Anima.of();
    }
    /**
     * 根据当前设置的sql流条件来创建count语句.
     *
     * @return count sql
     */
    private String buildCountSQL() {
        SQLParams sqlParams = new SQLParams();
        sqlParams.setModelClass(modelClass);
        sqlParams.setTableName(tableName);
        sqlParams.setPkName(primaryKeyColumn);
        sqlParams.setConditionSQL(conditionSQL);
        return getAnima().dialect().count(sqlParams);
    }

    /**
     * 通过当前sql流,构建一个分页sql
     *
     * @String sql 自定义的sql语句
     * @param pageRow 分页参数
     * @return paging sql
     */
    private String buildPageSQL(String sql, PageRow pageRow) {
        SQLParams sqlParams = new SQLParams();
        sqlParams.setModelClass(modelClass);
        sqlParams.setTableName(tableName);
        sqlParams.setSelectColumns(selectColumns);
        sqlParams.setConditionSQL(conditionSQL);
        sqlParams.setPkName(primaryKeyColumn);
        sqlParams.setCustomSQL(sql);
        sqlParams.setOrderBy(this.orderBySQL.toString());
        sqlParams.setPageRow(pageRow);
        sqlParams.setExcludedColumns(excludedColumns);
        return getAnima().dialect().paginate(sqlParams);
    }

    /**
     * 通过当前sql流,和一个模型实例,构建一个插入sql.List中的类型必须保持一致
     *
     * @param model 模型实例
     * @param <S>
     * @return insert sql
     */
    private <S extends Model> String buildInsertSQL(S model, List<Object> columnValues) {
        SQLParams sqlParams = new SQLParams();
        sqlParams.setColumnValues(columnValues);
        sqlParams.setModel(model);
        if (modelClass!=null) {
            sqlParams.setModelClass(modelClass);
        }else{
            sqlParams.setModelClass(model.getClass());             
            this.modelClass=(Class<T>) model.getClass();
            parse(modelClass);//解析class出表名和列名
        }
        sqlParams.setTableName(tableName);
        sqlParams.setPkName(primaryKeyColumn);
        return getAnima().dialect().insert(sqlParams);
    }

    /**
     * 通过当前sql流,和一个模型实例,构建一个更新sql.
     *
     * @param model         模型实例
     * @param updateColumns update columns
     * @param <S>
     * @return update sql
     */
    private <S extends Model> String buildUpdateSQL(S model, Map<String, Object> updateColumns) {

        SQLParams sqlParams = new SQLParams();
        sqlParams.setModel(model);
        sqlParams.setModelClass(modelClass);
        sqlParams.setTableName(tableName);
        sqlParams.setPkName(primaryKeyColumn);
        sqlParams.setConditionSQL(conditionSQL);
        sqlParams.setUpdateColumns(updateColumns);

        return getAnima().dialect().update(sqlParams);
    }

    /**
     * 通过当前的sql流构建删除语句.
     *
     * @param model 模型实例
     * @param <S>
     * @return delete sql
     */
    private <S extends Model> String buildDeleteSQL(S model) {
        SQLParams sqlParams = new SQLParams();
        sqlParams.setModel(model);
        sqlParams.setModelClass(modelClass);
        sqlParams.setTableName(tableName);
        sqlParams.setPkName(primaryKeyColumn);
        sqlParams.setConditionSQL(conditionSQL);
        return getAnima().dialect().delete(sqlParams);
    }

    public AnimaQuery<T> useSQL() {
        this.useSQL = true;
        return this;
    }

    /**
     * 在至此sql钱进行检查,主要是判断设置的modelClasss是否为null,如果为null则会抛出运行时异常
     */
    private void beforeCheck() {
        ifNullThrow(this.modelClass, new AnimaException(ErrorCode.FROM_NOT_NULL));
    }



    /**
     * 读取全局sql2o实例
     * 
     * @return
     */
    public Connection getCoon() {
        // 如果当前连接对象为null则尝试返回全局连接对象
        return getAnima().getConn();
    }

    /**
     * 设置模型需要join的属性
     *
     * @param models model list
     */
    private void setJoin(List<T> models) {
        if (null == models || models.isEmpty() ||
                joinParams.size() == 0) {
            return;
        }
        models.stream().filter(Objects::nonNull).forEach(this::setJoin);
    }

    /**
     * 设置模型需要join的属性
     *
     * @param model 模型实例
     */
    private void setJoin(T model) {
        for (JoinParam joinParam : joinParams) {
            try {
                Object leftValue = AnimaUtils.invokeMethod(
                        model,
                        getGetterName(joinParam.getOnLeft()),
                        AnimaUtils.EMPTY_ARG);

                String sql = "SELECT * FROM " + AnimaCache.getTableName(getAnima(), joinParam.getJoinModel())
                        +
                        " WHERE " + joinParam.getOnRight() + " = ?";

                Field field = model.getClass()
                        .getDeclaredField(joinParam.getFieldName());


                if (field.getType().equals(List.class)) {
                    if (AnimaUtils.isNotEmpty(joinParam.getOrderBy())) {
                        sql += " ORDER BY " + joinParam.getOrderBy();
                    }
                    List<? extends Model> list = this.queryList(joinParam.getJoinModel(), sql, new Object[]{leftValue});
                    AnimaUtils.invokeMethod(model, getSetterName(joinParam.getFieldName()), new Object[]{list});
                }

                if (field.getType().equals(joinParam.getJoinModel())) {
                    Object joinObject = this.queryOne(joinParam.getJoinModel(), sql, new Object[]{leftValue});
                    AnimaUtils.invokeMethod(model, getSetterName(joinParam.getFieldName()), new Object[]{joinObject});
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                log.warning("Set join error" + e);
            }
        }
    }
 


    /**
     * 批量保存
     * @param models
     */
    public void saveBatch(List<T> models) {
        if (models.size()==0) {
            return  ;
        }
        Connection coon = getCoon();
        try {
            for (int i = 0; i < models.size(); i++) {
                T model = models.get(i);
                List<Object> columnValues = AnimaUtils.toColumnValues(model, true);
                String sql = this.buildInsertSQL(model, columnValues);
                List<Object> params = columnValues.stream().filter(Objects::nonNull).collect(toList());
                Object returnKey = coon.insert(sql, AnimaCache.getPKColumn(model.getClass()),params.toArray());
                setPrimaryKeyValue(model, returnKey);
            }
        } catch (Exception e) {
            throw e;
        }finally{
            coon.close();
        }
    }


    public void deleteBatch(List<T> models) {
        if (models.size()==0) {
            return  ;
        }
        Connection coon = getCoon();
        try {
            for (int i = 0; i < models.size(); i++) {
                T model = models.get(i);
                Opt<Object> primaryKeyValue = model.getPrimaryKeyValue();
                if (primaryKeyValue.notNull_()) {
                    deleteById(primaryKeyValue.get());
                }
            }
        } catch (Exception e) {
            throw e;
        }finally{
            coon.close();
        }
    }

    public void deleteBatch(Integer... ids) {
        if (ids.length==0) {
            return  ;
        }
        Connection coon = getCoon();
        try {
            for (int i = 0; i <ids.length; i++) {
                Integer id = ids[i];
                deleteById(id.intValue());
            }
        } catch (Exception e) {
            throw e;
        }finally{
            coon.close();
        }
    }
}
