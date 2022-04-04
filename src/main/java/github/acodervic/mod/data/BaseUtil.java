package github.acodervic.mod.data;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import github.acodervic.mod.Constant;
import github.acodervic.mod.function.FunctionUtil;

/**
 * base,存储一些最基本的数据处理方法
 */
public interface BaseUtil {

    /**
     * 对参数进行null检查,如果存在null则动态抛出遗产
     *
     * @param args
     */
    public static void nullCheck(Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
               RuntimeException exception = new RuntimeException("参数标记为不为null,但是参数为null!,");
               exception.printStackTrace();
               throw exception;
            }
        }
    }

    /**
     * 判断是否为空
     * 
     * @param obj 被判断的对象
     * @return 是否为Null
     */
    public static boolean isNull(Object obj) {
        if (obj == null) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否不为空
     * 
     * @param obj 被判断的对象
     * @return 是否为Null
     */
    public static boolean notNull(Object obj) {
        if (obj == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断两个对象的toString方法是否相等
     * 
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 布尔值
     */
    public static boolean toStringEqua(Object obj1, Object obj2) {
        if (FunctionUtil.get(() -> obj1.toString()).orElse("null").equals(FunctionUtil.get(() -> obj2.toString()).orElse("null"))) {
            return true;
        }
        return false;
    }

    /**
     * 判断俩个对象是否引用同一个对象
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 布尔值
     */
    public static boolean sameObjRef(Object obj1, Object obj2) {
        nullCheck(obj1, obj2);
        if (obj1==obj2) {
            return true;
        }
        return false;
    }



    /**
     * 读取一个对象的引用hash,此hash 在一个对象的生命周期中是不会改变的(即使内部值改变).
     *和对象的hashCode不一样.hashcode是对对象内部的值进行hash,如果内部值改变则hashcode也会改变,此值用于对某一个对象进行标识
     内部由System.identityHashCode 实现
     * @param obj1 对象1
     * @return 字符串
     */
    public static String getObjRefHash(Object obj1) {
        nullCheck(obj1);
       return  System.identityHashCode(obj1)+"";
    }



    /**
     * 判断两个对象的类型是否相等
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 相当返回true 不相等返回false
     */
    public static boolean sameObjType(Object obj1, Object obj2) {
        nullCheck(obj1, obj2);
        if (obj1.getClass().getName().equals(obj2.getClass().getName())) {
            return true;
        }
        return false;
    }

    /**
     * 空字符串
     * 
     * @return
     */
    public static String nullString() {
        return "";
    }

    /**
     * 空字节数组
     * 
     * @return
     */
    public static byte[] nullByteArry() {
        return new byte[0];
    }

    /**
     * 空值
     * 
     * @return
     */
    public static Object nullValue() {
        return null;
    }

    /**
     * 空集合
     * 
     * @return
     */
    public static ArrayList<String> nullStringList() {
        return new ArrayList<String>();
    }

    /**
     * 空集合
     * 
     * @return
     */
    public static List<Integer> nullIntegerList() {
        return new ArrayList<Integer>();
    }

    /**
     * 空集合
     * 
     * @return
     */
    public static List nullList() {
        return new ArrayList<>();
    }

    /**
     * 空map
     * 
     * @return
     */
    public static Map nullMap() {
        return new HashMap<>();
    }

    // ===============================================================================通过接口来使用函数

    /**
     * 判断两个数据的toString方法是否相等
     * 
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 布尔
     */
    default boolean _toStringEqua(Object obj1, Object obj2) {
        if (FunctionUtil.get(() -> obj1.toString()).orElse("null").equals(FunctionUtil. get(() -> obj2.toString()).orElse("null"))) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前对象对象和另一个对象是否引用同一个对象
     * 
     * @param obj2 目标对象
     * @return 布尔
     */
    default boolean _sameObjRef(Object obj2) {
        nullCheck(obj2);
        if (this.hashCode() == obj2.hashCode()) {
            return true;
        }
        return false;
    }

    /**
     * 判断两个对象的类型是否相等
     * 
     * @return 相当返回true 不相等返回false
     */
    default boolean sameObjType(Object obj2) {
        nullCheck(obj2);
        if (this.getClass().getName().equals(obj2.getClass().getName())) {
            return true;
        }
        return false;
    }

    /**
     * 转换字符串为charset字符串
     * 
     * @param charset 字符串
     * @return 字符串
     */
    public static String parseCharsetStr(Optional<String> charset) {
        nullCheck(charset);
        return charset.orElse(Constant.defultCharsetStr);
    }

    /**
     * 转换字符串为charset字符串
     * 
     * @param charset 字符串(可选)null==utf8
     * @return 字符串
     */
    public static String parseCharsetStr(String charset_opt) {
        return optOfNullable(charset_opt).orElse(Constant.defultCharsetStr);
    }
    
    /**
     * 转换字符串为charset字符串
     * 
     * @param charset 字符串(可选)null==utf8
     * @return 字符串
     */
    public static Charset parseCharset(String charset_opt) {
        return Charset.forName(charset_opt == null ? Constant.defultCharsetStr : charset_opt);
    }
    // 调试相关==============================================================================================================================================

    /**
     * 多个对象联合输出
     * 
     * @param objs 多个对象
     * @return
     */
    public static String objToString(Object... objs) {
        if (objs == null) {
            return "";
        }
        String result = "";
        for (Object obj : objs) {
            result += (obj + ",");
        }
        return result;
    }
    // 空值处理=================================================

    /**
     * 构建一个不能为null值的Optional
     * 
     * @param <T>   类型
     * @param value 一个不为null的对象,若为null直接抛出null异常
     * @return Optional
     */
    public static <T> Optional<T> optOf(T value) {
        nullCheck(value);
        return Optional.of(value);

    }

    /**
     * 构建一个可以为null值的Optional
     * 
     * @param <T>   类型
     * @param value 一个可以为null的对象
     * @return Optional
     */
    public static <T> Optional<T> optOfNullable(T value) {
        return Optional.ofNullable(value);

    }


    /**
     * 构建运行时异常
     * @param message
     * @param caseT
     * @return
     */
    public static RuntimeException runtimeException(String message, Throwable caseT) {
        nullCheck(caseT);
        return new RuntimeException(message, caseT);
    }


/**
 * 构建运行时异常
 * @param message
 * @return
 */
public static RuntimeException runtimeException(String message) {
    nullCheck(message);
        return new RuntimeException(message);
    }
    /**
     * 构建运行时异常
     * @param caseT
     * @return
     */
    public static RuntimeException runtimeException(Throwable caseT) {
        nullCheck(caseT);
        return new RuntimeException(caseT);
    }





    /**
     * 将以字节单位的数值转换为输出单位
     *
     * @param size
     * @return
     */
    public static String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            // 因为如果以MB为单位的话，要保留最后1位小数，
            // 因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }

}
