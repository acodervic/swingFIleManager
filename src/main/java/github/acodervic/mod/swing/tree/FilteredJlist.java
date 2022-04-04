package github.acodervic.mod.swing.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.MyComponent;
import github.acodervic.mod.swing.tree.filter.TreeCellRendererCommponentArgsWarper;
import github.acodervic.mod.thread.Task;

/**
 *
 * Tree widget which allows the tree to be filtered on keystroke time. Only
 * nodes who's toString matches the search field will remain in the tree or its
 * parents.
 *
 * @author Copyright (c) Oliver.Watkins
 */

public class FilteredJlist<F> extends JPanel implements UtilFunInter {

    private String filteredText = "";
    private JScrollPane scrollpane = new JScrollPane();
    private JList jlist = new JList<>();
    private List<F> dataList = new ArrayList<>();
    final JTextField filtedTextField = new JTextField();
    Opt<Function<F, String>> filterFunction = new Opt<>();// 返回字符串 用来和搜索进行过滤
    Opt<Function<F, String>> treeNodeTextFunction = new Opt<>();
    Opt<Function<TreeCellRendererCommponentArgsWarper, Component>> treeCellRendererCommponentFunction = new Opt<>();// 渲染每个节点的函数,如果没有默认返回jlable
    JPopupMenu jPopupMenu;
    FilteredJlist me;
    Opt<Function<F, Icon>> nodeRander = new Opt();// 渲染函数,输入为一个节点,输出为一个JPanel,如果返回null,则使用默认的渲染器

    /**
     * @return the nodeRander
     */
    public Opt<Function<F, Icon>> getNodeRander() {
        return nodeRander;
    }

    /**
     * @param nodeRander the nodeRander to set
     */
    public void setNodeRander(Function<F, Icon> nodeRander) {
        this.nodeRander.of(nodeRander);
    }

    /**
     * 添加一个弹出菜单
     * 
     * @param title
     * @param action
     * @return
     */
    public JMenuItem addPopMenu(String title, Consumer<java.awt.event.ActionEvent> action) {
        if (this.jPopupMenu == null) {
            this.jPopupMenu = new JPopupMenu();
            new MyComponent<>(me).onRightClickEvent(evt -> {
                this.jPopupMenu.show(me, evt.getX(), evt.getY());
            });
            new MyComponent<>(me.getJlist()).onRightClickEvent(evt -> {
                this.jPopupMenu.show(me, evt.getX(), evt.getY());
            });
        }
        JMenuItem jMenuItem = new JMenuItem(title);
        jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MyComponent.getSwingEventPool().exec(new Task<>(() -> {
                    // 在新的线程处理防止阻塞swing
                    action.accept(evt);
                    return null;
                }));
            }
        });
        this.jPopupMenu.add(jMenuItem);
        return jMenuItem;
    }

    /**
     * @return the dataList
     */
    public List<F> getDataList() {
        return dataList;
    }

    /**
     * 渲染渲染的时候可以
     */
    public FilteredJlist() {
        this.me = this;
        setLayout(new BorderLayout());
        add(filtedTextField, BorderLayout.NORTH);
        scrollpane=new JScrollPane(getJlist());
        add(scrollpane, BorderLayout.CENTER);
        new MyComponent<>(getFIlterTextField()).onKeyDown(e -> {
            if (e.getKeyCode() == 10) {
                String searchText = getFIlterTextField().getText();
                // 进行过滤
                if (filterFunction.notNull_()) {
                    if (searchText.trim().length() > 0) {
                        Function fun = filterFunction.get();
                        List newList = new ArrayList<>(getDataList());
                        newList.removeIf(obj -> {
                            return !str(fun.apply(obj)).hasNoCase(searchText);
                        });
                        getJlist().setListData(new Vector<>(newList));
                    } else if (searchText.trim().length() == 0) {
                        // 清空过滤
                        updateJlist();
                    }
                } else {
                    if (searchText.trim().length() == 0) { // 清空过滤
                        updateJlist();
                    } else {
                        List newList = new ArrayList<>(getDataList());
                        // 默认调用toString进行过滤
                        newList.removeIf(obj -> {
                            return !str(obj).hasNoCase(searchText);
                        });
                        getJlist().setListData(new Vector<>(newList));
                    }
                }
            }
        });

        getJlist().setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Opt<Function<F, Icon>> nodeRander2 = getNodeRander();
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (component instanceof JLabel && notNull(value)&& notNull(component)) {
                    try {
                        if (nodeRander2.notNull_()) {
                            // 尝试渲染
                            Icon icon = nodeRander2.get().apply((F) value);
                            if(notNull(icon)){
                                ((JLabel) component).setIcon(icon);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return component;
            }
        });
    }

    /**
     *
     * @param treeCellRendererCommponentFunction
     */
    public void setTreeCellRendererCommponentFunction(
            Function<TreeCellRendererCommponentArgsWarper, Component> treeCellRendererCommponentFunction) {
        this.treeCellRendererCommponentFunction.of(treeCellRendererCommponentFunction);
    }

    /**
     * @return the treeCellRendererCommponentFunction
     */
    public Opt<Function<TreeCellRendererCommponentArgsWarper, Component>> getTreeCellRendererCommponentFunction() {
        return treeCellRendererCommponentFunction;
    }

    /**
     * @return the filtedTextField
     */
    public JTextField getFiltedTextField() {
        return filtedTextField;
    }

    /**
     * @return the filteredText
     */
    public String getFilteredText() {
        return filteredText;
    }

    /**
     * @return the treeNodeTextFunction
     */
    public Opt<Function<F, String>> getTreeNodeTextFunction() {
        return treeNodeTextFunction;
    }

    /**
     * 设置用来显示参数节点文本的函数
     * 
     * @param treeNodeTextFunction the treeNodeTextFunction to set
     */
    public void setTreeNodeTextFunction(Function<F, String> treeNodeTextFunction) {
        this.treeNodeTextFunction.of(treeNodeTextFunction);
    }

    /**
     * 设置过滤函数,如果不设置默认绑定的对象toString
     * 
     * @param filterFunction the filterFunction to set
     */
    public void setFilterFunction(Function<F, String> filterFunction) {
        this.filterFunction.of(filterFunction);
    }

    /**
     * @return the filterFunction
     */
    public Opt<Function<F, String>> getFilterFunction() {
        return filterFunction;
    }

    /**
     * 获取过滤的文本输入框
     *
     * @return
     */
    public JTextField getFIlterTextField() {
        return filtedTextField;
    }

    /**
     * @return the scrollpane
     */
    public JScrollPane getScrollpane() {
        return scrollpane;
    }

    /**
     * @return the jlist
     */
    public JList getJlist() {
        return jlist;
    }

    /**
     * 绑定数据源
     *
     * @param dataList
     */
    public void binDataList(List<F> dataList) {
        JList j = getJlist();
        this.dataList = dataList;
        j.setListData(new Vector<>(dataList));
    }

    /**
     * 更新Jlist
     */
    public void updateJlist() {
        binDataList(this.dataList);
    }

    /**
     * 启动单独选中
     */
    public void enableSingleSelectionMode() {
        getJlist().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    public List<F> getSelectedItem() {
        return getJlist().getSelectedValuesList();
    }

    public Opt<F> getSelectedFristItem() {
        List selectedValuesList = getJlist().getSelectedValuesList();
        Opt<F> ret = new Opt<>();
        if (selectedValuesList.size() > 0) {
            ret.of((F) selectedValuesList.get(0));
        }
        return ret;
    }

    public static void main(String[] args) {
        JFrame jf = new JFrame();
        FilteredJlist jlist = new FilteredJlist<>();
        jlist.addPopMenu("12313", e -> {
            System.out.println("asdadad");
            System.out.println(jlist.getSelectedItem());
            System.out.println(jlist.getSelectedFristItem().get());
            jlist.binDataList(utilFun.newList("12311", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933", "456222", "78933"));
        });
        jlist.setNodeRander(obj -> {
            return new FolderIcon16();
        });
        jlist.setFilterFunction(obj -> {
            return "1234556";
        });
        // jlist.enableSingleSelectionMode();
        ArrayList<String> newList = utilFun.newList("123", "456", "789");

        jlist.binDataList(newList);
        jf.add(jlist);
        jf.show();
    }
}
