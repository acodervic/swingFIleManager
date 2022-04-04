
package github.acodervic.mod.db.anima.core;

import java.math.BigInteger;

/**
 * Result Key
 * <p>
 * 当保存记录至数据库时,存储返回生成的key的主键包装
 */
public class ResultKey {

    private Object key;

    public ResultKey(Object key) {
        this.key = key;
    }

    public Integer asInt() {
        if (key instanceof Long) {
            return asLong().intValue();
        }
        if (key instanceof BigInteger) {
            return asBigInteger().intValue();
        }
        return (Integer) key;
    }


    public Long asLong() {
        if (key instanceof Integer) {
            return Long.parseLong(key.toString());
        }
        return (Long) key;
    }

    public BigInteger asBigInteger() {
        return (BigInteger) key;
    }

    public String asString() {
        return key.toString();
    }

}
