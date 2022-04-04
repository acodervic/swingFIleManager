package github.acodervic.mod.swing.table;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.awt.Component;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import github.acodervic.mod.data.str;

/**
 * 我的表格单元格渲染器,用来替换TableCellRenderer并实现更多功能 T 是绑定的实体属性,注意只能用在ListBindTableModel上
 *
 */
public class MyTableCellRenderer<T> extends DefaultCellEditor implements TableCellRenderer {
    public MyTableCellRenderer(JCheckBox checkBox) {
        super(checkBox);
    }

    public MyTableCellRenderer() {
        super(new JTextField());
    }

    /**
     *
     */
    private static final long serialVersionUID = -2280062791584592903L;

    Function<ColumnRenderObj, Component> tableColumnShowRenderFun;// 自定义显示渲染列,输入参数分别为 实体,被渲染的列名,返回一个组件,用于填充表格列
    Function<ColumnRenderObj, Component> tableColumnEditRenderFun;// 自定义编辑渲染列,输入参数分别为 实体,被渲染的列名,返回一个组件,用于填充表格列
    DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();// jtable默认的表格渲染器

    Component editComponent;// 当编辑的时候提供的组件
    Component showComponent;// 当显示渲染的时候提供的组建

    /**
     * 设置列显示渲染函数,输入参数分别为 实体,被渲染的列名,返回一个组件,用于填充表格列
     *
     * @param tableColumnRenderFun the tableColumnRenderFun to set
     */
    public void setTableColumnShowRenderFun(Function<ColumnRenderObj, Component> tableColumnRenderFun) {
        nullCheck(tableColumnRenderFun);
        this.tableColumnShowRenderFun = tableColumnRenderFun;
    }

    /**
     * 设置列编辑渲染函数,输入参数分别为 实体,被渲染的列名,返回一个组件,用于填充表格列
     *
     * @param tableColumnRenderFun the tableColumnRenderFun to set
     */
    public void setTableColumnEditRenderFun(Function<ColumnRenderObj, Component> tableColumnEditRenderFun) {
        nullCheck(tableColumnEditRenderFun);
        this.tableColumnEditRenderFun = tableColumnEditRenderFun;
    }

    /**
     * 渲染列并返回组件
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object text, boolean isSelected, boolean hasFocus,
            int row, int column) {
         ListBindTableModel<T> listModel = (ListBindTableModel) table.getModel();
        // 读取当前渲染的列
        String headerColumn = listModel.getHeaders().get(column);
        try {
            // 读取当前渲染的对象
            T dataByRowIndex = listModel.getDataByRowIndex(row);
            // 通知函数处理
            if (tableColumnShowRenderFun != null && dataByRowIndex != null && headerColumn != null) {
                ColumnRenderObj obj = new ColumnRenderObj<T>(dataByRowIndex, headerColumn, new str(text).to_s(), column,
                        row, isSelected);
                showComponent = tableColumnShowRenderFun.apply(obj);
            }
            // 如果创建失败.则使用默认的渲染器渲染返回
            if (showComponent == null) {
                showComponent = defaultTableCellRenderer.getTableCellRendererComponent(table, text, isSelected,
                        hasFocus, row, column);
            }
            // showComponent.setBackground(mtable.getBackground());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return showComponent;
    }

    /**
     * 覆盖提供编辑的方法
     *
     * @param table
     * @param value
     * @param isSelected
     * @param row
     * @param column
     * @return
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (editComponent == null) {
            // 初始化编辑组件
            try {
                if (tableColumnEditRenderFun != null) {
                    ListBindTableModel model = (ListBindTableModel) table.getModel();
                    ColumnRenderObj obj = new ColumnRenderObj<T>((T) model.getDataByRowIndex(row),
                            model.getColumnName(column), new str(value).to_s(), column, row, isSelected);
                    editComponent = tableColumnEditRenderFun.apply(obj);
                }
                if (editComponent == null) {
                    editComponent = super.getTableCellEditorComponent(table, value, isSelected, row, column);
                }
                MyTable mtable = (MyTable) table;
                editComponent.setForeground(mtable.getBackground());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //
        }

        // 否则返回 默认的编辑组件
        return editComponent;
    }

    /**
     * 当单元格编辑完成之后从如何组件读取数据
     */
    @Override
    public Object getCellEditorValue() {
        if (this.editComponent instanceof JComboBox) {
            return ((JComboBox) this.editComponent).getSelectedItem();
        } else if (this.editComponent instanceof JTextField) {
            return new str(((JTextField) this.editComponent).getText()).to_s();
        }
        return null;
    }

}
