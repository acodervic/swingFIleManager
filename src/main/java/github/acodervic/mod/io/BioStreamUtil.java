package github.acodervic.mod.io;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import github.acodervic.mod.Constant;
import github.acodervic.mod.data.ByteLIst;
import github.acodervic.mod.data.CharUtil;

/**
 * byteStream,用于从本地磁盘或远程磁盘中读取字节数据
 */
public class BioStreamUtil {
 
        /**
         * 读取输入流为字符串
         * 
         * @param in          输入流
         * @param charSet_opt 字符串编码 (可选null=)默认为utf8
         * @return
         * @throws IOException
         */
        public static String readInputStreamToString(InputStream in, String charSet_opt) throws IOException {
                nullCheck(in);
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                }
                String set = StandardCharsets.UTF_8.name();
                if (charSet_opt != null) {
                        set = charSet_opt;
                }
                return result.toString(set);
        }

        /**
         * 读取输入流为字节列表,读取失败则返回一个长度为0的字节列表
         * 
         * @param in 输入流
         * @return 字节列表
         */
        public static ByteLIst readInputStreamToBytes(InputStream in) {
                nullCheck(in);
                int length;
                ByteLIst data = new ByteLIst();
                try {
                        byte[] buffer = new byte[in.available()];
                        in.read(buffer);
                        data.put(buffer);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return data;

        }

        /**
         * 读取输入流为行列表
         * 
         * @param in          输入流
         * @param charSet_opt 字符串编码 (可选null=)默认为utf8
         * @return 行列表
         * @throws IOException
         */
        public static List<String> readInputStreamToLines(InputStream in, String charSet_opt) throws IOException {
                List<String> data = new ArrayList<String>();
                String set = Constant.defultCharsetStr;
                if (charSet_opt != null) {
                        set = charSet_opt;
                }
                try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String str = null;
                        while (true) {
                                str = CharUtil.toCharSet(reader.readLine(), set);
                                if (str != null)
                                        data.add(str);
                                else
                                        break;
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }
                return data;
        }

}