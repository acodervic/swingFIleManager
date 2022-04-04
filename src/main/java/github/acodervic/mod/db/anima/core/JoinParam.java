package github.acodervic.mod.db.anima.core;

import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.core.functions.TypeFunction;
import github.acodervic.mod.db.anima.enums.OrderBy;
import github.acodervic.mod.db.anima.utils.AnimaUtils;
 
/**
 * @author biezhi
 * @date 2018/4/16
 */
public class JoinParam {
 
    private Class<? extends Model> joinModel;
    private String                 onLeft;
    private String                 onRight;
    private String                 fieldName;
    private String                 orderBy;

    public JoinParam(Class<? extends Model> joinModel) {
        this.joinModel = joinModel;
    }

    /**
     * @return the onLeft
     */
    public String getOnLeft() {
        return onLeft;
    }

    /**
     * @return the onRight
     */
    public String getOnRight() {
        return onRight;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return the orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * @param orderBy the orderBy to set
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @param onLeft the onLeft to set
     */
    public void setOnLeft(String onLeft) {
        this.onLeft = onLeft;
    }

    /**
     * @param onRight the onRight to set
     */
    public void setOnRight(String onRight) {
        this.onRight = onRight;
    }

    /**
     * @return the joinModel
     */
    public Class<? extends Model> getJoinModel() {
        return joinModel;
    }

    /**
     * @param joinModel the joinModel to set
     */
    public void setJoinModel(Class<? extends Model> joinModel) {
        this.joinModel = joinModel;
    }
    public <T, R> JoinParam as(TypeFunction<T, R> function) {
        String fieldName = AnimaUtils.getLambdaColumnName(function);
        this.setFieldName(fieldName);
        return this;
    }

    public <T, S extends Model, R> JoinParam on(TypeFunction<T, R> left, TypeFunction<S, R> right) {
        String onLeft  = AnimaUtils.getLambdaFieldName(left);
        String onRight = AnimaUtils.getLambdaColumnName(right);
        this.setOnLeft(onLeft);
        this.setOnRight(onRight);
        return this;
    }

    public <S extends Model, R> JoinParam order(TypeFunction<S, R> rightField, OrderBy orderBy) {
        String columnName = AnimaUtils.getLambdaColumnName(rightField);
        this.orderBy = columnName + " " + orderBy.name();
        return this;
    }

    public JoinParam order(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }
}
