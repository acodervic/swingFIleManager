package github.acodervic.mod.data.map;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.str;

/**
 * 对hashmap的封装,大量数据不建议使用,因内部会维护一个Hashtable,就是在内存中会有两个引用
 *
 * @param <V>
 * @param <K>
 */
public class HMap<K, V> extends HashMap<K, V> implements UtilFunInter {
    /**
     * 和当前HMap保持同步的map数据引用
     */
    HashMap<K, V> mapData = new HashMap<K, V>();

    /**
     * 从hashmap 构建一个HMap
     *
     * @param <K>
     * @param <V>
     * @param mapdata
     * @return
     */
    public static <K, V> HMap<K, V> buildHMap(HashMap<K, V> mapdata) {
        HMap<K, V> hmap = new HMap<K, V>();
        hmap.setMapData(mapdata);
        return hmap;
    }

    /**
     * 流式添加
     * 
     * @param k
     * @param v
     * @return
     */
    public HMap<K, V> add_(K k, V v) {
        this.put(k, v);
        this.mapData.put(k, v);
        return this;
    }

    /**
     * 流式删除
     * 
     * @param k
     * @param v
     * @return
     */
    public HMap<K, V> remove_(K k, V v) {
        this.remove(k);
        this.mapData.remove(k);
        return this;
    }

    /**
     * 清除null元素
     * 
     * @return
     */
    public HMap<K, V> trim() {
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

    @Override
    public void clear() {
        super.clear();
        this.mapData.clear();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        super.putAll(m);
        this.mapData.putAll(m);
    }
    /**
     * 转为map
     * @return
     */
    public Map<K, V> toHashMap() {
        return this.mapData;
    }
    public static void main(String[] args) {
        HMap<String, Integer> a = new HMap<String, Integer>();
        a.add_("null", 1).add_("null2", 1).toJsonStr().print();
    }

    /**
     * @param mapData the mapData to set
     */
    public void setMapData(HashMap<K, V> mapData) {
        this.mapData = mapData;

    }

    /**
     * 如果hmap不包含key则执行
     * 
     * @param key
     * @param action
     */
    public void ifNotContainsKey(String key, Consumer<HMap> action) {
        nullCheck(key);
        if (!containsKey(key)) {
            action.accept(this);
        }
    }

    /**
     * 如果hmap包含key则执行
     * 
     * @param key
     * @param action
     */
    public void ifContainsKey(String key, Consumer<HMap> action) {
        nullCheck(key);
        if (containsKey(key)) {
            action.accept(this);
        }
    }
}