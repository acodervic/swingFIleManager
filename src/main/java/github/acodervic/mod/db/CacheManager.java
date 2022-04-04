package github.acodervic.mod.db;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.parseCharsetStr;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import github.acodervic.mod.code.Decode;
import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.io.FileReadUtil;
import github.acodervic.mod.io.FileUtil;
import github.acodervic.mod.io.FileWriteUtil;
/**
 * cacheManager.用来关闭本地缓存,每一个数据的分隔符为\n key:base64编码的数据,
 * 读取的时候从后往前读取,如果key已经重复读取则放弃数据
 */
public class CacheManager {
    private String delimiter = "\n";
    private File cacheFile = null;

    public CacheManager() {

    }

    HashMap<String, Object> cacheMap = new HashMap<String, Object>();

    /**
     * 
     * @param file 目标文件 
     * @param encoding_opt 编码(可选null=utf-8)
     */
    public CacheManager(File file, String encoding_opt) {
        nullCheck(file);
        cacheFile=file;
        if (cacheFile.exists()) {
            //从文件中读取缓存数据
            List<String>  lines= FileReadUtil.readFilesLinesToStringList(cacheFile, parseCharsetStr(encoding_opt));
            CacheManager cacheManager=new CacheManager();
            int 实际写入条数=0;
            //从后往前进行读取
            for (int i = (lines.size()-1); i >-1; i--) {
                String line=lines.get(i);
                try {
                                    //将json转换为一个hashmap值
                 github.acodervic.mod.db.Cache cache= JSONUtil.jsonToObj(line, Cache.class);
                 if (!this.cacheMap.containsKey(cache.getKey())) {
                    this.cacheMap.put(cache.getKey(), Decode.base64StrToStr(cache.getValue(),null));
                    System.out .println("读出一条缓存数据:key="+cache.getKey()+"  value="+cache.getValue()+"      index="+i);
                    实际写入条数++;
                 }
             } catch (Exception e) {
                 System.out.println("转换缓存行错误json:" + line);
             }
            }
            System.out.println("缓存文件读取完毕,条数"+this.cacheMap.size());
        }else{
            System.out.println("缓存文件"+cacheFile.getPath()+"不存在!写出数据");
            if (FileUtil.createFile(cacheFile, false)) {
                System.out.   println("无法创建缓存文件!"+cacheFile.getName());
            };
        }
    }

    
    /** 通过key读取数据
     * @param key  key
     * @return Object
     */
    public Object getCacheByKey(String key) {
        if (key != null) {
            Object data = this.cacheMap.get(key);
            if (data != null) {
                return data;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 添加缓存数据
     * 
     * @param cache cache
     */
    public void addCache(Cache cache) {
        int a = 12;
        this.cacheMap.put(cache.getKey(), cache.getValue());
        if (cacheFile.exists()) {
            Cache tmpCache = new Cache(cache.getKey(), cache.base64_value());
            // 写入到本地
            if (FileWriteUtil.appendWriteStrToFile(JSONUtil.objToJsonStr(tmpCache) + delimiter, cacheFile, null)) {
                System.out.println("写入一条缓存数据成功!:key=" + cache.getKey() + "  value=" + cache.getValue() + "     ");
            } else {
                System.out.println("写入一条缓存数据失败!:key=" + cache.getKey() + "  value=" + cache.getValue() + "      ");
            }
        } else {
            // 缓存文件无法写出
            System.out.println("缓存文件无法写出  文件不存在!");
        }

    }

    
    /** 
     * @return String
     */
    public String getDelimiter() {
        return delimiter;
    }

    
    /** 
     * @param delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    
    /** 
     * @return HashMap<String, Object>
     */
    public HashMap<String, Object> getCacheMap() {
        return this.cacheMap;
    }

    
    /** 
     * @param cacheMap
     */
    public void setCacheMap(HashMap<String, Object> cacheMap) {
        this.cacheMap = cacheMap;
    }
}