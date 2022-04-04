package github.acodervic.mod.data;

/**
 * 实体继承此类就可以实现json序列化
 */
public class toString {

    /**
     * 转json
     */
    public String  toString() {
        return JSONUtil.objToJsonStr(this);
    }
}