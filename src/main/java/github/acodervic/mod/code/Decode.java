package github.acodervic.mod.code;

import static github.acodervic.mod.data.BaseUtil.nullByteArry;
import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.nullString;
import static github.acodervic.mod.data.BaseUtil.parseCharset;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.codec.Base64Decoder;
import github.acodervic.mod.function.FunctionUtil;

/**
 * 解码数据
 */
public class Decode {



    // base64函数==================================================================================================================

    /**
     * 解码base64字符串为普通字符串,如果失败则返回空字符串
     * 
     * @param str         base64字符串
     * @param charset_opt 解码后的新的字符串的编码(可选,null则为utf-8)
     * @return 解码后的字符串
     */
    public static String base64StrToStr(String str, String charset_opt) {
        nullCheck(str);
        return FunctionUtil.tryReturn(() -> Base64Decoder.decodeStr(str, parseCharset(charset_opt)),
                nullString());
    }
 
    /**
     * url解码,如果解码失败则返回空字符串
     * @param url
     * @return
     */
    public static String urlToStr(String url) {
        nullCheck(url);
        try {
            String prevURL = "";
            String decodeURL = url;
            while (!prevURL.equals(decodeURL))
            {
                prevURL = decodeURL;
                decodeURL = URLDecoder.decode(decodeURL, "UTF-8");
            }
            return decodeURL;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 解码base64URL字符串为普通字符串,如果失败则返回空字符串
     * 
     * @param base64Str   base64字符串
     * @param charset_opt 解码后的新的字符串的编码(可选,null则为utf-8)
     * @return 解码后的字符串
     */
    public static String base64UrlStrToStr(String base64Str, String charset_opt) {
        nullCheck(base64Str);
        return FunctionUtil.tryReturn(() -> Base64Decoder.decodeStr(base64Str, parseCharset(charset_opt)),
                nullString());
    }

    /**
     * 解码base64字符串为字节数组,如果失败则返回空字节数组
     * 
     * @param base64Str base64字符串
     * @return 解码之后的字节数组
     */
    public static byte[] base64StrToBytes(String base64Str) {
        nullCheck(base64Str);
        return FunctionUtil.tryReturn(() -> Base64Decoder.decode(base64Str), nullByteArry());
    }

    /**
     * 解码base64URL字符串到字节数组,如果失败则返回空字节数组
     * 
     * @param base64Str base64字符串
     * @return 解码之后的字节数组
     */
    public static byte[] base64UrlStrToBytes(String base64Str) {
        nullCheck(base64Str);
        return FunctionUtil.tryReturn(() -> Base64Decoder.decode(base64Str), nullByteArry());
    }

    /**
     * 解码base64编码之后的字节数组
     *
     * @param base64Bytes base64编码之后的字节数组
     * @return 解码之后的字节数组
     */
    public static byte[] base64BytesToBytes(byte[] base64Bytes) {
        return FunctionUtil.tryReturn(() -> Base64Decoder.decode(base64Bytes), nullByteArry());
    }



    /**
     * 使用http分块传输解码数据
     *
     * @param minChunkedLength 最小传输单块长度
     * @param maxChunkedLength 最大传输单块长度
     * @param body             被传输的数据
     * @param comment          是否添加评论
     * @param maxCommentLength 最大评论长度
     * @param minCommentLength 最小评论长度
     * @return
     */
    public static String transferDncode(int minChunkedLen, int maxChunkedLen, String body, boolean comment,
            int maxCommentLength, int minCommentLength) {
        // Decoding
        String[] array_body = body.split("\r\n");
        List<String> list_string_body = Arrays.asList(array_body);
        List list_body = new ArrayList(list_string_body);
        list_body.remove(list_body.size() - 1);
        String decoding_body = "";
        for (int i = 0; i < list_body.size(); i++) {
            int n = i % 2;
            if (n != 0) {
                decoding_body += list_body.get(i);
            }
        }
        return decoding_body;
    }
}