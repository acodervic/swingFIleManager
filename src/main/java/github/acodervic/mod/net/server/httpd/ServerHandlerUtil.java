package github.acodervic.mod.net.server.httpd;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

//这里继承只是为了获取NanoHTTPD的镜头工具函数,实例化服务器请使用HttpServer
public class ServerHandlerUtil extends NanoHTTPD {
    /**
     * 这里继承只是为了获取NanoHTTPD的镜头工具函数,实例化服务器请使用HttpServer
     * 
     * @param port
     */
    public ServerHandlerUtil(int port) {
        super(port);
    }

    /**
     * 读取请求体大小.
     *
     * @param session 会话
     * @return 请求体大小,异常则返回0
     */
    public static int getBodySize(IHTTPSession session) {
        String contentLengthHeader = session.getHeaders().get(HttpHeader.CONTENT_LENGTH.toLowerCase());
        if (contentLengthHeader == null) {
            return 0;
        }

        int contentLength = 0;
        try {
            contentLength = Integer.parseInt(contentLengthHeader);
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse " + HttpHeader.CONTENT_LENGTH + " value: " + contentLengthHeader);
            e.printStackTrace();
            return 0;
        }

        if (contentLength <= 0) {
            return 0;
        }
        return contentLength;
    }

    /**
     * 设置空请求体
     *
     * @param session the session that has the request
     */
    public static void consumeBody(IHTTPSession session) {
        try {
            session.getInputStream().skip(getBodySize(session));
        } catch (IOException e) {
            System.err.println("Failed to consume body:");
            e.printStackTrace();
        }
    }

    /**
     * 读取参数列表的地一个参数,没有则返回null
     *
     * @param session 有请请求的会话
     * @param param   参数名
     * @return 第一个参数可能为Null
     */
    protected static String getFirstParamValue(IHTTPSession session, String param) {
        return session.getParameters().get(param) != null ? session.getParameters().get(param).get(0) : null;
    }
}
