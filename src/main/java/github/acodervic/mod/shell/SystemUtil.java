package github.acodervic.mod.shell;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.en_strToBase64Str;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.net.Proxy.Type;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSONObject;
import github.acodervic.mod.Constant;
import github.acodervic.mod.data.ArrayUtil;
import github.acodervic.mod.data.BaseUtil;
import github.acodervic.mod.data.ByteLIst;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.NumberUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.TimeUtil;
import github.acodervic.mod.data.str;
import github.acodervic.mod.data.list.AList;
import github.acodervic.mod.data.map.HMap;
import github.acodervic.mod.interfaces.DoOneJob;
import github.acodervic.mod.io.FileReadUtil;
import github.acodervic.mod.io.IoStream;
import github.acodervic.mod.msic.translate.baiduSDK.TransApi;
import github.acodervic.mod.net.http.HttpClient;
import github.acodervic.mod.net.http.Method;
import github.acodervic.mod.shell.misc.YoudaoApiTran;
import okhttp3.Response;

/**
 * exec
 */
public class SystemUtil {
    static Hashtable<String, String> translatedMap = new Hashtable<String, String>();// 已经被翻译过的句子表格
    static HttpClient def_hClient_opt = HttpClient.getUnsafeOkHttpClient(Constant.httpTimeOut);

    /**
     * 同步执行shell命令,等待执行完毕后返回执行的结果,如果执行错误返回null
     *
     * @param workDir 工作目录
     * @param commond 命令
     * @return 执行结果字符串
     */
    public static String syncExecShellString(DirRes workDir, String... commond) {
        if (isLinux()) {
            try {
                List<String> cmdAs = new ArrayList<String>();
                cmdAs.add("/bin/sh");
                cmdAs.add("-c");
                for (String cmd : commond) {
                    cmdAs.add(cmd);
                }
                Process process = null;
                if (workDir != null) {
                    process = Runtime.getRuntime().exec(cmdAs.toArray(new String[0]), null, workDir.getDir());
                } else {
                    process = Runtime.getRuntime().exec(cmdAs.toArray(new String[0]));
                }
                LineNumberReader br = new LineNumberReader(new InputStreamReader(process.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                try {
                    br.close();
                    process.exitValue();
                    process.destroy();
                } catch (Exception e) {
                    // e.printStackTrace();
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            // windows
            return "";
        }
    }

    /**
     * 同步执行shell
     *
     * @param commString
     * @return
     */
    public static String syncExecShellString(String commString) {
        String[] coms = { commString };
        return syncExecShellString(null, coms);
    }

    /**
     * 同步执行shell
     *
     * @param workDir    工作目录i
     * @param commString
     * @return
     */
    public static String syncExecShellString(DirRes workDir, String commString) {
        String[] coms = { commString };
        return syncExecShellString(workDir, coms);
    }

    /**
     * 异步执行shell命令,返回一个进程对象,如果执行错误返回null
     * 
     * @param commond 命令
     * @return 返回执行的进程对象
     */
    public static Process asyncExecShell(String commond) {
        if (isLinux()) {
            return asyncExecShell(null, ArrayUtil.newArray(commond));
        } else {
            // windows
            return null;
        }
    }

    /**
     * 异步执行shell命令,返回一个进程对象,如果执行错误返回null
     *
     * @param commond 命令
     * @return 返回执行的进程对象
     */
    public static Process asyncExecShell(DirRes workDir, String... commond) {
        if (isLinux()) {
            try {
                List<String> cmdAs = new ArrayList<String>();
                cmdAs.add("/bin/sh");
                cmdAs.add("-c");
                for (String cmd : commond) {
                    cmdAs.add(cmd);
                }
                if (workDir != null) {
                    return Runtime.getRuntime().exec(cmdAs.toArray(new String[0]), new String[0], workDir.getDir());
                } else {
                    return Runtime.getRuntime().exec(cmdAs.toArray(new String[0]));
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // windows
            return null;
        }
    }

    /**
     * 杀死进程
     *
     * @param process
     */
    public static void killProcess(Process process) {
        nullCheck(process);
        process.destroy();
    }

    /**
     * 杀死进程
     *
     * @param pid
     * @return
     */
    public static boolean killProcess(Integer pid) {
        try {
            if (isLinux()) {
                String[] comm = { "kill", "-9", pid + "" };
                if (asyncExecShell(null, comm) != null) {
                    // 判断进程是否被杀死
                }
                ;
                Thread.sleep(300);
                // 判断是否成功杀死
                // String[] isActive = { "ps aux | grep " + pid + " | awk '{print $2}'" };

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 终止程序
     */
    public static void exit() {
        System.exit(0);
    }

    /**
     * 休眠当前线程
     * 
     * @param mm 毫秒
     */
    public static void sleep(int mm) {
        try {
            Thread.sleep(mm);
        } catch (Exception e) {
            System.out.println(
                    "线程被外界通知中断!但无法中断.因为线程为sleep状态,如果有外部函数进行终止请求调用,请使用原生Thread.sleep,函数并捕捉中断异常!或者使用sleep(int mm, @NonNull Consumer<Exception> catchError!");
            e.printStackTrace();
        }
    }

    /**
     * 休眠当前线程
     *
     * @param mm              毫秒
     * @param catchError_topt 当线程发生异常时候的处理函数,一般在线程池任务请求中断的时候发生异常
     */
    public static void sleep(int mm, Consumer<Exception> catchError_topt) {
        try {
            Thread.sleep(mm);
        } catch (Exception e) {
            catchError_topt.accept(e);
        }
    }

    /**
     * 杀死进程,根据一个正则字符串,成功返回true,失败返回false
     * 
     * @param porcessName 字符串
     */
    public static boolean killProcess(String porcessName) {
        try {
            if (isLinux()) {
                String[] comm = { "ekill-noWarning", porcessName };
                if (asyncExecShell(null, comm) == null) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断是linux系统还是其他系统 如果是Linux系统，返回true，否则返回false
     */
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0;
    }

    /**
     * 获取当前用户的路径
     *
     * @return
     */
    public static String getMyDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取当前用户的路径
     *
     * @return
     */
    public static String getUserDir() {
        return getMyDir();
    }

    public static String getCurrentWorkingDirectoryPath() {
        return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
    }

    /**
     * 获得临时文件目录的路径 /tmp/
     * 
     * @return
     */
    public static String tmpDirName() {
        String tmpDirName = System.getProperty("java.io.tmpdir");
        if (!tmpDirName.endsWith(File.separator)) {
            tmpDirName += File.separator;
        }

        return tmpDirName;
    }

    /**
     * 检查JVM是否为debug模式。
     * 
     * @return
     */
    public static boolean isDebuggerAttached() {
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString()
                .indexOf("jdwp") >= 0;
        return isDebug;

    }

    /**
     * 获取当前进程用户
     *
     * @return
     */
    public static String getMyUser() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return processName.substring(processName.indexOf('@') + 1, processName.length());
    }

    /**
     * 获取当前进程的pid
     * 
     * @return
     */
    public static Integer getMyPid() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return NumberUtil.toInt(processName.substring(0, processName.indexOf('@')));
    }

    static String GOOGLE_URL = "https://translation.googleapis.com/language/translate/v2?target=zh&key=AIzaSyAYbyC6hP5yt9BOtek6RJZxUheYd230yQU&q=";
    static String CIBA_URL = "http://fy.iciba.com/ajax.php?a=fy&q=data&f=auto&t=auto&w=";


    /**
     * 有到翻译.翻译错误返回null
     * @param str_opt
     * @return
     */
    public static str youdaoTran(String str_opt) {
        // 已经翻译过
        if (translatedMap.containsKey(str_opt)) {
            return new str(translatedMap.get(str_opt));
        }
        str chinses = YoudaoApiTran.toChinses(str_opt);
        translatedMap.put(str_opt, chinses.to_s());
        return chinses;
    }

    static TransApi baiduTran = new TransApi("20200610000491698", "qJw_J81bkOPrenmzMKBN");

    /**
     * 百度翻译
     * 
     * @param data
     * @param hClient_opt
     * @return
     */
    public static str baiduAPITran(String data, HttpClient hClient_opt) {
        if (data == null) {
            return new str("");
        }
        // 已经翻译过
        if (translatedMap.containsKey(data)) {
            return new str(translatedMap.get(data));
        }
        String transResult = baiduTran.getTransResult(data);
        if (transResult != null && transResult.length() > 0) {
            translatedMap.put(data, transResult);
            return new str(transResult);
        }
        return new str(data);
    }
    /**
     * 词霸翻译,翻译失败则返回null
     * @param data
     * @param hClient_opt
     * @return
     */
    public static str cibaAPItran(String data, HttpClient hClient_opt) {
        nullCheck(data);
        if (data == null || "".equals(data)) {
			return new str("");
		}
    try {
        // 已经翻译过
        if (translatedMap.containsKey(data)) {
            return new str(translatedMap.get(data));
        }
        if (hClient_opt == null) {
            hClient_opt = def_hClient_opt;
        }
        str parseUnicode = hClient_opt.syncReq(CIBA_URL + data, Method.GET, null, null).bodyString().decodeUnicode();
        JSONObject json = parseUnicode.toJsonObject();
        str str = new str(json.getStr("status"));
        if (str.eq("1")) {
            github.acodervic.mod.data.str str2 = new str(json.getJSONObject("content").getStr("out").toString());
            translatedMap.put(data, str2.to_s());
            return str2;
        }else if(str.eq("0")){
            github.acodervic.mod.data.str str2 = new str(json.getJSONObject("content").getJSONArray("word_mean").get(0).toString());
            translatedMap.put(data, str2.to_s());
            return str2;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
        // 翻译失败
        return new str("");
    }

    /**
     * google翻译
     *
     * @param data        被翻译的数据
     * @param hClient_opt 请求的http客户端(可选,null=直连)
     * @return
     */
    public static String googleAPItran(String data, HttpClient hClient_opt) {
        nullCheck(data);
        if (data == null || "".equals(data)) {
			return "";
        }
        // 已经翻译过
        if (translatedMap.containsKey(data)) {
            return translatedMap.get(data);
        }
        if (hClient_opt == null) {
            hClient_opt = def_hClient_opt;
        }

        HMap<String, String> headers = new HMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("user-agent",
                "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Mobile Safari/537.36','Accept-Language':'zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        Response response = hClient_opt.syncReq(GOOGLE_URL + data, Method.GET, headers, null).getRawOkHttpRepsone();
        JSONObject tranJson;
        try {
            tranJson = JSONUtil.jsonToJsonObj(response.body().string());
            String string = tranJson.getJSONObject("data").getJSONArray("translations").getJSONObject(0)
                    .getStr("translatedText");
            translatedMap.put(data, string);
            return string;
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * 翻译文本数据
     * 
     * @param data
     * @param hClient_opt
     * @return
     */
    public static String translate(String data, HttpClient hClient_opt) {
        nullCheck(data);
        if (data.length() < 3) {
            return data;
        }
        if (hClient_opt == null) {
            hClient_opt = def_hClient_opt;
        }

        // 已经翻译过
        if (translatedMap.containsKey(data)) {
            return translatedMap.get(data);
        }
        try {
            String string = hClient_opt
                    .syncReq("http://127.0.0.1:56782/tran?data=" + en_strToBase64Str(data, null), Method.GET, null,
                            null)
                    .bodyString().toString();
            if (string.length() > 1) {
                translatedMap.put(data, string);
            }
            return string.replaceAll("。", ".").replaceAll("，", ",").replaceAll("）", ")").replaceAll("（", "(")
                    .replaceAll("“", "\"").replaceAll("”", "\"").replaceAll("；", ";").replaceAll("‘", "'")
                    .replaceAll("　", " ").replaceAll("’", "'").replaceAll("！", "!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 读取本地网卡接口列表
     * 
     * @return
     */
    public static List<String> getNetworkAddressList() {
        List<String> result = new ArrayList<String>();
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                        result.add(ip.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取ip位置
     *
     * @param ip
     * @return
     */
    public static String getIpLocation(String ip) {
        if (ip == null) {
            System.out.println("ip为空!");
            return null;
        }
        try {
            String u = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?query=" + ip
                    + "&co=&resource_id=6006&t=1433922612109&ie=utf8&oe=gbk&cb=op_aladdin_callback&format=json&tn=baidu&cb=jQuery110206955700272228569_1433922418817&_=1433922418822";
            Long starTime = TimeUtil.getNowLong();
            String data = FileReadUtil.readUrlToStr(u, def_hClient_opt, null, "gbk");
            System.out.println("获取ip归属地耗时" + (TimeUtil.getNowLong() - starTime));
            JSONObject json = JSONUtil.jsonToJsonObj(data.substring(data.indexOf("(") + 1, data.indexOf(")")));

            return json.getJSONArray("data").getJSONObject(0).getStr("location");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取系统文件分割符
     */
    public static String getSystemSeparator() {
        return File.separator;
    }


    /**
     * 生成一个uuid
     * 
     * @return
     */
    public static UUID getUUID() {
        return UUID.randomUUID();
    }

    /**
     * 将域名转换为ip地址
     * 
     * @param domain 域名
     * @return
     */
    public static String parseDomainToIp(String domain) {
        try {
            return InetAddress.getByName(domain).getHostAddress();
        } catch (UnknownHostException e) {

            e.printStackTrace();
            return null;
        }
    }




    /**
     * 克隆对象,被克隆的对象和其属性对象必须实现Serializable接口,对于不想被反序列化的对象则使用transient修饰
     * 
     * @param <T>
     * @param obj
     * @return
     */
    public static <T extends Serializable> T clone(T obj) {
        T cloneObj = null;
        try {
            // 写入字节流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();

            // 分配内存,写入原始对象,生成新对象
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());// 获取上面的输出字节流
            ObjectInputStream ois = new ObjectInputStream(bais);

            // 返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }

    /**
     * 当接收到ctrl_c命令行的时候执行操作
     * 
     * @param task
     */
    public static void onCtrlC(DoOneJob task) {
        nullCheck(task);
        sun.misc.SignalHandler handler = new sun.misc.SignalHandler() {
            @Override
            public void handle(sun.misc.Signal signal) {
                // 执行任务
                System.out.println("接收到ctrl");
                task.doJob();

            }
        };
        // 设置INT信号(Ctrl+C中断执行)交给指定的信号处理器处理，废掉系统自带的功能
        sun.misc.Signal.handle(new sun.misc.Signal("INT"), handler);
    }


    /**
     * 读取jar内部文件
     * @param jarFilePath
     * @return
     */
    public static byte[] getJarLibFile(String jarFilePath) {
        nullCheck(jarFilePath);
        try {
            return IoStream.readSteamOne(SystemUtil.class.getResource(jarFilePath).openStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseUtil.nullByteArry();
    }

    /**
     * 显示通知
     * 
     * @param title
     * @param message
     */
    public static void notify(String title, String message) {
        nullCheck(title, message);
        if (isLinux()) {
            syncExecShellString(
                    "notify-send  '" + title.replaceAll("'", "") + "'   '" + message.replaceAll("'", "") + "'");
        }
    }

    public void getTmpDir() {

    }

    /**
     * 判断地址是否为私有ip
     * 
     * @param ipAddress
     * @return
     */
    public boolean isPrivateIP(String ipAddress) {
        return NetUtil.isInnerIP(ipAddress);
    }

    /**
     * 读取jar中的文件
     * 
     * @param path
     * @return
     */
    public static ByteLIst getJarResources(String path) {
        nullCheck(path);
        try {
            return new ByteLIst(IoStream.readSteamOne(SystemUtil.class.getClassLoader().getResourceAsStream(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteLIst();
    }

    /**
     * 判断端口是否开放
     * 
     * @param hots
     * @param port
     * @param timeOut
     * @return
     */
    public static boolean portIsOpen(String hots, int port, int timeOut) {
        return NetUtil.isOpen(new InetSocketAddress(hots, port), timeOut);
    }

    /**
     * 获取一个本机可用的端口.大于1000
     * 
     * @return
     */
    public static int getAvailablePort() {
        return NetUtil.getUsableLocalPort(1001);
    }

    /**
     * 把文本设置到剪贴板（复制）
     */
    public static void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }

    /**
     * 从剪贴板中获取文本（粘贴）
     */
    public static String getClipboardString() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 异步执行shell命令,返回一个进程对象,如果执行错误返回null
     *
     * @param commond 命令
     * @return 返回执行的进程对象
     */
    public static Opt<Process> startPorcess(String... commond) {
        Opt<Process> ret = new Opt<Process>();
        try {
            return ret.of(Runtime.getRuntime().exec(commond));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

}