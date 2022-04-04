package github.acodervic.mod.io;

import static github.acodervic.mod.data.BaseUtil.isNull;
import static github.acodervic.mod.data.BaseUtil.nullByteArry;
import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.nullString;
import static github.acodervic.mod.data.BaseUtil.parseCharset;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import github.acodervic.mod.Constant;
import github.acodervic.mod.data.str;
import github.acodervic.mod.data.map.HMap;
import github.acodervic.mod.function.FunctionUtil;
import github.acodervic.mod.net.http.HttpClient;
import github.acodervic.mod.net.http.Method;
import github.acodervic.mod.net.http.RepLogic;

/**
 * FileRead
 */
public class FileReadUtil {
    public static boolean DEBUG = false;

    /**
     * 读取文本文件全部行，执行失败则返回一个长度为0的集合
     * 
     * @param file         文件
     * @param encoding_opt 编码格式null=utf8
     * @return 列表行
     * @throws IOException
     */
    public static List<String> readFilesLinesToStringList(File file, String encoding_opt) {
        nullCheck(file);
        List<String> lines = new ArrayList<String>();
        try {
            lines = FileUtil.readLines(file, parseCharset(encoding_opt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * 读取文本文件全部行，执行失败则返回一个长度为0的集合
     * 
     * @param file         文件
     * @param encoding_opt 编码格式null=utf8
     * @return 列表行
     * @throws IOException
     */
    public static List<str> readFilesLinesToStrList(File file, String encoding_opt) {
        nullCheck(file);
        List<str> lines = new ArrayList<str>();
        try {
            List<String> readLines = FileUtil.readLines(file, parseCharset(encoding_opt));
            for (int i = 0; i < readLines.size(); i++) {
                lines.add(new str(readLines.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }
    /**
     * 将文件读取到一个字符串中，执行失败则返回一个长度为0的字符串
     * 
     * @param file         目标文件
     * @param encoding_opt 编码格式null=系统默认编码
     * @return 字符串
     * @throws IOException
     */
    public static String readFileToString(File file, String encoding_opt) {
        nullCheck(file);
        return FunctionUtil.tryReturn(() -> FileUtil.readString(file, parseCharset(encoding_opt)), nullString());
    }

    /**
     * 读取文本文件的倒序第几行,执行失败则返回一个长度为0的字符串列表
     * 
     * @param file         目标文件
     * @param encoding_opt 编码格式null=系统默认编码
     * @return
     * @throws IOException
     */
    public static List<String> readFileLastLineIndex(File file, int lineCount, String encoding_opt)
            throws IOException {
        nullCheck(file);
        List<String> lines = new ArrayList<String>();
        // 假设最后一行不是空行
        RandomAccessFile rf = new RandomAccessFile(file, "r");
        long len = rf.length(); // 文件长度
        long nextend = len - 1;
        int c = -1;
        int count = 1;
        while (nextend > 0) {
            rf.seek(nextend);
            c = rf.read();
            if ((c == '\n') & count == lineCount) {
                String line = new String(rf.readLine().getBytes(encoding_opt));
                lines.add(line);
                break;
            } else if (c == '\n') {
                count++;
            }

            nextend--;
 }
        return lines;
    }

    /**
     * 读取文件到二进制字节数组,执行失败则返回一个长度为0的字节数组
     * 
     * @param file 目标文件
     * @return 字节数组
     * @throws IOException
     */
    public static byte[] readFileToByteArray(File file) {
        nullCheck(file);
        return FunctionUtil.tryReturn(() -> FileUtil.readBytes(file), nullByteArry());
    }

    /**
     * 读取网络上的url资源为file对象，执行失败则返回一个空的字符串
     * 
     * @param url        目标Url
     * @param httpClient_opt http客户端 (可选null=一个直连接的客户端)
     * @param header_opt 请求头(可选null=添加基本的用户代理)
     * @param String charset_opt 编码格式,如果为null,则尝试从返回的html中读取编码,如果读取失败或不是html则使用系统默认编码
     * @return
     */
    public static String readUrlToStr(String url, HttpClient httpClient_opt, HMap<String, String> header_opt,
            String charset_opt) {
        nullCheck(url);
        if (isNull(httpClient_opt)) {
            httpClient_opt = HttpClient.getUnsafeOkHttpClient(Constant.httpTimeOut);
        }
        if (header_opt==null) {
            // 如果headr为空,则进行实例化
            header_opt = new HMap<String, String>();
        }
        if (isNull(httpClient_opt)) {
            httpClient_opt = HttpClient.getUnsafeOkHttpClient(Constant.httpTimeOut);
        }
        if (!header_opt.containsKey("User-Agent")) {
            header_opt.put("User-Agent",
                    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Mobile Safari/537.36");
        }
        try {

            RepLogic logic =   httpClient_opt.syncReq(url, Method.GET, header_opt, null); // 读取输入流,每次读取一个字节
            logic.closeConnection();
            if (charset_opt!=null) {
                return logic.bodyString(charset_opt).toString();

            }else{
                return logic.bodyString().toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 读取网络上的url资源为,将body返回为bytes,如果处理或者连接失败返回长度为0字节数组,当heade为null时,默认头部只放入浏览器字段
     *
     * @param url        目标Url
     * @param httpClient_opt http客户端 (可选null=一个直连接的客户端)
     * @param header_opt 请求头(可选null=添加基本的用户代理)
     * @return
     */
    public static byte[] readUrlToBytes(String url, HttpClient httpClient_opt,
            HMap<String, String> header_opt) {
        nullCheck(url);
        if (header_opt == null || header_opt.size() == 0) {
            // 如果headr为空,则进行实例化
            header_opt = new HMap<String, String>();
        }
        if (!header_opt.containsKey("User-Agent")) {
            header_opt.put("User-Agent",
                    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Mobile Safari/537.36");
        }
        return httpClient_opt.syncReq(url, Method.GET, header_opt, null).getHttpRepsoneRequest()
                .getBytelist().toBytes();
    }

}