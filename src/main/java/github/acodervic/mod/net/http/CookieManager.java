package github.acodervic.mod.net.http;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import github.acodervic.mod.data.str;
import github.acodervic.mod.net.HttpUtil;
import github.acodervic.mod.shell.SystemUtil;
import okhttp3.Cookie;
import okhttp3.Cookie.Builder;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * SetCookiejar,此cookiejar 用于,从http响应中读取setcookie
 */
public class CookieManager {
    static  ArrayList<Cookie> cookies=new ArrayList<Cookie>();
    int logModel = 0;// 从http继承的日志模式

    /**
     * 根据url读取cookie,默认读取host,优先使用domain!
     * @param url
     * @return
     */
    public List<Cookie> getCookie(String url) {
        List<Cookie> cs=new ArrayList<Cookie>();
        URL u= HttpUtil.parseUrl(url);
         for (Cookie cookie : cookies) {
             if (cookie.domain().equals(u.getHost())) {
                 cs.add(cookie);
             }
         }
         return cs;
    }
    //用于扫描set-cookie响应中的cookie字段
     Interceptor setCookieInterceptor=new Interceptor(){
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            //扫描set-cookie
            if (originalResponse!=null) {
 
                if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                    for (String  setcookies : originalResponse.headers("Set-Cookie")) {
                         if (logModel == 2 && SystemUtil.isDebuggerAttached()) {
                            System.out.println("从url:"+chain.request().url().toString()+"  读取到set-cookie:"+setcookies);
                         } else if (logModel == 1) {
                             System.out.println(
                                     "从url:" + chain.request().url().toString() + "  读取到set-cookie:" + setcookies);
                         }
                        //对setCookie进行拆分和处理
                        new str(setcookies).split(";").forEach(cookie->{
                            try {
                                List<str> nameAndValue = cookie.split("=");
                                if (nameAndValue.size()>1) {
                                    String name=nameAndValue.get(0).toString();
                                    String value=nameAndValue.get(1).toString();
                                    Builder c = new Cookie.Builder();

                              /**
                               *   //读取 等特殊属性
                                if (name.trim().toUpperCase().equals("PATH")) {
                                    c.path(value);
                                }else if (name.trim().toUpperCase().equals("EXPIRES")) {
                                    c.expiresAt(Long.parseLong(value));
                                }else{
                               */
                                    //自定义的cookie
                                    c.name(name.trim()).value(value.trim());
                            /**
                             *     }
                             */
                                //设置host
                                c.domain(chain.request( ).url().host());
                                //添加到cookie
                                cookies.add(c.build());
                                }

                            } catch (Exception e) {
                                System.out.println("处理cookie异常!");
                                e.printStackTrace();
                            }

                        });
                    };   
                }
             }
            return originalResponse;//返回响应
        }
    };

    /**
     * @return the cookies
     */
    public static List<Cookie> getCookies() {
        return cookies;
    }

 

    /**
     * @return the setCookieInterceptor
     */
    public Interceptor getSetCookieInterceptor() {
        return setCookieInterceptor;
    }

    /**
     * @param setCookieInterceptor the setCookieInterceptor to set
     */
    public void setSetCookieInterceptor(Interceptor setCookieInterceptor) {
        this.setCookieInterceptor = setCookieInterceptor;
    }

    /**
     * @return the logModel
     */
    public int getLogModel() {
        return logModel;
    }

    /**
     * @param logModel the logModel to set
     */
    public void setLogModel(int logModel) {
        this.logModel = logModel;
    }

}