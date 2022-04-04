package github.acodervic.mod.swing.combox;

import java.awt.Component;
import java.util.function.Function;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * jcombox的绑定对象的显示值渲染器,目的是通过函数对象来代替对toString的显示值操作
 *
 * @param <U>
 */
public class JcomboxItemProveedoresRenderer<U> extends DefaultListCellRenderer {
    Function<U, String> ItemTextFunction;// 用于提供item的显示值的函数
    MyCombox myCombox;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        if (value == null) {
            setText("未选中");
            return this;
        }
        Object selectedItem = myCombox.getSelectedItemByIndex(index);
        if (selectedItem != null && ItemTextFunction != null) {// 如果这时候的value="未选中,则不需要进行处理"
            try {
                value = ItemTextFunction.apply((U) selectedItem);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

    /**
     * @param getItemTextFunction
     */
    public JcomboxItemProveedoresRenderer(Function<U, String> getItemTextFunction) {
        this.ItemTextFunction = getItemTextFunction;
    }

    /**
     * @param myCombox the myCombox to set
     */
    public void setMyCombox(MyCombox myCombox) {
        this.myCombox = myCombox;
    }
}