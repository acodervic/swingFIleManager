package github.acodervic.mod.crypt;

import static github.acodervic.mod.data.BaseUtil.nullByteArry;
import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.nullString;
import static github.acodervic.mod.utilFun.tryReturn;

import java.io.InputStream;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * Digest
 */
public class Digest {

    // Md5摘要==========================================================================================

    /**
     * 对字节数组进行md5摘要,返回摘要后的md5字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的md5字节数组
     */
    public static byte[] bytesToMd5Bytes(byte[] bytes) {
        return tryReturn(() -> DigestUtil.md5(bytes), nullByteArry());
    }

    /**
     * 对字符串进行md5摘要,返回摘要后的md5字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param str 输入字节数组
     * @return 摘要后的md5字节数组
     */
    public static byte[] strToMd5Bytes(String str) {
        nullCheck(str);
        return tryReturn(() -> DigestUtil.md5(str), nullByteArry());
    }

    /**
     * 对输流进行md5摘要,返回摘要后的md5字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param InputStream 输入流
     * @return 摘要后的md5字节数组
     */
    public static byte[] InputStreamToMd5Bytes(InputStream InputStream) {
        nullCheck(InputStream);
        return tryReturn(() -> DigestUtil.md5(InputStream), nullByteArry());
    }

    // 字符串z摘要-------------------------------
    /**
     * 对字符串进行md5摘要,返回md5十六进制字符串,出现异常则返回空字符串
     * 
     * @param str 输入字节数组
     * @return 摘要之后的16进制字符串
     */
    public static String bytesToMd5HexStr(byte[] str) {
        return tryReturn(() -> DigestUtil.md5Hex(str), nullString());
    }

    /**
     * 对字符串进行md5摘要,返回md5十六进制字符串,出现异常则返回空字符串
     * 
     * @param str 输入字符串
     * @return 摘要之后的16进制字符串
     */
    public static String strToMd5HexStr(String str) {
        return tryReturn(() -> DigestUtil.md5Hex(str), nullString());
    }

    /**
     * 对字节数组进行md5摘要,返回md5十六进制字符串,出现异常则返回空字符串
     * 
     * @param InputStream 输入流
     * @return 摘要之后的16进制字符串
     */
    public static String bytesToMd5HexStr(InputStream InputStream) {
        nullCheck(InputStream);
        return tryReturn(() -> DigestUtil.md5Hex(InputStream), nullString());
    }

    // SHA-1摘要==========================================================================================

    /**
     * 对字符串数据进行sha1摘要,返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param str 输入字符串
     * @return 摘要之后的字符串
     */
    public static String strToSha1Str(String str) {
        nullCheck(str);
        return tryReturn(() -> DigestUtil.sha1Hex(str), nullString());

    }

    /**
     * 对字节数组进行sha1摘要,返回摘要后的字符串,出现异常则返回长度为0的字节数组
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的字符串
     */
    public static String bytesToSha1Str(byte[] bytes) {
        return tryReturn(() -> DigestUtil.sha1Hex(bytes), nullString());
    }

    /**
     * 对输入流进行SHA-1摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param InputStream 输入流
     * @return 摘要后的字节数组
     */
    public static byte[] bytesToSha1Str(InputStream InputStream) {
        nullCheck(InputStream);
        return tryReturn(() -> DigestUtil.sha1(InputStream), nullByteArry());

    }

    // SHA-256摘要==========================================================================================

    /**
     * 对字符串进行sha256摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param str 输入字符串
     * @return 摘要后的数组
     */
    public static byte[] strToSha256bytes(String str) {
        nullCheck(str);
        return tryReturn(() -> DigestUtil.sha256(str), nullByteArry());
    }

    /**
     * 对字节数组进行sha256摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的数组
     */
    public static byte[] bytesToSha256bytes(byte[] bytes) {
        return tryReturn(() -> DigestUtil.sha256(bytes), nullByteArry());

    }

    /**
     * 对输入流进行sha256摘要，返回摘要后的字节数组,出现异常则返回长度为0的字节数组
     * 
     * @param InputStream 输入流
     * @return 摘要后的字节数组
     */
    public static byte[] InputStreamToSha256bytes(InputStream InputStream) {
        nullCheck(InputStream);
        return tryReturn(() -> DigestUtil.sha256(InputStream), nullByteArry());
    }

    /**
     * 对字符串进行sha256摘要，返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param str 输入字符串
     * @return 摘要后的字符串
     */
    public static String strToSha256Str(String str) {
        nullCheck(str);
        return tryReturn(() -> DigestUtil.sha256Hex(str), nullString());
    }

    /**
     * 对字节数组进行sha256摘要，返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param bytes 输入字节数组
     * @return 摘要后的字符串
     */
    public static String bytesToSha256Str(byte[] bytes) {
        return tryReturn(() -> DigestUtil.sha256Hex(bytes), nullString());

    }

    /**
     * 对输入流进行sha256摘要，返回摘要后的字符串,出现异常则返回空字符串
     * 
     * @param InputStream 输入流
     * @return 摘要后的字符串
     */
    public static String InputStreamToSha256Str(InputStream InputStream) {
        nullCheck(InputStream);
        return tryReturn(() -> DigestUtil.sha256Hex(InputStream), nullString());
    }

}