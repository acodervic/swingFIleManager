package github.acodervic.mod.net.proxy;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import github.acodervic.mod.data.toString;
import github.acodervic.mod.net.HttpUtil;
import github.acodervic.mod.net.http.HttpClient;

/**
 * del
 */
public class Delay extends toString implements Serializable {
    URL target;
    int  delayMs=0;
    int timeOut=10000;
    Date testTime;//上一次测试时间
    transient HttpClient delayTestHttpClient;
    int  status=1;//当前状态,-1 针对target无法联通(可能是因为防火墙或者其他原因).1针对target可以正常联通
    int priority=5;//优先级
    //http状态检查器


 
    public boolean isAvailable() {
        if (this.status==1&&this.getDelayMs()!=Proxy.timeOutMax) {
            return true;
        }
        return false;
    }
    public boolean isAvailable(int maxDelayMs) {
        if (this.status==1&&this.getDelayMs()!=Proxy.timeOutMax&&this.getDelayMs()<=maxDelayMs) {
            return true;
        }
        return false;
    }

 

    public int getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

 
    
    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

 
    public HttpClient getDelayTestHttpClient() {
        return delayTestHttpClient;
    }

    public void setDelayTestHttpClient(HttpClient delayTestHttpClient) {
        this.delayTestHttpClient = delayTestHttpClient;
    }
 
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

 
    /**
     * @return the target
     */
    public URL getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(URL target) {
        this.target = target;
    }

    /**
     * @param target
     * @param timeOut
     * @param delayTestHttpClient
     */
    public Delay(URL target, int timeOut, HttpClient delayTestHttpClient) {
        this.target = target;
        this.timeOut = timeOut;
        this.delayTestHttpClient = delayTestHttpClient;
    }

    /**
     * @param target
     * @param testTime
     */
    public Delay(URL target, Date testTime) {
        this.target = target;
        this.testTime = testTime;
    }

    /**
     * @param target
     */
    public Delay(URL target) {
        this.target = target;
    }
    
        /**
     * @param target
     */
    public Delay(String  target) {
        this.target = HttpUtil.parseUrl(target);
    }
}