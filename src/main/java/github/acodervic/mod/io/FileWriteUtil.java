package github.acodervic.mod.io;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.parseCharset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import github.acodervic.mod.data.str;

/**
 * FileWrite
 */
public class FileWriteUtil {

    /**
     * 写入字符串到磁盘文件,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param str         文本
     * @param destfile    文件
     * @param charset_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public static boolean writeStrToFile(String str, File destfile, String charset_opt) {
        nullCheck(str, destfile);
        try {
            FileUtil.writeString(str, destfile, parseCharset(charset_opt));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入字符串到磁盘文件,每个元素自动换行,注意文件将会被重写覆盖,如果该文件不存在，则创建该文件
     * 
     * @param strs        字符串列表
     * @param destfile    文件
     * @param charset_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public static boolean writeStrToFile(List<String> strs, File destfile, String charset_opt) {
        nullCheck(strs, destfile);
        try {

            FileUtil.writeLines(strs, destfile, parseCharset(charset_opt));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param str         字符串
     * @param destfile    目标文件
     * @param charset_opt 字符编码(可选null=UTF-8)
     * @return 成功true,失败false
     */
    public static boolean appendWriteStrToFile(String str, File destfile, String charset_opt) {
        nullCheck(str, destfile);
        try {
            FileUtil.appendString(str, destfile, parseCharset(charset_opt));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 追加写入字符串到磁盘文件中,如果该文件不存在，则创建该文件
     * 
     * @param lines 字符串列表
     * @param destfile 目标文件
     * @param lineSpitStr_opt 写入的换行符号 null=系统默认值
     * @return 成功true,失败false
     */
    public static boolean appendWriteStrToFile(List<String> lines, File destfile,
            String lineSpitStr_opt, String charset_opt) {
        nullCheck(lines, destfile);
        try {
            str str = new str("");
            for (int i = 0; i < lines.size(); i++) {
                str.insertStrToEnd(lines.get(i) + (lineSpitStr_opt == null ? "\n" : lineSpitStr_opt));
            }
            FileUtil.writeString(str.to_s(), destfile, parseCharset(charset_opt));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写入字节数组到磁盘文件中,如果文件存在文件将被重写覆盖, 如果该文件不存在，则创建该文件
     * 
     * @param byteData 字节数组
     * @param destfile     被写入的磁盘文件
     * @return 成功true,失败false
     */
    public static boolean writeByteArrayToFile(byte[] byteData, File destfile) {
        nullCheck(byteData, destfile);
        if (byteData==null) {
            System.out.println("writeByteArrayToFile被写入的byteData为null!放弃写入返回true"+destfile.toPath());
            return true;
        }
        try {
            FileUtil.writeBytes(byteData, destfile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 追加写入字节数组到磁盘文件中 如果该文件不存在，则创建该文件
     * 
     * @param byteData 字节数组
     * @param destfile     被写入的磁盘文件
     * @return 成功true,失败false
     */
    public static boolean appendWriteBytesToFile(byte[] byteData, File destfile) {
        nullCheck(destfile);
        try {
            FileOutputStream output = new FileOutputStream(destfile, true);
            try {
                output.write(byteData);
            } finally {
                output.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
 
  

}