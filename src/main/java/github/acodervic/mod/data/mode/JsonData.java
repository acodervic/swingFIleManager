package github.acodervic.mod.data.mode;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import github.acodervic.mod.data.str;

/**
 * 用于封装一个json对象
 */
public class JsonData {
    String key;
    Object value;
    Object parentObj;// 上层对象

    public <T> T getValue(Class<T> type) {
        return (T) getValue();
    }

    public JSONObject getJSONObjectValue() {
        return (JSONObject) getValue();
    }

    public JSONArray getJSONArrayValue() {
        return (JSONArray) getValue();
    }

    /**
     * 读取上层所属的jsonObject对象
     *
     * @return 返回JSONObject失败则返回null
     */
    public JSONObject getParentJSONObject() {
        try {
            return (JSONObject) getParentObj();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the key
     */
    public str getKeyStr() {
        return new str(key);
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the parentObj
     */
    public Object getParentObj() {
        return parentObj;
    }

    /**
     * @param parentObj the parentObj to set
     */
    public void setParentObj(Object parentObj) {
        this.parentObj = parentObj;
    }

}
