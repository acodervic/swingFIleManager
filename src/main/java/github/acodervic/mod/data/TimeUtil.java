package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import github.acodervic.mod.function.FunctionUtil;

/**
 * TimeUtil
 */
public class TimeUtil {

    /**
     *构造一个时间
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 时
     * @param min 分
     * @param second 分
     * @param millisecond 秒
     * @return 一个时间
     */
    public static Date cTime(int year, int month, int day, int hour, int min, int second, int millisecond) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_WEEK_IN_MONTH, month);
        cal.set(Calendar.DATE, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();   
     }
 


     /**
      * 构造一个时间
      * @param longTime 时间戳
       * @return 时间
      */
    public static Date cTime(long  longTime) {
        return new Date(longTime);
    }

    

    /**
     * 获取当前时间
     *
     * @return 时间
     */
    public static Date getNow() {
        return new Date();
    }

        /**
     * 获取当前时间戳
     * 
     * @return 时间戳
     */
    public static long getNowLong() {
        return System.currentTimeMillis();
    }

    /**
     * date转long
     * 
     * @param time 时间
     * @return 时间戳
     */
    public static long dateToLong(Date time) {
        nullCheck(time);
        return time.getTime();
    }

    /**
     * long转date
     * 
     * @param date 时间戳
     * @return 时间
     */
    public static Date longToDate(long date) {
        return new Date(date);
    }

    /**
     * 根据格式转换时间到字符串,处理出错返回null
     * 
     * @param date   时间对象
     * @param format_opt 输出格式,传递null时默认为yyyy.MM.dd-HH.mm.ss
     * @return 字符串
     */
    public static String dateToStr(Date date, String format_opt) {
        nullCheck(date);
        SimpleDateFormat df = new SimpleDateFormat(FunctionUtil. get(()-> format_opt).orElse("yyyy年MM月dd日 HH.mm.ss"));
        return df.format(date);
    }

    /**
     * 字符串转换为date对象,处理出错返回null
     * 
     * @param dateStr    被转换的字符串,格式必须和fromFromat表示的格式一致如2019年8月5日 15:35:46
     * @param format_opt  原始格式,如:yyyy年MM月dd日 HH:mm:ss
     * @return 时间
     */
    public static Date strToDate(String dateStr, String format_opt) {
        nullCheck(dateStr);
        SimpleDateFormat Formatjx = new SimpleDateFormat(FunctionUtil.get(()-> format_opt).orElse("yyyy年MM月dd日 HH.mm.ss"));
        try {
            return Formatjx.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取当前时间的日
     * @return 日子
     */
    public static int  getNowDay() {
        Calendar c = Calendar.getInstance();
         int date = c.get(Calendar.DATE);    
        return date;
     }

     /**
      *获取当前时间的月份
      * @return 月份
      */
     public static int  getNowMonth() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);   
        return month;
     }

     /**
      * 获取当前时间的年份
      * @return 年份
      */
     public static int  getNowYear() {
        Calendar c = Calendar.getInstance();
         int year = c.get(Calendar.YEAR);    
        return year;
     }

     /**
      * 获取当前时间的小时
      * @return 小时
      */
     public static int  getNowHour() {
        Calendar c = Calendar.getInstance();
         int hour = c.get(Calendar.HOUR_OF_DAY);    
        return hour;
     }

     /**
      * 获取当前时间的分钟
      * @return 分钟
      */
     public static int  getNowMinute() {
        Calendar c = Calendar.getInstance();
         int minute = c.get(Calendar.MINUTE);    
        return minute;
     }

     /**
      * 获取当前时间的秒
      * @return 秒
      */
     public static int  getNowSecond() {
        Calendar c = Calendar.getInstance();
         int SECOND = c.get(Calendar.SECOND);    
        return SECOND;
     }

     /**
      * 获取当前时间的毫秒
      * @return 毫秒
      */
     public static int  getNowMillisecond() {
        Calendar c = Calendar.getInstance();
         int millisecond = c.get(Calendar.MILLISECOND);    
        return millisecond;
     }
     
          /**
      * 转sqlDate
      * @param date
      * @return
      */
     public static java.sql.Date toSqlDate(long date) {
         return new java.sql.Date(date);
     }
     /**
      * 转sqlDate
      * @param date
      * @return
      */
      public static java.sql.Date toSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    static long secMs = (1000);
    static long minMs = secMs * 60;
    static long hovMs = minMs * 60;
    static long dayMs = 24 * hovMs;
    static long monthMs = 30 * dayMs;
    static long yearMs = 12 * monthMs;
    static long lordMs = 100 * yearMs;

    /**
     * 将一个间隔时间ms转换为字符串形式 如:3600000 => 一个小时
     *
     * @param date
     * @return
     */
    public static String getBetweenPrintTime(long dateValue) {
        String ret = "";
        long lord = 0;
        long year = 0;
        long mon = 0;
        long day = 0;
        long hov = 0;
        long min = 0;
        long sec = 0;
        long ms = 0;
        long date = dateValue;
        // 先判断世纪
        if (date > lordMs) {
            lord = date / lordMs;
            date = date % lordMs;
            ret += (lord + "个世纪 ");
        }
        if (date > yearMs) {
            year = date / yearMs;
            date = date % yearMs;
            ret += (year + "年 ");
        }
        if (date > monthMs) {
            mon = date / monthMs;
            date = date % monthMs;
            ret += (mon + "个月 ");
        }
        if (date > dayMs) {
            day = date / dayMs;
            date = date % dayMs;
            ret += (day + "天 ");
        }
        if (date > hovMs) {
            hov = date / hovMs;
            date = date % hovMs;
            ret += (hov + "个小时 ");
        }
        if (date > minMs) {
            min = date / minMs;
            date = date % minMs;
            ret += (min + "分钟 ");
        }
        if (date > secMs) {
            sec = date / secMs;
            date = date % secMs;
            ret += (sec + "秒 ");
        }
        if (date > 0) {
            ms = date;
            ret += (ms + "毫秒 ");
        }

        return ret;
    }

    /**
     * 活动输出时间的最大单位 如果 1年3个月5天 则输出1年
     * @param dateValue
     * @return
     */
    public static String getBetweenPrintMaxTime(long dateValue) {
        String ret = "";
        long lord = 0;
        long year = 0;
        long mon = 0;
        long day = 0;
        long hov = 0;
        long min = 0;
        long date = dateValue;
        // 先判断世纪
        if (date > lordMs) {
            lord = date / lordMs;
            date = date % lordMs;
            ret += (lord + "个世纪 ");
            return ret;
        }
        if (date > yearMs) {
            year = date / yearMs;
            date = date % yearMs;
            ret += (year + "年 ");
            return ret;
        }
        if (date > monthMs) {
            mon = date / monthMs;
            date = date % monthMs;
            ret += (mon + "个月 ");
            return ret;
        }
        if (date > dayMs) {
            day = date / dayMs;
            date = date % dayMs;
            ret += (day + "天 ");
            return ret;
        }
        if (date > hovMs) {
            hov = date / hovMs;
            date = date % hovMs;
            ret += (hov + "个小时 ");
            return ret;
        }
        if (date > minMs) {
            min = date / minMs;
            date = date % minMs;
            ret += (min + "分钟 ");
            return ret;
        }
        return ret;
    }
}