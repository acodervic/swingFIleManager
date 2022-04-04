package github.acodervic.mod.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.acodervic.mod.data.str;

/**
 * HttpUtil
 */
public class HttpUtil {
    static Hashtable<String, URL> parsedUrlMap = new Hashtable<String, URL>();// 存储所有转换过的url
    static Pattern p = Pattern.compile(":\\d+"); // look for the first occurrence of colon followed by a number

    /**
     * 将字符串转换为url
     * 
     * @param urlSrtr
     * @return
     */
    public static URL parseUrl(String urlSrtr) {
        if (urlSrtr != null && parsedUrlMap.containsKey(urlSrtr)) {
            // 注意为了防止旧的url对象被修改,仅仅通过内部属性构造返回新的url对象
            URL u = parsedUrlMap.get(urlSrtr);
            try {
                return new URL(u.getProtocol(), u.getHost(), u.getPort(), u.getFile());
            } catch (MalformedURLException e) {
            }
            // 失败之后再继续转换
        }
        // 进行转换
        URL url;
        try {
            url = new URL(new str(urlSrtr).trimLeft().to_s());

            int port = 80; // assumption of default port in the URL
            if (urlSrtr.indexOf("https://") == 0) {
                port = 443;
            }
            Matcher matcher = p.matcher(urlSrtr);
            if (matcher.find()) {
                String portStrWithColon = matcher.group();
                if (portStrWithColon.length() > 1) {
                    String portStr = portStrWithColon.substring(1);
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (NumberFormatException e) {
                    }
                }
            }
            String file = url.getFile();
            if (file.length() == 0) {
                file = "/";
            }
            URL ret = new URL(url.getProtocol(), url.getHost(), port, file);
            parsedUrlMap.put(urlSrtr, ret);
            return ret;
        } catch (MalformedURLException e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * 相对路径转绝对路径url
     *
     * @param absoluteUrlString http://www.baidu.com/1/2
     * @param relativelyString  ../../3
     * @return http://www.baidu.com/1/3
     */
    public static URL parseUrl(String absoluteUrlString, String relativelyString) {
        try {
            URL absoluteUrl = new URL(absoluteUrlString);

            return new URL(absoluteUrl, relativelyString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

 
 
}
