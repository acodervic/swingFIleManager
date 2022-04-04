package github.acodervic.mod.net.proxy;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.print;
import static github.acodervic.mod.utilFun.sleep;
import static github.acodervic.mod.utilFun.time_nowLong;
import static github.acodervic.mod.utilFun.tryDo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import github.acodervic.mod.Constant;
import github.acodervic.mod.net.HttpUtil;
import okhttp3.CookieJar;

/**
 * PorxyPool
 */
public class PorxyPool {

    // 创建一个单线程顺序池,用来执行三个线程的任务,执行三个任务的先后顺序和定时时间有关
    ScheduledExecutorService delayPool = Executors.newScheduledThreadPool(4);

    // 创建一个单线程顺序池,用来执行三个线程的任务,执行三个任务的先后顺序和定时时间有关
    public static ScheduledExecutorService delayTestPool = Executors.newScheduledThreadPool(8);

    // 所有代理内共享cookie的cookiejar
    CookieJar cookieJar;
    // 存储所有代理节点的map,key=UUID,proxy=实际的代理对象,uuid是唯一的在创建的时候自动生成
    Hashtable<String, Proxy> proxies = new Hashtable<String, Proxy>();
    int MaxRequestsPerHost = 200;
    int MaxRequests = 99999;
    boolean startProxyAvailableRestoreThead = false;// 是否已经启动,代理可用性扫描线程

    /**
     * 设置每个主机要同时执行的最大请求数。这通过URL的主机名限制了请求。请注意，对单个IP地址的并发请求可能仍会超出此限制：多个主机名可能共享一个IP地址或通过同一HTTP代理路由。
     * 如果maxRequestsPerHost调用时正在运行的请求多于请求，则这些请求将继续运行。
     * 
     * @param max
     */
    public void setMaxRequestsPerHost(int max) {
        this.MaxRequestsPerHost = max;
        // 更新
        proxies.values().forEach(proxy -> {
            proxy.getHttpClient().setMaxRequestsPerHost(max);

        });
    }

 
    /**
     *
     * 设置要同时执行的最大请求数。在此请求上方，内存中有队列，等待正在运行的调用完成。
     * 如果maxRequests调用时正在运行的请求多于请求，则这些请求将继续运行 * @param max
     */
    public void setMaxRequests(int max) {
        this.MaxRequests = max;
        // 更新
        proxies.values().forEach(proxy -> {
            proxy.getHttpClient().setMaxRequests(max);

        });
    }

 
    /**
     * 设置cookiejar
     *
     * @return
     */
    public PorxyPool setCookieJar(CookieJar cookieJar) {
        nullCheck(cookieJar);
        this.cookieJar = cookieJar;
        return this;
    }

    /**
     * 获取所有可用代理
     *
     * @return
     */
    public List<Proxy> getAvailablePorxys() {
        List<Proxy> proxys = new ArrayList<Proxy>();
        for (Iterator<String> iterator = this.proxies.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            Proxy proxy = this.proxies.get(key);
            if (proxy.isAvailable()) {
                proxys.add(proxy);
            }
        }
        return proxys;
    }

    /**
     * 获取所有可用代理
     * 
     * @return
     */
    public List<Proxy> getAvailablePorxys(String targetUrl) {
        return getAvailablePorxys(HttpUtil.parseUrl(targetUrl));
    }


    /**
     * 此函数会开启一个任务线程,用于扫描当前proxypool中的所有unavailableTagets,根据value时间撮检查,是否取消不可用状态.3秒钟检查一次
     */
    public void startProxyAvailableRestoreThead() {
        if (startProxyAvailableRestoreThead) {
            return ;
        }
        startProxyAvailableRestoreThead=true;
        print("代理池代理延迟可用性刷新线程已经启动!");
        new Thread(new Runnable(){
            @Override
            public void run() {
                while (true) {
                    proxies.values().forEach(proxy -> {
                        tryDo(() -> {
                            proxy.getUnavailableTagets().forEach((key, value) -> {
                                tryDo(() -> {
                                    if (time_nowLong() > value) {
                                        // 取消不可用
                                        proxy.getUnavailableTagets().remove(key);
                                    }
                                });
                            });
    
                        });
                    });
                    sleep(3000);
                }
            }

        }).start();;


    }

    /**
     * 获取所有可用代理
     * 
     * @return
     */
    public List<Proxy> getAvailablePorxys(URL tagetUrl) {
        List<Proxy> proxys = new ArrayList<Proxy>();
        for (Iterator<String> iterator = this.proxies.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            Proxy proxy = this.proxies.get(key);
            if (proxy.isAvailable(tagetUrl)) {
                proxys.add(proxy);
            }
        }
        // 排序Porxys
        proxys.sort(Comparator.comparing(Proxy::getTargetDelayMs, Comparator.reverseOrder()// 指定
        ));
        return proxys;
    }

    /**
     * 添加一个测试i代理
     * 
     * @param url
     */
    public void addAllPorxyDelay(String url) {
        for (Iterator<String> iterator = this.proxies.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            Proxy proxy = this.proxies.get(key);
            if (proxy.isAvailable()) {
                proxy.getDelays().put(HttpUtil.parseUrl(url).getHost() + ":" + HttpUtil.parseUrl(url).getPort(),
                        new Delay(HttpUtil.parseUrl(url), new Date()));
            }
        }

    }

    /**
     * 添加一个代理
     *
     * @param proxy
     */
    public void addProxy(Proxy proxy) {
        proxy.getHttpClient().setMaxRequestsPerHost(MaxRequestsPerHost);
        proxy.getHttpClient().setMaxRequests(MaxRequests);
        //关联porxypool
        proxy.setProxyPool(this);
        this.proxies.put(proxy.getUuid(), proxy);
        startProxyAvailableRestoreThead();
    }

    /**
     * 对所有代理添加延迟
     */
    public void addDealy(Delay delay) {
        for (Iterator<String> iterator = this.proxies.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            Proxy proxy = this.proxies.get(key);
            proxy.addDelay(delay);
        }
    }

    public void testAllPorxyDelayLoop(int delayTestTime) {
        // 初始化
        // 设置定时测试线程池延迟线程
        new Thread() {
            @Override
            public void run() {
                for (Iterator<String> iterator = proxies.keySet().iterator(); iterator.hasNext();) {
                    String key = iterator.next();
                    Proxy proxy = proxies.get(key);
                    delayPool.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            proxy.testDealys();
                            Constant.dbg("testDealys");
                        }
                    }, 0, delayTestTime, TimeUnit.MILLISECONDS);
                }

                // 输出当前可用代理
                // utilFun.printTable(headers, content);
            }
        }.start();

    }

    /**
     * 根据uuid读取代理
     *
     * @param uuid
     * @return
     */
    public Proxy getPorxy(String uuid) {
        if (this.proxies.containsKey(uuid)) {
            return this.proxies.get(uuid);
        }
        return null;
    }

}