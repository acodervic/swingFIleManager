package github.acodervic.mod;

import java.nio.charset.Charset;

/**
 * contstand
 */
public class Constant {
    public static String defultCharsetStr = "UTF-8";
    public static Charset defultCharset = Charset.forName(defultCharsetStr);
public static boolean DEBUG=false;
public static Long   httpTimeOut= (long) 30000;
public static String userAgent_IE11="Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko";
public static String nullString="";
public static int  nullInt=1;
public static int  nullInteget=Integer.valueOf(nullInt);

public static void dbg(String type,String mes) {
    if (DEBUG) {
        System.out.println("调试:     "+type+"        信息" + mes);
    }
}
public static void dbg(String mes) {
    if (DEBUG) {
        System.out.println("调试信息:" + mes);
    }
}
}