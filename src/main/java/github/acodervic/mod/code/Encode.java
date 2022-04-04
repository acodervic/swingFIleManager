package github.acodervic.mod.code;

import static github.acodervic.mod.data.BaseUtil.nullByteArry;
import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.nullString;
import static github.acodervic.mod.data.BaseUtil.parseCharset;

import java.nio.charset.Charset;
import java.util.List;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.net.URLEncoder;
import github.acodervic.mod.Constant;
import github.acodervic.mod.data.CharUtil;
import github.acodervic.mod.data.NumberUtil;
import github.acodervic.mod.function.FunctionUtil;

/**
 * 编码数据
 */
public class Encode {




    /**
     * url编码字符串,如果编码失败则返回空字符串
     * @param url
     * @return
     */
    public static String strToUrl(String url) {
        nullCheck(url);
        try { 
            return URLEncoder.createDefault().encode(url, Charset.forName(Constant.defultCharsetStr));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 字符串转base64字符串(先转byte然后在转字符串),失败则返回空字符串
     * @param str 原始输入字符串
     * @param charset_opt 输入字符串的编码(可选,null=utf8,将以此编码格式转换输入字符串str为字节数组)
     * @return base64字符串
     */
    public static String strToBase64Str(String str, String charset_opt) {
        nullCheck(str);
        return FunctionUtil.tryReturn(() -> Base64Encoder.encode(str, parseCharset(charset_opt)), nullString());
    }

    /**
     * 字符串转base64URL字符串(先转byte然后在转字符串),失败则返回空字符串
     * @param str 原始输入字符串
     * @param charset_opt  输入字符串的编码(可选,null=utf8,将以此编码格式转换输入字符串str为字节数组)
     * @return base64URL字符串
     */
    public static String strToBase64UrlStr(String str, String charset_opt) {
        nullCheck(str);
        return FunctionUtil.tryReturn(() -> Base64Encoder.encodeUrlSafe(str, parseCharset(charset_opt)), nullString());
    }

/**
      * 字符串转base64字节数组,(先转byte然后在转字符串),失败则返回空字节数组
 * @param str 原始输入字符串 
 * @param charset_opt 输入字符串的编码(可选,null=utf8,将以此编码格式转换输入字符串str为字节数组)
 * @return 字节数组
 */
public static byte[] strToBase64Bytes(String str, String charset_opt) {
    nullCheck(str);
        return strToBase64Str(str, charset_opt).getBytes();
    }

    /**
     * 将字节数组转换为base64字节数组,失败则返回空字节数组
     * 
     * @param bytes 原始字节数组
     * @return base64字节数组
     */
    public static byte[] bytesToBase64Bytes(byte[] bytes) {
        return FunctionUtil.tryReturn(() -> Base64Encoder.encode(bytes).getBytes(), nullByteArry());
    }

    /**
     * 将字节数组转换为base64URL字节数组,失败则返回空字符串
     * 
     * @param bytes 原始字节数组
     * @return base64URL字符串
     */ 
    public static String bytesToBase64SafeStr(byte[] bytes) {
        return FunctionUtil.tryReturn(() -> Base64Encoder.encodeUrlSafe(bytes), nullString());
    }


    /**
     * 将字节数组转换为base64字符串,失败则返回空字符串
     * 
     * @param bytes 原始字节数组
     * @return base64URL字符串
     */
    public static String bytesToBase64Str(byte[] bytes) {
        return FunctionUtil.tryReturn(() -> Base64Encoder.encode(bytes), nullString());
    }

    /**
     * 使用http分块传输编码数据
     *
     * @param minChunkedLength 最小传输单块长度
     * @param maxChunkedLength 最大传输单块长度
     * @param body             被传输的数据
     * @param comment          是否添加评论
     * @param maxCommentLength 最大评论长度
     * @param minCommentLength 最小评论长度
     * @return
     */
    public static String transferEncode(int minChunkedLength, int maxChunkedLength, String body, boolean comment,
            int maxCommentLength, int minCommentLength) {
        List<String> str_list = CharUtil.getStrRandomLenList(body, minChunkedLength, maxChunkedLength);
        String encoding_body = "";
        for (String str : str_list) {
            if (comment) {
                int commentLen = NumberUtil.getRandomNum(maxCommentLength, minCommentLength);
                encoding_body += String.format("%s;%s", NumberUtil.decimalToHex(str.length()),
                        CharUtil.getRandomString(commentLen));
            } else {
                encoding_body += NumberUtil.decimalToHex(str.length());
            }
            encoding_body += "\r\n";
            encoding_body += str;
            encoding_body += "\r\n";
        }
        encoding_body += "0\r\n\r\n";
        return encoding_body;
    }
}