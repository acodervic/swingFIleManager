package github.acodervic.mod.net.http;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.http_getUnsafeOkHttpClient;

import java.io.IOException;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import github.acodervic.mod.data.ByteLIst;
import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.io.IoStream;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
public class HttpRepsoneRepuest {
    private Response okhttpRep;
    private Request okHttpReq;
    private ByteLIst repsoneBodyBytes = new ByteLIst();

    /**
     * @param okhttpRep
     */
    public HttpRepsoneRepuest(Response okhttpRep) {
        if (okhttpRep != null) {
            this.okhttpRep = okhttpRep;
            try {
                //克隆读取避免影响原始响应对
                repsoneBodyBytes = new ByteLIst(
                        IoStream.readSteam(this.okhttpRep.peekBody((Long.MAX_VALUE)).byteStream()));
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }

    /**
     * 从http请求构建
     * 
     * @param okhttpRep
     */
    public HttpRepsoneRepuest(Request okHttpReq) {
        if (okHttpReq != null) {
            this.okHttpReq = okHttpReq;
        }
    }

    /**
     * 从http请求响应构建
     * 
     * @param okhttpRep
     */
    public HttpRepsoneRepuest(Request okHttpReq, Response okhttpRep) {
        if (okHttpReq != null) {
            this.okHttpReq = okHttpReq;
        }
        if (okhttpRep != null) {
            this.okhttpRep = okhttpRep;
            try {
                repsoneBodyBytes = new ByteLIst(this.okhttpRep.peekBody(Long.MAX_VALUE).bytes());
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
    }

    /**
     * 读取请求对象
     * 
     * @return
     */
    public Request getRequest() {
        return this.okHttpReq;
    }

    /**
     * 读取响应的数据流
     * @return
     */
    public ByteLIst getBytelist() {
        return this.repsoneBodyBytes;
    }

    /**
     * 获取原始okhttp响应
     * 
     * @return
     */
    public Response rawRepsone() {
        return this.okhttpRep;
    }

    /**
     * 返回请求体的str对象,如果读取出错则返回""的str
     *
     * @return
     */
    public str getBodyStr(String charset_opt) {
        return new str(getBytelist().toBytes(), charset_opt);
    }


    /**
     * 返回请求体的str对象,如果读取出错则返回""的str,
     *如果返回的响应不为null且,为html,则尝试自动从charset标签中读取编码
     * @return
     */
    public str getBodyStr() {
        return new str(getBytelist().toBytes(), null);

    }

    /**
     * 判断返回是否为200
     * 
     * @return
     */
    public boolean isOK() {
        if (this.rawRepsone() == null) {
            return false;
        }
        return this.okhttpRep.code() == 200;
    }

    public int code() {
        return this.okhttpRep.code();
    }

    /**
     * 如果返回为200..300之间的状态码则返回成功
     */
    public void isSuccessful() {
        this.okhttpRep.isSuccessful();
    }


    /**
     * 读取body为json对象,如果失败则返回一个null的Opt
     * 
     * @param charset_opt
     * @return
     */
    public Opt<JSONObject> getBodyJson(String charset_opt) {
        try {
            // 如果BodyStr中有值
            if (this.getBodyStr().notEmpty()) {
                return new Opt<JSONObject>(JSONUtil.jsonToJsonObj(repsoneBodyBytes.toString(charset_opt)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Opt<JSONObject>();
    }

    public Headers getRepsoneHanders() {
        return this.rawRepsone().headers();
    }

    public String getRepsoneHander(String name) {
        nullCheck(name);
        return this.rawRepsone().header(name);
    }

    public Headers getRequestHanders() {
        return this.getRequest().headers();
    }

    public String getRequestHander(String name) {
        nullCheck(name);
        return this.getRequest().header(name);
    }

    /**
     * 读取body为json对象,如果失败则返回一个null的Opt
     * 
     * @return
     */
    public Opt<JSONObject> getBodyJson() {
        return getBodyJson(null);
    }

    /**
     * 读取body为json对象,如果失败则返回一个null的Opt
     *
     * @param charset_opt
     * @return
     */
    public Opt<JSONArray> getBodyJsonArray(String charset_opt) {
        try {
            // 如果BodyStr中有值
            return new Opt<JSONArray>(JSONUtil.jsonToJsonObjArry(getBodyStr(charset_opt).toString()));

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return new Opt<JSONArray>();
    }



    /**
     * 读取body为json对象,如果失败则返回一个null的Opt
     * 
     * @return
     */
    public Opt<JSONArray> getBodyJsonArray() {
        return getBodyJsonArray(null);
    }

    public str getContentType() {
        try {
            return new str(this.okhttpRep.header("content-type"));
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
        // 被封装的响应可能为null,返回"""
        return new str("");
    }

    /**
     * 根据ContentType判断是否为html
     * 
     * @return
     */
    public boolean isHtml() {
        if (getContentType().has("html")) {
            return true;
        }
        return false;
    }

    /**
     * 根据ContentType判断否为文本内容
     * 
     * @return
     */
    public boolean isText() {
        if (getContentType().has("text")) {
            return true;
        }
        return false;
    }
  /**
     * 关闭连接
     */
    public void closeConnection() {
        if (this.okhttpRep!=null) {
            this.okhttpRep.close();
        }
   }
    /**
     * 根据ContentType判断是否为图片
     * 
     * @return
     */
    public boolean isImage() {
        if (getContentType().has("image")) {
            return true;
        }
        return false;
    }

    /**
     * 根据ContentType判断是否为css
     * 
     * @return
     */
    public boolean isCss() {
        if (getContentType().has("text/css")) {
            return true;
        }
        return false;
    }

    /**
     * 根据ContentType判断是否为javascript
     * 
     * @return
     */
    public boolean isJs() {
        if (getContentType().has("javascript")) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        http_getUnsafeOkHttpClient(10000).syncReq("https://adad1231345cdasaxxxS.com", Method.GET, null, null)
                .doVoid(sucess -> {
                    // sucess.getBodyStr().print();
                }, filed -> {
                    System.out.println(filed.getRequest().url().toString());
                    ;
                });

    }

    /**
     * 
     */
    public HttpRepsoneRepuest() {
    }

    /**
     * @return the okhttpRep
     */
    public Response getOkhttpsetOkhttpResponse() {
        return okhttpRep;
    }


}