package github.acodervic.mod.i118n;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.parseCharsetStr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import github.acodervic.mod.Constant;
import github.acodervic.mod.data.CharUtil;

/**
 * Properties
 */
public class Properties {

    File PropertiesFile = null;
    java.util.Properties prop = null;
    String charSet = Constant.defultCharsetStr;

    /**
     * 从文件构造Properties
     * 
     * @param filePath        文件路径
     * @param fileCharSet_opt 文件的编码(可选null=utf8)
     */
    public Properties(String filePath, String fileCharSet_opt) {
        nullCheck(filePath);
        this.charSet = parseCharsetStr(fileCharSet_opt);
        if (new File(filePath).exists()) {
            this.prop = new java.util.Properties();
            this.PropertiesFile = new File(filePath);
            try {
                // 先读取文件
                prop.load(new InputStreamReader(new FileInputStream(filePath), Constant.defultCharsetStr));
                Iterator<String> it = prop.stringPropertyNames().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    System.out.println(key + "                               " + prop.getProperty(key));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件" + filePath + "不存在!");
        }
    }

    /**
     * 根据key获取value
     * 
     * @param key key
     * @return 结果
     */
    public String get(String key) {
        return this.prop.getProperty(key);
    }

    /**
     * 根据key获取value,将返回值进行固定字符编码处理
     * 
     * @param key            key
     * @param desCharset_opt 以编码读取属性值(可选null=utf8)
     * @return 字符串
     */
    public String get(String key, String desCharset_opt) {
        try {
            return CharUtil.toCharSet(get(key), desCharset_opt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据key获取value,将返回值进行固定字符编码处理
     * 
     * @param key            key
     * @param desCharset_opt 以编码读取属性值(可选null=utf8)
     * @return
     */
    public String get(String key, String rawCharset, String desCharset_opt) {
        try {
            return CharUtil.toCharSet(get(key), rawCharset, desCharset_opt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}