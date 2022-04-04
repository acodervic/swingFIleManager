package github.acodervic.mod.net.http;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.parseCharsetStr;
import static github.acodervic.mod.utilFun.file_name;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import github.acodervic.mod.Constant;
import github.acodervic.mod.data.ByteLIst;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.data.map.HMap;
import github.acodervic.mod.net.HttpUtil;
import github.acodervic.mod.net.proxy.AutoProxySelector;
import github.acodervic.mod.net.proxy.PorxyPool;
import github.acodervic.mod.net.proxy.Proxy;
import github.acodervic.mod.shell.SystemUtil;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.Credentials;
import okhttp3.Dns;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okio.Buffer;
import okio.BufferedSource;


/**
 * HttpClient import rawhttp.core.RawHttpRequest;
 */
public class HttpClient {

    private OkHttpClient OkHttpClient = null;
    private CookieJar cookieJar_opt = null;
    private static CookieManager cookieManager = new CookieManager();
    private boolean printStackTrace = true;
    private AutoProxySelector autoProxySelector_opt;// 代理选择器,内部和proxyPool进行绑定,如果设置了autoProxySelector_opt,则会进行自动的响应检查和重试
    private PorxyPool proxyPool=null;//使用自动代理选择器的模式的时候绑定的代理池,用于读取httpStatusChecker
    private boolean allowDirectConnection = true;// 在代理不可用,或没有可用代理的时候运行直接连接
    private int logModel = 0; // 0关闭 1 手动log 2 自动log(如果在调试模式下自动开启log)

    public void PrintStackTrace() {
        this.printStackTrace = true;
    }

    public void dontPrintStackTrace() {
        this.printStackTrace = false;
    }

    public boolean needPrintStackTrace() {
        return printStackTrace;
    }



    /**
     * 关闭所有空闲连接,注意这对autoProxySelector模式下的客户端无效
     */
    public void closeIdleSocket() {
        OkHttpClient.connectionPool().evictAll();
    }

    /**
     * 设置针对单个主机的运行并发数,注意这对autoProxySelector模式下的客户端无效
     * 
     * @param max
     */
    public void setMaxRequestsPerHost(int max) {
        this.OkHttpClient.dispatcher().setMaxRequestsPerHost(max);
    }

    /**
     * 设置最大请求数量,,注意这对autoProxySelector模式下的客户端无效
     * 
     * @param max
     */
    public void setMaxRequests(int max) {
        this.OkHttpClient.dispatcher().setMaxRequests(max);
    }

    /**
     * 内部静态方法使用的构造函数
     *
     * @param OkHttpClient
     */
    public HttpClient(OkHttpClient OkHttpClient) {
        this.OkHttpClient = OkHttpClient;
    }

    /**
     * 通过AutoProxySelector来构造httpclient,在请求的时候httpclient会自动从AutoProxySelector中动态读取
     *
     * @param OkHttpClient
     */
    public HttpClient(AutoProxySelector autoProxySelector) {
        nullCheck(autoProxySelector);
        this.autoProxySelector_opt = autoProxySelector;
        //构建一个支持自动代理拦截器的okhttpClient
        try {
            this.OkHttpClient = getUnsafeOkHttpClient(Constant.httpTimeOut).getOkHttpClient();
            // 创建一个不验证证书链的信任管理器
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
             @Override
             public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                     throws CertificateException {
             }

             @Override
             public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                     throws CertificateException {
             }

             @Override
             public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                 return new java.security.cert.X509Certificate[] {};
             }
         } };

            // 将忽略SSL的证书管理器安装到SSL上下文中
         final SSLContext sslContext = SSLContext.getInstance("SSL");
         sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
         // Create an ssl socket factory with our all-trusting manager
         final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

         OkHttpClient.Builder builder = new OkHttpClient.Builder();
         builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
         builder.hostnameVerifier(new HostnameVerifier() {
             @Override
             public boolean verify(String hostname, SSLSession session) {
                 return true;
             }
         });
         addProxySelectorInterceptor(builder);

         this.OkHttpClient=builder.connectTimeout(Constant.httpTimeOut, TimeUnit.MILLISECONDS)
         .readTimeout(Constant.httpTimeOut, TimeUnit.MILLISECONDS)
         .addInterceptor(cookieManager.getSetCookieInterceptor()).build();
        } catch (Exception e) {
            //TODO: handle exception
        }

    }

    /**
     * 目标是否需要代理
     * 如果不需要自动代理则会返回null,如果需要代理但不没有可用代理则会返回长度为0的集合
     * 
     * @param uri
     * @return
     */
    public List<Proxy> getProxy(URI uri) {
        nullCheck(uri);
        if (this.autoProxySelector_opt == null) {
            return null;
        }
        // 从autoProxySelector中根据目标动态获取
        return autoProxySelector_opt.select(uri);
    }

    /**
     * 克隆响应
     * 
     * @param rawResponse the original {@link okhttp3.Response} as returned by
     *                    {@link Response#raw()}
     * @return a cloned {@link ResponseBody}
     */
    private ResponseBody cloneResponseBody(okhttp3.Response rawResponse) {

        final ResponseBody responseBody = rawResponse.body();
        final Buffer bufferClone = responseBody.source().buffer().clone();
        return ResponseBody.create(responseBody.contentType(), responseBody.contentLength(), bufferClone);
    }

    /**
     * 进行同步和异步请求
     *
     * @param client
     * @param request
     * @param callback_opt
     * @return
     */
    public RepLogic doRequest(OkHttpClient client, Request request, Callback callback_opt,
            List<Proxy> proxys_opt) {
        nullCheck(request);
        Response rep = null;// 默认响应为null
         try {
             //异步
             if (callback_opt!=null&&client!=null) {
                client.newCall(request).enqueue(callback_opt);
            } else {
                // 同步
                if (client != null) {
                    // 非自动分发模式直接使用自身client连接
                    rep = client.newCall(request).execute();
                    return new RepLogic(rep, request);
                } else {
                    // 代理池分发模式
                    if (proxys_opt == null && this.allowDirectConnection) {
                        // 没有可用代理且允许直接连接的时候使用 默认客户端发送
                        rep = this.OkHttpClient.newCall(request).execute();
                    } else if (proxys_opt == null && !this.allowDirectConnection) {
                        // 如果无可用代理,且不允许直连的时候 则返回Null的响应
                        rep = null;
                    } else if (proxys_opt != null && proxys_opt.size() > 0) {
                        String urlString = request.url().toString();

                        // 如果有可用代理则进行使用代理请求
                        // 进行测试
                        for (int i = 0; i < proxys_opt.size(); i++) {
                            Proxy nowPorxy = proxys_opt.get(i);
                            //在一次判断是否可用,因为代理状态可能会被随时刷新
                            if (!nowPorxy.isAvailable(request.url().toString())) {
                                continue;
                            }
                            try {
                            //    System.out.println("使用"+JSON.toJSONString(nowPorxy));
                                rep = nowPorxy.getHttpClient().getOkHttpClient().newCall(request).execute();
                            } catch (Exception e) {
                            }
                            // 代表请求失败,直接接切换下一个代理
                            if (rep == null) {
                                                                            //手动,设置代理100秒内为不可用
                                                                            nowPorxy.setForceUnavailable(urlString,100);
                                continue;
                            }
                        }

                    }else {
                        rep=null;
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RepLogic(rep, request);
    }


    /**
     * 手动开启log模式
     *
     * @return 客户端
     */
    public HttpClient log() {
        this.logModel = 1;
        this.OkHttpClient = this.OkHttpClient.newBuilder().addInterceptor(new LoggingInterceptor()).build();
        this.cookieManager.setLogModel(this.logModel);
        return this;
    }

    /**
     * 手动开启自动log模式(在调试器状态下自动进行日志输出)
     *
     * @return 客户端
     */
    public HttpClient autoLog() {
        this.logModel = 2;
        this.cookieManager.setLogModel(this.logModel);
        if (SystemUtil.isDebuggerAttached()) {
            this.OkHttpClient = this.OkHttpClient.newBuilder().addInterceptor(new LoggingInterceptor()).build();
        }
        return this;
    }

    /**
     * 添加自动代理的拦截器
     * @param builder
     */
    public void addProxySelectorInterceptor(OkHttpClient.Builder builder) {
        nullCheck(builder);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //原始请求
                Request originalRequest = chain.request();
       //Response response = chain.proceed(originalRequest);

                //读取代理
                List<Proxy> proxys = getProxy(originalRequest.url().uri());
                if (proxys.size()>0) {
                    //则进行循环测试代理请求
                    return doRequest(null, originalRequest, null, proxys).getRawOkHttpRepsone();
                }
                //返回null响应
                return null;
             }
        });
    }
    /**
     * 发送请求
     * 
     * @param request     请求对象
     * @param callback_op 回调对象(可选null==同步请求)
     * @return @
     */
    private RepLogic send(Request request, Callback callback_op) {
        nullCheck(request);
        if (this.autoProxySelector_opt == null) {
            // 使用httpClient直接发送
            return doRequest(this.OkHttpClient, request, callback_op, null);
        } else {
            // 代理分发器


            if (callback_op==null) {
                                        // 先读取代理
                                        List<Proxy> proxys = getProxy(request.url().uri());
                //同步代理分发
                return doRequest(null, request, callback_op, proxys);

            }else{
                //异步代理分发,由拦截器处理,这里无需传递porxy
                return doRequest(this.OkHttpClient, request, callback_op, null);

            }


        }

    }

    /**
     *
     * @param bodys 请求体(可选null=空的请求体)
     * @return
     */
    private okhttp3.FormBody.Builder addBodyFromParm(github.acodervic.mod.data.map.HMap<String, String> bodys) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (bodys != null) {
            // 判断是否有body提交数据
            Set<String> keySet = bodys.keySet();
            for (String name : keySet) {
                formBodyBuilder.add(name, bodys.get(name));
            }
        }
        return formBodyBuilder;
    }

    /**
     * 从bytes中构建body
     * 
     * @param bytes        字节数组
     * @param mediaTypeStr 请求内容类型
     * @return
     */
    private RequestBody addBodyBytes(byte[] bytes, String mediaTypeStr) {
        nullCheck(mediaTypeStr);
        return RequestBody.create(MediaType.get(mediaTypeStr), bytes);
    }

    /**
     * 添加请求头
     * 
     * @param headers_opt 请求头(可选null=没有请求头)
     * @param builder     构建器
     * @return
     */
    private static Builder addHeaders(HMap<String, String> headers_opt, Builder builder) {
        nullCheck(builder);
        if (headers_opt != null) {
            // 添加header
            Set<String> keySet = headers_opt.keySet();
            for (String name : keySet) {
                // HOST头不能瞎指定
                if (!name.toUpperCase().toString().equals("HOST")) {
                    builder.addHeader(name, headers_opt.get(name));
                }
            }
            return builder;
        } else {
            // 自动添加ua
            builder.addHeader("User-Agent", Constant.userAgent_IE11);

        }
        return builder;
    }

    /**
     * 通过方法填充body到httprequest
     * 
     * @param requestBuilder 请求构建器
     * @param method         方法
     * @param body_opt       请求体(可选null=空请求体)
     * @return
     */
    private static Builder buildRequestByMethod(Builder requestBuilder, Method method,
            RequestBody body_opt) {
        nullCheck(requestBuilder, method);
        // 判断方法类型,默认get
        if (method == Method.GET) {
            if (body_opt != null) {
                new Exception("HttpClient GET方法不允许有请求体!").printStackTrace();
            }
            requestBuilder = requestBuilder.get();
            Constant.dbg("HttpbuildRequestByMethod", "请求使用get,若存在body数据,放弃填充body数据");
        } else if (method == Method.POST) {
            if (body_opt != null) {
                requestBuilder = requestBuilder.post(body_opt);
            }
        } else if (method == Method.HEAD) {
            requestBuilder = requestBuilder.head();
        } else if (method == Method.PUT) {
            if (body_opt != null) {
                requestBuilder = requestBuilder.put(body_opt);
            }
        } else if (method == Method.DELETE) {
            // 判断是否要有提交的数据
            if (body_opt != null) {
                requestBuilder = requestBuilder.delete(body_opt);
            } else {
                requestBuilder = requestBuilder.delete();
            }
        }
        return requestBuilder;
    }

    /**
     * 发送http请求
     *
     * @param url         url
     * @param method      方法
     * @param headers_opt 请求体
     * @param bodys_opt   请求头
     * @return @
     */
    public RepLogic asyncReq(String url, Method method, HMap<String, String> headers_opt,
            HMap<String, String> bodys_opt, Callback callback) {
        nullCheck(url, method);
        try {
            // 添加请求体表单参数
            FormBody.Builder formBodyBuilder = addBodyFromParm(bodys_opt);
            Builder requestBuilder = new Request.Builder().url(url);
            // 添加请求头
            requestBuilder = addHeaders(headers_opt, requestBuilder);
            if (bodys_opt == null) {
                requestBuilder = buildRequestByMethod(requestBuilder, method, null);
            } else {
                requestBuilder = buildRequestByMethod(requestBuilder, method, formBodyBuilder.build());

            }
            return send(requestBuilder.build(), callback);
        } catch (Exception e) {
            if (needPrintStackTrace()) {
                e.printStackTrace();
                ;
            }
        }
        return null;
    }


    /**
     * 发送异步http请求,返回null
     *
     * @param url         url
     * @param method      方法
     * @param headers_opt 请求体
     * @param bodys_opt   请求头
     * @return @
     */
    public RepLogic asyncReq(String url, Method method, Callback callback) {
        return asyncReq(url, method, null, null, callback);
    }



    /**
     * 发送http请求,通过map填充参数
     *
     * @param url         url
     * @param method      方法
     * @param headers_opt 请求体
     * @param bodys_opt   请求头
     * @return @
     */
    public RepLogic syncReq(String url, Method method, HMap<String, String> headers_opt,
            HMap<String, String> bodys_opt) {
        return asyncReq(url, method, headers_opt, bodys_opt, null);
    }

    /**
     * 发送http请求
     *
     * @param url         url
     * @param method      方法
     * @return @
     */
    public RepLogic syncReq(String url, Method method) {
        return asyncReq(url, method, null, null, null);
    }





    /**
     * 发送同步请求使用byte填充请求体
     *
     * @param url         url
     * @param method      方法
     * @param headers_opt 请求体
     * @param bodys_opt   请求头
     * @return @
     */
    public RepLogic syncReqByBodyBytes(String url, Method method, HMap<String, String> headers_opt, String mediaTypeStr,
            ByteLIst bodyBytesList) {
        return ayncReqByBodyBytes(url, method, headers_opt, mediaTypeStr, bodyBytesList, null);
    }

    /**
     * 发送同步请求使用byte填充请求体
     *
     * @param url           url
     * @param method        方法
     * @param headers_opt   请求体
     * @param bodyBytesList bodybytes
     * @param callback      回调对象
     * @return @
     */
    public RepLogic ayncReqByBodyBytes(String url, Method method, HMap<String, String> headers_opt, String mediaTypeStr,
            ByteLIst bodyBytesList, Callback callback) {
        try {
            nullCheck(url, method, bodyBytesList);
            Builder requestBuilder = new Request.Builder().url(url);
            // 添加请求头
            requestBuilder = addHeaders(headers_opt, requestBuilder);
            // 通过字节流直接构造请求体,注意在填充body的时候,不同的数据格式解析方法也不一样
            // multipart的时候,body体中的数据可能是分段存放的,每一段之间使用分隔符进行分割,而且存在/r
            // String[] types = headers.get("Content-Type").split(";");
            // 剃掉types中的请求头的空格
            // for (int i = 0; i < types.length; i++) {
            // types[i] = CharUtil.trimLeft(types[i]);
            // }
            // 判断是不是多块数据.
            // if (ListUtil.arrayToStr(types, null).indexOf("multipart")!=-1) {

            // }
            // MediaType type = MediaType.get( headers.get("Content-Type"));
            RequestBody requestBody = null;
            if (bodyBytesList != null && bodyBytesList.size() > 0) {
                byte[] bodyBytes = bodyBytesList.toBytes();
                if (bodyBytes.length > 0) {
                    requestBody = RequestBody.create(MediaType.get(mediaTypeStr), bodyBytesList.toBytes());
                }
            }

            requestBuilder = buildRequestByMethod(requestBuilder, method, requestBody);

            // 发送请求
            return send(requestBuilder.build(), callback);

        } catch (Exception e) {
            if (needPrintStackTrace()) {
                e.printStackTrace();
                ;
            }
        }
        return null;
    }

    /**
     * 发送http请求
     * 
     * @param url         url
     * @param method      方法
     * @param headers_opt 请求体
     * @param bodys
     * @return "application/json; charset=utf-8" @
     */
    /**
     * 
     * @param url          url
     * @param method       方法
     * @param headers_opt  请求体param headers_opt
     * @param file         磁盘文件
     * @param mediaTypeStr 媒体字符串
     * @return @
     */
    public RepLogic syncReqByFile(String url, Method method, HMap<String, String> headers_opt, File file,
            String mediaTypeStr) {
        return asyncReqByFile(url, method, headers_opt, file, mediaTypeStr, null);
    }

    /**
     * 发送http请求通过文件填充body
     * 
     * @param url          url
     * @param method       方法
     * @param headers_opt  请求体param headers_opt
     * @param file         磁盘文件
     * @param mediaTypeStr
     * @param callback
     * @return @
     */
    public RepLogic asyncReqByFile(String url, Method method, HMap<String, String> headers_opt, File file,
            String mediaTypeStr, Callback callback) {
        nullCheck(url, method, mediaTypeStr, callback);
        MediaType type = MediaType.get(mediaTypeStr);
        RequestBody body = null;
        // 如果文件存在
        if (file.exists()) {
            // 将file附加到body中
            body = RequestBody.create(type, file);
        }
        Builder requestBuilder = new Request.Builder().url(url);
        // 添加请求头
        requestBuilder = addHeaders(headers_opt, requestBuilder);
        // 根据方法和请求体构造 请求
        requestBuilder = buildRequestByMethod(requestBuilder, method, body);
        // 发送请求
        return send(requestBuilder.build(), callback);
    }



    /**
     * 获得一个不安全的http代理客户端
     *
     * @param proxyType    代理类型
     * @param proxyHost    代理主机
     * @param proxyPort    代理端口
     * @param timeoutMs    延迟
     * @param userName_opt 用户名
     * @param passWord_opt 密码
     * @return
     */
    public static HttpClient getUnsafeOkHttpClientWithPorxy(Type proxyType, String proxyHost, int proxyPort,
            int timeoutMs, String userName_opt, String passWord_opt) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // 判断是否代理
            if (proxyHost != null && proxyHost.length() > 0) {
                builder.proxy(new java.net.Proxy(proxyType, new InetSocketAddress(proxyHost, proxyPort)));
                if (userName_opt != null && userName_opt.length() > 0) {
                    if (proxyType == Type.HTTP) {
                        // 设置代理认证
                        Authenticator proxyAuthenticator = new Authenticator() {
                            @Override
                            public Request authenticate(Route route, Response response) {
                                String credential = Credentials.basic(userName_opt, passWord_opt);
                                return response.request().newBuilder().header("Proxy-Authorization", credential)
                                        .build();
                            }
                        };
                        builder.proxyAuthenticator(proxyAuthenticator);
                        Constant.dbg("HTTP调试", "构建代理http客户端 代理主机:" + proxyHost + "   代理端口:" + proxyPort + " 具有代理认证!");

                    } else if (proxyType == Type.SOCKS) {
                        // 设置socket代理认证
                        return null;
                    }

                } else {
                    Constant.dbg("HTTP调试", "构建代理http客户端 代理主机:" + proxyHost + "   代理端口:" + proxyPort);
                }

            }
            // 设置连接超时时间,默认setcookie拦截器等
            return new HttpClient(builder.connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .addInterceptor(cookieManager.getSetCookieInterceptor()).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得一个不安全的http代理客户端
     *
     * @param timeoutMs_opt
     * @return
     */
    public static HttpClient getUnsafeOkHttpClient(Long timeoutMs_opt) {
        return getUnsafeOkHttpClientWhthProxySelector(timeoutMs_opt, null);
    }

    /**
     * 获得一个不安全的http代理客户端并使用代理选择器,注意当你使用了ProxySelector时候,代理选择器则会在内部自动使用代理重试,这会发出更多的大量的重试请求
     *
     * @param timeoutMs_opt         超时(可选null=Constant.httpTimeOut)
     * @param autoProxySelector_opt 代理选择器(可选null=不使用代理选择器)在设置了代理选择器之后会进行响应的检查测试和切换
     * @return
     */
    public static HttpClient getUnsafeOkHttpClientWhthProxySelector(Long timeoutMs_opt, AutoProxySelector autoProxySelector_opt) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            if (timeoutMs_opt == null) {
                timeoutMs_opt = Constant.httpTimeOut;
            }

            if (autoProxySelector_opt == null) {
                // 构造固定的okhttpclient和httpclient绑定
                return new HttpClient(builder.connectTimeout(timeoutMs_opt.longValue(), TimeUnit.MILLISECONDS)
                        .readTimeout(timeoutMs_opt.longValue(), TimeUnit.MILLISECONDS)
                        .addInterceptor(cookieManager.getSetCookieInterceptor()).build());
            } else {
                // 构造动态的和autoProxySelector绑定的okhttpcliet,在请求的时候会自动从proxypool中读取
                return new HttpClient(autoProxySelector_opt);
            }
            // 设置连接超时时间

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将响应转换为原始请求字节
     * 
     * @param response okhttp响应
     * @return 字节数组
     */
    public static byte[] parseResponsetoRawHttpMessageBytes(Response response) {
        // 先拼接头
        nullCheck(response);
        String protocol = response.protocol().toString().toUpperCase();
        String code = response.code() + "";
        String message = response.message();
        String allHeaders = protocol + " " + code + " " + message + "\n" + response.headers().toString();
        ByteLIst by = new ByteLIst(allHeaders.getBytes());
        try {
            by.put("\n".getBytes());
            by.put(response.body().bytes());
        } catch (Exception e) {
            Constant.dbg("parseResponsetoRawHttpMessageBytes", "从响应中转换响应体失败,跳过!");
            e.printStackTrace();
        }
        return by.toBytes();
    }



    /**
     * 将http响应转换为字符串包括头部和体,错误null
     * 
     * @param response    okhttp响应对象
     * @param charset_opt 编码格式(可选null=utf8)
     * @return
     */
    public static String parseRepsoneToString(Response response, String charset_opt) {
        nullCheck(response);
        // 进行拼接响应
        try {
            StringBuilder sb = new StringBuilder(response.headers().toString());
            sb.append("\n");
            sb.append(response.body().string());
            return new String(sb.toString().getBytes(), parseCharsetStr(charset_opt));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将http响应转换为字符串包括头部和体,错误null
     * 
     * @param response okhttp响应对象
     * @return
     */
    public static byte[] parseRepsoneToBytes(Response response) {
        nullCheck(response);
        // 进行拼接响应
        if (response != null) {
            try {
                ByteLIst byteLIst = new ByteLIst();
                byteLIst.put((response.protocol().toString().toUpperCase() + " " + response.code() + " "
                        + response.message() + "\n").getBytes());
                byteLIst.put(response.headers().toString().getBytes());
                byteLIst.put("\n".getBytes());
                byteLIst.put(response.body().bytes());
                Constant.dbg("parseRepsoneToBytes", new String(byteLIst.toBytes()));
                return byteLIst.toBytes();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        // 出现错误
        return null;
    }

 
    /**
     * 设置cookie处理函数,
     * 
     * @param cookieJar
     */
    public void setCookieJar(CookieJar cookieJar) {
        nullCheck(cookieJar);
        this.cookieJar_opt = cookieJar;
        this.OkHttpClient = this.OkHttpClient.newBuilder().cookieJar(cookieJar).build();
    }

    /**
     * 添加一个请求拦截器,当http进行请求之前,okhttp会调用此方法,可以在此方法中进行继续请求或对请求进行动态修改添加hander等操作
     * 如下修改即将请求的url,使用chain.proceed()来继续当前的处理 httpc.addInterceptor(new
     * Interceptor(){
     * 
     * @Override public Response intercept(Chain chain) { Request request =
     *           chain.request(); Response response =
     *           chain.proceed(request.newBuilder().url("https://www.jianshu.com/p/eaee7cd227cd").build());
     *           //可以继续对响应进行处理,如重发等,操作完成返回 repsone return response; } });
     * @param interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        nullCheck(interceptor);
        this.OkHttpClient = this.OkHttpClient.newBuilder().addInterceptor(interceptor).build();
    }

    /**
     * 添加一个网络拦截器 addInterceptor（应用拦截器）： 1，不需要担心中间过程的响应,如重定向和重试.
     * 2，总是只调用一次,即使HTTP响应是从缓存中获取. 3，观察应用程序的初衷. 不关心OkHttp注入的头信息如: If-None-Match.
     * 4，允许短路而不调用 Chain.proceed(),即中止调用. 5，允许重试,使 Chain.proceed()调用多次.
     * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     * addNetworkInterceptor（网络拦截器）： 1，能够操作中间过程的响应,如重定向和重试. 2，当网络短路而返回缓存响应时不被调用.
     * 3，只观察在网络上传输的数据. 4，携带请求来访问连接
     * 
     * @param interceptor
     */
    public void addNetworkInterceptor(Interceptor interceptor) {
        nullCheck(interceptor);
        this.OkHttpClient = this.OkHttpClient.newBuilder().addNetworkInterceptor(interceptor).build();
    }

    public void setDNSServer(List<String> dnservers) {
        nullCheck(dnservers);
        this.OkHttpClient = this.OkHttpClient.newBuilder().dns(new Dns() {
            @Override
            public List<InetAddress> lookup(String arg0) throws UnknownHostException {

                return ListUtil.cast(dnservers, new Function<String, InetAddress>() {

                    @Override
                    public InetAddress apply(String dnserver) {
                        try {
                            return InetAddress.getByName(dnserver);
                        } catch (UnknownHostException e) {

                            if (needPrintStackTrace()) {
                                e.printStackTrace();
                                ;
                            }
                        }
                        return null;
                    }

                });
            }
        }).build();

    }



    /**
     * 
     * @param url          下载连接
     * @param method       方法
     * @param headers_opt  请求头(可选)
     * @param bodys_opt    请求体(可选)
     * @param saveDir      保存目录(可选)
     * @param filename_op  保存的文件名称(可选)
     * @param listener_opt 文件下载进度监听器(可选)
     */
    public void asyncDownload(String url, Method method, HMap<String, String> headers_opt,
            HMap<String, String> bodys_opt, File saveDir, String filename_op,
            OnDownloadListener listener_opt) {
        nullCheck(url, method, saveDir);
        asyncReq(url, method, headers_opt, bodys_opt, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                if (listener_opt != null) {
                    listener_opt.onDownloadFailed("连接目标失败!");
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                File targetFile = null;
                String urlFileName = file_name(HttpUtil.parseUrl(url));
                boolean targetCanWrite = false;
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }

                // 创建文件/夹
                if (filename_op != null) {
                    // 是否进行覆盖,创建
                    targetFile = new File(new DirRes(saveDir).getDir(), filename_op);
                } else {
                    // 创建文件夹
                    targetFile = new File(new DirRes(saveDir).getDir(), urlFileName);
                }
                targetCanWrite = saveDir.canWrite();

                // 如果文件创建完成
                if (targetCanWrite) {
                    int progress = 0;
                    // 进行写入操作
                    try {
                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        fos = new FileOutputStream(targetFile);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);

                            if (listener_opt != null) {
                                sum += len;
                                int tempP = (int) (sum * 1.0f / total * 100);
                                if (tempP != progress) {
                                    progress = tempP;
                                    // 下载中
                                    listener_opt.onDownloading(progress);
                                }

                            }

                        }
                        fos.flush();
                        // 下载完成
                        if (listener_opt != null) {
                            listener_opt.onDownloadSuccess();
                        }
                    } catch (Exception e) {
                        if (needPrintStackTrace()) {
                            e.printStackTrace();
                            ;
                        }
                        if (listener_opt != null) {
                            listener_opt.onDownloadFailed("在处理下载流的时候和出现异常:");
                        }

                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (IOException e) {
                        }
                        try {
                            if (fos != null)
                                fos.close();
                        } catch (IOException e) {
                        }
                    }

                } else {
                    listener_opt.onDownloadFailed("目标" + targetFile.getAbsolutePath() + "不可以写!");
                }
            }
        });

    }


    /**
     * 同步下载文件到本地磁盘
     * @param url 目录url
     * @param method 请求方法
     * @param headers_opt 请求头_可选
     * @param bodys_opt 请求体_可选
     * @param saveDir 保存目录
     * @param filename_op 文件名_可选,如果null=则自动从url中提取文件名
     * @return 返回一个包装了被下载文件文件的fileres对象,如果失败则返回null
     */
    public FileRes syncDownload(String url, Method method, HMap<String, String> headers_opt,
            HMap<String, String> bodys_opt, File saveDir, String filename_op) {
        nullCheck(url, method, saveDir);
            FileRes resultFile=null;
        try {

            RepLogic rep = syncReq(url, method, headers_opt, bodys_opt);
            if (rep.getRawOkHttpRepsone() == null || !rep.getRawOkHttpRepsone().isSuccessful()) {

                return resultFile;
            }
            File targetFile = null;
            String urlFileName = file_name(HttpUtil.parseUrl(url));
            boolean targetCanWrite = false;
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // 创建文件/夹
            if (filename_op != null) {
                // 是否进行覆盖,创建
                targetFile = new File(new DirRes(saveDir).getDir(), filename_op);
            } else {
                // 创建文件夹
                targetFile = new File(new DirRes(saveDir).getDir(), urlFileName);
            }
            targetCanWrite = saveDir.canWrite();

            if (targetCanWrite && targetFile.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(targetFile);

                fos.write(rep.getRawOkHttpRepsone().body().bytes());
                fos.close();
            }
            resultFile=new FileRes(targetFile);

        } catch (Exception e) {
            if (needPrintStackTrace()) {
                e.printStackTrace();
            }
            return resultFile;
        }
        return resultFile;

    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed(String mess);
    }

    /**
     * @return the cookieManager
     */
    public static CookieManager getCookieManager() {
        return cookieManager;
    }

    /**
     * @return the okHttpClient
     */
    public OkHttpClient getOkHttpClient() {
        return OkHttpClient;
    }

    /**
     * @param okHttpClient the okHttpClient to set
     */
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        OkHttpClient = okHttpClient;
    }

    /**
     * @return the allowDirectConnection
     */
    public boolean isAllowDirectConnection() {
        return allowDirectConnection;
    }

    /**
     * @param allowDirectConnection the allowDirectConnection to set
     */
    public void setAllowDirectConnection(boolean allowDirectConnection) {
        this.allowDirectConnection = allowDirectConnection;
    }

    /**
     * 将requestBody转换为字符串
     * 
     * @param request
     * @return
     */
    private static String requestBodyToString(final RequestBody request) {
        try {
            if (request == null) {
                return "";
            }
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "读取请求体失败";
        }
    }

    private static String repsoneBodyToString(final ResponseBody response) {
        try {
            if (response == null) {
                return "";
            }
            final BufferedSource source = response.source();
            source.request(Integer.MAX_VALUE);
            final byte[] bytes = source.buffer().snapshot().toByteArray();
            return new String(bytes, "utf-8");
        } catch (final IOException e) {
            return "读取请求体失败";
        }
    }

    /**
     * LoggingInterceptor 日志拦截其次
     */
    public class LoggingInterceptor implements Interceptor {

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.currentTimeMillis();
            String log = "==================================================>>>请求<<<<============================================\n";
            log += request.url() + "\n";
            log += request.headers() + "\n";
            log += "\n";
            log += requestBodyToString(request.body()) + "\n";
            Response response = chain.proceed(request);
            if (response != null) {
                log += "----------------------------------------------->>>>请求结束<<<<-------------------------------------------\n";
                long t2 = System.currentTimeMillis();
                log += "----------------------------------------------->>>>响应:响应时间" + (t2 - t1) + "ms"
                        + "<<<<-----------------------------\n";
                log += response.request().url().toString() + "   " + response.message().toString() + "\n";
                log += response.headers().toString() + "\n";
                log += repsoneBodyToString(response.body()) + "\n";
                log += "==================================================>>>响应结束"
                        + " <<<<======================================\n";
            } else {
                log += "----------------------------------------------->>>>请求失败!<<<<-------------------------------------------\n";

            }
            System.out.println(log);
            return response;
        }
    }
}