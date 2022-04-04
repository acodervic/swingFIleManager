
package github.acodervic.mod.db.anima.core.dml;

import github.acodervic.mod.db.anima.Anima;
import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.core.AnimaQuery;
import github.acodervic.mod.db.anima.enums.DMLType;

/**
 * 提供了基于流的更新操作,本质设置模型classs,并返回新生成的AnimaQuery实例
 *
 */
public class Update {
    // 连接对象如果为null就是使用全局数据库查询
    private Anima anima;
    /**
     * 从指定模型来获取更新流
     * @param <T>
     * @param modelClass
     * @return
     */
    public <T extends Model> AnimaQuery<T> from(Class<T> modelClass) {
        return new AnimaQuery<T>(this.anima, DMLType.UPDATE).parse(modelClass);
    }

    /**
     * 构造指定数据库连接的更新对象
     *
     * @param sql2o
     */
    public Update(Anima anima) {
        this.anima = anima;
    }

    /**
     * 
     */
    public Update() {
    }

}
