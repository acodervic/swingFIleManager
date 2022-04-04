package github.acodervic.mod.swing.option;

import java.awt.Component;
import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;

/**
 * 抽象选项实体,和config共用
 */

public class Option<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -198096441386150573L;
    String id;// 参数id,可以为null,你可以选择使用id或者name进行参数定位
    String name;// 参数名称
    String desc;// 参数描述
    T value;// 参数值,可以设置此值初始化为默认值,如果是list则说明支持多选值
    ControlUi type = ControlUi.TEXTFILED;// 参数类型 默认为文本输入控件,如果有enumValue推荐设置为COMBOX,或者多选按钮
    transient Function<T, Boolean> verificationFun;// 验证是否合法的函数
    List<T> enumValue = new ArrayList<T>();// 默认的可选值
    transient Opt<Component> component = new Opt<Component>();// 组件,每次生成新的之后会被覆盖,如果为多选按钮组则会是一个List<JcheckBox>
    transient Function<Object, String> comboxOrRaioRander;// combox和多选jchecjbox的显示文本处理函数,因为有时候一些值无法覆盖其toString方法,如interger,key=枚举值对象,value=返回的显示函数

    /**
     * @return the component
     */
    public Opt<Component> getComponent() {
        return component;
    }

    /**
     * @return the comboxOrRaioRander
     */
    public Function<Object, String> getComboxOrRaioRander() {
        return comboxOrRaioRander;
    }

    /**
     * 设置combox/jcheckbox的文本显示函数,没有函数则默认使用toString函数
     * 
     * @param comboxOrRaioRander the comboxOrRaioRander to set
     * @return
     */
    public Option<T> setComboxOrRaioRander(Function<Object, String> comboxOrRaioRander) {
        this.comboxOrRaioRander = comboxOrRaioRander;
        return this;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     * @return
     */
    public Option<T> setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param enumValue the enumValue to set
     * @return 
     */
    public Option<T> setEnumValue(List<T> enumValue) {
        this.enumValue = enumValue;
        return this;
    }

    /**
     * @param name the name to set
     * @return
     */
    public Option<T> setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * @param value the value to set
     * @return
     */
    public Option<T> setValue(T value) {
        this.value = value;
        return this;
    }

    /**
     * @return the type
     */
    public ControlUi getType() {
        return type;
    }

    /**
     * @param type the type to set
     * @return
     */
    public Option<T> setType(ControlUi type) {
        this.type = type;
        return this;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     * @return
     */
    public Option<T> setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    /**
     * @return the verificationFun
     */
    public Function<T, Boolean> getVerificationFun() {
        return verificationFun;
    }

    /**
     * @param verificationFun the verificationFun to set
     * @return
     */
    public Option<T> setVerificationFun(Function<T, Boolean> verificationFun) {
        this.verificationFun = verificationFun;
        return this;
    }

    /**
     * @return the enumValue
     */
    public List<T> getEnumValue() {
        return enumValue;
    }

    public Option<T> addEnumValue(T... values) {
        if (type == ControlUi.MULTIPLERADIO && values.length == 1 && values[0] instanceof List) {
            // 当参数类型是多个可选值,构建的面板组件就是多重可选框
            // 一般只传递一个参数(list)
            List<T> enumlist = (List<T>) values[0];
            for (T t : enumlist) {
                if (enumValue != null) {
                    this.enumValue.add(t);
                }
            }
        } else {
            for (T t : values) {
                if (enumValue != null) {
                    this.enumValue.add(t);
                }
            }
        }

        return this;
    }

    public Option<T> addEnumValue(List<T> values) {
        for (T t : values) {
            if (enumValue != null) {
                this.enumValue.add(t);
            }
        }
        return this;
    }

    public void clearEnumValues() {
        this.enumValue.clear();
    }

    /**
     * 可选的控件类型
     */
    public static enum ControlUi {
        TEXTFILED, COMBOX, FILECHOSE, MULTIPLERADIO
    }

    /**
     * 
     */
    public Option() {
    }

}
