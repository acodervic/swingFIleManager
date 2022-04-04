package github.acodervic.mod.swing.combox;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.function.Function;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
/**
 * u代表绑定对象,p代表getSelectedItem()输出值
 */
public class MyCombox<U, P> extends JComboBox<U> {
    Function<U, P> setSelectedItemFun;// 读取选中项的函数

    public    MyCombox(ListBindComboBoxModel model) {
        super(model);
        JcomboxItemProveedoresRenderer jcomboxItemProveedoresRenderer = new JcomboxItemProveedoresRenderer(item  ->{
            return item.toString();
         });
         jcomboxItemProveedoresRenderer.setMyCombox(this);
        setRenderer(jcomboxItemProveedoresRenderer);
    }
    public    MyCombox() {
        super(new ListBindComboBoxModel<>());
    }
    /**
     * 设置输出函数
     *
     * @param setSelectedItemFun
     */
    public void setSelectedItemFun(Function<U, P> setSelectedItemFun) {
        nullCheck(setSelectedItemFun);
        this.setSelectedItemFun = setSelectedItemFun;
    }

    /**
     * 读取选中的值,通过自定义函数,如果未定义则返回Null
     * 
     * @return
     */
    public Opt<P> getSelectedItem_() {
        Object selectedItem = this.getSelectedItem();
        Opt<P> ret = new Opt<P>();
        if (setSelectedItemFun != null) {
            if (selectedItem!=null) {
                ret.of(this.setSelectedItemFun.apply((U) selectedItem));
            }
        }else{
            ret.of((P) selectedItem);
        }
        return ret;
    }

    @Override
    public int getSelectedIndex() {
        return ((ListBindComboBoxModel)this.getModel()).getNowSelectedIndex();
    }

    @Override
    public ComboBoxModel<U> getModel() {
        ListBindComboBoxModel model= ((ListBindComboBoxModel)super. getModel());
        return model;
    }
    @Override
    public Object getSelectedItem() {
        return ((ListBindComboBoxModel)this.getModel()).getSelectedItem();
    }

    @Override
    public synchronized void setSelectedItem(Object anObject) {
        //ListBindComboBoxModel model=   (ListBindComboBoxModel)getModel();
        //当选中项更改后触发监听器
        //if (selectedItemReminder != model.getSelectedItem()) {
            getModel().setSelectedItem(anObject);
            selectedItemChanged();
        //}else{
        //}
        //fireActionEvent();
    
    }
    @Override
    protected void selectedItemChanged() {
        if (getSelectedIndex()==-1) {
            //super的selectedItemChanged不会通知-1的情况需要手动通知
                fireItemStateChanged(new ItemEvent(this,ItemEvent.ITEM_STATE_CHANGED,
                                                   null,
                                                   ItemEvent.SELECTED));
        }else{
            super.selectedItemChanged();
        }
    }

    public U getSelectedItemByIndex(int index) {
        return getModel().getElementAt(index);
    }

    /**
     * 返回当前选中值,,如果可编辑则返回文本输入值,否则返回当前选中值
     * @return
     */
    public Opt getSelectedItemOrEditableString() {
        Opt<Object> ret=new Opt<>();
        if (isEditable) {//如果是可编辑的,则返回编辑的string
               str text =new str( ((JTextField)getEditor().getEditorComponent()).getText());
               if (text.notEmpty()) {
                   ret.of(text.to_s());
               }
        }else{
             ret.of(getSelectedItem_().get());
        }
        return ret;
    }

    @Override
    @Deprecated
    public void addItem(U item) {
        // TODO Auto-generated method stub
        throw new RuntimeException("请使用binddatasrouce来操作!");
    }


    /**
     * 绑定数据源
     * @param souces
     * @return
     */
    public String bindDataSources(List  souces) {
        return ((ListBindComboBoxModel)getModel()).bindDataSources(souces);
    }
}
