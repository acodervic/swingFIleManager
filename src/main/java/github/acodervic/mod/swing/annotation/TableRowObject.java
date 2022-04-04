package github.acodervic.mod.swing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于注解表格自动添加对象构造添加面板
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableRowObject {
    TableRowObjectType type();// 默认为字符串类型;

    String defaultVaule() default "";

    String lableText(); // 对象的显示标签

    String[] enumValues() default {};// 默认的枚举值

    boolean required() default false; // 是否必填

    String verificationRegex()

    default ".*"; // 验证用户输入值是否满足表达式

    String verificationErrorMessage()

    default "";// 值无法通过验证时的消息提示

    boolean editable() default true;// 默认combox是可以编辑的

    int comboxHeight() default 30;
}
