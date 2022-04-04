package github.acodervic.mod.swing.table;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import github.acodervic.mod.swing.MyComponent;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.awt.Color;
import java.awt.Component;

public class MyTable<T> extends JTable {
    Function<TipInfo<T>, String> rowHoverToolTipTextFUn;// List<T>为当前鼠标所在的行对象
    MyComponent<MyTable, T> myComponent;// 引用的myComponent
    ListBindTableModel<T> listBindTableModel;
    Color selectedRowBorderColor;// 选中行边框颜色
    Color selectedRowBackgroundColor;// 选中行背景颜色
    Color selectedRowTextColor;// 选中行背景颜色

    /**
     * @return the selectedRowBackgroundColor
     */
    public Color getSelectedRowBackgroundColor() {
        return selectedRowBackgroundColor;
    }

    /**
     * @return the selectedRowBorderColor
     */
    public Color getSelectedRowBorderColor() {
        if (selectedRowBorderColor == null) {
            // 默认是jlable颜色
            selectedRowBorderColor = new JLabel().getForeground();
        }
        return selectedRowBorderColor;
    }

    /**
     * 设置选中行边框颜色
     *
     * @param selectedRowBorderColor the selectedRowBorderColor to set
     */
    public void setSelectedRowBorderColor(Color selectedRowBorderColor) {
        this.selectedRowBorderColor = selectedRowBorderColor;
    }

    /**
     * 设置列显示渲染函数,输入参数分别为
     * 实体,被渲染的列名,返回一个组件,用于填充表格列,要求必须表格模式为ListBindTableModel,注意返回不能为可编辑对象,如Combox,JTextField等。因为此函数只提供显示组件
     * 如果需要可以修改的组件如Combox,则使用setTableColumnEditRenderFun函数
     *
     * @param tableColumnRenderFunP
     */
    public void setTableColumnShowRenderFun(Function<ColumnRenderObj, Component> tableColumnRenderFunP) {
        nullCheck(tableColumnRenderFunP);
        DefaultTableColumnModel defaultTableColumnModel = (DefaultTableColumnModel) getColumnModel();
        for (int i = 0; i < defaultTableColumnModel.getColumnCount(); i++) {
            MyTableCellRenderer<T> myTableCellRenderer = new MyTableCellRenderer<T>();
            myTableCellRenderer.setTableColumnShowRenderFun(tableColumnRenderFunP);
            defaultTableColumnModel.getColumn(i).setCellRenderer(myTableCellRenderer);
        }
    }

    /**
     * 设置列编辑渲染函数,输入参数分别为
     * 实体,被渲染的列名,返回一个组件,用于填充表格列,要求必须表格模式为ListBindTableModel,注意返回不能可编辑对象,如Combox,JTextField等。
     *
     * @param tableColumnEditRenderFunP
     */
    public void setTableColumnEditRenderFun(Function<ColumnRenderObj, Component> tableColumnEditRenderFunP) {
        nullCheck(tableColumnEditRenderFunP);
        DefaultTableColumnModel defaultTableColumnModel = (DefaultTableColumnModel) getColumnModel();
        for (int i = 0; i < defaultTableColumnModel.getColumnCount(); i++) {
            TableColumn column = defaultTableColumnModel.getColumn(i);
            MyTableCellRenderer<T> myTableCellRenderer = new MyTableCellRenderer<T>();
            myTableCellRenderer.setTableColumnEditRenderFun(tableColumnEditRenderFunP);
            // 每一列都是一个
            // 设置编辑函数
            column.setCellEditor(myTableCellRenderer);
        }
    }

    /**
     * 行渲染器
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        JComponent jc = (JComponent) c;
        if (jc !=null) {
                    // 选中行返回固定的边框
                    if (isRowSelected(row)){
                        jc.setBorder(new MatteBorder(1, 0, 1, 0, getSelectedRowBorderColor()));
                     }
                if (selectedRowTextColor!=null) {
                    jc.setForeground(selectedRowTextColor);
                }
        }
        if (jc==null) {
            throw new RuntimeException("返回了null的对象,发生错误!");
        }
        return c;
    }

    /**
     * @return the selectedRowTextColor
     */
    public Color getSelectedRowTextColor() {
        return selectedRowTextColor;
    }
    /**
     * @param selectedRowTextColor the selectedRowTextColor to set
     */
    public void setSelectedRowTextColor(Color selectedRowTextColor) {
        this.selectedRowTextColor = selectedRowTextColor;
    }
    Color tipBackground;
    Color tipForeground;
    @Override
    public String getToolTipText() {
        Point mousePosition = getMousePosition();
        if (this.rowHoverToolTipTextFUn != null && mousePosition != null) {
            int rowIndex = rowAtPoint(mousePosition);
            if (rowIndex != -1) {
                int columnindex = columnAtPoint(mousePosition);

                TipInfo<T> tipInfo = new TipInfo<T>(this.listBindTableModel.getDataByRowIndex(rowIndex), rowIndex,
                        columnindex);
                return this.rowHoverToolTipTextFUn.apply(tipInfo);
            }
        }
        return null;
    }

    /**
     * 设置行悬浮的处理函数,传递的T为当前悬浮行的对象
     *
     * @param rowHoverToolTipTextFUn the rowHoverToolTipTextFUn to set
     */
    public void setRowHoverToolTipTextFun(Function<TipInfo<T>, String> rowHoverToolTipTextFUn) {
        this.rowHoverToolTipTextFUn = rowHoverToolTipTextFUn;
    }

    @Override
    public ListBindTableModel<T> getModel() {
        return (ListBindTableModel<T>) super.getModel();
    }

    /**
     * 构造一个表格
     *
     * @param dm
     */
    public MyTable(MyComponent<MyTable, T> myComponent, ListBindTableModel model) {
        super(model);
        nullCheck(myComponent, model);
        this.myComponent = myComponent;
        this.listBindTableModel = model;
    }

    /**
     * @param tipBackground the tipBackground to set
     */
    public void setTipBackground(Color tipBackground) {
        this.tipBackground = tipBackground;
    }

    /**
     * @param tipForeground the tipForeground to set
     */
    public void setTipForeground(Color tipForeground) {
        this.tipForeground = tipForeground;
    }

    @Override
    public JToolTip createToolTip() {
        JToolTip createToolTip = super.createToolTip();
        MyCustomToolTip jt = new MyCustomToolTip(this);
        jt.setToolTipText(createToolTip.getTipText());
        return jt;
    }

    class MyCustomToolTip extends JToolTip {
        public MyCustomToolTip(JComponent component) {
            setComponent(component);
            setBackground(tipBackground);
            setForeground(tipForeground);
        }
    }

    int scrollableUnitIncrement=-1;//在滚动面板内每次滚动的像素
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if (scrollableUnitIncrement!=-1) {
            return scrollableUnitIncrement;
        }
        return  super.getScrollableUnitIncrement(visibleRect, orientation, direction);
    }


    /**
     * 设置表格在滚动面板内每次滚动的像素
     * @param scrollableUnitIncrement the scrollableUnitIncrement to set
     */
    public void setScrollableUnitIncrement(int scrollableUnitIncrement) {
        this.scrollableUnitIncrement = scrollableUnitIncrement;
    }
}
