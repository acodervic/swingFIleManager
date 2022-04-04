package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.function.Supplier;

/**
 * 逻辑处理对象
 */
public class Logic {
    
    Boolean  doBool=true;
    /**
     * 如果上一个逻辑处理返回为true,则执行相关操作
     * @param supplier
     * @return
     */
    public Logic IfTrue(Supplier<Boolean> supplier) {
        nullCheck(supplier);
        if (doBool) {
            this.doBool=supplier.get();
        }
        return this;
    }
    /**
     * 如果上一个逻辑处理返回为flase,则执行相关操作
     * @param supplier
     * @return
     */
    public Logic ifFlase(Supplier<Boolean> supplier) {
        nullCheck(supplier);
        if (!doBool) {
            this.doBool=supplier.get();
        }
        return this;
    }
    /**
     * 如果上一个逻辑处理返回为null,则执行相关操作
     * @param supplier
     * @return
     */
    public Logic ifNull(Supplier<Boolean> supplier) {
        nullCheck(supplier);
        if (doBool==null) {
            this.doBool=supplier.get();
        }
        return this;
    }
    /**
     * 创建一个逻辑处理对象
     * @param supplier
     */
    public Logic(Supplier<Boolean>  supplier){
        this.doBool=supplier.get();
    }    
}    
