package github.acodervic.mod.db.anima.enums;

/**
 * @author biezhi
 * @date 2018/3/16
 */
public enum ErrorCode {

    IS_NULL(1000, "Sql2o 实例没有配置, 请检查数据库配置!(如果是通过全局数据库操作,请执行setGlobalConnectionInstance(anima)设置全局数据库!) :)"),
    FROM_NOT_NULL(1001, "FromClass为null, 请检查 :)"),
    FUNCTION_COLUMN_NAME_INVALID(1002,"orLike传递的function没有提供列名");

    private Integer code;
    private String  msg;

    /**
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param code the code to set
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @param code
     * @param msg
     */
    private ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
