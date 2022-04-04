package github.acodervic.mod.net.server.httpd;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

import static github.acodervic.mod.net.server.httpd.ServerHandlerUtil.*;

import java.io.IOException;
import java.util.function.Function;

import org.apache.commons.compress.utils.IOUtils;
 
public class ServerHandler {

    private Function<IHTTPSession, Response> processFun = null;
    private String pathRegex;

    public ServerHandler(String pathRegex, Function<IHTTPSession, Response> processFun) {
        this.pathRegex = pathRegex;
        this.processFun = processFun;
    }

    /**
     * @return the pathRegex
     */
    public String getPathRegex() {
        return pathRegex;
    }

    /**
     * 处理请求
     * 
     * @param requestSession
     * @return
     */
    public Response hander(IHTTPSession requestSession) {
        if (requestSession != null && this.processFun != null) {
            return this.processFun.apply(requestSession);
        }
        return null;
    }

    /**
     * 从session中读取请求体.
     *
     * @param session the session that has
     * @return the body
     */
    public String getBody(IHTTPSession session) {
        int contentLength = getBodySize(session);
        if (contentLength == 0) {
            return "";
        }

        byte[] bytes = new byte[contentLength];
        try {
            IOUtils.toByteArray(session.getInputStream());
            return new String(bytes);
        } catch (IOException e) {
            System.err.println("Failed to read the body:");
            e.printStackTrace();
            return "";
        }
    }

}
