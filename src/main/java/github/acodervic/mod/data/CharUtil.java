package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullByteArry;
import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.nullString;
import static github.acodervic.mod.data.BaseUtil.parseCharsetStr;
import static github.acodervic.mod.utilFun.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.acodervic.mod.Constant;

/**
 * CharUtil
 */
public class CharUtil {



    /**
     * 是否为有效编码字符串
     * @param charset
     * @return
     */
    public boolean isValidCharSet(String charset) {
        str charSet=new str(charset);
        if (charSet.eqAnyIgnoreCase("UTF-8", "ISO-8859-1", "GBK", "GB2312")) {
            return true;
        }
        return false;
    }
    /**
     * 获取指定字符串出现的次数,如果字符串为Null或者次数为0显示-1
     * 
     * @param srcText  源字符串
     * @param findText_opt 要查找的字符串,null返回false
     * @return 出现次数
     */
    public static int showCount(String srcText, String findText_opt) {
        nullCheck(srcText);
        if (findText_opt == null) {
            return 0;
        }
        int count = 0;
        Pattern p = Pattern.compile(findText_opt);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * 去空格
     * 
     * @param str 源字符串
     * @return 去空格后的字符串
     */
    public static String trim(String str) {
        nullCheck(str);
        return str.trim();
    }

    /**
     * 判断字符串中是否包含字符串
     * 
     * @param str    字符串
     * @param hasStr_opt 包含的字符串,null返回false
     * @return true 或者false
     */
    public static boolean has(String str, String hasStr_opt) {
        nullCheck(str);
        if (hasStr_opt == null) {
            return false;
        }
        if (str.indexOf(hasStr_opt) != -1) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStrs_opt 包含的字符串,null返回false
     * @return true 或者false
     */
    public static boolean has(String str, List<String> hasStrs_opt) {
        nullCheck(str);
        if (hasStrs_opt == null) {
            return false;
        }
        for (String haHstr : hasStrs_opt) {
            if (str.indexOf(haHstr) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStrs_opt 包含的字符串,null返回false
     * @return true 或者false
     */
    public static boolean hasAll(String str, List<String> hasStrs_opt) {
        nullCheck(str);
        if (hasStrs_opt == null) {
            return false;
        }
        for (String haHstr : hasStrs_opt) {
            if (str.indexOf(haHstr) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 截取字符串
     * 
     * @param str        被截取的字符串,如果截取出错返回null
     * @param beginIndex 开始下标
     * @param endIndex   结束下标
     */
    public static String sub(String str, int beginIndex, int endIndex) {
        nullCheck(str);
        if (beginIndex >= endIndex || len(trim(str)) == 0) {
            return str;
        }
        return str.substring(beginIndex, endIndex);
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
    public static String sub(String str, String startStr, int startOffsetOfStartStr,
            int endOffsetOfStartStr) {
        nullCheck(str, startStr);
        int startIndex = str.indexOf(startStr);
        if (startIndex != -1) {
            if (startIndex + startOffsetOfStartStr > str.length()) {
                Constant.dbg("type", startStr + "的存在下标为:" + startOffsetOfStartStr + "  +" + startOffsetOfStartStr
                        + " >被截取的str=" + str + " 的长度。 无法正常截取!");
                return null;
            } else {
                startIndex += startOffsetOfStartStr;
                // 判断
                if (startIndex < 0) {
                    Constant.dbg("最终计算偏移后的的起始下标小于0.自动置0");
                    startIndex = 0;
                }
                if ((startIndex + endOffsetOfStartStr) < 0) {
                    Constant.dbg("最终计算偏移后的的结束下标小于起始下标.返回空字符串");
                    return "";
                }
                if ((startIndex + endOffsetOfStartStr) >= str.length()) {
                    Constant.dbg("type", startStr + "startIndex+startCount  (" + startIndex + "+" + endOffsetOfStartStr
                            + "=" + (startIndex + endOffsetOfStartStr) + ")  的长度。 自动调整endCount的长度到 字符串末尾!");
                    int autoEndCount = (str.length());
                    return str.substring(startIndex, autoEndCount);
                } else {
                    // 进行就截取并返回
                    return str.substring(startIndex, (startIndex + endOffsetOfStartStr));
                }
            }
        } else {
            Constant.dbg("type", "字符串:" + startStr + "   不存在于:" + str);
            return null;
        }
    }

    /**
     * 截取两个字符串中间内容
     * 
     * @param str      被截取的字符串
     * @param startStr 开始字符串
     * @param endStr   结束字符串
     */
    public static String subBetween(String str, String startStr, String endStr) {
        nullCheck(str, startStr, endStr);
        int startIndex = str.indexOf(startStr);
        int endIndex = str.indexOf(endStr, startIndex+1);
        if (startIndex == -1 || endIndex == -1) {
            Constant.dbg("字符串:" + str + "  中, 不存在:" + startStr + "  和  " + endStr + " 返回null");
            return null;
        } else if (endIndex <= startIndex) {
            Constant.dbg("字符串:" + str + "  中, endStr:的位置为" + endIndex + "  大于或等于startIndex:"
                    + (startIndex + startStr.length()) + " 不可截取!返回null");
            return null;
        }
        return str.substring(startIndex + startStr.length(), endIndex);
    }

    /**
     * 返回第一次出现的指定子字符串在此字符串中的索引。 未搜索到或者空字符则返回-1
     * 
     * @param str     被搜索的字符串
     * @param findStr_opt 想要搜索的字符串,,null返回-1
     */
    public static int indexOf(String str, String findStr_opt) {
        nullCheck(str);
        if (findStr_opt == null) {
            return -1;
        }
        return str.indexOf(findStr_opt);
    }

    /**
     * 从指定的索引处开始，返回第一次出现的指定子字符串在此字符串中的索引,未搜索到和搜索空字符串则返回-1
     * 
     * @param str       被搜索的字符串
     * @param findStr_opt   想要搜索的字符串,,null返回-1
     * @param formIndex 开始下标
     */
    public static int indexOfFrom(String str, String findStr_opt, int formIndex) {
        nullCheck(str);
        if (findStr_opt == null) {
            return -1;
        }
        return str.indexOf(findStr_opt, formIndex);
    }

    /**
     * 返回在此字符串中最右边出现的指定子字符串的索引。 ,未搜索到和搜索空字符串则返回-1
     * 
     * @param str     被搜索的字符串
     * @param findStr_opt 想要搜索的字符串,null返回-1
     */
    public static int lastIndexOf(String str, String findStr_opt) {
        nullCheck(str);
        if (findStr_opt == null) {
            return -1;
        }
        return str.lastIndexOf(findStr_opt);
    }

    /**
     * 从指定的索引处开始向后搜索，返回在此字符串中最后一次出现的指定子字符串的索引,未搜索到和搜索空字符串 则返回-1
     * 
     * @param str        被截取的字符串
     * @param findStr_opt 被找的str,,null返回-1
     * @param formIndex   结束下标
     */
    public static int lastIndexOfFrom(String str, String findStr_opt, int formIndex) {
        nullCheck(str);
        if (findStr_opt == null) {
            return -1;
        }
        return str.lastIndexOf(findStr_opt, formIndex);
    }

    /**
     * 返回字符串长度
     * 
     * @param str 源字符串
     * @return 长度
     */
    public static int len(String str) {
        nullCheck(str);
        return str.length();
    }

    /**
     * 返回去空格最左侧的字符串
     * 
     * @param str 源字符 串
     * @return 去空格之后的字符串
     */
    public static String trimLeft(String str) {
        nullCheck(str);
        if (trim(str).equals("")) {
            return nullString();
        } else {
            StringBuilder b=new StringBuilder(str);
            //搜索字符串并清除
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i)==' ') {
                    b.deleteCharAt(0);
                }else{
                break;
                }
            }
            return b.toString();
        }
    }

    /**
     * 返回去空格最右侧的字符串
     * 
     * @param str 源字符串
     * @return 去空格之后的字符串
     */
    public static String trimRight(String str) {
        nullCheck(str);
        if (str.equals("")) {
            return str;
        } else {
            return str.replaceAll("[　 ]+$", "");
        }
    }

    /**
     * 返回去空格中间的字符串,中间的定义是最左和最右之间的
     * 
     * @param str 源字符串
     * @return 结果
     */
    public static String trimMedium(String str) {
        nullCheck(str);
        // 先从左边开始搜索
        int leftEnd = -1;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') {
                leftEnd = i;
                break;
            }
        }
        String leftStr = sub(str, 0, leftEnd);

        // 从右边开始搜索
        int rightStart = -1;
        for (int i = str.length() - 1; i > 1; i--) {
            if (str.charAt(i) != ' ') {
                rightStart = i;
                break;
            }
        }

        String rightStr = sub(str, rightStart + 1, str.length());

        return leftStr + (trim(sub(str, leftEnd, rightStart + 1))) + rightStr;
    }

    /**
     * 替换字符串中的所有字符串为新的字符串,如果无法完成正常进行替换则返回Null
     * 
     * @param str           被替换的字符串
     * @param replaceString 替换前的字符串
     * @param replacement   替换后的字符串
     * @return 结果字符串
     */
    public static String replcaeAll(String str, String replaceString, String replacement) {
        nullCheck(str, replaceString, replacement);
        return str.replaceAll(replaceString, replacement);

    }

    /**
     * 替换字符串右边的字符为新字符,替换次数,当替换失败的时候返回Null
     * 
     * @param str           原始字符串
     * @param replaceString 被替换的字符串
     * @param replacement   替换后的字符串
     * @param maxCount      最大的替换计数
     * @return 结果字符串
     */
    public static String replcaeLeftStr(String str, String replaceString, String replacement, int replaceCount) {
        nullCheck(str, replaceString, replacement);
        if (replaceCount < 1) {
            return str;
        }
        String returnStr = str;
        for (int i = 0; i < replaceCount; i++) {
            if (indexOf(replaceString, replaceString) != -1) {
                returnStr = returnStr.replaceFirst(replaceString, replacement);
            } else {
                return returnStr;
            }
        }
        return returnStr;

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
    public static String replcaeRightStr(String str, String replaceString, String replacement, int replaceCount) {
        nullCheck(str, replaceString, replacement);

        if (replaceCount < 1) {
            return str;
        }
        StringBuilder returnStr = new StringBuilder(str);

        int nowIndex = 0;
        for (int i = 0; i < replaceCount; i++) {
            int replaceStrIndex = lastIndexOf(returnStr.toString(), replaceString);
            // 从后面开始找如果找到
            if (replaceStrIndex != -1) {
                returnStr = returnStr.replace(returnStr.lastIndexOf(replaceString),
                        returnStr.lastIndexOf(replaceString) + replaceString.length(), replacement);

            } else {
                return returnStr.toString();
            }

        }
        return returnStr.toString();

    }

    /**
     * 判断字符串中是否包含中文,如果字符串为null则返回false
     * 
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean hasChinese(String str) {
        nullCheck(str);
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 字符串转小写
     * 
     * @param str 源字符串
     * @return 结果字符串
     */
    public static String toLowerCase(String str) {
        nullCheck(str);
        return str.toLowerCase();
    }

    /**
     * 字符串转大写
     * 
     * @param str 源字符串
     * @return 结果字符串
     */
    public static String toUpperCase(String str) {
        nullCheck(str);
        return str.toUpperCase();
    }

/**
 * 删除字符串的下标字符 
 * @param str 源字符串
 * @param atIndex 下标
 * @return 结果字符串
 */
public static String deleteCharAt(String str, int atIndex) {
    nullCheck(str);
        if (atIndex < -1) {
            return str;
        }
        return new StringBuilder(str).deleteCharAt(atIndex).toString();
    }


    /**
     * 删除字符串的下标字符
     * @param str 源字符串
     * @param startIndex 开始下标
     * @param endIndex 结束下标
     * @return
     */
    public static String deleteStrs(String str, int startIndex, int endIndex) {
        nullCheck(str);
        if (startIndex < -1) {
            return nullString();
        }
        if (startIndex >= endIndex) {
            return str;
        }
        return new StringBuilder(str).delete(startIndex, endIndex).toString();
    }

    /**
     * 删除目标字符串中存在的字符
     * 
     * @param str        源字符串
     * @param deleteStrs_opt 需要被删除的字符串
     * @return 结果字符串
     */
    public static String deleteStrs(String str, List<String> deleteStrs_opt) {
        nullCheck(str);
        if (deleteStrs_opt == null) {
            return str;
        }
        String reult = str;
        for (String deleStr : deleteStrs_opt) {
            reult = replcaeAll(reult, deleStr, "");
        }
        return reult;
    }

    /**
     * 删除目标字符串中存在的字符
     *
     * @param str        源字符串
     * @param deleteStrs 需要被删除的字符串
     * @return 结果字符串
     */
    public static String deleteStrs(String str, String[] deleteStrs) {
        nullCheck(str);
        String reult = str;
        for (String deleStr : deleteStrs) {
            reult = replcaeAll(reult, deleStr, "");
        }
        return reult;
    }

    /**
     * 向字符串的下标处插入字符串
     * 
     * @param str         源字符串
     * @param insertStr_opt   需要插入的字符串
     * @param insertIndex 插入位置
     * @return 结果字符串
     */
    public static String insertStr(String str, String insertStr_opt, int insertIndex) {
        nullCheck(str);
        if (insertStr_opt == null) {
            return str;
        }
        if (insertIndex > (str.length() - 1) || insertIndex < 0) {
            return str;
        }
        return new StringBuffer(str).insert(insertIndex, insertStr_opt==null?nullString():insertStr_opt).toString();
    }

    /**
     * 往字符串末尾追加字符串
     * 
     * @param str        源字符串
     * @param appanedStr_opt 插入的字符串
     * @return 结果字符串
     */
    public static String insertStrToEnd(String str, String appanedStr_opt) {
        nullCheck(str);
        return new StringBuilder(str).append(appanedStr_opt==null?nullString():appanedStr_opt).toString();
    }

    /**
     * 往字符串头部追加字符串
     * 
     * @param str        源字符串
     * @param appanedStr 插入的字符串
     * @return 结果字符串
     */
    public static String insertStrToHead(String str, String headStr_opt) {
        nullCheck(str);
        return headStr_opt + (str==null?nullString():str);
    }

    /**
     * 根据下标获取字符串
     * 
     * @param str   源字符串
     * @param index 下标
     * @return 结果字符串
     */
    public static String charAt(String str, int index) {
        nullCheck(str);
        return str.charAt(index) + "";
    }

    /**
     * 字符串编码转换，若出现错误返回null
     * 
     * @param str            待转码的字符串
     * @param rawCharset     原始编码
     * @param desCharset_opt 目标编码
     * @return 结果字符串
     */
    public static String toCharSet(String str, String rawCharset, String desCharset_opt) {
        nullCheck(str, rawCharset);
        return tryReturn(() -> new String(str.getBytes(rawCharset), parseCharsetStr(desCharset_opt)), nullString());
    }

    /**
     * 字符串编码转换，若出现错误返回null
     * 
     * @param str            待转码的字符串
     * @param rawCharset     原始编码
     * @param desCharset_opt 目标编码
     * @return 结果字符串
     */
    public static String toCharSet(String str, String desCharset_opt) {
        nullCheck(str);
        return tryReturn(() -> new String(str.getBytes(), parseCharsetStr(desCharset_opt)), nullString());
    }

    /**
     * str到字节数组
     * 
     * @param str 源字符串
     * @return 结果字符串
     */
    public static byte[] toBytes(String str) {
        nullCheck(str);
        return str.getBytes();
    }

    /**
     * str到字节数组,以固定编码格式获取
     * 
     * @param str 源字符串
     * @param rawCharSet_opt 字符编码(unll=utf8)
     * @return 结果字符串
     */
    public static byte[] toBytes(String str, String rawCharSet_opt) {
        nullCheck(str);
        return tryReturn(() -> str.getBytes(parseCharsetStr(rawCharSet_opt)), nullByteArry());
    }

    /**
     * 字节数组到字符串,如果charSet为Null则默认使用utf8编码
     * 
     * @param bytes       字节数组
     * @param charSet_opt 目标编码(可选null=utf8)
     * @return 结果字符串
     */
    public static String bytesToStr(byte[] bytes, String charSet_opt) {
        try {
            nullCheck(bytes);
            return new String(bytes, parseCharsetStr(charSet_opt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 数值转字符串
     * @param num
     * @return
     */
    public static String toStr(int num) {
        return num + "";
    }

    /**
     * 数值转字符串
     * @param num
     * @return
     */
    public static String toStr(Long num) {
        return num + "";
    }

    /**
     * 分割字符串
     * 
     * @param str      源字符串
     * @param splitStr 分割字符串
     * @return 结果字符串
     */
    public static String[] splitToArray(String str, String splitStr) {
        nullCheck(str, splitStr);
        return str.split(splitStr);
    }

    /**
     * 生成的字符串每个位置都有可能是str中的一个字母或数字
     * 
     * @param length
     * @return
     */
    public static String getRandomStr(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            stringBuffer.append(str.charAt(number));
        }
        return stringBuffer.toString();
    }

    /**
     * 获取随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        Random random = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = str.charAt(random.nextInt(str.length()));
        }
        return new String(text);
    }

    /**
     * 获取随机字符串(不包含数字)
     *
     * @param length
     * @return
     */
    public static String getRandomStringNoMumberString(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = str.charAt(random.nextInt(str.length()));
        }
        return new String(text);
    }

    /**
     * 把原始字符串分割成指定范围的随着长度字符串列表
     * 
     * @param str    要分割的字符串
     * @param minLen 随机最小长度
     * @param maxLen 随机最大长度
     * @return
     */
    public static List<String> getStrRandomLenList(String str, int minLen, int maxLen) {
        List<String> list_str = new ArrayList<String>();
        int sum = 0;

        while (sum < str.length()) {
            int l = NumberUtil.getRandomNum(minLen, maxLen);
            list_str.add(sub(str, sum, sum + l));
            sum += l;
        }
        return list_str;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @return
     */
    public static List<String> getStrList(String inputString, int length) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size);
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     *
     * @param inputString 原始字符串
     * @param length      指定长度
     * @param size        指定列表大小
     * @return
     */
    public static List<String> getStrList(String inputString, int length, int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = sub(inputString, index * length, (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

}