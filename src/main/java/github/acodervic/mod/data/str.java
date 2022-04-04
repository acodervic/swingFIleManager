package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.newList;
import static github.acodervic.mod.utilFun.notNull;
import static github.acodervic.mod.utilFun.str;
import static github.acodervic.mod.utilFun.tryReturn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import github.acodervic.mod.Constant;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.code.Decode;
import github.acodervic.mod.code.Encode;
import github.acodervic.mod.crypt.Digest;
import github.acodervic.mod.data.list.AList;
import github.acodervic.mod.data.list.LList;
import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.data.mode.JsonData;
import github.acodervic.mod.data.mode.MatchAndPosition;
import github.acodervic.mod.data.mode.XmlNode;
import github.acodervic.mod.function.TrySupplierReturnFun;
import github.acodervic.mod.net.HttpUtil;

/**
 * str
 */
public class str {
    StringBuilder stringBuffer = new StringBuilder("");
    String charSet = Constant.defultCharsetStr;// 当前字符串的编码格式
    boolean dontPrintStackTrace = false;
    HirshbergMatcher hirshbergMatcher = new HirshbergMatcher();// 计算字符串相似度
    /**
     * @param string_opt 如果传入null,则内部字符串为""空字符串
     */
    public str(String string_opt) {
        if (string_opt == null) {
            this.stringBuffer = new StringBuilder("");
        } else {
            this.stringBuffer = new StringBuilder(string_opt);
        }
    }

    /**
     * @param string_opt 如果传入null,则内部字符串为""空字符串
     */
    public str(Object string_opt) {
        if (string_opt == null) {
            this.stringBuffer = new StringBuilder("");
        } else {
            this.stringBuffer = new StringBuilder(string_opt.toString());
        }
    }

    /**
     * 如果是空字符串设设置新字符串
     * 
     * @param data
     */
    public str ifEmptySetString(String data) {
        if (isEmpty()) {
            this.setString(data);
        }
        return this;
    }

    public str(char c) {
        this.stringBuffer = new StringBuilder(c);
    }

    /**
     * 等同isEmpty()判断是否其内部为空字符串""
     *
     * @return
     */
    public Boolean isNull() {
        return isEmpty();
    }

    /**
     * 等同isEmpty()判断是否其内部不为空字符串""
     *
     * @return
     */
    public Boolean isNotNull() {
        return !isEmpty();
    }

    /**
     * 判断是否其内部为空字符串""
     *
     * @return
     */
    public boolean isEmpty() {
        if (this.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 当字符串为 空字符串的时候对字符串进行操作,当操作完毕后返回this
     *
     * @param task
     * @return
     */
    public str onEmptyTryDo(Consumer<str> task) {
        nullCheck(task);
        if (isEmpty()) {
            task.accept(this);
        }
        return this;
    }

        /**
     * 当字符串为 空字符串的时候对字符串进行操作,当操作完毕后返回this
     *
     * @param task
     * @return
     */
        public str onNotEmptyTryDo(Consumer<str> task) {
            nullCheck(task);
            if (!isEmpty()) {
            task.accept(this);
        }
        return this;
    }

    /**
     * 是否以某字符串开头
     *
     * @param string
     * @return
     */
    public boolean startWith(String string) {
        return toString().startsWith(string);
    }

    /**
     * 是否以某字符串开头
     *
     * @param string
     * @return
     */
    public boolean startWith(String... strings) {
        String s= toString();
        for (int i = 0; i < strings.length; i++) {
            if (s.startsWith(strings[i])) {
                return true;
            }
        }
        return false;
    }



    /**
     * 是否以某字符串开头,忽略大小写
     *
     * @param string
     * @return
     */
    public boolean startWithNoCase(String... strings) {
        String s= toString().toLowerCase();
        for (int i = 0; i < strings.length; i++) {
            if (s.startsWith(strings[i].toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否以某字符串结尾
     * 
     * @param string
     * @return
     */
    public boolean endWithNoCase(String string) {
        if (string == null || string.equals("")) {
            return false;
        }
        return toString().toLowerCase().endsWith(string.toLowerCase());
    }

    /**
     * 是否以某字符串结尾(忽略大小写),只要有一个匹配则返回true
     * 
     * @param string
     * @return
     */
    public boolean endWithAnyNoCase(String... string) {
        if (string == null || string.equals("")) {
            return false;
        }
        String lowerCaseThis = toString().toLowerCase();
        for (int i = 0; i < string.length; i++) {
            String s = string[i];
            if (s != null && lowerCaseThis.equals(s.toLowerCase())) {
                return true;
            }

        }
        return false;
    }

    /**
     * 是否以某字符串结尾(忽略大小写),只要有一个匹配则返回true
     * 
     * @param string
     * @return
     */
    public boolean endWithAllNoCase(String... string) {
        if (string == null || string.equals("")) {
            return false;
        }
        String lowerCaseThis = toString().toLowerCase();
        Boolean pass = true;
        for (int i = 0; i < string.length; i++) {
            String s = string[i];
            if (!(s != null && lowerCaseThis.equals(s.toLowerCase()))) {
                pass = false;
                break;
            }

        }
        return pass;
    }

    /**
     * 是否以某字符串结尾
     * 
     * @param string
     * @return
     */
    public boolean endWith(String string) {
        if (string == null || string.equals("")) {
            return false;
        }
        return toString().endsWith(string);
    }

    /**
     * 是否以某个字符串结尾
     * 
     * @param string
     * @return
     */
    public boolean endWithAny(String... string) {
        for (int i = 0; i < string.length; i++) {
            boolean endsWith = toString().endsWith(string[i]);
            if (endsWith) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否以某个字符串结尾
     * 
     * @param string
     * @return
     */
    public boolean endWithAny(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            String end = strings.get(i);
            boolean endsWith = endWith(end);
            if (endsWith) {
                return true;
            }
        }
        return false;
    }

    public <T> T onEmptyTryReturn(TrySupplierReturnFun<T> supplier) {
        nullCheck(supplier);
        return tryReturn(supplier);
    }

    /**
     * 当为 "" 的时候返回值
     * 
     * @param <T>
     * @param obj
     * @return
     */
    public <T> T onEmptyReturn(T obj) {
        return obj;
    }

    /**
     * 判断是否其内部不为空字符串""
     * 
     * @return
     */
    public boolean notEmpty() {
        return !isEmpty();
    }

    /**
     * @param string
     */
    public str(String[] strings) {
        for (String s : strings) {
            if (notNull(s)) {
                this.stringBuffer.append(s);
            }
        }
    }

    /**
     * 根据字节构造字符串
     *
     * @param bytes
     * @param charSet_opt
     */
    public str(byte[] bytes, String charSet_opt) {
        nullCheck(bytes);
        this.stringBuffer = new StringBuilder(CharUtil.bytesToStr(bytes, charSet_opt));
        charSet = charSet_opt;
    }

    /**
     * 根据字节构造字符串
     *
     * @param bytes
     */
    public str(byte[] bytes) {
        nullCheck(bytes);
        this.stringBuffer = new StringBuilder(CharUtil.bytesToStr(bytes, charSet));
    }

    /**
     * 从文件构造str
     * 
     * @param file
     */
    public str(FileRes file) {
        nullCheck(file);
        this.stringBuffer = new StringBuilder(CharUtil.bytesToStr(file.readBytes(), charSet));
    }

    /**
     * 使用字符串列表来构造字符串,自动拼接
     * 
     * @param string
     */
    public str(List<String> strings) {
        nullCheck(strings);
        for (String s : strings) {
            if (notNull(s)) {
                this.stringBuffer.append(s);
            }
        }
    }

    /**
     * 使用字符串列表来构造字符串,自动拼接
     *
     * @param strings
     * @param splitString 拼接 符
     */
    public str(List<String> strings, String splitString) {
        nullCheck(strings, splitString);
        boolean start = true;
        for (String s : strings) {
            if (notNull(s)) {
                this.stringBuffer.append(s);
                if (start) {
                    this.stringBuffer.append(splitString);
                    start = false;
                }
            }
        }
    }

    /**
     * 使用占位符来构建字符串.%s 代表一个占位符号,按顺序填充
     *
     * @param string
     * @param 占位数据
     */
    public str(String string, String... 占位数据) {
        nullCheck(string);
        this.stringBuffer = new StringBuilder(String.format(string, 占位数据));
    }

    @Override
    public String toString() {
        return stringBuffer.toString();
    }

    /**
     * 转换为String字符串
     *
     * @return
     */
    public String to_s() {
        return this.toString();
    }

    /**
     * 转换为Float
     *
     * @return
     */
    public Float to_F() {
        return Float.parseFloat(this.toString().trim());
    }

    /**
     * 将字符串转换为integer,如果出错则返回null
     *
     * @return
     */
    public Integer to_I() {
        try {
            return Integer.parseInt(toString().trim());
        } catch (Exception e) {
            if (needPrintStackTrace()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 删除所有换行
     *
     * @return
     */
    public str trimAllWrap() {
        return str(toString().replaceAll("\n", ""));
    }

    /**
     * 将字符串转换为Boolean 如果出现错误则返回null
     * 
     * @return
     */
    public Boolean to_B() {
        try {
            return Boolean.parseBoolean(toString().trim());
        } catch (Exception e) {
            if (needPrintStackTrace()) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取指定字符串出现的次数,如果字符串为Null或者次数为0显示-1
     * 
     * @param srcText  源字符串
     * @param findText 要查找的字符串
     * @return 出现次数
     */
    public int showCount(String findText) {
        if (findText == null) {
            return -1;
        }
        return CharUtil.showCount(toString(), findText);
    }
    /**
     * 去空格
     *
     * @return 去空格后的字符串
     */
    public str trim() {
        return new str(CharUtil.trim(toString()));
    }

    /**
     * 判断字符串中是否包含字符串
     *
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean has(String hasStr) {
        if (hasStr == null) {
            return false;
        }
        return CharUtil.has(toString(), hasStr);
    }

    /**
     * 判断字符串中是否包含字符串
     *
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean has(str hasStr) {
        if (hasStr == null) {
            return false;
        }
        return CharUtil.has(toString(), hasStr.to_s());
    }
    /**
     * 判断是否包含正则表达式匹配的正则表达式
     *
     * @param regexs
     * @return
     */
    public boolean hasRegex(String... regexs) {
        if (regexs == null) {
            return false;
        }
        for (int i = 0; i < regexs.length; i++) {
            RegexUtil createRegex = RegexUtil.createRegex(regexs[i]);
            if (notNull(createRegex) && createRegex.has(toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含正则表达式匹配的正则表达式
     *
     * @param regexs
     * @return
     */
    public boolean hasRegex(List<RegexUtil> regexs) {
        if (regexs == null) {
            return false;
        }
        for (int i = 0; i < regexs.size(); i++) {
            RegexUtil regexUtil = regexs.get(i);
            if (notNull(regexUtil) && regexUtil.has(toString())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断字符串中是否包含字符串忽略大小写
     *
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasNoCase(String... hasStr) {
        if (hasStr == null) {
            return false;
        }
        String matchStr = toString().toUpperCase();
        for (String string : hasStr) {
            if (string != null) {
                if (CharUtil.has(matchStr, string.toUpperCase())) {
                    return true;
                }
                ;
            }
        }
        return false;
    }

    /**
     * 判断字符串中是否包含字符串忽略大小写
     *
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasNoCaseStringList(List<String> hasStr) {
        if (hasStr == null) {
            return false;
        }
        return hasNoCase(ListUtil.toStringArry(hasStr));
    }

    /**
     * 判断字符串中是否包含字符串忽略大小写
     *
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasNoCaseStrList(List<str> hasStr) {
        if (hasStr == null) {
            return false;
        }

        List<String> cases = new ArrayList<>();
        for (int i = 0; i < hasStr.size(); i++) {
            cases.add(hasStr.get(i).to_s());
        }
        return hasNoCaseStringList(cases);
    }

    /**
     * 判断字符串中必须包含的字符串忽略大小写
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasNoCaseAll(String... hasStr) {
        if (hasStr == null) {
            return false;
        }
        int strNum = 0;
        int rightNum = 0;
        String matchString = toString().toUpperCase();
        for (String string : hasStr) {
            if (string != null) {
                strNum = strNum + 1;
                if (CharUtil.has(matchString, string.toUpperCase())) {
                    rightNum = rightNum + 1;
                }
                ;
            }
        }
        if (strNum != 0 && rightNum != 7 && (strNum == rightNum)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串中必须包含的字符串忽略大小写
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasNoCaseAllStrs(List<str> hasStr) {
        if (hasStr == null) {
            return false;
        }
        List<String> cases = new ArrayList<>();
        for (int i = 0; i < hasStr.size(); i++) {
            cases.add(hasStr.get(i).to_s());
        }
        return hasNoCaseAllStringList(cases);
    }

    /**
     * 判断字符串中必须包含的字符串忽略大小写
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasNoCaseAllStringList(List<String> hasStr) {
        if (hasStr == null) {
            return false;
        }
        return hasNoCaseAll(ListUtil.toStringArry(hasStr));
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean has(List<String> hasStrs) {
        if (hasStrs == null) {
            return false;
        }
        return CharUtil.has(toString(), hasStrs);
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean has(String... hasStrs) {
        if (hasStrs == null) {
            return false;
        }
        for (String string : hasStrs) {
            if (string != null) {
                if (CharUtil.has(toString(), string)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasAll(List<String> hasStrs) {
        if (hasStrs == null) {
            return false;
        }
        return CharUtil.hasAll(toString(), hasStrs);
    }

    /**
     * 判断字符串中是否包含字符串列表,如果包含(至少一个)则返回true,否则false
     * 
     * @param str    字符串
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean hasAll(String... hasStrs) {
        if (hasStrs == null) {
            return false;
        }
        int strNum = 0;
        int rightNum = 0;
        for (String string : hasStrs) {
            if (string != null) {
                strNum = strNum + 1;
                if (CharUtil.has(toString(), string)) {
                    rightNum = rightNum + 1;
                }
            }
        }
        if (strNum != 0 & rightNum != 0 && (rightNum == strNum)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 截取字符串,返回新的str
     * 
     * @param str        被截取的字符串,如果截取出错返回null
     * @param beginIndex 开始下标
     * @param endIndex   结束下标
     */
    public str sub(int beginIndex, int endIndex) {
        return new str(this.stringBuffer.substring(beginIndex, endIndex));
    }

    /**
     * 截取字符串到最后
     *
     * @param startChar       开始的字符
     * @param IncloudStarChar 是否包含开始字符
     */
    public str subToEnd(String startChar, boolean IncloudStarChar) {
        nullCheck(startChar);
        int startCharindexOf = this.toString().indexOf(startChar);
        if (startCharindexOf == -1 || startCharindexOf == (this.toString().length() - 1)) {
            return new str("");
        }
        if (IncloudStarChar) {
            return sub(startCharindexOf, toString().length());

        } else {
            return sub(startCharindexOf + startChar.length(), toString().length());

        }

    }

    /**
     * 截取字符串到最后
     *
     * @param startIndex      开始的字符下标
     * @param IncloudStarChar 是否包含开始字符
     */
    public str subToEnd(int startIndex, boolean IncloudStarChar) {
        int startCharindexOf = startIndex;
        if (startCharindexOf == -1 || startCharindexOf == (this.toString().length() - 1)) {
            return new str("");
        }
        if (IncloudStarChar) {
            return sub(startCharindexOf, toString().length());

        } else {
            return sub(startCharindexOf + 1, toString().length());
        }
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
    public str sub(String startStr, int startOffsetOfStartStr, int endOffsetOfStartStr) {
        nullCheck(startStr);
        return new str(CharUtil.sub(toString(), startStr, startOffsetOfStartStr, endOffsetOfStartStr));
    }

    /**
     * 截取字符串,直到endchar
     * 
     * @param startINdex
     * @param endchar
     * @return
     */
    public str sub(int startINdex, String endchar) {
        nullCheck(endchar);
        int endcharIndex = toString().substring(startINdex).indexOf(endchar);
        if (endcharIndex == -1) {
            return this;
        }
        return new str(toString().substring(startINdex, endcharIndex));
    }

    /**
     * 读取字符串长度
     * 
     * @return
     */
    public int length() {
        return this.stringBuffer.length();
    }

    /**
     * 截取两个字符串中间内容
     *
     * @param str      被截取的字符串
     * @param startStr 开始字符串
     * @param endStr   结束字符串
     */
    public str subBetween(String startStr, String endStr) {
        nullCheck(startStr, endStr);
        return new str(CharUtil.subBetween(toString(), startStr, endStr));
    }

    /**
     * 返回第一次出现的指定子字符串在此字符串中的索引。 未搜索到或者空字符则返回-1
     * 
     * @param str     被搜索的字符串
     * @param findStr 想要搜索的字符串
     */
    public int indexOf(String findStr) {
        if (findStr == null) {
            return -1;
        }
        return CharUtil.indexOf(toString(), findStr);
    }

    /**
     * 在字符串中找到第多少个字符串的下标 ,如果不存在则返回-1
     *
     * @param findStr
     * @param count
     * @return
     */
    public int indexOfCount(String findStr, int count) {
        nullCheck(findStr);

        int searchFormIndex = 0;
        int nowcount = 0;
        while (true) {
            Integer index = indexOfFrom(findStr, searchFormIndex);
            if (index != -1) {
                nowcount += 1;
                searchFormIndex = index + 1;
                if (nowcount == count) {
                    return index;
                }
            } else {
                break;
            }
        }
        return -1;
    }

    /**
     * 在字符串中找到第多少个字符串的下标,如果不存在则返回-1
     *
     * @param findStr
     * @param count
     * @return
     */
    public int indexOfCountIgnoreCase(String findStr, int count) {
        nullCheck(findStr);
        int searchFormIndex = 0;
        int nowcount = 0;
        while (true) {
            String string = toString().toLowerCase();
            String searchStringLowerCase = findStr.toLowerCase();
            Integer index = string.indexOf(searchStringLowerCase, searchFormIndex);
            if (index != -1) {
                nowcount += 1;
                searchFormIndex = index + 1;
                if (nowcount == count) {
                    return index;
                }
            } else {
                break;
            }
        }
        return -1;
    }


    /**
     * 从指定的索引处开始，返回第一次出现的指定子字符串在此字符串中的索引,未搜索到和搜索空字符串则返回-1
     * 
     * @param str       被搜索的字符串
     * @param findStr   想要搜索的字符串
     * @param formIndex 开始下标
     */
    public int indexOfFrom(String findStr, int formIndex) {
        if (findStr == null) {
            return -1;
        }
        return CharUtil.indexOfFrom(toString(), findStr, formIndex);
    }

    /**
     * 返回在此字符串中最右边出现的指定子字符串的索引。 ,未搜索到和搜索空字符串则返回-1
     * 
     * @param str     被搜索的字符串
     * @param findStr 想要搜索的字符串
     */
    public int lastIndexOf(String findStr) {
        if (findStr == null) {
            return -1;
        }
        return CharUtil.lastIndexOf(toString(), findStr);
    }

    /**
     * 从指定的索引处开始向后搜索，返回在此字符串中最后一次出现的指定子字符串的索引,未搜索到和搜索空字符串 则返回-1
     *
     * @param str        被截取的字符串
     * @param beginIndex 开始下标
     * @param endIndex   结束下标
     */
    public int lastIndexOfFrom(String findStr, int formIndex) {
        if (findStr == null) {
            return -1;
        }
        return CharUtil.lastIndexOfFrom(toString(), findStr, formIndex);
    }

    /**
     * 返回字符串长度
     * 
     * @param str 源字符串
     * @return 长度
     */
    public int len(String str) {
        return CharUtil.len(toString());
    }

    /**
     * 返回去空格最左侧的字符串
     * 
     * @param str 源字符 串
     * @return 去空格之后的字符串
     */
    public str trimLeft() {
        return new str(CharUtil.trimLeft(toString()));
    }

    /**
     * 返回去空格最右侧的字符串
     * 
     * @param str 源字符串
     * @return 去空格之后的字符串
     */
    public str trimRight() {
        return new str(CharUtil.trimRight(toString()));
    }

    /**
     * 返回去空格中间的字符串,中间的定义是最左和最右之间的
     * 
     * @param str 源字符串
     * @return 结果
     */
    public str trimMedium() {
        return new str(CharUtil.trimMedium(toString()));
    }

    /**
     * 替换字符串中的所有字符串为新的字符串,如果无法完成正常进行替换则返回Null
     * 
     * @param str           被替换的字符串
     * @param replaceString 替换前的字符串
     * @param replacement   替换后的字符串
     * @return 结果字符串
     */
    public str replaceAll(String replaceString, String replacement) {
        return new str(CharUtil.replcaeAll(toString(), replaceString, replacement));
    }

    /**
     * 替换某个范围内的字符串,包含startIndex位置的字符串和endIndex的字符串
     * 
     * @param startIndex
     * @param endIndex
     * @param newStr
     * @return
     */
    public str replace(int startIndex, int endIndex, String newStr) {
        if (endIndex == -1 || startIndex == -1) {
            new Exception(" startIndex , endIndex 都不可等于-1! 无法截取!").printStackTrace();
            return new str("");
        }
        if (endIndex < startIndex) {
            new Exception(" startIndex >  endIndex! 无法截取!").printStackTrace();

            return new str("");
        }
        String string = toString();

        if (string.length() < (endIndex + 1)) {
            new Exception(" endIndex 大于目标字符串最大长度!").printStackTrace();
            return new str("");
        }
        String left = string.substring(0, startIndex);
        String right = string.substring(endIndex + 1, string.length() - 1);
        return new str(left + newStr + right);
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
    public str replcaeLeftStr(String replaceString, String replacement, int replaceCount) {
        return new str(CharUtil.replcaeLeftStr(toString(), replaceString, replacement, replaceCount));
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
    public str replcaeRightStr(String replaceString, String replacement, int replaceCount) {
        return new str(CharUtil.replcaeRightStr(toString(), replaceString, replacement, replaceCount));
    }

    /**
     * 判断字符串中是否包含中文,如果字符串为null则返回false
     * 
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public boolean hasChinese() {
        return CharUtil.hasChinese(toString());
    }

    /**
     * 字符串转小写
     *
     * @param str 源字符串
     * @return 结果字符串
     */
    public str toLowerCase() {
        return new str(CharUtil.toLowerCase(toString()));
    }

    /**
     * 字符串转大写
     * 
     * @param str 源字符串
     * @return 结果字符串
     */
    public str toUpperCase() {
        return new str(CharUtil.toUpperCase(toString()));
    }

    /**
     * 删除字符串的下标字符
     * 
     * @param str     源字符串
     * @param atIndex 下标
     * @return 结果字符串
     */
    public str deleteCharAt(int atIndex) {
        return new str(CharUtil.deleteCharAt(toString(), atIndex));
    }

    /**
     * 删除字符串的下标字符
     * 
     * @param str        源字符串
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return
     */
    public str deleteStrs(String... strings) {
        return new str(CharUtil.deleteStrs(toString(), strings));
    }

    /**
     * 删除字符串的下标字符
     * 
     * @param str        源字符串
     * @param startIndex 开始下标
     * @param endIndex   结束下标
     * @return
     */
    public str deleteStrs(int startIndex, int endIndex) {
        return new str(CharUtil.deleteStrs(toString(), startIndex, endIndex));
    }

    /**
     * 删除目标字符串中存在的字符
     * 
     * @param str        源字符串
     * @param deleteStrs 需要被删除的字符串
     * @return 结果字符串
     */
    public str deleteStrs(List<String> deleteStrs) {
        nullCheck(deleteStrs);
        return new str(CharUtil.deleteStrs(toString(), deleteStrs));
    }

    /**
     * 向字符串的下标处插入字符串,当出现异常时候返回null
     * 
     * @param str         源字符串
     * @param insertStr   需要插入的字符串
     * @param insertIndex 插入位置
     * @return 结果字符串
     */
    public str insertStr(String insertStr, int insertIndex) {
        nullCheck(insertStr, insertIndex);
        return new str(CharUtil.insertStr(toString(), insertStr, insertIndex));
    }

    /**
     * 往字符串末尾追加字符串
     * 
     * @param str        源字符串
     * @param appanedStr 插入的字符串
     * @return 结果字符串
     */
    public str insertStrToEnd(String appanedStr) {
        nullCheck(appanedStr);
        this.stringBuffer.append(appanedStr);
        return this;
    }

    /**
     * 往字符串头部追加字符串
     *
     * @param str        源字符串
     * @param appanedStr 插入的字符串
     * @return 结果字符串
     */
    public str insertStrToHead(String headStr) {
        nullCheck(headStr);
        this.stringBuffer.insert(0, headStr);
        return this;
    }

    /**
     * 根据下标获取字符串
     * 
     * @param str   源字符串
     * @param index 下标
     * @return 结果字符串
     */
    public str charAt(int index) {
        return new str(this.stringBuffer.charAt(index) + "");
    }

    /**
     * 字符串编码转换，若出现错误返回""
     *
     * @param str        待转码的字符串
     * @param desCharset 目标编码
     * @return 结果字符串
     */
    public str toCharSet(String desCharset) {
        nullCheck(desCharset);
        return new str(CharUtil.toCharSet(toString(), charSet, desCharset));
    }

    /**
     * 更改当前字符串流的编码,如果已经知道
     * 
     * @return
     */
    public str charSet(String charset) {
        nullCheck(charset);
        this.charSet = charset;
        return this;
    }

    /**
     * 读取到现在的字符编码
     * 
     * @return
     */
    public String nowCharSet() {
        return this.charSet;
    }

    /**
     * 清空str
     * 
     * @return
     */
    public str clear() {
        this.stringBuffer.delete(0, this.stringBuffer.length());
        return this;
    }

    /**
     * 重新设置字符串
     *
     * @param string
     * @return
     */
    public str setString(String string) {
        clear();
        if (notNull(string)) {
            this.stringBuffer.append(string);
        }
        return this;
    }

    /**
     * 解码Unicode字符串
     * 
     * @return
     */
    public str decodeUnicode() {
        return new str(UnicodeUtil.toString(toString()));
    }

    /**
     * 将字符串编码为Unicode字符串,自动跳过Ascii编码可以表示的字符串如字母,数字
     * 
     * @return
     */
    public str encodeUnicode() {
        return encodeUnicode(true);
    }

    /**
     * 将字符串编码为Unicode字符串
     * 
     * @param isSkipAscii 是否跳过Ascii字符串
     * @return
     */
    public str encodeUnicode(boolean isSkipAscii) {
        return new str(UnicodeUtil.toUnicode(toString(), isSkipAscii));
    }
    /**
     * str到字节数组
     *
     * @param str 源字符串
     * @return 结果字符串
     */
    public byte[] toBytes() {
        return CharUtil.toBytes(toString());
    }

    /**
     * str到字节数组,以固定编码格式获取
     * 
     * @param str            源字符串
     * @param rawCharSet_opt 字符编码(unll=utf8)
     * @return 结果字符串
     */
    public byte[] toBytes(String rawCharSet_opt) {
        return CharUtil.toBytes(toString(), rawCharSet_opt);
    }

    /**
     * 分割字符串
     *
     * @param str      源字符串
     * @param splitStr 分割字符串
     * @return 结果字符串
     */
    public AList<str> split(String splitStr) {
        nullCheck(splitStr);
        String[] splitArray = CharUtil.splitToArray(toString(), splitStr);
        AList<str> list = new AList<str>();
        for (String str : splitArray) {
            list.add(new str(str));
        }
        return list;
    }

    /**
     * 按换行符分割字符串
     *
     * @param str      源字符串
     * @param splitStr 分割字符串
     * @return 结果字符串
     */
    public AList<str> splitLines() {
        AList<str> list = new AList<str>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(toBytes())));
        String str = null;
        try {
            while ((str = br.readLine()) != null) {
                list.add(new str(str));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 比较字符串,如果一个匹配则返回true
     *
     * @param string_opt 若为null返回flase
     * @return
     */
    public boolean eqAny(String... string_opt) {
        String s = toString();
        if (notNull(string_opt)) {
            for (int i = 0; i < string_opt.length; i++) {
                if (new str(string_opt[i]).eq(new str(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 比较字符串,如果全部相同则返回true
     *
     * @param string_opt 若为null返回flase
     * @return
     */
    public boolean eqAll(String... string_opt) {
        String s = toString();
        if (notNull(string_opt)) {
            for (int i = 0; i < string_opt.length; i++) {
                if (!new str(string_opt[i]).eq(new str(s))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 比较字符串等于equals
     *
     * @param str_opt 若为返回flase
     * @return
     */
    public boolean eq(str str_opt) {
        return this.toString().equals(str_opt.toString());
    }

    /**
     * 比较字符串 等于equals
     *
     * @param str_opt 若为回flase
     * @return
     */
    public boolean eq(String str_opt) {
        return this.toString().equals(str_opt);
    }

    /**
     * 比较对象toString字符串 等于equals
     *
     * @param obj_opt 若为回flase
     * @return
     */
    public boolean eq(Object obj_opt) {
        if (obj_opt == null) {
            return false;
        }
        return this.toString().equals(obj_opt.toString());
    }

    /**
     * 判断字符串是匹配某个字符串包含字符串,忽略大小写,如果一个相同则返回true
     *
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean eqAnyIgnoreCase(String... hasStr) {
        if (hasStr == null) {
            return false;
        }
        String matchStr = toString().toUpperCase();

        for (String string : hasStr) {
            if (string != null) {
                if (matchStr.equals(string.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断字符串是匹配某个字符串包含字符串,忽略大小写,如果全部相同则返回true,否则返回false
     *
     * @param hasStr 包含的字符串
     * @return true 或者false
     */
    public boolean eqAllIgnoreCase(String... hasStr) {
        if (hasStr == null) {
            return false;
        }
        String matchStr = toString().toUpperCase();

        for (String string : hasStr) {
            if (string != null) {
                if (!matchStr.equals(string.toUpperCase())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断当前str内部存储的内容 是否为有效编码字符串
     * 
     * @param charset
     * @return
     */
    public boolean isValidCharSet() {
        if (this.eqAnyIgnoreCase("UTF-8", "ISO-8859-1", "GBK", "GB2312")) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为某种编码
     *
     * @param charset
     * @return
     */
    public boolean isCharset(String charset) {
        nullCheck(charset);
        String string = toString();
        return tryReturn(() -> {
            return new String(string.getBytes(charset), charset).length() == string.length();
        }, false);
    }

    /**
     * 输出当前内容
     *
     * @return
     */
    public str print() {
        utilFun.print(this.toString());
        return this;
    }

    /**
     * 输出当前内容
     *
     * @return
     */
    public str print(PrintColor color, boolean allLines) {
        nullCheck(color);
        String strings = this.toString().replaceAll("\n", "\n" + color.getCode());
        utilFun.print(color.getCode() + strings + color.getCode());
        return this;
    }

    /**
     * 判断字符串是否为URL
     * 
     * @param urls 用户头像key
     * @return true:是URL、false:不是URL
     */
    public boolean isHttpUrl() {
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";// 设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());// 比对
        Matcher mat = pat.matcher(to_s());
        isurl = mat.matches();// 判断是否匹配
        if (isurl) {
            isurl = true;
        }
        return isurl;
    }

    /**
     * 把原始字符串分割成指定长度的字符串列表
     * 
     * @param length 指定长度
     * @return
     */
    public List<String> getStrList(int length) {
        String str = to_s();
        int len = length();
        String[] arr = new String[(len + length - 1) / length];
        for (int i = 0; i < len; i += length) {
            int n = len - i;
            if (n > length)
                n = length;
            arr[i / length] = str.substring(i, i + n);
        }
        return newList(arr);
    }

    /**
     * 响应是否为xml?
     * 
     * @return
     */
    public boolean isXml() {
        try {
            toXml();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 字符串是否为数字(会自动进行去空格操作)
     *
     * @return
     */
    public Boolean isNum() {
        try {
            if (isEmpty()) {
                return false;
            }
            Integer.parseInt(toString().trim());
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 字符串转xmlDocument,内部会最自动删除所有换行,注意输入字符串必须以<开头,否则会报错!
     * 
     * @return 出现错误返回null
     */
    public XmlNode toXml() {// trimAllWrap保证不要存在换行
        return new XmlNode(XMLUtil.strToDocument(this.trimAllWrap().to_s()).getDocumentElement());// 读取根节点
    }

    /**
     * 转换为jsonArray
     *
     * @return
     */
    public JSONArray toJsonArray() {
        return JSONUtil.jsonToJsonObjArry(toString());
    }

    /**
     * 迭代json的所有元素JSONObject/JSONArray/其他数据(toString()的结果)
     *
     * @param objJson          被遍历的json可以为JSONObject/JSONArray/
     * @param onJsonObject_opt 当找到JSONObject的时候的处理函数 key 为此JSONOBJECT在父层的key
     * @param onJsonArray_opt  当找到JSONOArray的时候的处理函数key 为此JSONARRAY在父层的key
     * @param onData_opt       当找到其他类型的数据的时候处理函数key 为此数据在父层的key
     * @return Object 被处理的j数据,可能是一个JSONobject可能是一个JSONArray,如果错误则返回null
     */
    public Object IteratorJSON(Consumer<JsonData> onJsonObject_opt, Consumer<JsonData> onJsonArray_opt,
            Consumer<JsonData> onData_opt) {
        try {
            if (isJsonObject()) {
                return JSONUtil.IteratorJSON(toJsonObject(), onJsonObject_opt, onJsonArray_opt, onData_opt);
            } else if (isJsonArray()) {
                return JSONUtil.IteratorJSON(toJsonArray(), onJsonObject_opt, onJsonArray_opt, onData_opt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转为url
     *
     * @return
     */
    public URL toURL() {
        return HttpUtil.parseUrl(toString());
    }

    /**
     *
     * 内部json转换为对象
     * 
     * @param <T>
     * @param c
     * @return
     */
    public <T> T toObject(Class<T> c) {
        return JSONUtil.jsonToObj(toString(), c);
    }

    /**
     *
     * 内部json转换为LList
     * 
     * @param <T>
     * @param c
     * @return
     */
    public <T> LList<T> toList(Class<T> c) {
        return ListUtil.listToLlist(JSONUtil.jsonArryStrToObjList(toString(), c));
    }

    /**
     *
     * 内部json转换为JSON对象
     * 
     * @param <T>
     * @param c
     * @return
     */
    public JSONObject toJsonObject() {
        if (isJson()) {
            return JSONUtil.jsonToJsonObj(toString());
        }
        return JSONUtil.jsonToJsonObj("{}");
    }

    /**
     * 编码为base64URL
     * 
     * @return
     */
    public str encodeBase64URLsafe() {
        return new str(Encode.bytesToBase64SafeStr(toBytes()));
    }

    /**
     * 解码为base64URL
     * 
     * @return
     */
    public str decodeBase64URLsafe() {
        return new str(Decode.base64UrlStrToStr(toString(), charSet));
    }

    /**
     * base64解码当前字符串
     * 在解码的时候自动将base64字符串中的空格替换为+号,因为base64经过Url传输的时候,如果在客户端没有进行safeBase64处理则会出现+,服务端接受之后会将其替换为空格,会导致解析出错
     *
     * @return
     */
    public str decodeBase64() {

        return new str(Decode.base64StrToBytes(toString().replaceAll(" ", "+")));
    }

    /**
     * base64编码当前字符串
     *
     * @return
     */
    public str encodeBase64() {
        return new str(Encode.strToBase64Str(toString(), charSet));
    }


    /**
     * url编码
     * 
     * @return
     */
    public str encodeUrl() {
        return new str(Encode.strToUrl(toString()));
    }

    /**
     * url解码
     *
     * @return
     */
    public str decodeUrl() {
        return new str(Decode.urlToStr(toString()));
    }

    /**
     * str的操作中不要报告错误PrintStackTrace
     */
    public str dontPrintStackTrace() {
        dontPrintStackTrace = true;
        return this;
    }

    /**
     * 是否需要报告错误?
     * 
     * @return
     */
    public boolean needPrintStackTrace() {
        if (dontPrintStackTrace) {
            return false;
        }
        return true;
    }

    /**
     * 直接写入到文件,副噶原文件
     * 
     * @param file
     * @return
     */
    public boolean writeToFile(FileRes file) {
        return file.writeStr(toString(), this.charSet);
    }

    /**
     * 追加写入到文件,不覆盖
     *
     * @param file
     * @return
     */
    public boolean appendToFIle(FileRes file) {
        return file.appendWriteStr(toString(), this.charSet);
    }

    /**
     * 是否为html
     * 
     * @return
     */
    public boolean isHtml() {
        return RegexUtil.createRegex(RegexUtil.htmlTags).has(toString());
    }

    /**
     * 是否为json
     *
     * @param printStackTrace 是否输出转换时出现的异常堆栈
     * @return
     */
    public boolean isJson(Boolean printStackTrace) {
        if (printStackTrace == null) {
            printStackTrace = false;
        }
        if (isEmpty()) {
            return false;
        }
        String string = toString();
        try {
            JSONUtil.jsonToJsonObj(string);
            return true;
        } catch (Exception e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
        try {
            JSONUtil.jsonToJsonObjArry(string);
            return true;
        } catch (Exception e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否为json,不输出错误堆栈
     *
     * @return
     */
    public boolean isJson() {
        return isJson(false);
    }


    /**
     * 字符串是否为JsonObject
     *
     * @return
     */
    public boolean isJsonObject(Boolean printStackTrace) {
        if (printStackTrace == null) {
            printStackTrace = false;
        }
        try {
            String string = toString();
            JSONUtil.jsonToJsonObj(string);
            return true;
        } catch (Exception e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 字符串是否为JsonObject,不输出转换错误堆栈
     *
     * @return
     */
    public boolean isJsonObject() {
        return isJsonObject(false);
    }

    /**
     * 字符串是否为jsonArray
     *
     * @param printStackTrace 是否输出错误堆栈
     * @return
     */
    public boolean isJsonArray(Boolean printStackTrace) {
        if (printStackTrace == null) {
            printStackTrace = false;
        }
        if (isEmpty()) {
            return false;
        }
        try {
            String string = toString();
            JSONUtil.jsonToJsonObjArry(string);
            return true;
        } catch (Exception e) {
            if (dontPrintStackTrace) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 字符串是否为jsonArray,当转换错误不输出堆栈
     *
     * @return
     */
    public boolean isJsonArray() {
        return isJsonArray(false);
    }

    /**
     * 求hashmd5 如果字符串为null字符或者为 ""则返回null否则返回md5
     *
     * @return
     */
    public String hashMd5() {
        if (isEmpty()) {
            return null;
        }
        return Digest.strToMd5HexStr(toString());
    }

    /**
     * 求hashSHA256 如果字符串为null字符或者为 ""则返回null否则返回md5
     *
     * @return
     */
    public String hashSHA256() {
        if (isEmpty()) {
            return null;
        }
        return Digest.strToSha256Str(toString());
    }



 
    /**
     * 读取正则表达式匹配到的字符串,不会返回null,如果匹配识别则只会返回长度为0列表
     *
     * @param regexStr
     * @return
     */
    public List<String> match(String regexStr) {
        nullCheck(regexStr);
        return RegexUtil.createRegex(regexStr).getMatchs(toString());
    }

    /**
     * 读取正则表达式匹配到的字符串,不会返回null,如果匹配识别则只会返回长度为0列表
     *
     * @param regexStr
     * @return
     */
    public List<MatchAndPosition> matchAndPostions(String regexStr) {
        nullCheck(regexStr);
        return RegexUtil.createRegex(regexStr).getMatchAndPostions(toString());
    }

    /**
     * 根据正则表达式替换字符串中的数据,从左侧开始
     *
     * @param content     目标内容
     * @param count       替换的次数
     * @param replacement 替换之后内容
     * @return 结果字符串
     */
    public String replaceFromLeft(int count, String str, String replacement) {
        nullCheck(replacement);
        String data = toString();
        for (int i = 0; i < count; i++) {
            data = data.replaceFirst(str, replacement);
        }
        return data;
    }

    /**
     * 等同eq函数
     */
    @Override
    public boolean equals(Object obj) {
        return eq(obj);
    }

    /**
     * 和另外一个字符串比较相似度,最大为1
     * 
     * @param str
     * @return
     */
    public double getMatchRatio(String str) {
        nullCheck(str);
        return hirshbergMatcher.getMatchRatio(this.to_s(), str);
    }


    /**
     * 转换为字符数组
     * 
     * @return
     */
    public char[] to_charArray() {
        String string = toString();
        char[] charArray = new char[string.length()];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = string.charAt(i);
        }
        return charArray;
    }

    /**
     * 克隆字符串
     */
    public str clone() {
        return str(this);
    }
}