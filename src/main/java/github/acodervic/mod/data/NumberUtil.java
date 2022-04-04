package github.acodervic.mod.data;

import java.text.NumberFormat;
import java.util.Random;

/**
 * NumberUtil
 */
public class NumberUtil {

    /**
     * 加法计算
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int add(int a, int b) {
        return a + b;
    }

    /**
     * 减法计算
     *
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int les(int a, int b) {
        return a - b;
    }

    /**
     * 乘法
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int mul(int a, int b) {
        return a * b;
    }

    /**
     * 除法
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static double div(double a, double b) {
        return a / b;
    }

    /**
     * 除法
     * 
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int div(int a, int b) {
        return a / b;
    }

    /**
     * 求余数
     * @param a a
     * @param b b
     * @return 结果
     */
    public static int rem(int a, int b) {
        return a % b;
    }

    /**
     * 求余数
     * @param a a
     * @param b b
     * @return 结果
     */
    public static double rem(double a, double b) {
        return a % b;
    }

    /**
     * 判断是否为数值
     * @param data 字符串
     * @return 结果
     */
    public static boolean isNumber(String data) {
        try {
            Integer.parseInt(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回一个随机数
     * 
     * @param MIN 随机数最小值
     * @param MAX 随机数最大值
     * @return 结果
     */
    public static int random(int MIN, int MAX) {
        return new Random().nextInt(MAX - MIN + 1) + MIN;
    }

    /**
     * 转换为integer,如果出错,返回null
     *
     * @param intstr 被转换的字符串
     * @return 结果
     */
    public static Integer toInt(String intstr) {
        try {
            return Integer.parseInt(CharUtil.trim(intstr));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 求百分比a/b
     *
     * @param a                     a
     * @param b                     b
     * @param maximumFractionDigits 保留小数点
     * @return
     */
    public static String percentage(int a, int b, int maximumFractionDigits) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(maximumFractionDigits);
        return numberFormat.format((float) a / (float) b * 100);
    }

    /**
     * 获取min到max范围的随机数
     * 
     * @param min 最小数
     * @param max 最大数
     * @return 在min到max之间的一个随机数
     */
    public static Integer getRandomNum(int min, int max) {
        Random random = new Random();
        int num = random.nextInt(max) % (max - min + 1) + min;
        return num;
    }

    /**
     * 将10进制转换为16进制
     * 
     * @param decimal 10进制
     * @return 16进制
     */
    public static String decimalToHex(int decimal) {
        String hex = Integer.toHexString(decimal);
        return hex.toUpperCase();
    }
}