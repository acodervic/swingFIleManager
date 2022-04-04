package github.acodervic.mod.swing.annotation;

/**
 * 此注解用于注解表格自动添加对象构造添加面板
 */
public enum TableRowObjectType {
    STRING("STRING"), INT("INT"), BOOL("BOOL");

    String type;

    /**
     * @param type
     */
    private TableRowObjectType(String type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

}
