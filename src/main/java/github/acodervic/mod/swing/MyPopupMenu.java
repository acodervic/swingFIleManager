package github.acodervic.mod.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.Task;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.awt.event.ActionEvent;

/**
 * 我的弹出菜单,可以同时作为菜单项和菜单节点使用,MyPopupMenu可以表示一个菜单节点
 */
public class MyPopupMenu {
    Boolean isMenuNode = false;// 是否为菜单节点,如果不是菜单节点那么就是菜单项
    AbstractButton component;// 真实包装的组件,当isMenuNode=true的时候是JMenu,false的时候是JMenu
    List<MyPopupMenu> subPopupMenu = new ArrayList<MyPopupMenu>();// 子菜单项/节点
    String text = "";

    public Boolean isMenuNode() {
        return isMenuNode;
    }

    /**
     *
     * 读取菜单,一般用于添加到表格等
     * 
     * @return the subPopupMenu
     */
    public AbstractButton getMenu() {
        return component;
    }

    /**
     * @param isMenuNode the isMenuNode to set
     */
    public void setIsMenuNode(Boolean isMenuNode) {
        this.isMenuNode = isMenuNode;
    }

    /**
     * 获取菜单项
     *
     * @return
     */
    public JMenuItem getJMenuItem() {
        return (JMenuItem) this.component;
    }

    /**
     * 设置菜单
     * 
     * @param JMenu
     */
    public void setJMenu(JMenu JMenu) {
        nullCheck(JMenu);
        this.component = JMenu;
    }
    /**
     * 获取菜单节点
     *
     * @return
     */
    public JMenu getJMenu() {
        return (JMenu) this.component;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * 清空所有子菜单
     */
    public void clearAllSubMenus() {
        if (isMenuNode) {
            component.removeAll();
            this.subPopupMenu.clear();
        }
    }
    /**
     *
     * 添加一个子菜单,并绑定action(只可以在菜单节点上调用)
     *
     * @param JMenuItem
     * @return
     */
    public MyPopupMenu addSubJMenuItem(String menuText, Consumer<ActionEvent> action) {
        nullCheck(menuText, action);
        if (!isMenuNode) {
            throw new RuntimeException(
                    "当前MyPopupMenu的类型为一个菜单项类型(isMenuNode=true),调用addSubJMenuItem绑定失败!,如果需要添加子菜单项,请提前通过setIsMenuNode(true)先将其转换为菜单节点类型!");
        }
        // 自动转换为节点
        isMenuNode = true;
        MyPopupMenu subJmenuitem = new MyPopupMenu(menuText);
        // 绑定点击事件
        subJmenuitem.getJMenuItem().addActionListener(e -> {
            FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                action.accept(e);
                return null;
            }));
        });
        this.subPopupMenu.add(subJmenuitem);
        this.component.add(subJmenuitem.getMenu());

        return this;
    }

    /**
     *
     * 添加一个子菜单节点(只可以在菜单节点上调用),返回添加后的菜单
     *
     * @param JMenuItem
     * @return
     */
    public MyPopupMenu addSubJMenuNode(String nodeText) {
        nullCheck(nodeText);
        if (!isMenuNode) {
            throw new RuntimeException(
                    "当前MyPopupMenu的类型为一个菜单项类型(isMenuNode=false),调用addSubJMenuItem绑定失败!,如果需要添加子菜单项,请提前通过setIsMenuNode(true)将其转换为菜单节点类型!");
        }
        // 自动转换为节点
        isMenuNode = true;
        MyPopupMenu subJmenuNode = new MyPopupMenu(nodeText, true);
        this.subPopupMenu.add(subJmenuNode);
        // 添加到子菜单
        this.getJMenu().add(subJmenuNode.getMenu());
        return subJmenuNode;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 构造一个菜单项
     *
     * @param text
     */
    public MyPopupMenu(String text) {
        this.text = text;
        initComponent();
    }

    /**
     * 构造一个菜单项/节点
     *
     * @param isMenuNode
     * @param text
     */
    public MyPopupMenu(String text, Boolean isMenuNode) {
        this.isMenuNode = isMenuNode;
        this.text = text;
        initComponent();
    }

    void initComponent() {
        if (this.component == null) {
            if (this.isMenuNode) {
                this.component = new JMenu(this.text);
            } else {
                this.component = new JMenuItem(this.text);
            }

        }
    }

}
