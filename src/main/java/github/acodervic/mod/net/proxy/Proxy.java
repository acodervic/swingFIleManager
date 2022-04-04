package github.acodervic.mod.net.proxy;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.http_getUnsafeOkHttpClientWithPorxy;
import static github.acodervic.mod.utilFun.tryDo;

import java.io.IOException;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import github.acodervic.mod.Constant;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.data.str;
import github.acodervic.mod.net.HttpUtil;
import github.acodervic.mod.net.http.HttpClient;
import github.acodervic.mod.net.http.Method;
import github.acodervic.mod.shell.SystemUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.Response;
 
/**
 * proxy
 */
public class Proxy {
    public static int timeOutMax = 999999;
    String uuid = SystemUtil.getUUID().toString();
    Integer connTimeOut;
    Type proxyType;
    str proxyClass = new str("");// 代理类,如shaodows
    String host;
    Integer port;
    String username;
    String password;
    transient PorxyPool proxyPool;// 父proxyPool
    // 目标延迟对象,主要是记录当前代理节点,到目标服务器的请求延迟.key=host:port , value=用来描述延迟时间的对象
    Map<String, Delay> delays = new Hashtable<String, Delay>();
    // 不可用的目标,用户通过手动设置 host:host 来告知当前porxy对于特定目标不可用,此集合比delays的优先级高,key=host:port
    // value为取消封禁的延迟时间戳,时间点到之后会被用于定时清除
    Map<String, Long> unavailableTagets = new Hashtable<String, Long>(); // 后台会有线程自动清除状态

    int status = 1;// 可用, 0 不可用
    int connectionCount = -1;// 当前连接数量
    int averageConnectionDelay;// 平均连接延迟
    int targetDelayMs;
    transient CookieJar cookieJar;// 引用proxyPool中的cookieJar,所有的httpclient都引用同一个,目的是共享cookie
    transient Object attachObject;// 一个附加对象,用于存储附加内容
    transient HttpClient httpClient;

    /**
     * 判断当前代理是否可用
     *
     * @return
     */
    public boolean isAvailable() {
        if (this.status == 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前代理针对某i一主机是否可用
     *
     * @return
     */
    public boolean isAvailable(String url) {
        nullCheck(url);
        return isAvailable(HttpUtil.parseUrl(url));
    }


    /**
     * 判断当前代理针对某i一主机是否可用
     *
     * @return
     */
    public boolean isAvailable(URL url) {
        String key = url.getHost() + ":" + url.getPort();
        if (!this.isAvailable()) {
            return false;
        }
        // 手动检测设置
        if (this.unavailableTagets.containsKey(key)) {
            return false;
        }

        // 检测延迟检查的结果
        if (this.delays.containsKey(key)) {
            // 判断是否有主机
            Delay delay = this.delays.get(key);
            if (delay.isAvailable()) {
                return true;
            } else {
                return false;
            }
        }
        // 则默认返回true
        return true;
    }

    /**
     * @return Type
     */
    public Type getProxyType() {
        return proxyType;
    }

    /**
     * @param proxyType
     */
    public void setProxyType(Type proxyType) {
        this.proxyType = proxyType;
    }

    /**
     * @return String
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return Integer
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return String
     */
    public String getUser() {
        return username;
    }

    /**
     * @param username
     */
    public void setUser(String username) {
        this.username = username;
    }

    /**
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Map<String, Delay>
     */
    public Map<String, Delay> getDelays() {
        return this.delays;
    }

    /**
     * @param delays
     */
    public void setDelays(Map<String, Delay> delays) {
        this.delays = delays;
    }

    public Proxy(Type proxyType, String host, Integer port, String username, String password) {
        this.proxyType = proxyType;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        initHttpClient();
    }

    public Proxy(Type proxyType, String host, Integer port, String username, String password, int targetDelayMs) {
        this.proxyType = proxyType;
        this.host = host;
        this.targetDelayMs = targetDelayMs;
        this.port = port;
        this.username = username;
        this.password = password;
        initHttpClient();
    }

    /**
     * 初始化http客户端
     */
    public void initHttpClient() {
        tryDo(() -> {
            this.httpClient = http_getUnsafeOkHttpClientWithPorxy(proxyType, host, port,
                    Constant.httpTimeOut.intValue());
            if (this.cookieJar != null) {
                // 设置cookiejar
                this.httpClient.setCookieJar(this.cookieJar);

            }
        });
    }

    /**
     * @param delay
     */
    public void addDelay(Delay delay) {
        this.getDelays().put(delay.getTarget().getHost() + ":" + delay.getTarget().getPort(), SystemUtil.clone(delay));
    }

    /**
     * @return HttpClient
     */
    public HttpClient getHttpClient() {
        if (this.httpClient == null) {
            httpClient = HttpClient.getUnsafeOkHttpClientWithPorxy(this.proxyType, this.host, this.port,
                    this.connTimeOut.intValue(), this.username, this.password);
        }
        return httpClient;
    }

    /**
     * @param httpClient
     */
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * @return Integer
     */
    public Integer getConnTimeOut() {
        return connTimeOut;
    }

    /**
     * @param connTimeOut
     */
    public void setConnTimeOut(Integer connTimeOut) {
        this.connTimeOut = connTimeOut;
    }

    /**
     * 设置代理针对莫一目标不可用
     */
    public void setUnavailable(String url) {
        setUnavailable(HttpUtil.parseUrl(url));
    }

    /**
     * 强制设置代理针对某一目标不可用
     * 
     * @param url        url
     * @param expireTIme 到期时间点,单位秒,之后过期
     */
    public void setForceUnavailable(String url, int expireTIme) {
        nullCheck(url, expireTIme);
        URL parseUrl = HttpUtil.parseUrl(url);
        String key = parseUrl.getHost() + ":" + parseUrl.getPort();
        if (unavailableTagets.containsKey(url)) {
            // 删除
            unavailableTagets.remove(key);
        }
        // 重新设置
        unavailableTagets.put(key, Long.parseLong(System.currentTimeMillis() + "" + (expireTIme * 1000)));
    }

    /**
     * 设置代理针对莫一目标不可用
     */
    public void setUnavailable(URL url) {
        String key = url.getHost() + ":" + url.getPort();
        // 找到延迟的状态并设置为-1
        if (this.getDelays().containsKey(key)) {
            // 设置为-1
            this.getDelays().get(key).setStatus(-1);
        }
    }

    public void testDealys() {
        // 定时测试代理
        Set<String> delayTargetHosts = getDelays().keySet();

        for (String delayTargetHost : delayTargetHosts) {
            PorxyPool.delayTestPool.submit(new Runnable() {
                @Override
                public void run() {
                    Delay delay = getDelays().get(delayTargetHost);
                    // 对delayTargetHost进行测试
                    long timeStart = utilFun.time_nowLong();
                    HttpClient delayHttpClient = delay.getDelayTestHttpClient();
                    if (delayHttpClient == null) {
                        delayHttpClient = utilFun.http_getUnsafeOkHttpClientWithPorxy(getProxyType(), getHost(),
                                getPort(), delay.getDelayMs());
                        delay.setDelayTestHttpClient(delayHttpClient);
                    }
                    Constant.dbg("发送http" + delay.getTarget());
                    delayHttpClient.asyncReq(delay.getTarget().toString(), Method.GET, null, null, new Callback() {
                        @Override
                        public void onResponse(Call arg0, Response arg1) throws IOException {
                            Constant.dbg("得到返回");
                            // 链接成功
                            long timeEnd = utilFun.time_nowLong();
                            // 重新设置延迟
                            delay.setDelayMs((int) (timeEnd - timeStart));

                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                            delay.setDelayMs(timeOutMax);
                            Constant.dbg("请求失败！" + arg1);
                        }
                    });
                }
            });

        }

    }

    /**
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return int
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return int
     */
    public int getConnectionCount() {
        return connectionCount;
    }

    /**
     * @param connectionCount
     */
    public void setConnectionCount(int connectionCount) {
        this.connectionCount = connectionCount;
    }

    /**
     * @return int
     */
    public int getAverageConnectionDelay() {
        return averageConnectionDelay;
    }

    /**
     * @param averageConnectionDelay
     */
    public void setAverageConnectionDelay(int averageConnectionDelay) {
        this.averageConnectionDelay = averageConnectionDelay;
    }

    /**
     * @return int
     */
    public static int getTimeOutMax() {
        return timeOutMax;
    }

    /**
     * @param timeOutMax
     */
    public static void setTimeOutMax(int timeOutMax) {
        Proxy.timeOutMax = timeOutMax;
    }

    /**
     * @return int
     */
    public int getTargetDelayMs() {
        return targetDelayMs;
    }

    /**
     * @param targetDelayMs
     */
    public void setTargetDelayMs(int targetDelayMs) {
        this.targetDelayMs = targetDelayMs;
    }

    /**
     * @param url
     * @return Delay
     */
    public Delay getDelay(String url) {
        URL target = HttpUtil.parseUrl(url);
        String key = target.getHost() + ":" + target.getPort();
        if (this.delays.containsKey(key)) {
            return this.delays.get(key);
        }
        return null;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the proxyClass
     */
    public str getProxyClass() {
        return proxyClass;
    }

    /**
     * @param proxyClass the proxyClass to set
     */
    public void setProxyClass(String proxyClass) {
        this.proxyClass = new str(proxyClass);
    }

    /**
     * @return the attachObject
     */
    public Object getAttachObject() {
        return attachObject;
    }

    /**
     * @param attachObject the attachObject to set
     */
    public void setAttachObject(Object attachObject) {
        this.attachObject = attachObject;
    }

    /**
     * @return the unavailableTagets
     */
    public Map<String, Long> getUnavailableTagets() {
        return unavailableTagets;
    }

    /**
     * @param unavailableTagets the unavailableTagets to set
     */
    public void setUnavailableTagets(Map<String, Long> unavailableTagets) {
        this.unavailableTagets = unavailableTagets;
    }

    /**
     * @return the proxyPool
     */
    public PorxyPool getProxyPool() {
        return proxyPool;
    }

    /**
     * @param proxyPool the proxyPool to set
     */
    public void setProxyPool(PorxyPool proxyPool) {
        this.proxyPool = proxyPool;
    }
}