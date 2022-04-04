package github.acodervic.mod.data.map;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import github.acodervic.mod.data.JSONUtil;

/**
 * MapUtil
 */
public class MapUtil {

    /**
     * map转为json
     * @param map 输入map
     * @return json字符串
     */
    public static String toJsonStr(Map map) {
         //直接序列化输出
        return JSONUtil.objToJsonStr(map);
    }

    /**
     * 删除value=null的值
     * @param map
     */
    public static void removeNullValue(Map map) {
        nullCheck(map);
        map.keySet().forEach( key ->{
            if (map.get(key)==null) {
                map.remove(key);
            }
        });
    }

    public static void newLinkMap(String key, String value) {

    }


    /**
     * 从Map中读取key为list
     * @param <T>
     * @param <E>
     * @param resource
     * @return
     */
    public static <T, E> List<T> keyList(Map<T, E> resource) {
        nullCheck(resource);
        List<T> keyList=new ArrayList<T>();
        resource.keySet().forEach( key ->{
            keyList.add(key);
        });
        return keyList;
    }


        /**
     * 从Map中读取key为list
     * @param <T>
     * @param <E>
     * @param resource
     * @return
     */
        public static <T, E> List<E> valueList(Map<T, E> resource) {
            nullCheck(resource);
        List<E> keyList=new ArrayList<E>();
        resource.values().forEach( value ->{
            keyList.add(value);
        });
        return keyList;
    }




}