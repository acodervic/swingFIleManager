package github.acodervic.mod.db.anima.event;

/**
 * eventAction
 */
public enum EventActionEnum {
    UPDATE("UPDATE"), INSERT("INSERT"), DELETE("DELETE"), CHANGE("CHANGE");

    private String action;

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * 构造方法必然是private修饰的 就算不写，也是默认的
     *
     * @param num
     * @param desc
     */
    private EventActionEnum(String action) {
        this.action = action;
    }
}