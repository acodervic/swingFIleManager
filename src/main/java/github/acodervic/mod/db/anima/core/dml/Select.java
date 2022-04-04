
package github.acodervic.mod.db.anima.core.dml;

import java.util.Map;

import github.acodervic.mod.db.anima.Anima;
import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.core.AnimaQuery;
import github.acodervic.mod.db.anima.core.ResultList;

/**
 * 基本的查询对象,可以实现多种查询方案,常用函数为from,本质为创建一个AnimaQuery查询实例并返回
 *
 */
public class Select {
    private String columns;
    // 连接对象如果为null就是使用全局数据库查询
    private Anima anima;

    public Select(Anima anima, String columns) {
        this.columns = columns;
        this.anima = anima;
    }

    public Select(Anima anima) {
        this.anima = anima;
    }

    /**
     * 基于流的AnimaQuery查询
     *
     * @param <T>
     * @param modelClass 返回的模型class
     * @return
     */
    public <T extends Model> AnimaQuery<T> from(Class<T> modelClass) {
        return new AnimaQuery<>(this.anima, modelClass).select(this.columns);
    }


    /**
     * 基于sql的直接查询,并映射为指定模型的结果集
     * 
     * @param <T>
     * @param type   返回的模型class
     * @param sql    查询sql
     * @param params 填充参数
     * @return
     */
    public <T> ResultList<T> bySQL(Class<T> type, String sql, Object... params) {
        return new ResultList<>(this.anima, type, sql, params);
    }

    /**
     * 基于sql的复杂查询,如多表操作时候的查询,可能无法直接映射为模型,通过map就可以读取返回的数据库记录,key=列名,value的列值
     * 
     * @param <T>
     * @param sql    查询sql
     * @param params 填充参数
     * @return
     */
    public <T extends Map<String, Object>> ResultList<T> bySQL(String sql, Object... params) {
        return new ResultList<>(this.anima, null, sql, params);
    }

    /**
     * 
     */
    public Select() {
    }

}
