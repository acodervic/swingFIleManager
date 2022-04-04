package github.acodervic.mod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Proxy;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import github.acodervic.mod.code.Decode;
import github.acodervic.mod.code.Encode;
import github.acodervic.mod.crypt.Digest;
import github.acodervic.mod.data.ArrayUtil;
import github.acodervic.mod.data.BaseUtil;
import github.acodervic.mod.data.ByteLIst;
import github.acodervic.mod.data.CharUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.Logic;
import github.acodervic.mod.data.NumberUtil;
import github.acodervic.mod.data.ObjectUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.RegexUtil;
import github.acodervic.mod.data.TimeUtil;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.data.str;
import github.acodervic.mod.data.list.AList;
import github.acodervic.mod.data.list.LList;
import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.data.map.HMap;
import github.acodervic.mod.data.map.MapUtil;
import github.acodervic.mod.data.mode.CompareObj;
import github.acodervic.mod.function.FunctionUtil;
import github.acodervic.mod.function.TrySupplierFun;
import github.acodervic.mod.function.TrySupplierReturnFun;
import github.acodervic.mod.interfaces.DoOneJob;
import github.acodervic.mod.io.BioStreamUtil;
import github.acodervic.mod.io.FileReadUtil;
import github.acodervic.mod.io.FileUtil;
import github.acodervic.mod.io.FileWriteUtil;
import github.acodervic.mod.io.IoType;
import github.acodervic.mod.net.HttpUtil;
import github.acodervic.mod.net.http.HttpClient;
import github.acodervic.mod.shell.ConsoleTable;
import github.acodervic.mod.shell.SystemUtil;
import github.acodervic.mod.thread.ExecPool;
import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.TimePool;

/**
 * utilFun
 */
public class utilFun {
    /**
     * 构建ByteLIst
     * 
     * @param bytes
     * @return
     */
    public static ByteLIst byteList(byte[] bytes) {
        return new ByteLIst(bytes);
    }

    /**
     * 构建ByteLIst
     * 
     * @param bytes
     * @return
     */
    public static ByteLIst byteList( List<Byte> bytes) {
        return new ByteLIst(bytes);
    }

    /**
     * 构建ByteLIst
     * 
     * @param file
     * @return
     */
    public static ByteLIst byteList( FileRes file) {
        return new ByteLIst(file.getFile());
    }

    /**
     * 构建ByteLIst
     *
     * @param byteBuffer
     * @return
     */
    public static ByteLIst byteList( ByteBuffer byteBuffer) {
        return new ByteLIst(byteBuffer);
    }

    /**
     * 输出
     * 
     * @param str
     */
    public static void print(List list) {
        if (notNull(list)) {
            System.out.print("[");

            // 循环输出
            for (int i = 0; i < list.size(); i++) {
                if (notNull(list.get(i))) {
                    System.out.print(list.get(i).toString());
                } else {
                    System.out.print("null");
                }
                if ((i + 1) != list.size()) {
                    System.out.print("  ,  ");

                }
            }
        }
        System.out.print("]");
        System.out.println();
    }

    /**
     * 输出
     *
     * @param str
     */
    public static void print(String str) {
        System.out.println(str);
    }

    /**
     * 输出
     * 
     * @param str
     */
    public static void print(Object obj) {
        System.out.println(obj);
    }

    /**
     * 错误输出
     *
     * @param str
     */
    public static void printError( String str) {
        System.err.println(str);
    }
    // ============================================================================================================

    //
    // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

    /**
     * 输出数组中的数据,自动调用集合中的tostring 方法
     * 
     * @param datas    数组
     * @param splitStr 每个数组toString()之间的间隔字符串
     */
    public static void arry_printData(Object[] datas,  String splitStr) {

        ArrayUtil.printData(datas, splitStr);
    }

    /**
     * 输出集合中的数据,自动调用集合中的tostring 方法
     * 
     * @param datas    数组对象
     * @param splitStr 间隔字符串
     * @return
     */
    public static void arr_printData(List<Object> datas,  String splitStr) {

        ArrayUtil.printData(datas, splitStr);
    }

    // MapUtil方法============================================================================================================

    /**
     * map转为json
     * 
     * @param map 输入map
     * @return json字符串
     */
    public static String map_toJson(Map map) {
        return MapUtil.toJsonStr(map);
    }



   /**
    * 删除value=null的值
    * @param map
    */
   public static void map_removeNullValue( Map map) {
      MapUtil.removeNullValue(map);
   }




   /**
    * 从Map中读取key为list
    * @param <T>
    * @param <E>
    * @param resource
    * @return
    */
   public static <T, E> List<T> map_keyList( Map<T, E> resource) {
       return MapUtil.keyList(resource);
   }


       /**
    * 从Map中读取key为list
    * @param <T>
    * @param <E>
    * @param resource
    * @return
    */
       public static <T, E> List<E> map_valueList( Map<T, E> resource) {
     return MapUtil.valueList(resource);
   }



    // BaseUtil============================================================================================================

    /**
     * 构建一个可以为null值的Optional
     * 
     * @param <T>   类型
     * @param value 一个可以为null的对象
     * @return Optional
     */
    public static <T> Optional<T> optOfNullable(T value) {
        return BaseUtil.optOfNullable(value);

    }

    /**
     * 构建一个不能为null值的Optional
     * 
     * @param <T>   类型
     * @param value 一个不为null的对象,若为null直接抛出null异常
     * @return Optional
     */
    public static <T> Optional<T> optOf(T value) {
        return BaseUtil.optOf(value);

    }

    /**
     * 读取值,内部对值进行非空验证 使用方法 get( ()->str.toString()).orElse("null");
     * 通过str.toString()获取值,如果出现空值则返回null
     * 
     * @param <T>      类型
     * @param resolver 一段有返回值的lambda表达式
     * @return 一个值
     */
    public static <T> Optional<T> get(Supplier<T> resolver) {
        return FunctionUtil.get(resolver);
    }

    /**
     * 判断是否为空
     *
     * @param obj 被判断的对象
     * @return 是否为Null
     */
    public static boolean isNull(Object obj) {
        return BaseUtil.isNull(obj);
    }

    /**
     * 判断是否不为空
     * 
     * @param obj 被判断的对象
     * @return 是否为Null
     */
    public static boolean notNull(Object obj) {
        return BaseUtil.notNull(obj);
    }

    /**
     * 判断两个对象的toString方法是否相等
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 布尔值
     */
    public static boolean toStringEq(Object obj1, Object obj2) {
        return BaseUtil.toStringEqua(obj1, obj2);
    }

    /**
     * 判断俩个对象是否引用同一个对象
     * 
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 布尔值
     */
    public static boolean sameRef(Object obj1, Object obj2) {
        return BaseUtil.sameObjRef(obj1, obj2);
    }



    /**
     * 读取一个对象的引用hash,此hash 在一个对象的生命周期中是不会改变的(即使内部值改变).
     *和对象的hashCode不一样.hashcode是对对象内部的值进行hash,如果内部值改变则hashcode也会改变,此值用于对某一个对象进行标识
     内部由System.identityHashCode 实现
     * @param obj1 对象1
     * @return 字符串
     */
    public static String getObjRefHash( Object obj1) {
        return BaseUtil.getObjRefHash(obj1);
     }


    /**
     * 判断两个对象的类型是否相等
     * 
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 相等true否则false
     */
    public static boolean sameType(Object obj1, Object obj2) {
        return BaseUtil.sameObjType(obj1, obj2);
    }

    // CharUtil============================================================================================================
    /**
     * 获取指定字符串出现的次数,如果字符串为Null或者次数为0显示-1
     * 
     * @param srcText  源字符串
     * @param findText 要查找的字符串
     * @return 出现次数
     */
    public static int findCount( String srcText,  String findText) {
        return CharUtil.showCount(srcText, findText);
    }

    /**
     * 创建一个str
     *
     * @param str_opt
     * @return
     */
    public static str str(String str_opt) {
        return new str(str_opt);
    }


    /**
     * 创建一个str
     *
     * @param str_opt
     * @return
     */
    public static str str(Object str_opt) {
        return new str(str_opt);
    }
    /**
     * 使用字符串列表来构造字符串,自动拼接
     *
     * @param strings
     * @param splitString 拼接 符
     */
    public str str( List<String> strings,  String splitString) {
        return new str(strings, splitString);
    }
    /**
     * 创建一个str
     *
     * @param str_opt
     * @return
     */
    public static str str( FileRes file) {
        return new str(file);
    }



    /**
     * 创建一个str
     * 
     * @param bytes_opt
     * @return
     */
    public static str str(ByteLIst bytes_opt) {
        return new str(bytes_opt.toBytes());
    }

    /**
     * 使用固定编码字节数组创建一个str,
     * 
     * @param str
     * @return
     */
    public static str str(ByteLIst bytes_opt, String charSet_opt) {
        return new str(bytes_opt.toBytes(), charSet_opt);
    }

    /**
     * @param string
     * @return
     */
    public static github.acodervic.mod.data.str str(String... strings) {
        return new str(strings);
    }

    /**
     * @param string
     * @return
     */
    public static github.acodervic.mod.data.str str(List<String> strings_opt) {
        return new str(strings_opt);
    }

    /**
     * 使用占位符来构建字符串.%s 代表一个占位符号,按顺序填充
     *
     * @param string
     * @param 占位数据
     */
    public static str str(String str_opt, String... 占位数据) {
        return new str(str_opt, 占位数据);
    }

    /**
     * 判断字符串中是否包含字符串
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public static boolean hasStr( String str,  String hasStr) {
        return CharUtil.has(str, hasStr);
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public static boolean hasStr( String str, List<String> hasStrs) {
        return CharUtil.has(str, hasStrs);
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public static boolean hasAllStr( String str, List<String> hasStrs) {
        return CharUtil.hasAll(str, hasStrs);
    }

    /**
     * 截取字符串
     * 
     * @param str        被截取的字符串,如果截取出错返回null
     * @param beginIndex 开始下标
     * @param endIndex   结束下标
     */
    public static String subString( String str, int beginIndex, int endIndex) {
        return CharUtil.sub(str, beginIndex, endIndex);
    }

    /**
     * 截取字符串.从str中找到starStr,如果找到则以startOffsetOfStartStr作为起始截取点,endOffsetOfStartStr作为结束截取点,如果没有找到字符串则返回null,其余下标错误会自动进行修复
     * 如最终截取的起始偏移小于0则置0,最终截取的结束下标大于字符串的长度则置于字符串末尾 如最终截取的结束字符串下标小于起始字符串下标则返回 ""字符串
     * 
     * @param str                   被截取的字符串
     * @param startStr              截取搜索的字符串
     * @param startOffsetOfStartStr 相对搜索到字符串的起始偏移,可以为负数
     * @param endOffsetOfStartStr   相对搜索到字符串的结束偏移,可以为负数
     * @return
     */
    public static String subString( String str,  String startStr, int startOffsetOfStartStr,
            int endOffsetOfStartStr) {
        return CharUtil.sub(str, startStr, startOffsetOfStartStr, endOffsetOfStartStr);
    }

    /**
     * 截取两个字符串中间内容
     * 
     * @param str      被截取的字符串
     * @param startStr 开始字符串
     * @param endStr   结束字符串
     */
    public static String subBetween( String str,  String startStr,  String endStr) {
        return CharUtil.subBetween(str, startStr, endStr);
    }

    /**
     * 返回第一次出现的指定子字符串在此字符串中的索引。 未搜索到或者空字符则返回-1
     * 
     * @param str     被搜索的字符串
     * @param findStr 想要搜索的字符串
     */
    public static int indexOf( String str,  String findStr) {
        return CharUtil.indexOf(str, findStr);
    }

    /**
     * 从指定的索引处开始，返回第一次出现的指定子字符串在此字符串中的索引,未搜索到和搜索空字符串则返回-1
     * 
     * @param str       被搜索的字符串
     * @param findStr   想要搜索的字符串
     * @param formIndex 开始下标
     */
    public static int indexOfFrom( String str,  String findStr, int formIndex) {
        return CharUtil.indexOfFrom(str, findStr, formIndex);
    }

    /**
     * 返回在此字符串中最右边出现的指定子字符串的索引。 ,未搜索到和搜索空字符串则返回-1
     * 
     * @param str     被搜索的字符串
     * @param findStr 想要搜索的字符串
     */
    public static int lastIndexOf( String str,  String findStr) {
        return CharUtil.lastIndexOf(str, findStr);
    }

    /**
     * 从指定的索引处开始向后搜索，返回在此字符串中最后一次出现的指定子字符串的索引,未搜索到和搜索空字符串 则返回-1
     * 
     * @param str        被截取的字符串
     * @param beginIndex 开始下标
     * @param endIndex   结束下标
     */
    public static int lastIndexOfFrom( String str,  String findStr, int formIndex) {
        return CharUtil.lastIndexOfFrom(str, findStr, formIndex);
    }

    /**
     * 返回字符串长度
     * 
     * @param str 源字符串
     * @return 长度
     */
    public static int len( String str) {
        return CharUtil.len(str);
    }

    /**
     * 去空格
     * 
     * @param str 源字符串
     * @return 去空格后的字符串
     */
    public static String trim( String str) {
        return CharUtil.trim(str);
    }

    /**
     * 返回去空格最左侧的字符串
     * 
     * @param str 源字符 串
     * @return 去空格之后的字符串
     */
    public static String trimLeft( String str) {
        return CharUtil.trimLeft(str);
    }

    /**
     * 返回去空格最右侧的字符串
     * 
     * @param str 源字符串
     * @return 去空格之后的字符串
     */
    public static String trimRight( String str) {
        return CharUtil.trimRight(str);
    }

    /**
     * 返回去空格中间的字符串,中间的定义是最左和最右之间的
     * 
     * @param str 源字符串
     * @return 结果
     */
    public static String trimMedium( String str) {
        return CharUtil.trimMedium(str);
    }

    /**
     * 替换字符串中的所有字符串为新的字符串,如果无法完成正常进行替换则返回Null
     * 
     * @param str             被替换的字符串
     * @param replace String 替换前的字符串
     * @param replacement     替换后的字符串
     * @return 结果字符串
     */
    public static String replcaeAll( String str,  String replaceString,  String replacement) {
        return CharUtil.replcaeAll(str, replaceString, replacement);
    }

    /**
     * 替换字符串右边的字符为新字符,替换次数,当替换失败的时候返回Null
     * 
     * @param str           原始字符串
     * @param replaceString 被替换的字符串
     * @param replacement   替换后的字符串
     * @param replaceCount  最大的替换计数
     * @return 结果字符串
     */
    public static String replcaeLeftStr( String str,  String replaceString,  String replacement,
            int replaceCount) {
        return CharUtil.replcaeLeftStr(str, replaceString, replacement, replaceCount);
    }

    /**
     * 替换字符串右边的字符为新字符,当替换失败的时候返回Null
     * 
     * @param str           原始字符串
     * @param replaceString 被替换的字符串
     * @param replacement   替换后的字符串
     * @param replaceCount  最大的替换计数
     * @return 结果字符串
     */
    public static String replcaeRightStr( String str,  String replaceString,
             String replacement, int replaceCount) {
        return CharUtil.replcaeRightStr(str, replaceString, replacement, replaceCount);
    }

    /**
     * 判断字符串中是否包含中文,如果字符串为null则返回false
     * 
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean hasChinese( String str) {
        return CharUtil.hasChinese(str);
    }

    /**
     * 字符串转小写
     * 
     * @param str 源字符串
     * @return 结果字符串
     */
    public static String toLowerCase( String str) {
        return CharUtil.toLowerCase(str);

    }

    /**
     * 字符串转大写
     * 
     * @param str 源字符串
     * @return 结果字符串
     */
    public static String toUpperCase( String str) {
        return CharUtil.toUpperCase(str);
    }

    /**
     * 删除字符串的下标字符
     * 
     * @param str     源字符串
     * @param atIndex 下标
     * @return 结果字符串
     */
    public static String delCharAt( String str, int atIndex) {
        return CharUtil.deleteCharAt(str, atIndex);
    }

    /**
     * 删除字符串的下标字符
     * 
     * @param str        源字符串
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return
     */
    public static String delStrs( String str, int startIndex, int endIndex) {
        return CharUtil.deleteStrs(str, startIndex, endIndex);
    }

    /**
     * 删除目标字符串中存在的字符
     * 
     * @param str        源字符串
     * @param deleteStrs 需要被删除的字符串
     * @return 结果字符串
     */
    public static String delStrs( String str, List<String> deleteStrs) {
        return CharUtil.deleteStrs(str, deleteStrs);
    }

    /**
     * 删除目标字符串中存在的字符
     * 
     * @param str        源字符串
     * @param deleteStrs 需要被删除的字符串
     * @return 结果字符串
     */
    public static String delStrs( String str, String... deleteStrs) {
        return CharUtil.deleteStrs(str, deleteStrs);
    }

    /**
     * 向字符串的下标处插入字符串,当出现异常时候返回null
     * 
     * @param str         源字符串
     * @param insertStr   需要插入的字符串
     * @param insertIndex 插入位置
     * @return 结果字符串
     */
    public static String insertStr( String str,  String insertStr, int insertIndex) {
        return CharUtil.insertStr(str, insertStr, insertIndex);
    }

    /**
     * 往字符串末尾追加字符串
     * 
     * @param str        源字符串
     * @param appanedStr 插入的字符串
     * @return 结果字符串
     */
    public static String insertStrEnd( String str,  String appanedStr) {
        return CharUtil.insertStrToEnd(str, appanedStr);
    }

    /**
     * 往字符串末尾追加字符串
     * 
     * @param str        源字符串
     * @param appanedStr 插入的字符串
     * @return 结果字符串
     */
    public static String insertStrToHead( String str,  String headStr) {
        return CharUtil.insertStrToEnd(str, headStr);
    }

    /**
     * 根据下标获取字符串
     * 
     * @param str   源字符串
     * @param index 下标
     * @return 结果字符串
     */
    public static String charAt( String str, int index) {
        return CharUtil.charAt(str, index);
    }

    /**
     * 字符串编码转换，若出现错误返回null
     * 
     * @param str            待转码的字符串
     * @param rawCharset     原始编码
     * @param desCharset_opt 目标编码
     * @return 结果字符串
     */
    public static String toCharset( String str,  String rawCharset, String desCharset_opt) {
        return CharUtil.toCharSet(str, rawCharset, desCharset_opt);
    }

    /**
     * 字符串编码转换，若出现错误返回null
     * 
     * @param str            待转码的字符串
     * @param rawCharset     原始编码
     * @param desCharset_opt 目标编码
     * @return 结果字符串
     */
    public static String toCharset( String str,  String desCharset) {
        return CharUtil.toCharSet(str, desCharset);
    }

    /**
     * str到字节数组
     * 
     * @param str 目标字符串
     * @return 字节数组
     */
    public static ByteLIst strToBytes( String str) {
        return new ByteLIst(CharUtil.toBytes(str));
    }

    /**
     * str到字节数组,以固定编码格式获取
     * 
     * @param str            目标字符串
     * @param rawCharSet_opt 字符编码(unll=utf8)
     * @return 编码之后的字符串
     */
    public static ByteLIst strToBytes( String str, String rawCharSet_opt) {
        return new ByteLIst(CharUtil.toBytes(str, rawCharSet_opt));
    }

    /**
     * 字节数组到字符串,如果charSet为Null则默认使用utf8编码
     * 
     * @param bytes       字节数组
     * @param charSet_opt 目标编码(可选null=utf8)
     * @return 结果字符串
     */
    public static String byteToStr( ByteLIst bytes, String charSet_opt) {
        return CharUtil.bytesToStr(bytes.toBytes(), charSet_opt);
    }

    /**
     * 数值转字符串
     * 
     * @param num 数值
     * @return 字符串
     */
    public static String intToStr(int num) {
        return CharUtil.toStr(num);
    }

    /**
     * 数值转字符串
     * 
     * @param num
     * @return
     */
    public static String longToStr(Long num) {
        return CharUtil.toStr(num);
    }

    /**
     * 数值转字符串
     * 
     * @param num
     * @return
     */
    public static Long intToLong(Integer num) {
        return num.longValue();
    }

    /**
     * 分割字符串
     *
     * @param str      目标字符串
     * @param splitStr 切割字符串标志
     * @return 字符串数组
     */
    public String[] split( String str,  String splitStr) {
        return CharUtil.splitToArray(str, splitStr);
    }


    // JSONutil============================================================================================================

    /**
     * 将json字符串序列化为java对象
     * 
     * @param <T>     类 型
     * @param jsonStr json字符串
     * @param clazz   类类型
     * @return 对象
     */
    public static <T> T json_toObj( String jsonStr, Class<T> clazz) {
        return JSONUtil.jsonToObj(jsonStr, clazz);
    }

    /**
     * 将json列表字符串转为一个java对象集合
     * 
     * @param <T>         类型
     * @param jsonArryStr json数组字符串
     * @param clazz       类类型
     * @return 集合
     */
    public static <T> List<T> json_strTojList( String jsonArryStr, Class<T> clazz) {
        return JSONUtil.jsonArryStrToObjList(jsonArryStr, clazz);
    }

    /**
     * 将json列表字符串转为jsonObject列表
     * 
     * @param jsonStr json字符串
     * @return 数组对象
     */
    public static JSONArray json_toArry( String jsonStr) {
        return JSONUtil.jsonToJsonObjArry(jsonStr);
    }

    /**
     * 将json转换为jsonObject
     *
     * @param jsonStr json字符串
     * @return json对象
     */
    public static JSONObject json_toObj( String jsonStr) {
        return JSONUtil.jsonToJsonObj(jsonStr);
    }

    /**
     * 将java对象转为json字符串
     * 
     * @param javaObject java对象
     * @return json字符串
     */
    public static String json_toStr(Object javaObject) {
        return JSONUtil.objToJsonStr(javaObject);
    }

    /**
     * 将resultSet转化为JSON数组
     * 
     * @param rs 结果集
     * @return json数组
     * @throws SQLException
     * @throws JSONException
     */
    public static JSONArray json_resultSetToJsonArry(ResultSet rs) {
        return JSONUtil.resultSetToJsonArry(rs);
    }

    /**
     * 将resultSet转化为JSONObject
     * 
     * @param rs 结果集
     * @return json对象
     * @throws SQLException
     * @throws JSONException
     */
    public static JSONObject json_resultSetToJsonObject(ResultSet rs) {
        return JSONUtil.resultSetToJsonObject(rs);
    }

    // ListUtil============================================================================================================

    /**
     * 生成一个泛型集合
     * 
     * @param <T>      类型
     * @param elements 元素
     * @return 集合
     */
    public static <T> ArrayList<T> newList(T... elements) {
        return ListUtil.newList(elements);
    }

    /**
     * 转为json字符串
     * 
     * @param list 集合
     * @return json字符串
     */
    public static String list_toJsonStr(List list) {
        return ListUtil.toJsonStr(list);
    }

    /**
     *对集合进行随机排序
     * @param list
     */
    public static void list_randomSort(List list){
        ListUtil.randomSortList(list);
    }
    /**
     * 将集合对象转为数组对象,使用的时候自己进行强制转换
     * 
     * @param <T>
     * @param data 传入的泛型集合
     * @return 返回一个Object数组,使用的时候自己转换
     */
    public static <T> Object[] list_toArry(List<T> data) {
        return ListUtil.toArry(data);
    }

    /**
     * 截取下标中的第几个到第几个,例如1,2,3,4,5 当start=2,end=4的时候，将返回2,3,4
     *
     * @param <T>   类型protocol
     * @param data  集合数据
     * @param start 开始的字符位置,第几个(非下标!)
     * @param end   结束的字符位置，第几个(非下标!)
     * @return 被截取的集合
     */
    public static <T> List<T> list_sub(List<T> data, int start, int end) {
        return ListUtil.sub(data, start, end);
    }

    /**
     * 调用list所有元素的toString方法来拼接字符串.每个字符串中间使用固定字符分割
     * 
     * @param listData      为集合数据
     * @param delimiter_opt 每个数据拼接之前的间隔符(可选null="")
     * @return 拼接之后的字符串
     */
    public static String list_toStr(List listData,  String delimiter) {
        return ListUtil.toStr(listData, delimiter);
    }

    /**
     * 获得数值集合里面的最大值的坐标
     *
     * @param numbers 数值列表
     * @return 最大值坐标
     */
    public static int list_maxNumOfIndex(List<Integer> numbers) {
        return ListUtil.maxNumOfIndex(numbers);
    }

    /**
     * 获取集合中的最小值
     * 
     * @param numbers 数值列表
     * @return 最小值
     */
    public static Integer list_minNum(List<Integer> numbers) {
        return ListUtil.minNum(numbers);
    }

    /**
     * 获取集合中的最小值所在的下标
     * 
     * @param numbers 数值列表
     * @return 最小值的下标
     */
    public static Integer list_minNumIndex(List<Integer> numbers) {
        return ListUtil.minNumOfIndex(numbers);
    }

    /**
     * 求和
     * 
     * @param numbers
     * @return
     */
    public static Integer list_sum(List<Integer> numbers) {
        return ListUtil.sum(numbers);
    }

    /**
     * 求平均值
     * 
     * @param numbers 数值列表
     * @return 平均值
     */
    public static Double list_averageValue(List<Integer> numbers) {
        return ListUtil.averageValue(numbers);
    }

    /**
     * 求中位数
     * 
     * @param numbers 数值列表
     * @return 中位数
     */
    public static Double list_midiumNum(List<Integer> numbers) {
        return ListUtil.midiumNum(numbers);
    }

    /**
     * 判断同一个数字在,集合中出现的次数
     * 
     * @param numbers 数值列表
     * @param num     数值
     * @return 出现的次数
     */
    public static int list_count(List<Integer> numbers, int num) {
        return ListUtil.count(numbers, num);
    }

    /**
     * list转换类型,如果在处理对象函数接口中返回null,代表丢弃元素,即删除元素
     * 
     * @param <T>
     * @param <D>
     * @param rawList  原始集合
     * @param coustFun 处理数据的函数式编程对象,在其中对某个对象进行处理返回,返回的数据类型必须一致
     * @return //将每个原始元素转换为 String List<Integer> list2=create(3,4,1);
     *         List<String> data= cast(list2, (rawObj)->{
     *
     *         return rawObj.toString(); }); System.out.println(toJsonStr(data );
     */
    public static <T, D> List<T> list_cast(List<D> rawList, Function<D, T> coustFun) {
        return ListUtil.cast(rawList, coustFun);
    }

    /**
     * 求两个集合的差集 如:list1=1,2,3 list2=3.4,5 返回12 求list1的差集合
     * 
     * @param <T>
     * @param <D>
     * @param list1
     * @param list2
     * @return Integer[] array2 = create(3, 4, 5); List<String> array3 =
     *         differenceArray1(array1, array2, (obj) -> { CompareObj
     *         compareObj=(CompareObj) obj; if
     *         (compareObj.getObjA().toString().equals(compareObj.getObjB().toString()))
     *         { return true; }else{ return false; } });
     *         System.out.println(ListUtil.toJsonStr(array3 );
     */
    public static <T, D> List<T> list_diffList1(List<T> list1, List<D> list2, Predicate<CompareObj> compareFun) {
        return ListUtil.differenceList1(list1, list2, compareFun);
    }

    /**
     * 求两个集合的交集 如:list1=1,2,3 list2=3.4,5 返回3 求list1的差集合 使用方法  String []
     * array1=create("1","2","3"); Integer[] array2 = create(3, 4, 5); List<String>
     * array3 = sameArray1(array1, array2, (obj) -> { CompareObj
     * compareObj=(CompareObj) obj; if
     * (compareObj.getObjA().toString().equals(compareObj.getObjB().toString())) {
     * return true; }else{ return false; } });
     * System.out.println(ListUtil.toJsonStr(array3 ); * @param <T>
     * 
     * @param <D>
     * @param list1
     * @param list2
     * @return
     */
    public static <T, D> List<T> list_same1(List<T> list1, List<D> list2, Predicate compareFun) {
        return ListUtil.sameList1(list1, list2, compareFun);
    }

    /**
     * 对集合进行去重复,返回去重后的集合
     * 
     * @param list        原始集合
     * @param biPredicate 比较函数,true代表元素重复则删除当前比较的元素,false代表非重复则保留元素
     * @return 新的集合,实际上无需接收返回,输入的list已经被成功处理了 直接使用即可
     */
    public static List list_duplicate( List list,  BiPredicate biPredicate) {
        return ListUtil.duplicate(list, biPredicate);
    }

    /**
     * 对集合进行排序 按从小到大进行排序 List<Integer> list3= sort(list2,new Comparator<Integer>() {
     *
     * @Override public int compare(Integer o1, Integer o2) { return
     *           o1.compareTo(o2); } });
     * @param <T>     结果
     * @param list    结果
     * @param sortFun 排序函数
     * @return 排序之后的新集合
     */
    public static <T> List<T> list_sort( List<T> list,  Comparator<? super T> sortFun) {
        return ListUtil.sort(list, sortFun);
    }


    /**
     * 分割List
     * @param <T>
     * @param list
     * @param groupSize
     * @return
     */
    public static <T> List<List<T>> list_split(List<T> list, int groupSize) {
        return ListUtil.splitList(list, groupSize);
    }


        /**
     * 将List转为AList
     * @param <T>
     * @param list
     * @return
     */
        public static <T> AList<T> listToAlist( List<T> list) {
        return ListUtil.listToAlist(list);
    }

    /**
     * 将List转为LList
     * @param <T>
     * @param list
     * @return
     */
    public static <T> LList<T> listToLlist( List<T> list) {
        return ListUtil.listToLlist(list);
    }
    /**
     * 过滤集合中的数据,比较函数返回true则需要集合成员。false则丢弃,最终返回所有需要的成员 获取集合中大于小于20元素 List<Integer>,
     * list2= filter(list, new Predicate<Integer>() {
     *
     * @Override public boolean test(Integer t) { return t<20; } });
     * @param <T>        类型
     * @param rawlist    输入集合
     * @param compareFun 比较函数
     * @return 新的结果集合
     */
    public static <T> List<T> list_filter( List<T> rawlist,  Predicate compareFun) {
        return ListUtil.filter(rawlist, compareFun);
    }



    // ArrayUtil============================================================================================================

    /**
     * 复制数组
     * 
     * @param data
     * @return
     */
    public ByteLIst cloneArray( ByteLIst bytes) {
        return new ByteLIst(ArrayUtil.cloneArray(bytes.toBytes()));
    }

    /**
     * 数组对比
     * 
     * @param data1 第一个
     * @param data2 第二个
     * @return
     */
    public boolean equalsBytes( ByteLIst data1,  ByteLIst data2) {
        return ArrayUtil.equals(data1.toBytes(), data2.toBytes());
    }

    /**
     * 求两个数组的差集 如:list1=1,2,3 list2=3.4,5 返回12 求list1的差集合
     * 
     * @param <T>    类型
     * @param <D>    类型
     * @param array1 数组1
     * @param array2 数组2
     * @return 一个差集数组
     */
    public static <T, D> List<T> arr_diffArray1(T[] array1, D[] array2, Predicate compareFun) {
        return ArrayUtil.differenceArray1(array1, array2, compareFun);
    }

    /**
     * 求两个集合的差集 如:list1=1,2,3 list2=3.4,5 返回3 求list1的差集合，如果需要数组自己转换
     * 
     * @param <T>   类型
     * @param <D>   类型
     * @param list1 集合1
     * @param list2 集合2
     * @return
     */
    public static <T, D> List<T> arr_same1(T[] array1, D[] array2, Predicate compareFun) {
        return ArrayUtil.sameArray1(array1, array2, compareFun);
    }

    /**
     * 生成一个泛型泛型数组
     * 
     * @param <T>      类型
     * @param elements 同类型的元素
     * @return
     */
    public static <T> T[] newArray(T... elements) {
        return ArrayUtil.newArray(elements);
    }

    /**
     * 切割数组,若arrays=1,2,3,4 当start=1,end=2 返回的数组 1,2
     * 
     * @param <T>   类型
     * @param array 原始数组
     * @param start 开始的字符位置,第几个(非下标!)
     * @param end   结束的字符位置，第几个(非下标!)
     * @return
     */
    public static <T> T[] arr_sub(T[] array, int start, int end) {
        return ArrayUtil.sub(array, start, end);
    }

    /**
     * 数组转list
     * 
     * @param <T>   类型
     * @param array 原始数组
     * @return 一个lis
     */
    public static <T> List<T> arr_toList(T[] array) {
        return ArrayUtil.toList(array);
    }

    /**
     * 获取集合中的最大值,如果集合为长度为0,返回-1
     *
     * @param numbers
     * @return
     */
    public static Integer arr_maxNum(List<Integer> numbers) {
        return ArrayUtil.maxNum(numbers);
    }

    /**
     * 获得数组里面的最大值的坐标
     * 
     * @param numbers 输入数值数组
     * @return 数组里面的最大值的坐标
     */
    public static int arr_maxNumOfIndex(int[] numbers) {
        return ArrayUtil.maxNumOfIndex(numbers);
    }

    /**
     * 调用数组所有元素的toString方法来拼接字符串
     * 
     * @param listData      为集合数据
     * @param delimiter_opt 每个数据拼接之前的间隔符
     * @return
     */
    public static String arr_toStr(Object[] datas, String delimiter_opt) {
        return ArrayUtil.toStr(datas, delimiter_opt);
    }

    /**
     * 判断同一个数字在,数组中出现的次数
     * 
     * @param numbers 目标整型数组
     * @param num     目标数值
     * @return 次数
     */
    public static int arr_countNumShow(int[] numbers, int num) {
        return ArrayUtil.countNumShow(numbers, num);
    }

    /**
     * 调用数组所有元素的toString方法来拼接字符串
     * 
     * @param listData 为集合数据
     * @return 最终字符串
     */
    public static String arr_toJsonStr(Object[] datas) {
        return ArrayUtil.toJsonStr(datas);
    }

    /**
     * 获取数组中的最大值
     * 
     * @param numbers 目标数值数组
     * @return 出现的最大值
     */
    public static int arr_max(int[] numbers) {
        return ArrayUtil.max(numbers);
    }

    /**
     * 获取数组中的最小值
     * 
     * @param numbers 目标数值数组
     * @return 最小值
     */
    public static int arr_min(int[] numbers) {
        return ArrayUtil.minNum(numbers);
    }

    /**
     * 获取目标数值数组的最小值所在的下标,如果集合为null或者长度为0,返回-1
     * 
     * @param numbers 目标数值数组
     * @return 最小值存在的下标
     */
    public static Integer arr_minIndex(int[] numbers) {
        return ArrayUtil.minNumOfIndex(numbers);
    }

    /**
     * 求数值数组中中位数,如果数值数组为0则返回-1
     * 
     * @param numbers 目标数值数组
     * @return 中位数
     */
    public static Double arr_midium(int[] numbers) {
        return ArrayUtil.midiumNum(numbers);
    }

    /**
     * 求数组平均值
     * 
     * @param numbers 目标数值数组
     * @return 平均值
     */
    public static double arr_average(int[] numbers) {
        return ArrayUtil.averageValue(numbers);
    }

    /**
     * 求数组和
     * 
     * @param numbers 目标数值数组
     * @return 和
     */
    public static int arr_sum(int[] numbers) {
        return ArrayUtil.sum(numbers);
    }

    /**
     * 输出数组中的数据,自动调用集合中的tostring 方法
     * 
     * @param arrays       数值数组
     * @param splitStr_opt 分割的字符串(可选,null="")
     */
    public static void arr_print(Object[] datas,  String splitStr_opt) {
        ArrayUtil.printData(datas, splitStr_opt);
    }

    // numberUtil============================================================================================================

    /**
     * 求百分比a/b
     *
     * @param a                     a
     * @param b                     b
     * @param maximumFractionDigits 保留小数点
     * @return
     */
    public static String percentage(int a, int b, int maximumFractionDigits) {
        return NumberUtil.percentage(a, b, maximumFractionDigits);
    }

    /**
     * 加法计算
     *
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int num_add(int a, int b) {
        return NumberUtil.add(a, b);
    }

    /**
     * 减法计算
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int num_les(int a, int b) {
        return NumberUtil.les(a, b);
    }

    /**
     * 乘法
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int num_mul(int a, int b) {
        return NumberUtil.mul(a, b);
    }

    /**
     * 除法
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static double num_div(double a, double b) {
        return NumberUtil.div(a, b);
    }

    /**
     * 除法
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int num_div(int a, int b) {
        return NumberUtil.div(a, b);
    }

    /**
     * 求余数
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int num_rem(int a, int b) {
        return NumberUtil.rem(a, b);
    }

    /**
     * 求余数
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static double num_rem(double a, double b) {
        return NumberUtil.rem(a, b);
    }

    /**
     * 判断是否为数值
     * 
     * @param data 字符串
     * @return 结果
     */
    public static boolean num_isNum( String data) {
        return NumberUtil.isNumber(data);
    }

    /**
     * 返回一个随机数
     * 
     * @param MIN 随机数最小值
     * @param MAX 随机数最大值
     * @return 结果
     */
    public static int num_random(int MIN, int MAX) {
        return NumberUtil.random(MIN, MAX);
    }

    /**
     * 转换为integer,如果出错,返回null
     * 
     * @param intstr 被转换的字符串
     * @return
     */
    public static Integer num_toInt( String intstr) {
        return NumberUtil.toInt(intstr);
    }

    // RegexUtil============================================================================================================

    /**
     * 创建一个正则表达式帮助
     * 
     * @param pattern 表达式字符串
     * @return 正则表达式工具
     */
    public static RegexUtil regex_create( String pattern) {
        return RegexUtil.createRegex(pattern);
    }

    // TimeUtil============================================================================================================

    /**
     * 构造一个时间
     * 
     * @param year        年
     * @param month       月
     * @param day         日
     * @param hour        时
     * @param min         分
     * @param second      分
     * @param millisecond 秒
     * @return 一个时间
     */
    public static Date time_create(int year, int month, int day, int hour, int min, int second, int millisecond) {
        return TimeUtil.cTime(year, month, day, hour, min, second, millisecond);
    }

    /**
     * 日期转sql日期
     * 
     * @param date
     * @return
     */
    public static java.sql.Date time_toSqlDate( Date date) {
        return TimeUtil.toSqlDate(date);
    }

    /**
     * 创建当前sql日期
     * 
     * @param date
     * @return
     */
    public static java.sql.Date newSqlDate() {
        return time_toSqlDate(time_now());
    }

    /**
     * 日期转sql日期
     * 
     * @param date
     * @return
     */
    public static java.sql.Date time_toSqlDate( long date) {
        return TimeUtil.toSqlDate(date);
    }

    /**
     * 构造一个时间
     * 
     * @param longTime 时间戳
     * @return 时间
     */
    public static Date time_create(long longTime) {
        return TimeUtil.cTime(longTime);
    }

    /**
     * 获取当前时间
     * 
     * @return
     */
    public static Date time_now() {
        return TimeUtil.getNow();
    }

    /**
     * 获取当前时间戳
     * 
     * @return
     */
    public static long time_nowLong() {
        return TimeUtil.getNowLong();
    }

    /**
     * date转long
     * 
     * @param time
     * @return
     */
    public static long time_toLong(Date time) {
        return TimeUtil.dateToLong(time);
    }

    /**
     * long转date
     * 
     * @param date
     * @return
     */
    public static Date time_longToDate(long date) {
        return TimeUtil.longToDate(date);
    }

    /**
     * 根据格式转换时间到字符串,处理出错返回null
     * 
     * @param date   时间对象
     * @param format 输出格式,传递null时默认为yyyy.MM.dd-HH.mm.ss
     * @return
     */
    public static String time_toStr(Date date,  String format) {
        return TimeUtil.dateToStr(date, format);
    }

    /**
     * 字符串转换为date对象,处理出错返回null
     * 
     * @param dateStr    被转换的字符串,格式必须和fromFromat表示的格式一致如2019年8月5日 15:35:46
     * @param fromFromat 原始格式,如:yyyy年MM月dd日 HH:mm:ss
     * @return
     */
    public static Date time_toDate( String dateStr,  String fromFromat) {
        return TimeUtil.strToDate(dateStr, fromFromat);
    }

    /**
     * 获取当前时间的日
     * 
     * @return 日子
     */
    public static int time_nowDay() {

        return TimeUtil.getNowDay();
    }

    /**
     * 获取当前时间的月份
     * 
     * @return 月份
     */
    public static int time_nowMonth() {

        return TimeUtil.getNowMonth();
    }

    /**
     * 获取当前时间的年份
     * 
     * @return
     */
    public static int time_nowYear() {

        return TimeUtil.getNowYear();
    }

    /**
     * 获取当前时间的小时
     * 
     * @return
     */
    public static int time_nowHour() {

        return TimeUtil.getNowHour();
    }

    /**
     * 获取当前时间的分钟
     * 
     * @return
     */
    public static int time_nowMinute() {

        return TimeUtil.getNowMinute();
    }

    /**
     * 获取当前时间的秒
     * 
     * @return
     */
    public static int time_nowSecond() {

        return TimeUtil.getNowSecond();
    }

    /**
     * 获取当前时间的毫秒
     * 
     * @return
     */
    public static int time_nowMillisecond() {

        return TimeUtil.getNowMillisecond();
    }



    // httpUtil============================================================================================================

    /**
     * 获得一个不安全的http代理客户端
     * 
     * @param proxyType 代理类型
     * @param proxyHost 代理主机
     * @param proxyPort 代理端口
     * @param timeoutMs 延迟
     * @return
     */
    public static HttpClient http_getUnsafeOkHttpClientWithPorxy(Proxy.Type proxyType,  String proxyHost,
            int proxyPort, int timeoutMs) {
        return HttpClient.getUnsafeOkHttpClientWithPorxy(proxyType, proxyHost, proxyPort, timeoutMs, null,
                null);
    }

    /**
     * 获得一个不安全的http代理客户端
     *
     * @param proxyType    代理类型
     * @param proxyHost    代理主机
     * @param proxyPort    代理端口
     * @param timeoutMs    延迟
     * @param userName_opt 用户名
     * @param passWord_opt 密码
     * @return
     */
    public static HttpClient http_getUnsafeOkHttpClientWithPorxy(Proxy.Type proxyType,  String proxyHost,
            int proxyPort, int timeoutMs, String username, String password) {

        return HttpClient.getUnsafeOkHttpClientWithPorxy(proxyType, proxyHost, proxyPort, timeoutMs,
                username, password);
    }

    /**
     * 获得一个不安全的http代理客户端
     *
     * @param timeoutMs 超时
     * @return
     */
    public static HttpClient http_getUnsafeOkHttpClient(long timeoutMs) {
        return HttpClient.getUnsafeOkHttpClient(Long.valueOf(timeoutMs));
    }


 


    /**
     * 相对路径转绝对路径url
     *
     * @param absoluteUrlString http://www.baidu.com/1/2
     * @param relativelyString  ../../3
     * @return http://www.baidu.com/1/3
     */
    public static URL parseUrl( String url) {
        return HttpUtil.parseUrl(url);
    }




    /**
     * 相对路径转绝对路径url
     *
     * @param absoluteUrlString http://www.baidu.com/1/2
     * @param relativelyString  ../../3
     * @return http://www.baidu.com/1/3
     */
    public static URL parseUrl( String absoluteUrlString,  String relativelyString) {
        return HttpUtil.parseUrl(absoluteUrlString,relativelyString);
    }



    // fileread============================================================================================================

    /**
     * 创建一个文件夹对象
     * 
     * @param file 文件夹对象
     * @return
     */
    public static DirRes dirres( FileRes file) {
        return new DirRes(file.getFile());
    }

    /**
     * 创建一个文件夹对象, 此路径可以是绝对也可以是相对路径(也可以是文件名),相对路径的函数来源于SystemUtil.getMyDir()
     * 
     * @param dirPathOrName 文件夹对象
     * @return
     */
    public static DirRes dirres( String dirPathOrName) {
        return new DirRes(dirPathOrName);
    }


    /**
     * 创建一个文件对象
     * 
     * @param file 文件对象
     * @return
     */
    public static FileRes fileres( File file) {
        return new FileRes(file);
    }

    /**
     * 创建一个文件对象, 此路径可以是绝对也可以是相对路径(也可以是文件名),相对路径的函数来源于SystemUtil.getMyDir()
     * 
     * @param filePathOrName 文件对象路径
     * @return
     */
    public static FileRes fileres( String filePathOrName) {
        return new FileRes(filePathOrName);
    }

    /**
     * 读取文本文件全部行，执行失败则返回一个长度为0的集合
     * 
     * @param file        文件
     * @param charSet_opt 编码格式null=utf8
     * @return 列表行
     * @throws IOException
     */
    public static List<String> file_readLines( FileRes file, String charSet_opt) {
        return FileReadUtil.readFilesLinesToStringList(file.getFile(), charSet_opt);
    }

        /**
     * 读取jar内部文件
     * @param jarFilePath
     * @return
     */
        public byte[] getJarLibFile( String jarFilePath) {
        return SystemUtil.getJarLibFile(jarFilePath);
    }

    /**
     * 将文件读取到一个字符串中
     * 
     * @param file
     * @param charSet_opt 编码格式null=系统默认编码
     * @return
     * @throws IOException
     */
    public static String file_read( FileRes file, String charSet_opt) {

        return FileReadUtil.readFileToString(file.getFile(), charSet_opt);
    }

    /**
     * 读取文本文件的倒序第几行
     * 
     * @param file
     * @throws IOException
     */
    public static List<String> file_readLastLine( FileRes file, int lineCount, String charSet_opt)
            throws IOException {

        return FileReadUtil.readFileLastLineIndex(file.getFile(), lineCount, charSet_opt);
    }

    /**
     * 读取文件到二进制字节数组
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static ByteLIst file_readByteList( FileRes file) {

        return new ByteLIst(FileReadUtil.readFileToByteArray(file.getFile()));
    }

    /**
     * 读取网络上的url资源为file对象，执行失败则返回一个空的字符串
     * 
     * @param url            目标Url
     * @param httpClient_opt http客户端 (可选null=一个直连接的客户端)
     * @param charset_opt 响应编码格式
     * @param header_opt     请求头(可选null=添加基本的用户代理)
     * @return
     */
    public static String file_readUrl( String url, HttpClient httpClient, HMap<String, String> header_opt,
            String charset_opt) {

        return FileReadUtil.readUrlToStr(url, httpClient, header_opt,charset_opt);
    }

    /**
     * 读取网络上的url资源为,将body返回为bytes,如果处理或者连接失败返回长度为0字节数组,当heade为null时,默认头部只放入浏览器字段
     * 
     * @param url            目标Url
     * @param httpClient_opt http客户端 (可选null=一个直连接的客户端)
     * @param header_opt     请求头(可选null=添加基本的用户代理)
     * @return
     */
    public static ByteLIst file_readUrlToBytes( String url, HttpClient httpClient,
            HMap<String, String> header_opt) {

        return new ByteLIst(FileReadUtil.readUrlToBytes(url, httpClient, header_opt));
    }

    // fileUtil============================================================================================================

    /**
     * 获取文件/夹名后缀名(.a=a 不包含.) 当没有文件扩展名的时候返回空字符串
     *
     * @param fileOrDir 文件/夹
     * @return 返回扩展名
     */
    public static String file_extName( FileRes fileOrDir) {

        return FileUtil.getExtensionName(fileOrDir.getFile());
    }

    /**
     * 获取文件/夹名后缀名 当没有文件扩展名的时候返回空字符串
     * 
     * @param url 文件/夹
     * @return 返回扩展名
     */
    public static String file_extName(URL url) {

        return FileUtil.getExtensionName(url);
    }

    /**
     * 获取文件/夹名,不包含后缀
     * 
     * @param fileOrDir 文件/夹
     * @return 文件名,不包含后缀
     */
    public static String file_baseName( FileRes fileOrDir) {

        return FileUtil.getBaseName(fileOrDir.getFile());
    }

    /**
     * 获取文件/夹名,不包含后缀
     * 
     * @param url 文件/夹
     * @return 文件/夹名,不包含后缀
     */
    public static String file_baseName(URL url) {

        return FileUtil.getBaseName(url);
    }

    /**
     * 获取文件/夹名,包含后缀
     * 
     * @param url 文件/夹
     * @return 文件/夹名,包含后缀
     */
    public static String file_name(URL url) {

        return FileUtil.getName(url);
    }

    /**
     * 获取文件/夹名,包含后缀
     * 
     * @param fileOrDir 文件/夹
     * @return 文件/夹名,包含后缀
     */
    public static String file_name( FileRes fileOrDir) {

        return FileUtil.getName(fileOrDir.getFile());
    }

    /**
     * 读取文件/夹所在的目录
     * 
     * @param fileOrDir 文件/夹
     * @return 文件/夹所在的目录
     */
    public static String file_fullPath( FileRes fileOrDir) {

        return FileUtil.getFullPath(fileOrDir.getFile());
    }

    /**
     * 新建一个file对象,可以使用绝对路径,也可以使用相对路径
     * 
     * @param path 文件路径
     * @return
     */
    public static File file_new( String path) {

        return FileUtil.newFile(path);
    }

    /**
     * 读取文件/夹所在的目录
     * 
     * @param url 文件
     * @return 文件所在的目录
     */
    public static String file_fullPath(URL url) {

        return FileUtil.getFullPath(url);
    }

    /**
     * 从路径中读取目录名
     * 
     * @param file 文件
     * @return 目录名
     */
    public static String file_dirName( FileRes file) {

        return FileUtil.getDirName(file.getFile());
    }





    /**
     * 判断文件是否匹配后缀,如果匹配成功则返回true
     * 
     * @param file      文件
     * @param extension 扩展名
     * @return 成功则返回true
     */
    public static boolean file_isExt( FileRes file,  String extension) {

        return FileUtil.isExtension(file.getFile(), extension);
    }

    /**
     * 判断文件名是否匹配后缀 数组,如果包含一个则返回true
     * 
     * @param file      文件
     * @param extension 扩展名
     * @return true成功false失败
     */
    public static boolean file_isExt( FileRes file, String... extensions) {

        return FileUtil.isExtension(file.getFile(), extensions);

    }

    /**
     * 复制磁盘文件到目标磁盘文件
     * 
     * @param srcFile
     * @param destFile
     * @return 成功true
     */
    public static boolean file_copy( FileRes srcFile,  FileRes destFile) {

        return FileUtil.copyFileTo(srcFile.getFile(), destFile.getFile());
    }

    /**
     * 复制磁盘文件到某个目录
     *
     * @param src FileRes 源文件
     * @param destDir     文件目录
     * @return true成功false失败
     */
    public static boolean file_copyToDir( FileRes srcFile,  FileRes destDir) {

        return FileUtil.copyFileToDirectory(srcFile.getFile(), destDir.getFile());
    }

    /**
     * 获取远程url资源的数据流，可以是一个文件,也可以是一堆字符
     * 
     * @param uri 远程url地址
     * @return 返回读取到的字节流，如果读取异常则返回null
     */
    public static ByteLIst file_urlBytes(URL uri) {

        return new ByteLIst(FileUtil.getURIFileArrayBytes(uri));
    }

    /**
     * 删除磁盘文件/夹
     * 
     * @param srcFileOrDir 文件
     * @return true成功false失败
     */
    public static boolean file_del( FileRes srcFileOrDir) {

        return FileUtil.deleteFileOrDir(srcFileOrDir.getFile());
    }

    /**
     * 移动文件或者目录,移动前后文件完全一样,如果目标文件夹不存在则创建。
     * 
     * @param resFileOrDir 源文件路径
     * @param distDir      目标文件夹
     * @return true成功false失败
     */
    public static boolean file_move( FileRes resFileOrDir,  FileRes distDir) throws IOException {

        return FileUtil.moveFileOrDir(resFileOrDir.getFile(), distDir.getFile());
    }

    /**
     * 创建空文件夹,如果文件夹已经存在则返回false,如果不存在则创建文件夹,成功返回true不成功则返回false
     * 
     * @param dir 目录
     * @return true成功false失败
     */
    public static boolean file_createDir( FileRes dir) {

        return FileUtil.createDir(dir.getFile());
    }

    /**
     * 创建一个文件,Override参数决定是否进行覆盖,如果文件真实被创建成功则返回true,其它情况一律返回false
     * 在写入之前确保文件的中父文件夹是存在的,否则无法创建文件并返回false
     *
     * @param file     被写入的文件对象
     * @param Override 是否覆盖磁盘已有文件
     * @return
     */
    public static boolean file_createFile( FileRes file, boolean override) {

        return FileUtil.createFile(file.getFile(), override);
    }

    /**
     * 清空磁盘目录中的文件
     * 
     * @param dir 文件夹
     * @return true成功false失败
     */
    public static boolean file_clearDir( FileRes dir) {

        return FileUtil.deleteDirFiles(dir.getFile());
    }

    /**
     * 获得某个文件夹下面的文件夹或者文件
     * 
     * @param outDirAndFiles   输出参数,使用完毕之后可以直接遍历传入的outPutFiles
     * @param dir              起始目录
     * @param setDeep          设置发现深度
     * @param deep             起始深度值,必须为-1!
     * @param pattern          匹配文件或文件夹的的正则表达式,*代表所有 例如,匹配后缀
     *                         ([^\\s]+(\\.(?i)(jpg|png))$),
     * @param onlyMathFilename 正则表达式是否只匹配文件/夹名称(包含后缀)默认匹配文件的完整限定名 ,当匹配正则为 *
     *                         的时候,这个参数true和false都不会影响到程序的逻辑
     * @param ioType           设置想要得到的io类型.可选的值FILE文件,DIR文件夹,,FILE_AND_DIR文件夹和文件
     */
    public static void file_getAllDirAndFileByDir(List<File> outDirAndFiles,  FileRes dir, int setDeep,
            int deep,  String pattern, boolean onlyMathFilename, IoType ioType) {

        FileUtil.getAllDirAndFileByDir(outDirAndFiles, dir.getFile(), setDeep, deep, pattern, onlyMathFilename, ioType);

    }

    /**
     * 重命名文件或文件夹
     * 
     * @param oldFileOrDir 源文件路径
     * @param newName      新名字
     * @return
     * @return 操作成功标识
     */
    public static boolean file_rename( FileRes oldFileOrDir,  String newName) {

        return FileUtil.renameFileOrDir(oldFileOrDir.getFile(), newName);
    }

    // thread()-=================================================================================================================

    /**
     * 获取定长线程池
     *
     * @param poolNamePrefix 池前缀
     * @param poolLength
     * @return
     */
    public static FixedPool getFixedPool(int poolLength, String poolNamePrefix) {
        return ExecPool.getFixedPool(poolLength,poolNamePrefix);
    }

    /**
     * 获取定长线程池
     * 
     * @param poolLength
     * @return
     */
    public static TimePool getTimerPool(int poolLength) {
        return ExecPool.getTimerPool(poolLength);
    }

    // fileWrite============================================================================================================

    /**
     * 写入字符串到磁盘文件,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param str         文本
     * @param file        文件
     * @param charSet_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public static boolean file_write( String line,  FileRes file,  String charSet_opt) {
        return FileWriteUtil.writeStrToFile(line, file.getFile(), charSet_opt);
    }

    /**
     * 写入字符串到磁盘文件,每个元素自动换行,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     *
     * @param strs        字符串列表
     * @param file        文件
     * @param charSet_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public static boolean file_write(List<String> strs,  FileRes file,  String charSet_opt) {

        return FileWriteUtil.writeStrToFile(strs, file.getFile(), charSet_opt);
    }

    /**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param str         字符串
     * @param file        目标文件
     * @param charSet_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public static boolean file_appendStr( String str,  FileRes file, String charSet_opt) {

        return FileWriteUtil.appendWriteStrToFile(str, file.getFile(), charSet_opt);
    }

    /**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param lines           字符串列表
     * @param file            目标文件
     * @param lineSpitStr_opt 写入的换行符号 null=系统默认值
     * @param charset_opt     charset编码
     * @return 成功true,失败false
     */
    public static boolean file_appendStr(List<String> lines,  FileRes file, String lineSpitStr_opt,
            String charset_opt) {

        return FileWriteUtil.appendWriteStrToFile(lines, file.getFile(), lineSpitStr_opt, charset_opt);
    }

    /**
     * 写入字节数组到磁盘文件中,如果文件存在文件将被重写覆盖, 如果该文件不存在，则创建该文件
     * 
     * @param bytes 字节数组
     * @param file  被写入的磁盘文件
     * @return 成功true,失败false
     */
    public static boolean file_writeBytes( ByteLIst bytes,  FileRes file) {

        return FileWriteUtil.writeByteArrayToFile(bytes.toBytes(), file.getFile());
    }

    /**
     * 追加写入字节数组到磁盘文件中 如果该文件不存在，则创建该文件
     *
     * @param bytes 字节数组
     * @param file  被写入的磁盘文件
     * @return 成功true,失败false
     */
    public static boolean file_appendBytes( ByteLIst bytes,  FileRes file) {

        return FileWriteUtil.appendWriteBytesToFile(bytes.toBytes(), file.getFile());
    }

    // SystemUtil============================================================================================================

    /**
     * 显示通知
     *
     * @param title
     * @param message
     */
    public static void notify( String title,  String message) {
        SystemUtil.notify(title, message);
    }
    /**
     * 检查JVM是否为debug模式。
     *
     * @return
     */
    public static boolean sys_isDbg() {

        return SystemUtil.isDebuggerAttached();
    }

    /**
     * 当接收到ctrl_c命令行的时候执行操作
     * 
     * @param task
     */
    public static void onCtrlC( DoOneJob task) {
        SystemUtil.onCtrlC(task);
    }

    /**
     * 生成一个uuid
     * 
     * @return
     */
    public static UUID sys_getUUID() {

        return SystemUtil.getUUID();
    }

    /**
     * 获得临时文件目录的路径 /tmp/
     * 
     * @return
     */
    public static String sys_tmpDirName() {

        return SystemUtil.tmpDirName();
    }


        /**
         * 启动一个进程
         *
         * @param commond
         * @return
         */
        public static Opt<Process> startPorcess(String... commond) {
            return SystemUtil.startPorcess(commond);
        }

        /**
         * /** 同步执行shell命令,等待执行完毕后返回执行的结果,如果执行错误返回null
         *
         * @param commond 命令
         * @return 执行结果字符串
         */
    public static String exec2String(String... commond) {
        return SystemUtil.syncExecShellString(null, commond);
    }

    /**
     * 同步执行shell命令,等待执行完毕后返回执行的结果,如果执行错误返回null
     *
     * @param workDir 工作目录i
     * @param commond 命令
     * @return 执行结果字符串
     */
    public static String exec2String(DirRes workDir, String... commond) {
        return SystemUtil.syncExecShellString(workDir, commond);
    }

    /**
     * 同步执行shell命令,等待执行完毕后返回执行的结果,如果执行错误返回null
     *
     * @param commond 命令
     * @return 执行结果字符串
     */
    public static String exec2String( String commond) {

        return SystemUtil.syncExecShellString(commond);
    }

    /**
     * 异步执行shell命令,返回一个进程对象,如果执行错误返回null
     *
     * @param commond 命令
     * @return 返回执行的进程对象
     */
    public static Process asyncExec(String... commond) {
        return SystemUtil.asyncExecShell(null, commond);
    }

    /**
     * 异步执行shell命令,返回一个进程对象,如果执行错误返回null
     *
     * @param workDir 工作目录
     * @param commond 命令
     * @return 返回执行的进程对象
     */
    public static Process asyncExec(DirRes workDir, String... commond) {
        return SystemUtil.asyncExecShell(workDir, commond);
    }

    /**
     * 杀死进程
     * 
     * @param process
     */
    public static void killProcess(Process process) {

        SystemUtil.killProcess(process);
    }

    /**
     * 杀死进程
     * 
     * @param pid
     * @return
     */
    public static boolean killProcess(Integer pid) {

        return SystemUtil.killProcess(pid);
    }

    /**
     * 输出集合为表格到控制台headers为表格头，content为表格f体,外层list代表整个表格,内层list代表一行
     * 
     * @param headers headers
     * @param content content
     */
    public static void printTable( ArrayList<String> headers,  ArrayList<ArrayList<String>> content) {
        ConsoleTable consoleTable = new ConsoleTable(headers, content);
        consoleTable.printTable();
    }

    /**
     * google翻译
     * 
     * @param data        被翻译的数据
     * @param hClient_opt 请求的http客户端(可选,null=直连)
     * @return
     */
    public static String googleTran( String data, HttpClient httpClient) {

        return SystemUtil.googleAPItran(data, httpClient);
    }

    /**
     * 终止程序
     */
    public static void exit() {

        SystemUtil.exit();
    }

    /**
     * 休眠当前线程
     *
     * @param mm 毫秒
     */
    public static void sleep(int mm) {

        SystemUtil.sleep(mm);
    }

    /**
     * 休眠当前线程
     *
     * @param mm         毫秒
     * @param catchError 当线程发生异常时候的处理函数,一般在线程池任务请求中断的时候发生异常
     */
    public static void sleep(int mm,  Consumer<Exception> catchError) {
        SystemUtil.sleep(mm, catchError);
    }

    /**
     * 杀死进程,根据一个正则字符串,成功返回true,失败返回false
     * 
     * @param porcessName 字符串
     */
    public static boolean killProcess( String commName) {

        return SystemUtil.killProcess(commName);
    }

    /**
     * 判断是linux系统还是其他系统 如果是Linux系统，返回true，否则返回false
     */
    public static boolean isLinux() {

        return SystemUtil.isLinux();
    }

    /**
     * 将域名转换为ip地址
     * 
     * @param domain 域名
     * @return
     */
    public static github.acodervic.mod.data.str parseDomainToIp( String domain) {
        return str(SystemUtil.parseDomainToIp(domain));
    }

    /**
     * 将域名转换为ip地址
     * 
     * @param domain 域名
     * @return
     */
    public static github.acodervic.mod.data.str parseDomainToIp( str domain) {

        return str(SystemUtil.parseDomainToIp(domain.toString()));
    }

    /**
     * 获取ip位置
     * 
     * @param ip
     * @return
     */
    public static String getIpLocation( String ip) {

        return SystemUtil.getIpLocation(ip);
    }

    /**
     * 获取当前可执行文件运行的路径
     *
     * @return
     */
    public static String getMyDir() {

        return SystemUtil.getMyDir();
    }

    /**
     * 获取当前进程用户
     * 
     * @return
     */
    public static String getMyUser() {

        return SystemUtil.getMyUser();
    }

    /**
     * 获取当前进程的pid
     * 
     * @return
     */
    public static Integer getMyPid() {

        return SystemUtil.getMyPid();
    }

    // Encode============================================================================================================
    /**
     * url编码字符串
     * 
     * @param str
     * @return
     */
    public static String en_strToUrlStr( String str) {
        return Encode.strToUrl(str);
    }

    /**
     * 字符串转base64字符串(先转byte然后在转字符串),失败则返回空字符串
     * 
     * @param str         原始输入字符串
     * @param charset_opt 输入字符串的编码(可选,null=utf8,将以此编码格式转换输入字符串str为字节数组)
     * @return base64字符串
     */
    public static String en_strToBase64Str( String str,  String charSet_opt) {

        return Encode.strToBase64Str(str, charSet_opt);
    }

    /**
     * 字符串转base64URL字符串(先转byte然后在转字符串),失败则返回空字符串
     * 
     * @param str         原始输入字符串
     * @param charset_opt 输入字符串的编码(可选,null=utf8,将以此编码格式转换输入字符串str为字节数组)
     * @return base64URL字符串
     */
    public static String en_strToBase64UrlStr( String str, String charSet_opt) {

        return Encode.strToBase64UrlStr(str, charSet_opt);
    }

    /**
     * 字符串转base64字节数组,(先转byte然后在转字符串),失败则返回空字节数组
     *
     * @param str         原始输入字符串
     * @param charset_opt 输入字符串的编码(可选,null=utf8,将以此编码格式转换输入字符串str为字节数组)
     * @return ByteLIst
     */
    public static ByteLIst en_strToBase64Bytes( String str,  String charset_opt) {

        return new ByteLIst(Encode.strToBase64Bytes(str, charset_opt));

    }

    /**
     * 将字节数组转换为base64字节数组,失败则返回空字节数组
     * 
     * @param bytes 原始字节数组
     * @return ByteLIst
     */
    public static ByteLIst en_bytesToBase64Bytes( ByteLIst bytes) {

        return new ByteLIst(Encode.bytesToBase64Bytes(bytes.toBytes()));
    }

    /**
     * 将字节数组转换为base64URL字节数组,失败则返回空字节数组
     * 
     * @param bytes 原始字节数组
     * @return base64URL字符串
     */
    public static String en_bytesToBase64SafeStr( ByteLIst bytes) {

        return Encode.bytesToBase64SafeStr(bytes.toBytes());
    }



    // Decode============================================================================================================

    /**
     * 解码url字符串
     * 
     * @param urlStr
     * @return
     */
    public static String de_URLstrToStr( String urlStr) {
        return Decode.urlToStr(urlStr);
    }

    /**
     * 解码base64字符串为普通字符串,如果失败则返回空字符串
     * 
     * @param str         base64字符串
     * @param charset_opt 解码后的新的字符串的编码(可选,null则为utf-8)
     * @return 解码后的字符串
     */
    public static String de_base64StrToStr( String base64Str, String charset_opt) {

        return Decode.base64StrToStr(base64Str, charset_opt);
    }

    /**
     * 解码base64URL字符串为普通字符串,如果失败则返回空字符串
     * 
     * @param base64Str   base64字符�����
     * @param charset_opt 解码后的新的字符串的编码(可选,null则为utf-8)
     * @return 解码后的字符串
     */
    public static String de_base64UrlStrToStr( String base64Str, String charset_opt) {

        return Decode.base64UrlStrToStr(base64Str, charset_opt);
    }

    /**
     * 解码base64字符串为字节数组,如果失败则返回空字节数组
     * 
     * @param base64Str base64字符串
     * @return 解码之后的字节数组
     */
    public static ByteLIst de_base64StrToBytes( String base64Str) {

        return new ByteLIst(Decode.base64StrToBytes(base64Str));
    }

    /**
     * 解码base64URL字符串到字节数组,如果失败则返回空字节数组
     * 
     * @param base64Str base64字符串
     * @return 解码之后的字节数组
     */
    public static ByteLIst de_base64UrlStrToBytes( String base64Str) {

        return new ByteLIst(Decode.base64UrlStrToBytes(base64Str));
    }

    /**
     * 解码base64编码之后的字节数组
     * 
     * @param base64Bytes base64编码之后的字节数组
     * @return 解码之后的字节数组
     */
    public static ByteLIst de_base64BytesToBytes( ByteLIst base64Bytes) {

        return new ByteLIst(Decode.base64BytesToBytes(base64Bytes.toBytes()));
    }

    // ============================================================================================================
    // Md5摘要==========================================================================================

    /**
     * 对字节数组进行md5摘要,返回摘要后的md5字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的md5字节数组
     */
    public static ByteLIst hash_bytesToMd5Bytes(byte[] bytes) {

        return new ByteLIst(Digest.bytesToMd5Bytes(bytes));
    }

    /**
     * 对字符串进行md5摘要,返回摘要后的md5字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param str 输入字节数组
     * @return 摘要后的md5字节数组
     */
    public static ByteLIst hash_strToMd5Bytes( String str) {

        return new ByteLIst(Digest.strToMd5Bytes(str));
    }

    /**
     * 对输流进行md5摘要,返回摘要后的md5字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param InputStream 输入流
     * @return 摘要后的md5字节数组
     */
    public static ByteLIst hash_InputStreamToMd5Bytes(InputStream InputStream) {

        return new ByteLIst(Digest.InputStreamToMd5Bytes(InputStream));
    }

    // 字符串z摘要-------------------------------
    /**
     * 对字符串进行md5摘要,返回md5十六进制字符串,出现异常则返回空字符串
     * 
     * @param str 输入字节数组
     * @return 摘要之后的16进制字符串
     */
    public static String hash_bytesToMd5HexStr(byte[] bytes) {

        return Digest.bytesToMd5HexStr(bytes);
    }

    /**
     * 对字符串进行md5摘要,返回md5十六进制字符串,出现异常则返回空字符串
     * 
     * @param str 输入字符串
     * @return 摘要之后的16进制字符串
     */
    public static String hash_strToMd5HexStr( String str) {

        return Digest.strToMd5HexStr(str);
    }

    /**
     * 对字节数组进行md5摘要,返回md5十六进制字符串,出现异常则返回空字符串
     * 
     * @param InputStream 输入流
     * @return 摘要之后的16进制字符串
     */
    public static String hash_bytesToMd5HexStr(InputStream InputStream) {

        return Digest.bytesToMd5HexStr(InputStream);
    }



    // 字符串z摘要-------------------------------

    // SHA-1摘要==========================================================================================

    /**
     * 对字符串数据进行sha1摘要,返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param str 输入字符串
     * @return 摘要之后的字符串
     */
    public static String hash_strToSha1Str( String str) {

        return Digest.strToSha1Str(str);
    }

    /**
     * 对字节数组进行sha1摘要,返回摘要后的字符串,出现异常则返回长度为0的字节数组
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的字符串
     */
    public static String hash_bytesToSha1Str( ByteLIst bytes) {

        return Digest.bytesToSha1Str(bytes.toBytes());
    }

    /**
     * 对输入流进行SHA-1摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param InputStream 输入流
     * @return 摘要后的字节数组
     */
    public static ByteLIst hash_bytesToSha1Str(InputStream InputStream) {

        return new ByteLIst(Digest.bytesToSha1Str(InputStream));

    }

    // SHA-256摘要==========================================================================================

    /**
     * 对字符串进行sha256摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param str 输入字符串
     * @return 摘要后的数组
     */
    public static ByteLIst hash_strToSha256bytes( String str) {

        return new ByteLIst(Digest.strToSha256bytes(str));
    }

    /**
     * 对字节数组进行sha256摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的数组
     */
    public static ByteLIst hash_bytesToSha256bytes( ByteLIst bytes) {

        return new ByteLIst(Digest.bytesToSha256bytes(bytes.toBytes()));
    }

    /**
     * 对输入流进行sha256摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param InputStream 输入流
     * @return 摘要后的字节数组
     */
    public static ByteLIst hash_InputStreamToSha256bytes(InputStream InputStream) {

        return new ByteLIst(Digest.InputStreamToSha256bytes(InputStream));
    }

    /**
     * 对字符串进行sha256摘要，返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param str 输入字符串
     * @return 摘要后的字符串
     */
    public static String hash_strToSha256Str( String str) {

        return Digest.strToSha256Str(str);
    }

    /**
     * 对字节数组进行sha256摘要，返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的字符串
     */
    public static String hash_bytesToSha256Str( ByteLIst bytes) {

        return Digest.bytesToSha256Str(bytes.toBytes());
    }

    /**
     * 对输入流进行sha256摘要，返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param InputStream 输入流
     * @return 摘要后的字符串
     */
    public static String hash_InputStreamToSha256Str( InputStream InputStream) {

        return Digest.InputStreamToSha256Str(InputStream);

    }

    // bioStreamUtil
    // 摘要==========================================================================================
    /**
     * 读取输入流为字符串
     * 
     * @param in          输入流
     * @param charSet_opt 字符串编码 (可选null=)默认为utf8
     * @return
     * @throws IOException
     */
    public static String bio_readInputStreamToString( InputStream in, String charSet_opt) throws IOException {

        return BioStreamUtil.readInputStreamToString(in, charSet_opt);
    }

    /**
     * 读取输入流为字节列表,读取失败则返回一个长度为0的字节列表
     * 
     * @param in 输入流
     * @return 字节列表
     */
    public static ByteLIst bio_readInputStreamToBytes(InputStream in) throws IOException {

        return BioStreamUtil.readInputStreamToBytes(in);
    }

    /**
     * 读取输入流为行列表
     * 
     * @param in          输入流
     * @param charSet_opt 字符串编码 (可选null=)默认为utf8
     * @return 行列表
     * @throws IOException
     */
    public static List<String> bio_readInputStreamToLines(InputStream in, String charSet_opt) throws IOException {

        return BioStreamUtil.readInputStreamToLines(in, charSet_opt);

    }

    /**
     * 创建一个逻辑表达式
     * 
     * @param supplier
     * @return
     */
    public static Logic Logic( Supplier<Boolean> supplier) {
        return new Logic(supplier);
    }

    // 函数式编程函数=================================================================================================================================
    /**
     * 如果k为null则抛出异常e
     * 
     * @param <K>
     * @param <T>
     * @param k_opt 检测为null的对象
     * @param e     如果k为null则抛出的异常对象
     * @throws T
     */
    public static <K, T extends Exception> void ifNullThrow(K k_opt,  T e) throws T {
        FunctionUtil.ifThrow(k_opt == null, e);
    }

    /**
     * 如果flag为true则抛出异常e
     * 
     * @param <T>
     * @param flag 标记判断
     * @param e    被抛出的异常
     * @throws T
     */
    public static <T extends Exception> void ifThrow( boolean flag,  T e) throws T {
        FunctionUtil.ifThrow(flag, e);
    }

    /**
     * 如果value为null则调用runnable的run函数,注意是调用run而不是新建线程,会同步阻塞当前线程
     * 
     * @param value_opt 判断是否为null的值
     * @param runnable  如果为null则执行的runnale的对象
     */
    public static void ifNullThen(Object value_opt,  Runnable runnable) {
        FunctionUtil.ifNullThen(value_opt, runnable);
    }

    /**
     * 如果value不为null则调用runnable的run函数,注意是调用run而不是新建线程,会同步阻塞当前线程
     * 
     * @param value_opt 判断是否为null的值
     * @param runnable  如果为null则执行的runnale的对象
     */
    public static void ifNotNullThen(Object value_opt,  Runnable runnable) {
        FunctionUtil.ifNotNullThen(value_opt, runnable);
    }

    /**
     * 如果flag为true,则调用t对象的run方法
     * 
     * @param <T>
     * @param flag 判断标记
     * @param t    执行run函数的对象,一般为runnable实例
     */
    public static <T extends Runnable> void ifThen( boolean flag,  T t) {
        FunctionUtil.ifThen(flag, t);
    }

    /**
     * 如果flag为true,则调用t1对象的run方法,否则则调用t2对象的run方法
     * 
     * @param <T>
     * @param flag 判断标记
     * @param t1   执行run函数的对象,一般为runnable实例
     * @param t2   执行run函数的对象,一般为runnable实例
     */
    public static <T extends Runnable> void ifThen( boolean flag,  T t1,  T t2) {
        FunctionUtil.ifThen(flag, t1, t2);
    }

    /**
     * 如果value不为null则返回t1否则返回t2,t1和t2必须类型相同
     * 
     * @param <K>
     * @param <T>
     * @param value_opt 被判断是否为null的值
     * @param t1        null时候的返回
     * @param t2        不为null时候的返回
     * @return
     */
    public static <K, T> T ifNotNullReturn(K value_opt,  T t1,  T t2) {
        return FunctionUtil.ifNotNullReturn(value_opt, t1, t2);
    }

    /**
     * 如果value不为null则返回supplier函数式提供的结果
     * 
     * @param <T>
     * @param value_opt 判断不为null的变量
     * @param supplier  提供的结果的supplier函数式
     * @return
     */
    public static <T> T ifNotNullReturn(T value_opt,  Supplier<T> supplier) {
        return FunctionUtil.ifNotNullReturn(value_opt, supplier);
    }

    /**
     * 如果flag为true则返回t1否则返回t2.t1和t2必须类型相同
     * 
     * @param <T>
     * @param flag 判断变量
     * @param t1
     * @param t2
     * @return
     */
    public static <T> T ifReturn(boolean flag, T t1, T t2) {
        return FunctionUtil.ifReturn(flag, t1, t2);
    }

    /**
     * 如果flag为true则返回t1否则返回t2.t1和t2必须类型相同
     * 
     * @param <T>
     * @param flag 判断变量
     * @param s1   s1的返回值函数式
     * @param s2   s2的返回函数式
     * @return
     */
    public static <T> T ifReturn(boolean flag, Supplier<T> s1, Supplier<T> s2) {
        return ifReturn(flag, s1, s2);
    }

    /**
     * 如果flag为true则返回t1,否则抛出一个RuntimeException
     * 
     * @param <T>
     * @param flag 判断的标志变量
     * @param t1   flag为null返回的变量
     * @param e    flag为flase时候抛出的运行时异常
     * @return
     */
    public static <T> T ifReturnOrThrow(boolean flag, T t1, RuntimeException e) {
        return FunctionUtil.ifReturnOrThrow(flag, t1, e);
    }


    /**
     * 尝试做一些任务,而无需处理异常,当内部发生异常则返回异常对象
     *
     * @param task
     * @param reportTheError 是否报告异常? true为报告
     * @return
     */
    public static Exception tryDo( TrySupplierFun task,  boolean reportTheError) {
        return FunctionUtil.tryDo(task, reportTheError);
    }

    /**
     * 尝试做一些任务,而无需处理异常,当内部发生异常则返回异常对象
     * 
     * @param task
     * @return
     */
    public static Exception tryDo( TrySupplierFun task) {
        return FunctionUtil.tryDo(task, true);
    }

    /**
     * 执行表达式并尝试返回结果,如果出现异常则返回errorVal
     *
     * @param <T>          类型
     * @param supplier     有返回值的表达式
     * @param errorVal_opt 执行表达式异常之后的返回(可选)可以为null
     * @return 一个返回值
     */
    public static <T> T tryReturn( TrySupplierReturnFun<T> supplier, T errorVal_opt) {
        return FunctionUtil.tryReturn(supplier, errorVal_opt, null);
    }


    /**
     * 执行表达式并尝试返回结果,如果出现异常则返回errorVal
     *
     * @param <T>          类型
     * @param supplier     有返回值的表达式
     * @param errorVal_opt 执行表达式异常之后的返回(可选)可以为null
     * @param printStackTrace 是否报告异常? true 为是printStackTrace()
     * @return 一个返回值
     */
    public static <T> T tryReturn( TrySupplierReturnFun<T> supplier, T errorVal_opt,boolean printStackTrace) {
        return FunctionUtil.tryReturn(supplier, errorVal_opt, null,printStackTrace);
    }


    /**
     * 执行表达式并尝试返回结果,如果出现异常则返回errorVal
     *
     * @param <T>              类型
     * @param supplier         有返回值的表达式
     * @param errorVal         执行表达式异常之后的返回
     * @param errorMessage_opt 一段错误信息
     * @return 一个返回值
     */
    public static <T> T tryReturn( TrySupplierReturnFun<T> supplier,  T errorVal,
            String errorMessage_opt) {
        return FunctionUtil.tryReturn(supplier, errorVal, errorMessage_opt);
    }

    /**
     * 执行表达式并尝试返回结果,如果出现异常则返回errorVal
     *
     * @param <T>              类型
     * @param supplier         有返回值的表达式
     * @param errorVal         执行表达式异常之后的返回
     * @param errorMessage_opt 一段错误信息
     * @param printStackTrace 是否报告异常? true 为是printStackTrace()
     * @return 一个返回值
     */
    public static <T> T tryReturn( TrySupplierReturnFun<T> supplier,  T errorVal,
            String errorMessage_opt,boolean   printStackTrace){
        return FunctionUtil.tryReturn(supplier, errorVal, errorMessage_opt,printStackTrace);
    }




    /**
     * 执行表达式并尝试返回结果,如果出现异常则返回errorVal,默认自动报告异常printStackTrace()
     *
     * @param <T>          类型
     * @param supplier     有返回值的表达式
     *      * @return 一个返回值 执行失败则返回null
     */
    public static <T> T tryReturn( TrySupplierReturnFun<T> supplier) {
        return FunctionUtil.tryReturn(supplier, null, null);
    }

    /**
     * 执行表达式并尝试返回结果,如果出现异常则返回errorVal,
     *
     * @param <T>          类型
     * @param printStackTrace 是否报告异常? true 为是printStackTrace()
     * @param supplier     有返回值的表达式
     *      * @return 一个返回值 执行失败则返回null
     */
    public static <T> T tryReturn( TrySupplierReturnFun<T> supplier,boolean printStackTrace) {
        return FunctionUtil.tryReturn(supplier, null, null,printStackTrace);
    }




    // =========杂项=================================================================================================================================

    /**
     * 构造一个value对象,用来包装各种基本对象
     * 
     * @param value
     * @return
     */
    public static Value value(Object value) {
        return new Value(value);
    }

    /**
     * 构造一个value对象,用来包装各种基本对象
     * 
     * @param value
     * @return
     */
    public static Value value() {
        return new Value();
    }

    /**
     * 构建运行时异常
     * 
     * @param message
     * @param caseT
     * @return
     */
    public static RuntimeException runtimeException(String message,  Throwable caseT) {
        return BaseUtil.runtimeException(message, caseT);
    }

    /**
     * 构建运行时异常
     *
     * @param message
     * @return
     */
    public static RuntimeException runtimeException( String message) {
        return BaseUtil.runtimeException(message);
    }

    /**
     * 构建运行时异常
     * 
     * @param caseT
     * @return
     */
    public static RuntimeException runtimeException( Throwable caseT) {
        return BaseUtil.runtimeException(caseT);
    }


    // swing==================================================================================





        /**
         * 创建一个可能为null的对象并包装值
         *
         * @param <T>
         * @param value
         * @return
         */
        public static <T> Opt<T> createMybeNull(T value) {
            return new Opt<T>().of(value);
        }

        /**
         * 创建一个可能为null的对象
         *
         * @param <T>
         * @param value
         * @return
         */
        public static <T> Opt<T> createMybeNull(Class<T> type) {
            return new Opt<T>();
        }

        // ObjectUtil============================================================================================================================
        /**
         * 对象序列化为byte[]
         *
         * @param obj
         * @return
         */
        public static byte[] objToBytes( Serializable obj) {
            return ObjectUtil.objToBytes(obj);
        }

        /**
         * 将对象转换为base64
         *
         * @param obj
         * @return
         */
        public static String objToBase64( Serializable obj) {
            return ObjectUtil.objToBase64(obj);
        }

        /**
         * 将字节数组转换为字符串
         *
         * @param <T>
         *
         * @param obj
         * @return
         */
        public static <T> Opt<T> bytesToObj( byte[] data,  Class<T> c) {
            return ObjectUtil.bytesToObj(data, c);
        }

        /**
         * 将base64转换为对象
         *
         * @param <T>
         *
         * @param obj
         * @return
         */
        public static <T> Opt<T> base64StrToObj( String base64,  Class<T> c) {
            return ObjectUtil.base64StrToObj(base64, c);
        }

        /**
         * 自动进行null检查,如果出现null则抛出NPE
         * 
         * @param objs
         */
        public static void nullCheck(Object... objs) {
            BaseUtil.nullCheck(objs);
        }

}