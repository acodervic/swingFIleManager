package github.acodervic.mod.net.server.httpd;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.net.server.httpd.ServerHandlerUtil.consumeBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import fi.iki.elonen.NanoHTTPD;
import github.acodervic.mod.crypt.MyKeyStore;
import github.acodervic.mod.data.str;

public class HttpServer extends NanoHTTPD {
    // 处理服务的handlers
    private List<ServerHandler> handlers = new ArrayList<ServerHandler>();
    // 处理404请求的header
    private ServerHandler handler404 = new ServerHandler("", session -> {
        consumeBody(session);
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML,
                "<html><head><title>404</title></head><body>404 Not Found</body></html>");
    });

    public HttpServer(int port) {
        super(port);
    }

    public HttpServer(String hostName, int port) {
        super(hostName, port);
    }

    /**
     * 启用https
     * 
     * @param keystore
     * @return
     */
    public boolean enableSSL(MyKeyStore keystore) {
        nullCheck(keystore);
        try {
            this.setServerSocketFactory(new SecureServerSocketFactory(
                    NanoHTTPD.makeSSLSocketFactory(keystore.getRawKeyStore(), keystore.getKeyManagers()), null));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        for (ServerHandler handler : handlers) {
            if (new str(uri).hasRegex(handler.getPathRegex())) {
                return handler.hander(session);
            }
        }
        return handler404.hander(session);
    }

    /**
     * 添加一个路径处理器
     *
     * @param pathRegex  路径拦截表达式
     * @param processFun 处理函数
     */
    public void addHandler(String pathRegex, Function<IHTTPSession, Response> processFun) {
        ServerHandler nanoServerHandler = new ServerHandler(pathRegex, processFun);
        this.handlers.add(nanoServerHandler);
    }

    public void removeHandler(ServerHandler handler) {
        this.handlers.remove(handler);
    }

    public void setHandler404(ServerHandler handler) {
        this.handler404 = handler;
    }

    /**
     * 在新线程中启动
     */
    public void startWithNewThread() {
        new Thread(() -> {
            try {
                start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
