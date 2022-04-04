package github.acodervic.mod.db;

import github.acodervic.mod.code.Encode;

/**
 * cache,用来写入缓存到磁盘,或者读取缓存到磁盘
 */
public class Cache {
    String key;
    Object  value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    //读取真实值
    public  String  base64_value(){
            return Encode.strToBase64UrlStr(this.value.toString(),null);
    }

    public String getValue() {
        return this.value.toString();
    }

    public void setValue(Object  value) {
        //写数据的时候是base64字符
        this.value = value;
    }

    public Cache(String key, Object  value) {
        this.key = key;
        this.value = value;
    }

    public Cache() {
    }
    
    
}