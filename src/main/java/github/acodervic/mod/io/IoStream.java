package github.acodervic.mod.io;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.str;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.function.Function;

import cn.hutool.core.io.IoUtil;
import github.acodervic.mod.data.ByteLIst;
import github.acodervic.mod.data.str;
/**
 * byteStream,用于从本地磁盘或远程磁盘中读取字节数据
 */
public class IoStream {

    /**
     * 读取当输入流为字节数组并返回,如果流对端一直未关闭则会一种阻塞
     *
     * @param inputStream
     * @return
     */
    public static byte[] readSteam(InputStream inputStream) {
        try {
            return IoUtil.readBytes(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteLIst().toBytes();
    }

    /**
     * 带次从输入流中进行一次数据读取,如果失败则返回 长度为0的字节数组
     * @param inputStream
     * @return
     */

    public static byte[] readSteamOne(InputStream inputStream) {
        nullCheck(inputStream);
        try {
                    int   availableint=inputStream.available();
                    byte[] data=new byte[availableint];
                     inputStream.read(data);
                     return data;
        } catch (Exception e) {
            e.printStackTrace();
         }
         return new ByteLIst().toBytes();
    }

    /**
     * 同步读取inputStream
     * 
     * @param <T>
     * @param inputStream 收入流
     * @param fun         函数处理 strLine ->{ return "str";}
     * @return 返回所有结果
     */
    public static <T> str syncReadInputSteam(InputStream inputStream, Function<str, String> fun) {
        nullCheck(inputStream, fun);
        LineNumberReader br = new LineNumberReader(new InputStreamReader(inputStream));
        str result =str("");
        String line;
        try {
            while ((line = br.readLine()) != null) {
                result.insertStrToEnd(fun.apply(str(line)));
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (Exception e) {
        }
        return result;
    }
}