package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import github.acodervic.mod.data.mode.JsonData;

/**
 * 进行json格式的数据处理工具
 */
public class JSONUtil {

    /**
     * 将json字符串序列化为java对象
     * 
     * @param <T>     类 型
     * @param jsonStr json字符串
     * @param clazz   类类型
     * @return 对象
     */
    public static <T> T jsonToObj(String jsonStr, Class<T> clazz) {
        nullCheck(jsonStr, clazz);
        return cn.hutool.json.JSONUtil.toBean(jsonStr, clazz);
    }

    /**
     * 将json列表字符串转为一个java对象集合
     * 
     * @param <T>         类型
     * @param jsonArryStr json数组字符串
     * @param clazz       类类型
     * @return 集合
     */
    public static <T> List<T> jsonArryStrToObjList(String jsonArryStr, Class<T> clazz) {
        nullCheck(jsonArryStr, clazz);
        return cn.hutool.json.JSONUtil.toList(jsonArryStr, clazz);
    }

    /**
     * 将json列表字符串转为jsonObject列表
     * 
     * @param jsonStr json字符串
     * @return 数组对象
     */
    public static JSONArray jsonToJsonObjArry(String jsonStr) {
        nullCheck(jsonStr);
        return cn.hutool.json.JSONUtil.parseArray(jsonStr);
    }

    /**
     * 将json转换为jsonObject
     * 
     * @param jsonStr json字符串
     * @return json对象
     */
    public static JSONObject jsonToJsonObj(String jsonStr) {
        nullCheck(jsonStr);
        return cn.hutool.json.JSONUtil.parseObj(jsonStr);
    }

    /**
     * 将java对象转为json字符串
     * 
     * @param javaObject java对象
     * @return json字符串
     */
    public static String objToJsonStr(Object javaObject) {
        nullCheck(javaObject);
        if (javaObject instanceof String) {
            return javaObject.toString();
        }
        if (javaObject instanceof Integer) {
            return javaObject.toString();
        }
        if (javaObject instanceof Boolean) {
            return javaObject.toString();
        }
        return cn.hutool.json.JSONUtil.toJsonStr(javaObject);
    }

    /**
     * 将resultSet转化为JSON数组
     * 
     * @param rs 结果集
     * @return json数组
     * @throws SQLException
     * @throws JSONException
     */

    public static JSONArray resultSetToJsonArry(ResultSet rs) {
        nullCheck(rs);
        try {
            // json数组
            JSONArray array = new JSONArray();
            // 获取列数
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 遍历ResultSet中的每条数据
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonObj.put(columnName, value);
                }
                array.add(jsonObj);
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将resultSet转化为JSONObject
     * 
     * @param rs 结果集
     * @return json对象
     * @throws SQLException
     * @throws JSONException
     */

    public static JSONObject resultSetToJsonObject(ResultSet rs) {
        nullCheck(rs);
        try {
            // json对象
            JSONObject jsonObj = new JSONObject();
            // 获取列数
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            // 遍历ResultSet中的每条数据
            if (rs.next()) {
                // 遍历每一列
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    jsonObj.put(columnName, value);
                }
            }
            return jsonObj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 迭代json的所有元素JSONObject/JSONArray/其他数据(toString()的结果)
     *
     * @param objJson          被遍历的json可以为JSONObject/JSONArray/
     * @param onJsonObject_opt 当找到JSONObject的时候的处理函数 key 为此JSONOBJECT在父层的key
     * @param onJsonArray_opt  当找到JSONOArray的时候的处理函数key 为此JSONARRAY在父层的key
     * @param onData_opt       当找到其他类型的数据的时候处理函数key 为此数据在父层的key
     * @return迭代处理之后的Object
     *
     */
    public static Object IteratorJSON(Object objJson, Consumer<JsonData> onJsonObject_opt,
            Consumer<JsonData> onJsonArray_opt, Consumer<JsonData> onData_opt) {

        nullCheck(objJson);
        /**
         * jsonArray
         */
        if (objJson instanceof JSONArray) {
            JSONArray objArray = (JSONArray) objJson;
            for (int i = 0; i < objArray.size(); i++) {
                IteratorJSON(objArray.get(i), onJsonObject_opt, onJsonArray_opt, onData_opt);
            }
        } else if (objJson instanceof JSONObject) {
            /**
             * jsonObject
             */
            JSONObject jsonObject = (JSONObject) objJson;
            Iterator it = jsonObject.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object object = jsonObject.get(key);
                if (object != null) {
                    if (object instanceof JSONArray) {
                        JSONArray objArray = (JSONArray) object;
                        JsonData jsonData = new JsonData();
                        jsonData.setKey(key);
                        jsonData.setValue(objArray);
                        jsonData.setParentObj(jsonObject);
                        // 先挂钩处理
                        if (onJsonArray_opt != null) {
                            onJsonArray_opt
                                    .accept(jsonData);
                        }
                        // objArray可能会有两种表现形式,一种是有key的,一种单纯的数据集合,分别对这两种情况进行处理
                        for (int i = 0; i < objArray.size(); i++) {
                            Object object2 = objArray.get(i);
                            if (object2 instanceof JSONObject || object2 instanceof JSONArray) {
                                // 继续迭代
                                IteratorJSON(object2, onJsonObject_opt, onJsonArray_opt, onData_opt);
                            } else {
                                // 既不是JSONObject也不是JSONArray,就代表是单纯的数据
                                if (onData_opt != null) {
                                    // 默认情况将调用 objiect的toString()来包装数据
                                    JsonData jsonData1 = new JsonData();
                                    jsonData1.setKey(key);
                                    jsonData1.setValue(object2);
                                    jsonData1.setParentObj(objArray);
                                    onData_opt.accept(
                                            jsonData1);
                                }
                            }
                        }

                    } else if (object instanceof JSONObject) {
                        JSONObject objJSON = (JSONObject) object;
                        if (onJsonObject_opt != null) {
                            JsonData jsonData = new JsonData();
                            jsonData.setKey(key);
                            jsonData.setValue(objJSON);
                            jsonData.setParentObj(jsonObject);
                            onJsonObject_opt
                                    .accept(jsonData);
                        }
                        IteratorJSON(objJSON, onJsonObject_opt, onJsonArray_opt, onData_opt);
                    } else {
                        if (onData_opt != null) {
                            JsonData jsonData = new JsonData();
                            jsonData.setKey(key);
                            jsonData.setValue(object);
                            jsonData.setParentObj(jsonObject);
                            // 默认情况将调用 objiect的toString()来包装数据
                            onData_opt.accept(jsonData);

                        }

                    }
                }

            }
        }
        return objJson;// 返回最终结果
    }

    /**
     * 格式化json字符串
     * 
     * @return
     */
    public static String formatJsonStr(String json) {
        nullCheck(json);
        return cn.hutool.json.JSONUtil.formatJsonStr(json);
    }
}