
package github.acodervic.mod.db.anima.core;

import java.util.List;
import java.util.Map;

import github.acodervic.mod.db.anima.Anima;
import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.page.Page;
import github.acodervic.mod.db.anima.page.PageRow;

/**
 * ResultList
 * <p>
 * Get a list of collections or single data
 *
 * @author biezhi
 * @date 2018/3/16
 */
public class ResultList<T> {


    private final Class<T> type;
    private final String   sql;
    private final Object[] params;
    private Anima anima;

    public ResultList(Anima anima, Class<T> type, String sql, Object[] params) {
        this.type = type;
        this.sql = sql;
        this.params = params;
        this.anima = anima;
    }

    public T one() {
        return new AnimaQuery<>(this.anima).useSQL().queryOne(type, sql, params);
    }

    public List<T> all() {
        return new AnimaQuery<>(this.anima).useSQL().queryList(type, sql, params);
    }

    public List<Map<String, Object>> maps(){
        return new AnimaQuery<>(this.anima).useSQL().queryListMap(sql, params);
    }

    public <S extends Model> Page<S> page(PageRow pageRow) {
        Class<S> modelType = (Class<S>) type;
        return new AnimaQuery<>(this.anima, modelType).useSQL().page(sql, params, pageRow);
    }

    public <S extends Model> Page<S> page(int page, int limit) {
        return this.page(new PageRow(page, limit));
    }

}
