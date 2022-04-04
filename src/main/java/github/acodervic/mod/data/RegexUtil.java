package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.acodervic.mod.data.mode.MatchAndPosition;
import github.acodervic.mod.function.FunctionUtil;
import github.acodervic.mod.shell.SystemUtil;

/**
 * RegexUtil
 */
public class RegexUtil {
    static Map<String, RegexUtil> regexPatternMap = new HashMap<String, RegexUtil>();
    // 自带的正则表达式
    public final static String URL = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public final static String PHONE = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
    public final static String IP = "^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$";
    public final static String htmlComments = "<!--(.|[\r\n])*?-->"; // html注释
    public final static String htmlTags = "<[^>]+>";// html标签
    public final static String APPVERSION = "(?:(\\d+)\\.)?(?:(\\d+)\\.)?(?:(\\d+)\\.\\d+)";// 程序版本

     Pattern pattern = null;

     private RegexUtil(Pattern p) {
         nullCheck(p);
         this.pattern = p;

    }

    /**
     * 创建一个正则表达式帮助
     * 
     * @param pattern 表达式字符串
     * @return 正则表达式工具
     */
    public static RegexUtil createRegex(String pattern) {
        nullCheck(pattern);
        RegexUtil p = regexPatternMap.get(pattern);
        if (p != null) {
            return p;
        }
        p = new RegexUtil(Pattern.compile(pattern));
        regexPatternMap.put(pattern, p);
        return p;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        nullCheck(pattern);
        this.pattern = pattern;
    }

    /**
     * 是否存在匹配的字符串
     * 
     * @param content 目标内容
     * @return 结果字符串
     */
    public boolean has(String content) {
        if (content == null) {
            return false;
        }
        return this.pattern.matcher(FunctionUtil.get(() -> content.toString()).orElse("")).find();
    }

    /**
     * 得到匹配的字符串
     *
     * @param content 目标内容
     * @return 结果字符串
     */
    public List<String> getMatchs( String content) {
        // 现在创建 matcher 对象
        Matcher m = this.pattern.matcher(FunctionUtil.get(()-> content.toString()  ).orElse(""));
        List<String> matchs = new ArrayList<String>();
        while (m.find()) {
            matchs.add(m.group());
        }
        return matchs;
    }

    /**
     * 得到匹配的字符串的详细对象
     *
     * @param content 目标内容
     * @return 找到结果字符串的详细信息
     */
    public List<MatchAndPosition> getMatchAndPostions(String content) {
        // 现在创建 matcher 对象
        Matcher m = this.pattern.matcher(FunctionUtil.get(() -> content.toString()).orElse(""));
        List<MatchAndPosition> matchs = new ArrayList<MatchAndPosition>();
        while (m.find()) {
            // 提取组
            List<String> grouplist = new ArrayList<String>();
            if (m.groupCount() > 0) {
                for (int i = 0; i < (m.groupCount() + 1); i++) {
                    grouplist.add(m.group(i));
                }
            }
            MatchAndPosition matchAndPosition = new MatchAndPosition();
            matchAndPosition.setMatch(m.group());
            matchAndPosition.setStartIndex(m.start());
            matchAndPosition.setEndIndex(m.end());
            matchAndPosition.setMatch(m.group());
            matchAndPosition.setSearch(content);
            matchAndPosition.setGroups(grouplist);
            matchs.add(matchAndPosition);
        }
        return matchs;
    }


    /**
     * 根据正则表达式替换文本中的数据
     * @param content 目标内容
     * @param replacement 替换之后内容
     * @return 结果字符串
     */
    public String replaceAll(String content, String replacement) {
        nullCheck(replacement);
        return FunctionUtil.get(()-> content.toString()  ).orElse("").replaceAll(this.pattern.pattern(), replacement);
    }

    /**
     * 按照正则表达式分割字符串
     * @param content 目标内容
     * @return 字符串数组
     */
    public String[] split( String content) {
        return this.pattern.split(FunctionUtil.get(()-> content.toString()  ).orElse(""));
    }

    @Override
    public String toString() {
        return  pattern.toString();
    }

    public static void main(String[] args) {
        String syncExecShellString = SystemUtil.syncExecShellString(new DirRes("/home/w/.getsploit/searchExploitsLog"),
                "/usr/bin/proxychains4  /usr/bin/python3  /home/w/mytool/zap/wappalyzer/getsploit.py   -m  'Debian'    -c  99999  --json --local");
        System.out.println(syncExecShellString.length());
    }
}