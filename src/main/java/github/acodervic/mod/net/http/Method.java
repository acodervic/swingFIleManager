package github.acodervic.mod.net.http;

/**
 * Method
 */
public enum Method {
    GET("GET"), POST("POST"), HEAD("HEAD"), PUT("PUT"), DELETE("DELETE");

    public final String value;

    private Method(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    /**
     * 通过字符串,获取方法枚举
     * @param str 字符串方法
     * @return
     */
    public static  Method getMethodByStr(String str) {
        switch (str.toUpperCase()) {
        case "GET":
            return GET;
        case "POST":
            return POST;
        case "HEAD":
            return HEAD;
        case "PUT":
            return PUT;
        case "DELETE":
            return DELETE;
        }
        return null;
    }
}