package github.acodervic.mod.data.map;

import java.util.Hashtable;
import java.util.Map;

import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.str;

/**
 * 对hashtab的封装,大量数据不建议使用,因内部会维护一个Hashtable,就是在内存中会有两个引用
 *
 * @param <V>
 * @param <K>
 */
public class TMap<K, V> extends Hashtable<K, V> {
    /**
     * 和当前HMap保持同步的map数据引用
     */
    Hashtable<K, V> mapData = new Hashtable<K, V>();
    /**
     * 流式添加
     * 
     * @param k
     * @param v
     * @return
     */
    public synchronized TMap<K, V> add_(K k, V v) {
        this.put(k, v);
        this.mapData.put(k, v);
        return this;
    }

    /**
     * 从hashmap 构建一个HMap
     *
     * @param <K>
     * @param <V>
     * @param mapdata
     * @return
     */
    public static <K, V> TMap<K, V> buildTMap(Hashtable<K, V> mapdata) {
        TMap<K, V> tmap = new TMap<K, V>();
        tmap.setMapData(mapdata);
        return tmap;
    }

    /**
     * 流式删除
     * 
     * @param k
     * @param v
     * @return
     */
    public synchronized TMap<K, V> remove_(K k, V v) {
        this.remove(k);
        this.mapData.remove(k);
        return this;
    }

    /**
     * 清除null元素
     *
     * @return
     */
    public synchronized TMap<K, V> trim() {
        this.keySet().forEach(key -> {
            if (get(key) == null) {
                this.remove(key);
                this.mapData.remove(key);
            }
        });
        return this;
    }

    /**
     * 转为json字符串
     * 
     * @return
     */
    public str toJsonStr() {
        return new str(JSONUtil.objToJsonStr(this));
    }


    /**
     * 转为hashtabs map
     * @return
     */
    public Map<K, V> toHashTable() {
        return mapData;
    }

    @Override
    public synchronized void clear() {
        super.clear();
        this.mapData.clear();
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> t) {
        super.putAll(t);
        this.mapData.putAll(t);
    }

    /**
     * @param mapData the mapData to set
     */
    public void setMapData(Hashtable<K, V> mapData) {
        this.mapData = mapData;
    }

}