package github.acodervic.mod.swing;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.newList;
import static github.acodervic.mod.utilFun.notNull;
import static github.acodervic.mod.utilFun.print;
import static github.acodervic.mod.utilFun.sleep;
import static github.acodervic.mod.utilFun.str;
import static github.acodervic.mod.utilFun.value;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.data.str;
import github.acodervic.mod.reflect.ReflectUtil;
import github.acodervic.mod.swing.annotation.TableRowObject;
import github.acodervic.mod.swing.combox.JcomboxItemProveedoresRenderer;
import github.acodervic.mod.swing.combox.ListBindComboBoxModel;
import github.acodervic.mod.swing.combox.MyCombox;
import github.acodervic.mod.swing.table.ColumnRenderObj;
import github.acodervic.mod.swing.table.EditData;
import github.acodervic.mod.swing.table.ListBindTableModel;
import github.acodervic.mod.swing.table.MyTable;
import github.acodervic.mod.swing.table.TipInfo;
import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.Task;
import github.acodervic.mod.thread.TimePool;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.FieldInfo;
import net.miginfocom.swing.MigLayout;

/**
 * 所有swing编程的顶层封装
 *
 * @param 包裹的swing组件
 * @param
 */
public class MyComponent<T, U> {
    T JComponents;// 包裹的组件
    // 右键弹出菜单
    JPopupMenu m_popupMenu = new JPopupMenu();
    static FixedPool swingEventPool;
    static int addEditwidth = 550;

    public synchronized static FixedPool getSwingEventPool() {
        if (swingEventPool == null) {
            swingEventPool = new FixedPool(2, 60, "MyComponentWwingEventPool");

        }
        return swingEventPool;
    }

    /**
     * 右键弹出菜单,返回新添加的JMenuItem对象
     *
     * @param name   菜单名称
     * @param action 执行函数
     * @return
     */
    public JMenuItem addPopupMenu(String name, Consumer<java.awt.event.ActionEvent> action) {
        nullCheck(this.JComponents);
        nullCheck(name, action);
        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText(name);
        delMenItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getSwingEventPool().exec(new Task<>(() -> {
                    // 在新的线程处理防止阻塞swing
                    action.accept(evt);
                    return null;
                }));
            }
        });

        // 添加菜单
        m_popupMenu.add(delMenItem);
        // 将菜单绑定到容器
        // 如果是table这里绑定两个,一个是容器本身,一个是容器的父容器,为的是,避免table的行数为0的时候菜单依然有效
        JComponent t = (JComponent) get();
        if (this.JComponents instanceof JTable) {
            if (t.getParent() == null) {
                print("注册菜单 " + name + " 到父面板失败,请保证调用addPopupMenu的时候jtale已经被添加到一个面板之上!");
            } else {
                print("注册菜单 " + name + " 到父面板");
                new MyComponent<>(t.getParent()).onRightClickEvent(evt -> {
                    m_popupMenu.show(t, evt.getX(), evt.getY());
                });
            }
        } else {
            if (t.getParent().getParent() != null) {
                // 如果有可能则添加到更加上级的面板
                new MyComponent<>(t).onRightClickEvent(evt -> {
                    m_popupMenu.show(t, evt.getX(), evt.getY());
                });
            }
        }
        //必须注册到当前组建上面
        new MyComponent<>(t).onRightClickEvent(evt -> {
            m_popupMenu.show(t, evt.getX(), evt.getY());
        });
        return delMenItem;
    }

    /**
     * 右键弹出菜单,返回新添加的JMenuItem对象
     *
     * @param name   菜单名称
     * @param action 执行函数
     * @return
     */
    public MyPopupMenu addPopupMenu(MyPopupMenu popupMenu) {
        nullCheck(this.JComponents);
        nullCheck(popupMenu);
        // 添加菜单
        m_popupMenu.add(popupMenu.getMenu());
        // 将菜单绑定到容器
        // 如果是table这里绑定两个,一个是容器本身,一个是容器的父容器,为的是,避免table的行数为0的时候菜单依然有效
        JComponent t = (JComponent) get();
        if (this.JComponents instanceof JTable) {
            print("注册菜单 popupMenu.getMenu() 到父面板");
            new MyComponent<>(t.getParent()).onRightClickEvent(evt -> {
                m_popupMenu.show(t, evt.getX(), evt.getY());
            });
        }
        print("注册菜单 popupMenu.getMenu() 到主面板");
        new MyComponent<>(t).onRightClickEvent(evt -> {
            m_popupMenu.show(t, evt.getX(), evt.getY());
        });

        return popupMenu;
    }

    /**
     * 读取组件
     *
     * @return
     */
    public T get() {
        return this.JComponents;
    }

    /**
     * 当点击的时候触发
     *
     * @return
     */
    public MyComponent<T, U> onClick(Consumer consumer) {
        nullCheck(this.JComponents);
        nullCheck(consumer);
        return onClick(consumer, 1);
    }

    /**
     * 当点击的时候触发,传递Event对象
     *
     * @return
     */
    public MyComponent<T, U> onClickEvent(Consumer<java.awt.event.MouseEvent> consumer) {
        nullCheck(this.JComponents);
        nullCheck(consumer);
        return onClickEvent(consumer, 1);
    }

    /**
     * 当点击的时候触发
     *
     * @return
     */
    public MyComponent<T, U> onClick(Consumer consumer, int clickCount) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == clickCount && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                        getSwingEventPool().exec(new Task<>(() -> {
                            consumer.accept(JComponents);
                            return null;
                        }));
                    }
                }
            });
        }
        return this;
    }

    /**
     * 当点击的时候触发,传递event
     * 
     * @return
     */
    public MyComponent<T, U> onClickEvent(Consumer<java.awt.event.MouseEvent> consumer, int clickCount) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        if (e.getClickCount() == clickCount && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                            consumer.accept(e);
                        }
                        return null;
                    }));
                }
            });
        }
        return this;
    }

    /**
     * 当双击的时候触发,传递event
     *
     * @return
     */
    public MyComponent<T, U> onDoubleClickEvent(Consumer<java.awt.event.MouseEvent> consumer) {
        nullCheck(this.JComponents);
        return onClickEvent(consumer, 2);
    }

    /**
     * 当双击的时候触发
     *
     * @return
     */
    public MyComponent<T, U> onDoubleClick(Consumer consumer) {
        nullCheck(this.JComponents);
        return onClick(consumer, 2);
    }

    /**
     * 右键点击元素的时候触发 传递event
     * 
     * @param consumer
     * @param clickCount
     * @return
     */
    public MyComponent<T, U> onRightClickEvent(Consumer<java.awt.event.MouseEvent> consumer, int clickCount) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                         if (e.getClickCount() == clickCount && e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                            consumer.accept(e);
                    }
                  }
            });
        }
        return this;

    }

    /**
     * 右键点击元素的时候触发
     *
     * @param consumer
     * @param clickCount
     * @return
     */
    public MyComponent<T, U> onRightClick(Consumer consumer, int clickCount) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        if (e.getClickCount() == clickCount && e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                            consumer.accept(JComponents);
                        }
                        return null;
                    }));
                }
            });
        }
        return this;

    }

    /**
     * 右键点击元素的时候触发
     *
     * @param consumer
     * @param clickCount
     * @return
     */
    public MyComponent<T, U> onRightClick(Consumer consumer) {
        nullCheck(this.JComponents);
        return onRightClick(consumer, 1);
    }

    /**
     * 右键点击元素的时候触发 传递event
     * 
     * @param consumer
     * @return
     */
    public MyComponent<T, U> onRightClickEvent(Consumer<java.awt.event.MouseEvent> consumer) {
        nullCheck(this.JComponents);
        return onRightClickEvent(consumer, 1);
    }

    /**
     * 右键点击元素的时候触发 传递event
     * 
     * @param consumer
     * @return
     */
    public MyComponent<T, U> onDoubleRightClickEvent(Consumer<java.awt.event.MouseEvent> consumer) {
        nullCheck(this.JComponents);
        return onRightClickEvent(consumer, 2);
    }

    /**
     * 右键点击元素的时候触发
     *
     * @param consumer
     * @param clickCount
     * @return
     */
    public MyComponent<T, U> onDoubleRightClick(Consumer consumer) {
        nullCheck(this.JComponents);
        return onRightClick(consumer, 2);
    }

    /**
     * 当鼠标进入控件
     *
     * @return
     */
    public MyComponent<T, U> onMouseIn(Consumer consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        consumer.accept(JComponents);
                        return null;
                    }));
                }
            });
        }
        return this;
    }

    /**
     * 当鼠标移出控件
     *
     * @return
     */
    public MyComponent<T, U> onMouseOut(Consumer consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        consumer.accept(JComponents);
                        return null;
                    }));
                }

            });
        }
        return this;
    }

    /**
     * 当鼠标按下的时候,不分鼠标键
     *
     * @return
     */
    public MyComponent<T, U> onMousePressed(Consumer consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        consumer.accept(JComponents);
                        return null;
                    }));
                }

            });
        }
        return this;
    }

    /**
     * 当鼠标弹起的时候调用,不分鼠标键
     *
     * @return
     */
    public MyComponent<T, U> onMouseReleased(Consumer consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        consumer.accept(JComponents);
                        return null;
                    }));
                }

            });
        }
        return this;
    }

    /**
     * 当键盘按下的时候被调用
     *
     * @return
     */
    public MyComponent<T, U> onKeyDown(Consumer<KeyEvent> consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            nullCheck(consumer);
            ((JComponent) this.JComponents).addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        consumer.accept(e);
                        return null;
                    }));
                }
            });
        }
        return this;
    }

    public void setTableColumnEditRenderFun(Function<ColumnRenderObj, Component> tableColumnRenderFunP) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            nullCheck(tableColumnRenderFunP);
            getMyTable().setTableColumnEditRenderFun(tableColumnRenderFunP);
            getMyTable().setTableColumnShowRenderFun(tableColumnRenderFunP);
            getMyTable().repaint();
        }
    }

    /**
     * 设置列显示渲染函数,输入参数分别为
     * 实体,被渲染的列名,返回一个组件,用于填充表格列,要求必须表格模式为ListBindTableModel,注意返回不能为可编辑对象,如Combox,JTextField等。因为此函数只提供显示组件
     * 如果需要可以修改的组件如Combox,则使用setTableColumnEditRenderFun函数, 返回null默认使用原始的组建渲染单元格
     *
     * @param tableColumnRenderFunP
     */
    public void setTableColumnShowRenderFun(Function<ColumnRenderObj, Component> tableColumnRenderFunP) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            nullCheck(tableColumnRenderFunP);
            getMyTable().setTableColumnShowRenderFun(tableColumnRenderFunP);
            getMyTable().repaint();
        }
    }

    /**
     * 当焦点改变的时候触发
     *
     * @param get_opt
     * @param lost_opt
     * @return
     */
    public MyComponent<T, U> onFocus(Consumer get_opt, Consumer lost_opt) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JComponent) {
            ((JComponent) this.JComponents).addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent e) {
                    if (notNull(get_opt)) {
                        getSwingEventPool().exec(new Task<>(() -> {
                            get_opt.accept(JComponents);
                            return null;
                        }));
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (notNull(lost_opt)) {
                        getSwingEventPool().exec(new Task<>(() -> {
                            lost_opt.accept(JComponents);
                            return null;
                        }));
                    }
                }
            });
        }
        return this;
    }

    /**
     * 当前下拉框 被选中的时候触发,通过e.getItem() 即可读取被选中的内容
     *
     * @param consumer
     * @return
     */
    public MyComponent<T, U> onComboxSelectChange(Consumer<ItemEvent> consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof MyCombox) {
            ((MyCombox) this.JComponents).addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    //不能在线程池中转发!否则会出现各种ui错误!
                         if (e.getStateChange() == ItemEvent.SELECTED) {
                            getSwingEventPool().exec(new Task<>(()  ->{
                                consumer.accept(e);
                                return null;
                            }));
                        }
                }
            });
        }
        return this;
    }

    /**
     * 当前下拉框 被选中的时候触发,通过e.getItem() 即可读取被选中的内容
     *
     *
     * @param consumer
     * @return
     */
    public MyComponent<T, U> onComboxSelectChangeValue(Consumer<Opt<U>> consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof MyCombox) {
            MyCombox jComboBox = (MyCombox) this.JComponents;
            onComboxSelectChange(e -> {
                 Object selectedItem = jComboBox.getSelectedItem();
                try {
                    if (selectedItem == null ) {
                        // 代表未选中
                        consumer.accept(new Opt<U>());
                    } else {
                        consumer.accept(new Opt<U>(((U) selectedItem)));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        }
        return this;
    }

    /**
     * 设置表格的行过滤函数
     *
     * @param
     * @param filteredRowsFun
     * @return
     */
    public MyComponent<T, U> addTableRowsFilterFun(Function<U, Boolean> filteredRowsFun) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
            model.addFilterRowsFun(filteredRowsFun);
        }
        return this;
    }

        /**
     * 获取表格的行过滤函数
     *
     * @param
     * @param filteredRowsFuns
     * @return
     */
    public List<Function> getTableRowsFilterFunList() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
            return model.getFilteredRowsFuns();
        }
        return new ArrayList<>(); 
    }


    /**
     * 删除过滤规则并关闭过滤模式
     *
     * @param
     * @param filteredRowsFuns
     */
    public void deleteTableRowsFilterFun() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
            model.setRowListFunction(null);
            stopFilteredRows();// 关闭过滤模式
        }
    }

    /**
     * 开始进行过滤行，让表格进入过滤模式
     *
     * @param
     * @param filteredRowsFuns
     */
    public void startFilterRows() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
            model.filterRows();
        }
    }

    /**
     * 开始停止过滤行,让表格回到普通模式
     *
     * @param
     * @param filteredRowsFuns
     */
    public void stopFilteredRows() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
            model.stopFilterRows();
        }
    }

    /**
     * 当前表格 被选中的时候触发, 即可读取被选中的内容(被消费的是jtable)
     *
     * @param consumer 传递jtable直接操作
     * @return
     */
    public MyComponent<T, U> onSelectedRowsChange(Consumer<JTable> consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            // 选中行改变
            JTable jTable = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) jTable.getModel();
            jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        int[] selectedRows = jTable.getSelectedRows();
                        List<Integer> selectRowsInteger = new ArrayList<Integer>();
                        for (int i = 0; i < selectedRows.length; i++) {
                            selectRowsInteger.add(selectedRows[i]);
                        }
                        // 当鼠标弹起的时候触发
                        if (!e.getValueIsAdjusting() && model.selectChange(selectRowsInteger)) {
                            consumer.accept(jTable);
                        }
                        return null;
                    }));
                }
            });
        }
        return this;
    }

    /**
     * 当前表格 被选中的时候触发,通过e.getItem() 即可读取被选中的内容 在swing的处理线程中同步处理
     *
     *
     * @param consumer 消费者为当前选中的行,如果没有则返回一个size=0的list
     * @param clazz    行对象类型
     * @return
     */
    public MyComponent<T, U> onSelectedRowsChangeValuesSync(Consumer<List<U>> consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            // 选中行改变
            JTable jTable = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) jTable.getModel();
            jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int[] selectedRows = jTable.getSelectedRows();
                    List<Integer> selectRowsInteger = new ArrayList<Integer>();
                    for (int i = 0; i < selectedRows.length; i++) {
                        selectRowsInteger.add(selectedRows[i]);
                    }
                    // 当鼠标弹起的时候触发
                    if (!e.getValueIsAdjusting() && model.selectChange(selectRowsInteger)) {
                        consumer.accept(getSelectedItems());
                    }
                }
            });
        }
        return this;
    }

     int tableLastSelectedColumnIndex = -1;

     public void enableSelectColumnHeaderColor(Color c, Color rawFontColor, Color rawBackgroundColor) {
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
            table.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    java.awt.Point p = e.getPoint();
                    int rowAtPoint = table.rowAtPoint(p);

                    if (rowAtPoint > 0) {
                        // 高亮header
                        tableLastSelectedColumnIndex = table.columnAtPoint(p);
                        table.getTableHeader().repaint();
                    } else {
                        tableLastSelectedColumnIndex = -1;
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                }
            });

            // 用于高亮header的渲染器
            TableCellRenderer headerColumnRenderer = new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    // 选获取默认渲染完成的表头列
                    Component tableCellRendererComponent = defaultTableCellRenderer.getTableCellRendererComponent(table,
                            value, isSelected, hasFocus, row, column);

                    if (tableLastSelectedColumnIndex != -1) {
                        if (tableLastSelectedColumnIndex == column) {
                            tableCellRendererComponent.setForeground(c);
                        } else {
                            // 设置原始前景颜色
                            if (notNull(rawFontColor)) {
                                tableCellRendererComponent.setForeground(rawFontColor);
                            }
                            if (notNull(rawBackgroundColor)) {
                                tableCellRendererComponent.setBackground(rawBackgroundColor);
                            }
                        }
                    }
                    return tableCellRendererComponent;
                }

            };
            // 设置列头渲染器
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setHeaderRenderer(headerColumnRenderer);
            }

        }
    }

    /**
     * 
     * 当前表格 被选中的时候触发,通过e.getItem() 即可读取被选中的内容
     *
     *
     * @param consumer 消费者为当前选中的行,如果没有则返回一个size=0的list
     * @param clazz    行对象类型
     * @return
     */
    public MyComponent<T, U> onSelectedRowsChangeValues(Consumer<List<U>> consumer) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            // 选中行改变
            JTable jTable = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) jTable.getModel();
            jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        int[] selectedRows = jTable.getSelectedRows();
                        List<Integer> selectRowsInteger = new ArrayList<Integer>();
                        for (int i = 0; i < selectedRows.length; i++) {
                            selectRowsInteger.add(selectedRows[i]);
                        }
                        // 当鼠标弹起的时候触发
                        if (!e.getValueIsAdjusting() && model.selectChange(selectRowsInteger)) {
                            consumer.accept(getSelectedItems());
                        }
                        return null;
                    }));
                }
            });
        }
        return this;
    }

    /**
     * 读取下拉框或者表格的地一个选中项,如果没有则返回null的Opt
     *
     * @param clazz
     * @return
     */
    public Opt<U> getSelectedItem() {
        nullCheck(this.JComponents);
        try {
            if (this.JComponents instanceof MyCombox) {
                Object selectedItem = ((MyCombox) this.JComponents).getSelectedItem();
                if (selectedItem == null ) {
                    return new Opt<U>();
                }
                return new Opt<U>((U) selectedItem);
            }
            if (this.JComponents instanceof JTable) {
                JTable jtable = (JTable) this.JComponents;
                ListBindTableModel model = (ListBindTableModel) jtable.getModel();
                if (jtable.getSelectedRows().length != 0) {
                    return new Opt<U>((U) model.getTableDataList().get(jtable.getSelectedRows()[0]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Opt<U>();
    }

    /**
     * 读取下拉框选中项,如果没有则返回null
     *
     * @param clazz
     * @return
     */
    public List<U> getSelectedItems() {
        nullCheck(this.JComponents);
        List<U> returns = new ArrayList<U>();

        if (this.JComponents instanceof MyCombox) {

        } else if (this.JComponents instanceof JTable) {
            JTable jtable = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) jtable.getModel();
            int[] rows = jtable.getSelectedRows();
            List<U> tableDataList = model.getTableDataList();
            for (int i = 0; i < rows.length; i++) {
                try {
                    returns.add(tableDataList.get(rows[i]));
                } catch (Exception e) {
                }
            }

        }
        return returns;
    }

    /**
     * 读取下拉框/表格 选中项的下标,如果没有则返回-1
     *
     * @param clazz
     * @return
     */
    public int getSelectedIndex() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof MyCombox) {
            return ((MyCombox) this.JComponents).getSelectedIndex();
        }
        if (this.JComponents instanceof JTable) {
            return ((JTable) this.JComponents).getSelectedRow();
        }
        return -1;
    }

    /**
     * 设置下拉框的 item 的文本读取函数,注意不是选中值,而是点击出现下拉的每个对象的显示值,必须在调用此方法之前进行数据绑定!否则会出错!
     *
     * @param
     * @param getItemTextFunction
     * @return
     * @return
     */
    public MyComponent<T, U> setJcomBoxItemTextFunction(Function<U, String> getItemTextFunction) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof MyCombox) {
            nullCheck(getItemTextFunction);
            MyCombox jComboBox = (MyCombox) this.JComponents;
            JcomboxItemProveedoresRenderer aRenderer = new JcomboxItemProveedoresRenderer(getItemTextFunction);
            aRenderer.setMyCombox(jComboBox);
            jComboBox.setRenderer(aRenderer);
            
            /**
             *           new Thread(() -> {
                // 为了避免在绑定之前进行渲染会出现NPE异常,所以延迟1秒钟加载
                sleep(1000);
                //jComboBox.setRenderer(new JcomboxItemProveedoresRenderer<U>(getItemTextFunction));
            }).start();
             */
        }
        return this;
    }

    /**
     * 给下拉框添加实体
     *
     * @param item
     * @return
     * @return
     */
    public MyComponent<T, U> addItem(U item) {
        nullCheck(this.JComponents);
        getSwingEventPool().exec(new Task<>(() -> {
            ((JComboBox<U>) JComponents).addItem(item);
            return null;
        }));
        return this;
    }

    /**
     * 给下拉框添加实体
     *
     *
     * @param item
     * @return
     */
    public MyComponent addItems(List<U> items) {
        nullCheck(this.JComponents);
        JComboBox<U> combox = ((JComboBox<U>) this.JComponents);
        getSwingEventPool().exec(new Task<>(() -> {
            for (int i = 0; i < items.size(); i++) {
                combox.addItem(items.get(i));
            }
            return null;
        }));
        return this;
    }

    /**
     * 自动修改对象
     * 
     * @param row                 被修改的对象
     * @param title_String_opt    标题
     * @param components_opt      自定义组建,和类属性名一致
     * @param onNewRowSaveFun_opt 处理自动修改后的对象,返回true则关闭窗口
     * @param comboxNeedSelectIndex0 如果是combox 且其绑定有值是否选中地一个下标的值
     * @param comboxNeedAutoAddItemByFieldValueOrEnumValues 如果是combox,是否将当前实体对象的值/枚举注解值 通过addItem添加到下拉框, 当被填充的值有其他含义(如关联其它表的id)这时候最好选择false,然后通过setSelectedItemIf 函数进行手动绑定可选数据+选中
     * @return
     */
    public Map<String, MyCombox> editRow(U row, String title_String_opt, Map<String, MyCombox> components_opt,
            Function<U, Boolean> onNewRowSaveFun_opt,boolean comboxNeedSelectIndex0,boolean comboxNeedAutoAddItemByFieldValueOrEnumValues) {
        nullCheck(row);
        Map<String, MyCombox> retAllComponets=new HashMap<>();
        if (notNull(components_opt)) {
            components_opt.forEach((k,v)  ->{
                retAllComponets.put(k, v);
            });
        }
        // 构建添加面板
        JFrame editFrame = new JFrame(title_String_opt != null ? title_String_opt + "_dialog" : "添加行_dialog");
        JButton save = new JButton("保存");
        JButton canel = new JButton("取消");
        // 存储组件的map
        HashMap<String, MyCombox> textFieldMaps = new HashMap<String, MyCombox>();
        HashMap<String, MyCombox> requiredTextFieldMaps = new HashMap<String, MyCombox>();// 必须组件
        HashMap<String, String> typeMaps = new HashMap<String, String>();
        HashMap<String, str> verificationRegexs = new HashMap<String, str>();
        HashMap<String, String> verificationErrorMessages = new HashMap<String, String>();
        comboxNeedAutoAddItemByFieldValueOrEnumValues=true;
        // 构造填充数据面板
        // 读取类被注解需要填充的字段并生产ui
        List<FieldInfo> rowClassInfo = null;
        // 尝试获取两次,有时候一次获取不到
        try {
            rowClassInfo = ReflectUtil.getClassFiledsInfo(row.getClass());
        } catch (Exception e) {
            try {
                rowClassInfo = ReflectUtil.getClassFiledsInfo(row.getClass());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        if (rowClassInfo == null) {
            rowClassInfo = new ArrayList<>();
        }
        // 设置布局
        editFrame.getContentPane().setLayout(new MigLayout("wrap 4", "[][grow][grow][grow]"));
        // 从rowClassInfo中读取被TableRowObject注解的字段
        for (int i = 0; i < rowClassInfo.size(); i++) {
            FieldInfo fieldInfo = rowClassInfo.get(i);
            AnnotationInfo tableRowObjectAnnotationInfo = fieldInfo.getAnnotationInfo(TableRowObject.class.getName());
            if (tableRowObjectAnnotationInfo != null) {
                String filedName = fieldInfo.getName();

                AnnotationParameterValueList parameterValues = tableRowObjectAnnotationInfo.getParameterValues();
                // 读取类型
                String type = parameterValues.get("type").getValue().toString();

                // 读取枚举值
                String[] enumValues = (String[]) parameterValues.get("enumValues").getValue();

                // 读取默认值
                str defaultVaule = str(parameterValues.get("defaultVaule").getValue());

                // 读取是否可编辑
                boolean editable = str(parameterValues.get("editable").getValue()).to_B();

                // 读取验证表达式
                str verificationRegex = str(parameterValues.get("verificationRegex").getValue());
                verificationRegexs.put(filedName, verificationRegex);

                // 读取表达和i验证失败的提示消息
                str verificationErrorMessage = str(parameterValues.get("verificationErrorMessage").getValue());
                verificationErrorMessages.put(filedName, verificationErrorMessage.to_s());

                // 读取是否可编辑
                int heigtt = str(parameterValues.get("comboxHeight").getValue()).to_I();

                // 读取值
                Object value = null;
                try {
                    Field f1 = row.getClass().getDeclaredField(filedName);
                    f1.setAccessible(true);// 设置可访问
                    value = f1.get(row);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 读取是否必填
                boolean required = (boolean) parameterValues.get("required").getValue();

                // 读取显示标签
                String lableText = str(parameterValues.get("lableText").getValue()).to_s();
                editFrame.getContentPane().add(new JLabel(lableText), ",width 30%!");
                MyCombox combox = null;
                // 如果是手动提供的组件
                if (components_opt != null && components_opt.get(filedName) != null) {
                    combox = components_opt.get(filedName);
                } else {
                    // 自动生成组件 并当定数据
                    combox = new MyCombox<>(new ListBindComboBoxModel<>());
                    // 绑定数据
                    ListBindComboBoxModel model = (ListBindComboBoxModel) combox.getModel();
                    // jJComboBox.sets
                    combox.setName(filedName);// 使用属性名作为控件名称
                    retAllComponets.put(filedName, combox);
                    if (editable) {
                        List<Object> data = new ArrayList<>();
                        data.add(value);
                        // 再添加枚举值
                        for (int j = 0; j < enumValues.length; j++) {
                            data.add(enumValues[j]);
                        }
                        model.bindDataSources(data);// 绑定数据

                    } else {
                        // 当不可编辑时候
                        // 只有comboxNeedAutoAddItemByFieldValue=true 才自动添加绑定值+枚举
                        if (comboxNeedAutoAddItemByFieldValueOrEnumValues) {
                            List<Object> data = new ArrayList<>();
                            data.add(value);
                            // 再添加枚举值
                            for (int j = 0; j < enumValues.length; j++) {
                                data.add(enumValues[j]);
                            }
                            model.bindDataSources(data);// 绑定数据
                        }

                    }
                    // 选中默认值
                    if (comboxNeedSelectIndex0 && combox.getItemCount() > 0) {
                        combox.setSelectedIndex(0);
                    } else {
                        if (value != null) {
                            combox.setSelectedIndex(0);// value默认添加到第一个所以直接选中
                        }
                    }
                }
                typeMaps.put(filedName, type);
                if (notNull(combox)) {
                                        // 默认边框
                                        combox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                                        combox.setEditable(editable);// 设置可以编辑
                                        editFrame.getContentPane().add(combox, "wrap,growx,span 3,width 67%!");
                                        // 放入集合中
                                        textFieldMaps.put(filedName, combox);
                                        if (required) {
                                            requiredTextFieldMaps.put(filedName, combox);
                                        }
                    
                }
            }
        }

        // ====================

        // 当按下 ESC 关闭窗口
        editFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
        editFrame.getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editFrame.dispose();
            }
        });

        editFrame.setLocationRelativeTo(null);

        // 计算添加窗体大小
        int height = ((textFieldMaps.size() - 1) * 39);
        editFrame.add(save, "span 2,growx");
        editFrame.add(canel, "span 2,growx");
        height += (39 * 2);
        editFrame.setSize(addEditwidth, height);
        editFrame.setVisible(true);
        moveFrameCenter(editFrame);
        new MyComponent(canel).onClick((obj) -> {
            editFrame.dispose();
            ;
        });
        // 点击保存
        new MyComponent(save).onClick(obj -> {
            boolean pass = true;
            // 进行必填检测
            Object[] requiredKeys = requiredTextFieldMaps.keySet().toArray();
            for (int i = 0; i < requiredKeys.length; i++) {
                MyCombox jComboBox = requiredTextFieldMaps.get(requiredKeys[i].toString());
                Opt selectedItemOrEditableString = jComboBox.getSelectedItemOrEditableString();
                if (str(selectedItemOrEditableString.get()).isEmpty()) {
                    jComboBox.setBorder(BorderFactory.createLineBorder(Color.RED));
                    if (pass) {
                        pass = false;
                    }
                } else {
                    jComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                }
            }

            if (!pass) {
                return;
            }
            boolean pass2 = true;
            Object[] verificationRegexsKeys = verificationRegexs.keySet().toArray();
            for (int i = 0; i < verificationRegexsKeys.length; i++) {
                String key = verificationRegexsKeys[i].toString();
                str regex = verificationRegexs.get(key);
                MyCombox combox = textFieldMaps.get(key);
                // 进行验证检测
                if (!str(combox.getSelectedItemOrEditableString().get()  ).hasRegex(regex.to_s())) {
                    MessageBox.showInfoMessageDialog(null, "验证无法通过", str(verificationErrorMessages.get(key)).to_s());
                    pass2 = false;
                }
            }
            if (!pass2) {
                return;
            }
            // 当点击保存,则自动构造一个 行对象并通过set填充对象属性.要求被保存的函数必须提供无参构造函数
            // 构造一个默认的行空对象
            try {
                // 先读取所有控件的数据
                textFieldMaps.keySet().forEach(filedName -> {
                    try {
                        // 通过fileName来找到set方法并填充到对象内部
                        Field f1 = row.getClass().getDeclaredField(filedName);
                        f1.setAccessible(true);
                        // 解析参数类型并自动将string转换
                        MyCombox jComboBox = textFieldMaps.get(filedName);
                        Opt selectValue = jComboBox.getSelectedItemOrEditableString();
                       
                        if (selectValue.notNull_()) {
                            str value=str(selectValue.get());
                            // 覆盖属性
                            String string = typeMaps.get(filedName);
                            switch (string.toString()) {
                                case "mrrobot.swing.annotation.TableRowObjectType.STRING":
                                    f1.set(row, value.to_s());
                                    break;
                                case "mrrobot.swing.annotation.TableRowObjectType.INT":
                                    f1.set(row, value.to_I());
                                    break;
                                case "mrrobot.swing.annotation.TableRowObjectType.BOOL":
                                    f1.set(row, value.to_B());
                                    break;
                                default:
                                    // String
                                    f1.set(row, value.to_s());
                                    break;
                            }
                        } else {
                            // 说明属性已经被置null
                            f1.set(row, null);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                getSwingEventPool().exec(new Task<>(() -> {
                    // 将设置好的对象发送给处理函数
                    if (onNewRowSaveFun_opt.apply(row)) {
                        // 关闭窗口
                        editFrame.dispose();
                    }
                    return null;
                }));

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return retAllComponets;// 返回传递进来的Ui方便操作
    }

    /**
     * 给表格添加实体
     *
     * @param
     *
     * 
     * @param item
     * @return
     */
    public MyComponent addRow(U row) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            getSwingEventPool().exec(new Task<>(() -> {
                ListBindTableModel model = (ListBindTableModel) ((JTable) JComponents).getModel();
                model.addRow(row);
                return null;
            }));
        }
        return this;
    }

    /**
     * 给表格添加实体,会直接添加到当前绑定的数据集
     * 
     *
     * @param row
     * @return
     */
    public MyComponent addRows(ArrayList<U> rows) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            getSwingEventPool().exec(new Task<>(() -> {
                ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
                for (int i = 0; i < rows.size(); i++) {
                    U row = rows.get(i);
                    model.addRow(row);
                }
                return null;
            }));
        }
        return this;
    }

    /**
     * 移动frame到屏幕中央
     * 
     * @param frame
     */
    public static void moveFrameCenter(JFrame frame) {
        if (frame != null) {
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
            int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
            frame.setLocation(x, y);
        }

    }

    /**
     * 按下esc关闭 jfFrame
     * 
     * @param jfFrame
     */
    public static void escCloseFrame(JFrame jfFrame) {
        jfFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
        jfFrame.getRootPane().getActionMap().put("Cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jfFrame.dispose();
            }
        });

    }

    /**
     * 清空下拉框或jtable,注意会清除绑定的数据
     */
    public void removeAllItems() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof MyCombox) {
            ((MyCombox) this.JComponents).removeAllItems();
        } else if (this.JComponents instanceof JTable) {
            ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
            model.clearAllRowsWithData();
        }
    }

    /**
     * 按照下标删除下拉框或jtable
     */
    public void removeItems(List<Integer> indexs) {
        nullCheck(this.JComponents);
        nullCheck(indexs);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof MyCombox) {
                MyCombox jComboBox = ((MyCombox) this.JComponents);
                for (int i = 0; i < indexs.size(); i++) {
                    Integer index = indexs.get(i);
                    jComboBox.remove(index);
                    return null;
                }
            } else if (this.JComponents instanceof JTable) {
                ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
                model.removeRowsByIndex(indexs);
            }
            return null;
        }));
    }

    /**
     * 清空下拉框或jtable
     */
    public void removeItem(Integer index) {
        nullCheck(index);
        if (this.JComponents instanceof MyCombox) {
            ((MyCombox) JComponents).remove(index);
        } else if (this.JComponents instanceof JTable) {
            ListBindTableModel model = (ListBindTableModel) ((JTable) this.JComponents).getModel();
            model.removeRowsByIndex(newList(index));
        }
    }

    /**
     * 清空下拉框或jtable 当前选择的项
     */
    public void removeSelectedItem() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof MyCombox) {
            MyCombox jcomboBox = ((MyCombox) this.JComponents);
            jcomboBox.remove(jcomboBox.getSelectedIndex());

        } else if (this.JComponents instanceof JTable) {
            JTable jtable = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) jtable.getModel();
            int[] selectedRows = jtable.getSelectedRows();
            // 马上取消选中,否则在删除的过程中会不断触发选中事件导致删除混乱
            jtable.clearSelection();
            model.removeRowsByIndex(selectedRows);
        }
    }

    /**
     * 设置工具提示
     * 
     * @param text
     * @return
     */
    public MyComponent setToolTipText(String text) {
        nullCheck(this.JComponents);
        nullCheck(text);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof JComponent) {
                ((JComponent) this.JComponents).setToolTipText(text);
            }
            return null;
        }));
        return this;
    }

    /**
     * 给combox和jtable绑定一个空数据源,用于清除显示数据
     *
     * @return
     */
    public void setEmpty() {
        nullCheck(this.JComponents);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof JTable) {
                JTable table = (JTable) this.JComponents;
                ListBindTableModel model = (ListBindTableModel) table.getModel();
                model.setEmpty();
            } else if (this.JComponents instanceof MyCombox) {
                MyCombox combox = (MyCombox) this.JComponents;
                ListBindComboBoxModel model = (ListBindComboBoxModel) combox.getModel();
                model.setEmpty();
            }
            return null;
        }));
    }

    /**
     * 构建下拉框并绑定数据
     *
     * @param
     *
     * @param headers
     * @param data_opt
     * @param selectRows 同时可以在表格中选中的行数
     * @return
     */
    public   synchronized void initCombox(String soucesName_opt, List<U> data_opt) {
        ListBindComboBoxModel<U> myComboBoxModel = new ListBindComboBoxModel<U>(soucesName_opt, data_opt);
        MyCombox<U,U> jComboBox = new MyCombox(myComboBoxModel);
        // 处理列宽
        this.JComponents = (T) jComboBox;
        setComboxRightClickUnSelected();
    }

    /**
     * combox启用右键 空选
     */
     void setComboxRightClickUnSelected() {
        onRightClick(obj  ->{
            setComboxUnSelected();
        });
    }

    /**
     * 构建一个下拉框
     *
     * @param
     *
     * @param headers
     * @param data_opt
     * @param selectRows 同时可以在表格中选中的行数
     * @return
     */
    public  synchronized void initCombox() {
        ListBindComboBoxModel<U> myComboBoxModel = new ListBindComboBoxModel<U>();
        MyCombox jComboBox = new MyCombox(myComboBoxModel);
        // 处理列宽
        this.JComponents = (T) jComboBox;
        setComboxRightClickUnSelected();
    }

    /**
     * 构建一个下拉框
     *
     * @param
     *
     * @param headers
     * @param data_opt
     * @return
     */
    public synchronized void initCombox(MyCombox comboBox) {
        nullCheck(comboBox);
        ListBindComboBoxModel<U> myComboBoxModel = new ListBindComboBoxModel<U>();
        comboBox.setModel(myComboBoxModel);
        // 处理列宽
        this.JComponents = (T) comboBox;
        setComboxRightClickUnSelected();
    }

    /**
     * 构建一个表格,内部会自动加上序号列
     *
     * @param
     *
     * @param headers
     * @param data_opt
     * @param selectRows 同时可以在表格中选中的行数
     * @return
     */
    public  synchronized void initTable(List<String> headers, List<U> data_opt, int selectRows,
            String soucesName) {
        nullCheck(soucesName);
        JTable jTable = new MyTable(this, new ListBindTableModel(data_opt, headers, soucesName));
        jTable.setSelectionMode(selectRows);
        // 处理列宽
        this.JComponents = (T) jTable;
    }

    /**
     * 构建一个表格,内部会自动加上序号列
     *
     * @param
     *
     * @param headers 表头
     *
     * @return
     */
    public synchronized void initTable(List<String> headers) {
        JTable jTable = new MyTable(this, new ListBindTableModel(null, headers, null));
        // 处理列宽
        this.JComponents = (T) jTable;
    }

    /**
     * 设置表格鼠标悬浮时候的提示文本
     *
     * @param
     *
     * @param headers 表头
     *
     * @return
     */
    public void setTableRowHoverTipTextFun(Function<TipInfo<U>, String> rowHoverToolTipTextFUn) {
        nullCheck(this.JComponents);
        nullCheck(rowHoverToolTipTextFUn);
        getMyTable().setRowHoverToolTipTextFun(rowHoverToolTipTextFUn);
    }

    /**
     * 读取MyTable
     * 
     * @return
     */
    public MyTable getMyTable() {
        return (MyTable) this.JComponents;
    }

    /**
     * 设置combox的选中项,注意只适用于ListBindComboBoxModel的combox,且item必须存在于ListBindComboBoxModel内部的当前选中的list!
     *
     * @param
     * @param item
     * @return
     */
    public Boolean setComboxSelectedItem(U item) {
        nullCheck(this.JComponents);
             if (this.JComponents instanceof MyCombox) {
                MyCombox combox = (MyCombox) this.JComponents;
                ListBindComboBoxModel model = (ListBindComboBoxModel) combox.getModel();
                    //找到 item在 当前下拉框中的下标
                    //必须在swing的渲染线程中调用!
                        SwingUtilities.invokeLater(() -> {
                            model.setSelectedItem(item);
                            combox.repaint();
                        });
                    return true;
            }
        return false;
    }

    /**
     * 设置table的列宽度 每一个参数的都是 0.2 0.3 0.5 加起来等一1
     *
     * @param percentages
     */
    public void setColumnWidth(double... percentages) {
        nullCheck(this.JComponents);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof JTable) {
                final double factor = 10000;
                TableColumnModel model = ((JTable) this.JComponents).getColumnModel();
                for (int columnIndex = 0; columnIndex < percentages.length; columnIndex++) {
                    try {
                                            // DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
                    // renderer.setHorizontalAlignment(SwingConstants.LEFT); // 表头向左对齐
                    // renderer.setBackground(Color.LIGHT_GRAY); //不设置颜色
                    TableColumn column = model.getColumn(columnIndex);
                    // column.setHeaderRenderer(renderer);
                    column.setPreferredWidth((int) (percentages[columnIndex] * factor));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return null;
        }));
    }

    /**
     *
     * @param
     * @param headers
     * @param data
     * @return
     */
    public synchronized void initTable(List<String> headers, List<U> data, String soucesName) {
        initTable(headers, data, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, soucesName);
    }

    /**
     * 设置表格最终List处理函数 ,注意传入的参数是原始绑定的数据集
     * (如果是过滤模式+排序模式则传递是是先过滤然后在排序的临时数据集),在非过滤模式,函数内部
     * 直接操作输入集合会直接操作(remove/add)原始绑定的数据集,如果你只是想要在修改最终结果显示.必须现将原始数据集添加到一个新的集合中(addAll)然后对新的集合进行操作。
     * 如果处理函数返回null,则会直接使用getTableBindDataList(true)返回的集合数据
     * 
     * @param processResultFun
     * @return
     */
    public MyComponent<T, U> setTableProcessResultFun(Function<List<U>, List<U>> processResultFun) {
        nullCheck(this.JComponents);
        nullCheck(processResultFun);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof JTable) {
                JTable jtable = ((JTable) this.JComponents);
                ListBindTableModel model = (ListBindTableModel) jtable.getModel();
                model.setProcessResultFun(processResultFun);
            }
            return null;
        }));
        return this;
    }

    /**
     * 选取选中行
     *
     * @param clazz
     * @return
     */
    public List<U> getSelectedRows() {
        nullCheck(this.JComponents);
        List<U> selectedRowsData = new ArrayList<U>();
        if (this.JComponents instanceof JTable) {
            JTable jtable = ((JTable) this.JComponents);
            int[] selectedRows = jtable.getSelectedRows();
            ListBindTableModel model = (ListBindTableModel) jtable.getModel();
            for (int i = 0; i < selectedRows.length; i++) {
                try {
                    selectedRowsData.add((U) model.getDataByRowIndex(selectedRows[i]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return selectedRowsData;
    }

    /**
     * 获取表格选中最首行
     * @return
     */
    public Opt<U> getSelectedFirstRow() {
        Opt<U> ret=new Opt<>();
        List<U> selectedRows = getSelectedRows();
        if (selectedRows.size()>0) {
            ret.of(selectedRows.get(0));
        }
        return ret;
    }

        /**
     * 获取表格选中最尾行
     * @return
     */
    public Opt<U> getSelectedLastRow() {
        Opt<U> ret=new Opt<>();
        List<U> selectedRows = getSelectedRows();
        if (selectedRows.size()>0) {
            ret.of(selectedRows.get(selectedRows.size()-1));
        }
        return ret;
    }
    /**
     * 在修改了表格内绑定的List,之后来更新表格,此操作会整体刷新表格
     */
    public synchronized void updateTable() {
        nullCheck(this.JComponents);
        updateTable(false);
    }

    /**
     * 更新combox
     */
    public synchronized void updateCombox() {
        nullCheck(this.JComponents);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof MyCombox) {
                MyCombox combox = (MyCombox) this.JComponents;
                ListBindComboBoxModel model = (ListBindComboBoxModel) combox.getModel();
                // 如果 model 中当前绑定的list已经为0或者为null,则清空当前选中的项目
                if (model.getNowBindList() == null || model.getNowBindList().size() == 0) {
                    model.setSelectedItem(null);
                }
                // 为了重新刷新combox必须重新设置ListBindComboBoxModel!
                combox.setModel(new ListBindComboBoxModel<>());
                combox.setModel(model);
            }
            return null;
        }));
    }

    /**
     * 清空所有表格行,并不清除当前数据,其实本质是切换到一个list,size=0的数据源
     */
    public synchronized void clearTableRows() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.clearRows();
        }
    }

        /**
     * 清空所有表格行,并不清除当前数据,其实本质是切换到一个list,size=0的数据源
     */
    public synchronized void clearTableFilters() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.clearFilterRows();;
        }
    }

    /**
     * 对表格数据进行排序(使用已经设置的排序器)
     */
    public void tableSortRows() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.sortRows();
        }
    }

    /**
     * 对表格数据进行排序
     */
    public void tableSortRows(Comparator<U> sortFun) {
        nullCheck(this.JComponents);
        nullCheck(sortFun);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.sortRows(sortFun);
        }
    }

    /**
     * 清除已经设置的表格排序器
     */
    public synchronized void clearTableSortRows() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.clearSortRows();

        }
    }

    /**
     * 对表格列的比进行输出,便于设置setColumnWidth函数
     */
    public void autoPrintTableColumnWidth(String tableName) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            TableColumnModel columnModel = table.getColumnModel();
            TimePool.getStaticTimePool().Interval(3000, () -> {
                String widths = "";
                DecimalFormat df = new DecimalFormat("0.000");
                int d = 0;
                for (int i = 0; i < model.getHeaders().size(); i++) {
                    TableColumn column = columnModel.getColumn(i);
                    // 计算当前列和表格宽度的比
                    double a = ((double) column.getWidth()) / ((double) table.getWidth());
                    d += (a * 1000);
                    // 如果是最后一个列,则将所以不足100的补足到最后一列上
                    if (i == model.getHeaders().size() - 1) {
                        if ((100 - d) > 0) {
                            double b = ((double) (1000 - d)) / 1000;
                            a += b;
                        }
                    }
                    widths += ("," + df.format(a));
                }
                // 输出比
                print("表格" + tableName + "  的所有列宽度比为  " + widths);
            });
        }
    }

    /**
     * 对表格数据进行排序(使用已经设置的排序器)
     */
    public synchronized void clearTableProcessResultFun() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.clearProcessResultFun();// 必须立即调用

        }
    }

    /**
     * 开启自动更新table 后台会开启一个线程来监控表格数据源,此函数对List的修改和删除都有效
     *
     * @return
     */
    public MyComponent autoUpdateRows(int updateTimeMs, Boolean autoSrollToEnd) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            new Thread(() -> {
                JTable table = (JTable) this.JComponents;
                ListBindTableModel model = (ListBindTableModel) table.getModel();
                if (autoSrollToEnd == null) {
                    autoSrollToEnd.valueOf("true");
                }
                Value fistSrollEnd = value(true);// 第一次增加默认滚动到表尾
                while (true) {
                    sleep(updateTimeMs);
                    // 刷新一次数据
                    updateTable(autoSrollToEnd);
                }

            }).start();
        }
        return this;
    }

    /**
     * 更新表格数据,
     *
     * @param autoSrollToEnd 是否自动滚动到末尾
     */
    private synchronized void updateTable(Boolean autoSrollToEnd) {
        nullCheck(this.JComponents);
        nullCheck(autoSrollToEnd);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof JTable) {
                JTable table = (JTable) this.JComponents;
                ListBindTableModel model = (ListBindTableModel) table.getModel();
                if (autoSrollToEnd == null) {
                    autoSrollToEnd.valueOf("true");
                }
                Value fistSrollEnd = model.getFistSrollEnd();// 第一次增加默认滚动到表尾
                // if (model.bindDataIsChange()) {
                if (true) {
                    // 表格需要重新渲染
                    // 先清除先前的过滤数据,如果有的话
                    model.clearFilterRows();
                    // 进行重新数据渲染
                    if (model.processResultFunRunTime == ListBindTableModel.RUNINFIRST) {
                        model.processResult();
                    }
                    model.filterRows();// 如果有过滤函数自动进行过滤
                    model.sortRows();// 如果有排序函数则自动进行排序
                    if (model.processResultFunRunTime == ListBindTableModel.RUNINLAST) {
                        model.processResult();
                    }
                    // 刷新数据到表格中
                    // 表格初始化的时候直接滚动到末尾
                    if (fistSrollEnd.getBoolean()) {
                        table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
                        fistSrollEnd.setValue(false);
                    }
                    boolean allowSrollToEnd = false;
                    // System.out.println(getTableVisibleScope().getY()+" "+table.getRowCount());
                    if (getTableVisibleScope().getY() >= table.getRowCount() - 3) {
                        allowSrollToEnd = true;
                    }
                    int[] selectedRow = table.getSelectedRows();
                    model.fireTableDataChanged();
                    // 不改变选中行,因为表格刷新之后选中行会改变
                    if (selectedRow.length != 0) {
                        try {
                            if (selectedRow.length == 1) {
                                table.setRowSelectionInterval(selectedRow[0], selectedRow[0]);
                            } else {
                                table.setRowSelectionInterval(selectedRow[0], selectedRow[selectedRow.length - 1]);
                            }
                        } catch (Exception e) {
                        }
                    }
                    // 滚动到末尾
                    if (autoSrollToEnd && allowSrollToEnd) {
                        // 判断当前用户是否已经滚动到末尾
                        // 先读取当前表格的可视化区域,如果用户没有拉到末尾则不进行自动滚动
                        table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
                    }
                }
            }
            return null;
        }));

    }

    /**
     * 当点击表头时候触发,会返回,点击表头的字符串值
     * 
     * @return
     */
    public MyComponent<T, U> onTableHeaderClick(Consumer<String> action) {
        nullCheck(this.JComponents);
        nullCheck(action);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            table.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    getSwingEventPool().exec(new Task<>(() -> {
                        if (e.getClickCount() == 1 && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                            // 通知已经点击了表头标题
                            // 点击的第几列表头
                            int columnAtPoint = getMyTable().getTableHeader().columnAtPoint(e.getPoint());
                            if (columnAtPoint > -1 && model.getHeaders().get(columnAtPoint) != null) {
                                action.accept(model.getHeaders().get(columnAtPoint).toString());
                            }
                        }
                        return null;
                    }));
                }
            });
        }
        return this;
    }

    /**
     * 设置ProcessResultFun在update时候的运行时间,可用的值: ListBindTableModel.RUNINLAST
     * 在过滤和排序之前运行 ListBindTableModel.RUNINFIRST 在过滤和排序之后运行
     *
     * @param runTime
     * @return
     */
    public MyComponent<T, U> setTableProcessResultFunRunTime(int runTime) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.setProcessResultFunRunTime(runTime);
        }
        return this;
    }

    /**
     * 设置表格名称
     * 
     * @param name
     * @return
     */
    public MyComponent<T, U> setTableName(String name) {
        nullCheck(this.JComponents);
        nullCheck(name);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.setTableName(name);
        }
        return this;
    }

    /**
     * 获取当前table的可视行范围
     * 
     * @return
     */
    public Point getTableVisibleScope() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            java.awt.Rectangle vr = table.getVisibleRect();
            int firstRow = table.rowAtPoint(vr.getLocation());
            vr.translate(0, vr.height);
            int endRow = table.rowAtPoint(vr.getLocation());
            if (firstRow != -1 && endRow == -1) {
                endRow = table.getRowCount() + 1;
                firstRow += 1;
            }
            return new Point(firstRow, endRow);
        }
        return null;
    }

    /**
     * 获取当前table的可视行数
     *
     * @return
     */
    public int getTableVisibleRowNum() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            java.awt.Rectangle vr = table.getVisibleRect();
            int firstRow = table.rowAtPoint(vr.getLocation());
            vr.translate(0, vr.height);
            int endRow = table.rowAtPoint(vr.getLocation());
            if (firstRow != -1 && endRow == -1) {
                endRow = table.getRowCount() + 1;
                firstRow += 1;
            }
            return endRow - firstRow;
        }
        return 0;
    }

    /**
     * 开启自动更新table 后台会开启一个线程来监控表格数据源,此函数对List的修改和删除都有效 默认每次插入新行滚动到末尾
     * 
     * @return
     */
    public MyComponent autoUpdateRows() {
        return autoUpdateRows(300, true);
    }

    /**
     * 添加一个数据源
     *
     * @param
     * @param name
     * @param souces
     * @return 返回数据源key
     */
    public String addDataSrouces(String name, List<U> souces) {
        nullCheck(this.JComponents);
        nullCheck(name, souces);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            return model.addDataSrouces(name, souces);
        }
        return "";
    }

    /**
     * 添加一个数据源
     *
     * @param
     * @param name
     * @param souces
     * @return 返回数据源key
     */
    public String addDataSrouces(List<U> souces) {
        nullCheck(this.JComponents);
        nullCheck(souces);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            return model.addDataSrouces(null, souces);
        }
        return "";
    }

    /**
     * 绑定数据源(给表格或下拉框添加数据源,会覆盖原来显示的),如果数据源不存在则自动调用addDataSrouces添加到数据源maps,如果数据源已经先前被绑定到table,则调用swich切换到数据源并刷新表格
     *
     * @param
     *
     * @param souces
     * @return
     * @return 数据源的key
     */
    public synchronized MyComponent binDataSources(List<U> souces) {
        nullCheck(this.JComponents);
        nullCheck(souces);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.bindDataSources(souces);
        } else if (this.JComponents instanceof MyCombox) {
            // 绑定combox
            MyCombox combox = (MyCombox) this.JComponents;
            ListBindComboBoxModel model = (ListBindComboBoxModel) combox.getModel();
            model.bindDataSources(souces);
            //刷新Combox,不然不会显示!
            SwingUtilities.invokeLater(()  ->{
                combox.repaint();
            });
        }
        return this;
    }

    /**
     * 当表格 列编辑完成的时候被调用
     *
     * @param
     * @param fun
     * @return
     */
    public MyComponent setEditFinishFunction(BiConsumer<U, EditData> fun) {
        nullCheck(this.JComponents);
        nullCheck(fun);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.setOnEditFinishFunction(fun);
        }
        return this;
    }

    /**
     * 切换数据源
     * 
     * @param name
     * @param souces
     * @return
     */
    public boolean swichDataSrouces(String name) {
        nullCheck(this.JComponents);
        nullCheck(name);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            return model.switchListSource(name);
        }
        return false;
    }

    /**
     * 设置处理行的函数.提供一个List<String> 每一个string代表一个列(不包含 序号 列),当每次读取行的时候,都会调用此函数进行数据提供,
     * 注意此函数内部处理应该非常迅速,否则当数据量大的时候,表格的滚动事件也会调用此函数,会变得非常卡顿.而且会卡住当前的swing的Ui渲染线程!
     *
     * @param
     *
     * @param rowClumunListFunction
     * @return
     */
    public MyComponent setRowListFunction(Function<U, List> rowClumunListFunction) {
        nullCheck(this.JComponents);
        nullCheck(rowClumunListFunction);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.setRowListFunction(rowClumunListFunction);
            return this;
        }
        return this;
    }

    public Opt<Function<T, List<String>>> getRowListFunction() {
        Opt<Function<T, List<String>>> ret = new Opt<>();
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            ret.of(model.getRowListFunction());
        }
        return ret;
    }

    /**
     * 设置jtable的那些列允许编辑,通过返回true,或false 来判断是否允许编辑,默认编辑组件为JtextFeild
     *
     * @param <D>
     * @param editCloumnFunction
     * @return
     */
    public MyComponent setTableEditCloumnFun(BiFunction<U, String, Boolean> editCloumnFunction) {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.setEditCloumnFun(editCloumnFunction);
            return this;
        }
        return this;

    }

    /**
     * 读取表格内绑定的List
     *
     * @param clazz
     * @return
     */
    public List<U> getBindDataList() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            return (List<U>) model.getTableDataList();
        }else if(this.JComponents instanceof MyCombox){
            MyCombox combox = (MyCombox) this.JComponents;
            ListBindComboBoxModel model = (ListBindComboBoxModel)combox.getModel();
            return model .getNowBindList();
        }
        return new ArrayList<U>();
    }

    /**
     * 在表的结尾添加一个空行,反映到数据绑定上,是添加一个新的null对象
     * 
     * @return
     */
    public MyComponent addEmptyRow() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            model.addRow(null);
        }
        return this;
    }

    /**
     * 滚动到某一行
     *
     * @param clazz
     * @return
     */
    public void scrollToRow(int rowIndex) {
        nullCheck(this.JComponents);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof JTable) {
                JTable table = (JTable) this.JComponents;
                ListBindTableModel model = (ListBindTableModel) table.getModel();
                table.scrollRectToVisible(table.getCellRect(rowIndex, 0, true));
            }
            return null;
        }));
    }

    /**
     * 滚动到某一行
     *
     * @param clazz
     * @return
     */
    public void scrollToLastRow() {
        nullCheck(this.JComponents);
        getSwingEventPool().exec(new Task<>(() -> {
            if (this.JComponents instanceof JTable) {
                JTable table = (JTable) this.JComponents;
                ListBindTableModel model = (ListBindTableModel) table.getModel();
                table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
            }
            return null;
        }));
    }

    /**
     * 重新绘制组件
     */
    public void repaint() {
        ((Component) this.JComponents).repaint();
    }

    /**
     * 设置combox选中index为-1
     */
    public void setComboxUnSelected() {
        nullCheck(this.JComponents);
        if (this.JComponents instanceof MyCombox) {
            SwingUtilities.invokeLater(()  ->{
                MyCombox jComboBox = (MyCombox) this.JComponents;
                jComboBox.setSelectedItem(null);
                jComboBox.repaint();
            });
        }
    }
    /**
     * 设置表格选中行边框颜色.table必须为myTable
     *
     * @param selectedRowBorderColor
     */
    public void setTableSelectedRowBorderColor(Color selectedRowBorderColor) {
        if (this.JComponents instanceof JTable) {
            getMyTable().setSelectedRowBorderColor(selectedRowBorderColor);
        }
    }

        /**
     * 设置表格选中行字体颜色.table必须为myTable
     *
     * @param selectedRowTextColor
     */
    public void setTableSelectedRowTextColor(Color selectedRowTextColor) {
        if (this.JComponents instanceof JTable) {
            getMyTable().setSelectedRowTextColor(selectedRowTextColor);
        }
    }



    /**
     * 显示一个添加数据的窗口
     *
     * @param title_String_opt    窗口的标题,可以为null
     * @param components_opt      一个key=Class的属性名(不分大小写)value为MyCombox组件的
     *                            map,注意MyCombox必须覆盖 setSelectedItemFun函数
     * @param onNewRowSaveFun_opt 当点击保存的函数, 如果 customPanleFun_opt不会null则此函数无效
     *                            参数=HashMap<String,Object>
     *
     *                            =HashMap<保存的列名,保存的列值,当返回true之后会关闭窗口
     * @param comboxNeedSelectIndex0 如果是combox 且其绑定有值是否选中地一个下标的值
     * @return
     */
    public Map<String, MyCombox> addRow(Class<U> rowsClass, String title_String_opt,
            Map<String, MyCombox> components_opt,
            Function<U, Boolean> onNewRowSaveFun_opt,boolean comboxNeedSelectIndex0) {
                Map<String, MyCombox> retAllComponets=new HashMap<>();
                if (notNull(components_opt)) {
                    components_opt.forEach((k,v)  ->{
                        retAllComponets.put(k, v);
                    });
                }
        if (this.JComponents instanceof JTable) {
            JTable table = (JTable) this.JComponents;
            ListBindTableModel model = (ListBindTableModel) table.getModel();
            // 构建添加面板
            JFrame addFrame = new JFrame(title_String_opt != null ? title_String_opt + "_dialog" : "添加行_dialog");
            JButton save = new JButton("保存");
            JButton canel = new JButton("取消");
            // 存储组件的map
            HashMap<String, MyCombox> textFieldMaps = new HashMap<String, MyCombox>();
            HashMap<String, MyCombox> requiredTextFieldMaps = new HashMap<String, MyCombox>();// 必须组件
            HashMap<String, String> typeMaps = new HashMap<String, String>();
            HashMap<String, str> verificationRegexs = new HashMap<String, str>();
            HashMap<String, String> verificationErrorMessages = new HashMap<String, String>();

            // 构造填充数据面板
            // 读取类被注解需要填充的字段并生产ui
            List<FieldInfo> rowClassInfo = null;
            // 尝试获取两次,有时候只获取一次会失败
            try {
                rowClassInfo = ReflectUtil.getClassFiledsInfo(rowsClass);
            } catch (Exception e) {
                try {
                    rowClassInfo = ReflectUtil.getClassFiledsInfo(rowsClass);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            if (rowClassInfo == null) {
                rowClassInfo = new ArrayList<>();
            }
            // 设置布局
            addFrame.getContentPane().setLayout(new MigLayout("", "[][][][]"));
            // 从rowClassInfo中读取被TableRowObject注解的字段
            for (int i = 0; i < rowClassInfo.size(); i++) {
                FieldInfo fieldInfo = rowClassInfo.get(i);
                AnnotationInfo tableRowObjectAnnotationInfo = fieldInfo
                        .getAnnotationInfo(TableRowObject.class.getName());
                if (tableRowObjectAnnotationInfo != null) {
                    String filedName = fieldInfo.getName();

                    AnnotationParameterValueList parameterValues = tableRowObjectAnnotationInfo.getParameterValues();
                    // 读取类型
                    String type = parameterValues.get("type").getValue().toString();

                    // 读取枚举值
                    String[] enumValues = (String[]) parameterValues.get("enumValues").getValue();

                    // 读取默认值
                    str defaultVaule = str(parameterValues.get("defaultVaule").getValue());

                    // 读取是否必填
                    boolean required = (boolean) parameterValues.get("required").getValue();

                    boolean editable = str(parameterValues.get("editable").getValue()).to_B();

                    // 读取验证表达式
                    str verificationRegex = str(parameterValues.get("verificationRegex").getValue());
                    verificationRegexs.put(filedName, verificationRegex);

                    // 读取表达和i验证失败的提示消息
                    str verificationErrorMessage = str(parameterValues.get("verificationErrorMessage").getValue());
                    verificationErrorMessages.put(filedName, verificationErrorMessage.to_s());

                    // 读取显示标签
                    String lableText = str(parameterValues.get("lableText").getValue()).to_s();
                    JLabel label = new JLabel(lableText);
                    addFrame.getContentPane().add(label, "width 30%!");
                    MyCombox jJComboBox = null;
                    // 如果是手动提供的组件
                    if (components_opt != null && components_opt.get(filedName) != null) {
                        jJComboBox = components_opt.get(filedName);
                    } else {
                        // 自动生成组件
                        jJComboBox = new MyCombox<>();
                        //绑定默认数据
                        ListBindComboBoxModel commodel=(ListBindComboBoxModel) jJComboBox.getModel();;
                        jJComboBox.setEditable(editable);// 设置可以编辑
                        jJComboBox.setName(filedName);// 使用属性名作为控件名称
                        retAllComponets.put(jJComboBox.getName(), jJComboBox);
                        //只有不是传递进的组件时候才自动绑定
                        if (components_opt != null && components_opt.get(filedName) == null) {
                            List data=new ArrayList<>();
                            // 加载默认数据
                            if (defaultVaule.notEmpty()) {
                                // 加载下拉文本框
                                // 先添加默认值
                                data.add(defaultVaule.to_s());
                            }
                            // 再添加枚举值
                            for (int j = 0; j < enumValues.length; j++) {
                                data.add(enumValues[j]);
                            }
                            commodel.bindDataSources(data);
                        }
                    }
                    if (jJComboBox != null) {

                        typeMaps.put(filedName, type);
                        // 选中默认值
                        if (comboxNeedSelectIndex0&&jJComboBox.getItemCount() > 0) {
                            jJComboBox.setSelectedIndex(0);
                        }
                        // 默认边框
                        jJComboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                        addFrame.getContentPane().add(jJComboBox, "wrap,growx,span 3,width 67%!");
                        // 放入集合中
                        textFieldMaps.put(filedName, jJComboBox);
                        if (required) {
                            requiredTextFieldMaps.put(filedName, jJComboBox);
                        }
                    }
                }

            }

            // 当按下 ESC 关闭窗口
            addFrame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
            addFrame.getRootPane().getActionMap().put("Cancel", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addFrame.dispose();
                }
            });

            addFrame.setLocationRelativeTo(null);


            // 计算添加窗体大小
            int height = ((textFieldMaps.size() - 1) * 39);
            addFrame.add(save, "span 2,growx");
            addFrame.add(canel, "span 2,growx");
            height += (39 * 2);
            addFrame.setSize(addEditwidth, height);
            addFrame.setVisible(true);
            moveFrameCenter(addFrame);
            new MyComponent(canel).onClick((obj) -> {
                addFrame.dispose();
            });
            // 点击保存
            new MyComponent(save).onClick(obj -> {
                boolean pass = true;
                // 进行必填检测
                Object[] requiredKeys = requiredTextFieldMaps.keySet().toArray();
                for (int i = 0; i < requiredKeys.length; i++) {
                    MyCombox comboBox = requiredTextFieldMaps.get(requiredKeys[i].toString());
                    Opt value=comboBox.getSelectedItemOrEditableString();
                    if (value.isNull_()) {
                        //代表选中为null
                        comboBox.setBorder(BorderFactory.createLineBorder(Color.RED));
                        if (pass) {
                            pass = false;
                        }
                    } else {
                        comboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    }
                }
                if (!pass) {
                    return;
                }



                boolean pass2 = true;
                Object[] verificationRegexsKeys = verificationRegexs.keySet().toArray();
                for (int i = 0; i < verificationRegexsKeys.length; i++) {
                    String key = verificationRegexsKeys[i].toString();
                    str regex = verificationRegexs.get(key);
                    MyCombox combox = textFieldMaps.get(key);
                    Object value=combox.getSelectedItemOrEditableString().get();//到这里的都是已经填好的
                    // 进行验证检测
                    if (!str(value).hasRegex(regex.to_s())) {
                        MessageBox.showInfoMessageDialog(null, "验证无法通过",
                                str(verificationErrorMessages.get(key)).to_s());
                        pass2 = false;
                    }
                }
                if (!pass2) {
                    return;
                }
                // 当点击保存,则自动构造一个 行对象并通过set填充对象属性.要求被保存的函数必须提供无参构造函数
                // 构造一个默认的行空对象
                try {
                    U newInstance = rowsClass.newInstance();
                    // 先读取所有控件的数据
                    textFieldMaps.keySet().forEach(filedName -> {
                        try {
                            // 通过fileName来找到set方法并填充到对象内部
                            Field f1 = newInstance.getClass().getDeclaredField(filedName);
                            f1.setAccessible(true);
                            // 解析参数类型并自动将string转换
                            MyCombox jComboBox = textFieldMaps.get(filedName);
                            Opt selectValue = jComboBox.getSelectedItemOrEditableString();
                      
                            if (selectValue.notNull_()) {
                                str vaule=str(selectValue.get());
                                // 覆盖属性
                                switch (typeMaps.get(filedName).toString()) {
                                    case "mrrobot.swing.annotation.TableRowObjectType.STRING":
                                        f1.set(newInstance, vaule.to_s());
                                        break;
                                    case "mrrobot.swing.annotation.TableRowObjectType.INT":
                                        f1.set(newInstance, vaule.to_I());
                                        break;
                                    case "mrrobot.swing.annotation.TableRowObjectType.BOOL":
                                        f1.set(newInstance, vaule.to_B());
                                        break;
                                    default:
                                        // String
                                        f1.set(newInstance, vaule.toString());
                                        break;
                                }
                            } else {
                                // 说明属性已经被置null
                                f1.set(newInstance, null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    getSwingEventPool().exec(new Task<>(() -> {
                        // 将设置好的对象发送给处理函数
                        if (onNewRowSaveFun_opt.apply(newInstance)) {
                            // 关闭窗口
                            addFrame.dispose();
                        }
                        return null;
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        }
        return retAllComponets;// 返回传递进来的Ui方便操作
    }

    /**
     * @param jComponents
     */
    public MyComponent(T jComponents) {
        JComponents = jComponents;
    }

    /**
     * @param jComponents
     */
    public MyComponent() {
    }

    /**
     * 判断当前鼠标是否在某一个组件上
     */
    public static boolean isMouseWithinComponent(Component c) {
        java.awt.Point mousePos = MouseInfo.getPointerInfo().getLocation();
        Rectangle bounds = c.getBounds();
        bounds.setLocation(c.getLocationOnScreen());
        return bounds.contains(mousePos);
    }

    /**
     * 设置选中combox或者table
     * @param selectFun
     * @return
     */
    public Boolean setSelectedItemIf(Function<U,Boolean>  selectFun) {
        nullCheck(this.JComponents);
        List<U> dataList = getBindDataList();
        Boolean isCombox= (get() instanceof MyCombox);
        Boolean isTable= (get() instanceof JTable);
    if (isTable) {
        JTable table=(JTable)get() ;
        for (int i = 0; i < dataList.size(); i++) {
            U u = dataList.get(i);
            Boolean needSelected = selectFun.apply(u);
            if (needSelected) {
                table.setRowSelectionInterval(i, i);
            }
        }
    }else{
        for (int i = 0; i < dataList.size(); i++) {
            U u = dataList.get(i);
            Boolean needSelected = selectFun.apply(u);
            if (needSelected) {
                     setComboxSelectedItem(u);
                    return true;
            }
        }
    }
        return false;
    }

    public void clearFilterRowFuns() {
        if ((get() instanceof JTable)) {
            getMyTable().getModel().clearProcessResultFun();
        }
    }

/**
 * 滚动到选中行
 * @param table
 * @param rowIndex
 * @param vColIndex
 */
    public   void scrollToJtableVisible( int rowIndex) {
        if ( (get() instanceof JTable)) {
            JTable table=(JTable)get();
            table .scrollRectToVisible(new Rectangle(table.getCellRect(rowIndex, 0, true)));
        }


    }


    public void setJComponents(Component com ) {
        this.JComponents=(T)com;
    }

    
}