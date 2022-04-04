package github.acodervic.mod.net.proxy;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.tryDo;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import github.acodervic.mod.data.Value;
import github.acodervic.mod.net.http.RepLogic;

/**
 * 自动代理选择器
 */
public class AutoProxySelector {
    // 当前代理选择器使用的代理池
    PorxyPool proxyPool = null;
    int maxRequestCountSetScope = 20;;

    // 在默认情况下,如果对一个目标请求次数大于50,则,将目标主机视为 范围内主机
    scopes scopes = new scopes();
    // 模式:1.随机可用模式;,2.自定义pac模式,3:auto模式

    /**
     * 返回的代理会保存在每次的请求中,httpclient内部会 进行检查和重试,注意此函数不会返回null,最多返回长度为0的集合
     */
    public List<Proxy> select(URI uri) {
        if (scopes.addCount(uri.getHost()).getInt() > maxRequestCountSetScope) {
            tryDo(() -> {
                // 针对同一请求已大于80此自动加入目标
                proxyPool.addAllPorxyDelay(uri.toURL().toString());
            });
        }
        try {

            // 读取可用代理
            // 注意为了保证每次代理尽可能的不顺序重复,因为请求的时候会循环调用,我们将会处理并打散代理顺序
            List<Proxy> availablePorxys = proxyPool.getAvailablePorxys(uri.toURL());
            Collections.shuffle(availablePorxys);
            return availablePorxys;
        } catch (Exception e) {
            e.printStackTrace();

            // 出现异常则返回空的代理
            return new ArrayList<Proxy>();
        }
    }

    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        // 则设置代理池某个代理不可用不可用

    }

    /**
     * 当代理成功连接并返回响应后会回调
     * 
     * @param rep
     */
    public boolean connectSucess(RepLogic rep, Proxy proxy) {
        // 进行httpchecker的检查,然后httpclient内部会半段是否进行重试,如果返回false则会进行下一个代理的重试
        return false;
    }

    /**
     * @param proxyList
     */
    public AutoProxySelector(PorxyPool proxyPool) {
        nullCheck(proxyPool);
        this.proxyPool = proxyPool;
    }

    public void req() {

    }

    /**
     * scopes
     */
    public class scopes {
        // 用于统计某个目标的请求计数情况
        Hashtable<String, Value> requestCount = new Hashtable<String, Value>();

        /**
         * 某个目标添加一次记录,返回对当前目标的请求次数
         * 
         * @param host
         * @return
         */
        public Value addCount(String host) {
            nullCheck(host);
            if (requestCount.containsKey(host)) {
                Value value = requestCount.get(host);
                value.setValue(value.getInt() + 1);
            } else {
                requestCount.put(host, new Value(1));
            }
            return requestCount.get(host);
        }

        /**
         * 获取针对某目标的请求数
         * 
         * @param host
         * @return
         */
        public int getCountByHost(String host) {
            if (requestCount.containsKey(host)) {
                return requestCount.get(host).getInt();
            }
            return 0;
        }

    }


}