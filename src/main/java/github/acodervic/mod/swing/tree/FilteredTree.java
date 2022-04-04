package github.acodervic.mod.swing.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.MyComponent;
import github.acodervic.mod.swing.MyScrollBarUI;
import github.acodervic.mod.swing.panel.MyJScrollPane;
import github.acodervic.mod.swing.tree.filter.FiltedTreeRenderer;
import github.acodervic.mod.swing.tree.filter.TreeCellRendererCommponentArgsWarper;
import github.acodervic.mod.swing.tree.filter.TreeNodeFilterBuilder;
import github.acodervic.mod.thread.Task;

 
/**
 *
 * Tree widget which allows the tree to be filtered on keystroke time. Only
 * nodes who's toString matches the search field will remain in the tree or its
 * parents.
 *
 * @author Copyright (c) Oliver.Watkins
 */

public class FilteredTree<F> extends JPanel implements UtilFunInter {

    private String filteredText = "";
    private DefaultTreeModel originalTreeModel;
    private JTree tree = new JTree();
    private MyJScrollPane scrollpane  =new MyJScrollPane(tree)    ;
    private DefaultMutableTreeNode originalRoot;
    final JTextField filtedTextField = new JTextField();
    Opt<Function<F,Boolean>> filterFunction=new Opt<>();//返回字符串 用来和搜索进行过滤
    Opt<Function<F,String>> treeNodeTextFunction=new Opt<>();
    Opt<Function<TreeCellRendererCommponentArgsWarper,List<Component>>> treeCellRendererCommponentFunction=new Opt<>();//渲染每个节点的函数,如果没有默认返回jlable
    JPopupMenu jPopupMenu;
    FilteredTree me;
    Function<DefaultMutableTreeNode,Icon>    nodeIconRander;//渲染函数,输入为一个节点,输出为一个JPanel,如果返回null,则使用默认的渲染器
    HashMap userObjMap=new HashMap<>();


    /**
     * 根据userObj找到绑定结点
     * @param obj
     * @return
     */
    public List<DefaultMutableTreeNode> getNodesByUserObject(F obj) {
        List<DefaultMutableTreeNode> ret=new ArrayList<>();
        if (notNull(obj)&& notNull(originalRoot)) {
            //遍历
            traversideNodes(originalRoot, node  ->{
                if (node.getUserObject()==obj) {
                    ret.add(node);
                }
            });
        }
        return ret;
    }

    /**
     * 根据userObject找到第一个绑定的节点
     * @param obj
     * @return
     */
    public Opt<DefaultMutableTreeNode> getNodeByUserObjectFirst(F obj) {
        Opt<DefaultMutableTreeNode> ret=new Opt<>();
        List<DefaultMutableTreeNode> nodeByUserObject = getNodesByUserObject(obj);
        if (nodeByUserObject.size()>0) {
            ret.of(nodeByUserObject.get(0));
        }
        return ret;
    }


    /**
     * 遍历节点
     */
    public   void traversideNodes(DefaultMutableTreeNode aNode,Consumer<DefaultMutableTreeNode> onFindNewNode) {
        String name = aNode.toString();
        int level = aNode.getLevel();
        String placement = "";
        while (level > 0) {
             level--;
        }
        if (aNode.isLeaf()) {
            return;
        }
        for (int i = 0; i < aNode.getChildCount(); i++) {
            DefaultMutableTreeNode node=(DefaultMutableTreeNode) aNode.getChildAt(i);
            if (onFindNewNode!=null) {
                try {
                    onFindNewNode.accept(node);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            traversideNodes(node,onFindNewNode);
        }
    }

    /**
     * 设置节点图标渲染函数
     * @param fun_opt
     */
    public void setNodeIconRanderFun(Function<DefaultMutableTreeNode,Icon> fun_opt) {
        if (fun_opt!=null) {
            this.nodeIconRander=fun_opt;
        }
    }

    /**
     * @return the nodeIconRander
     */
    public Function<DefaultMutableTreeNode, Icon> getNodeIconRanderFun() {
        return nodeIconRander;
    }

    /**
     * 添加一个弹出菜单
     * @param title
     * @param action
     * @return
     */
    public JMenuItem addPopMenu(String title, Consumer<java.awt.event.ActionEvent> action) {
        if (this.jPopupMenu==null) {
            this.jPopupMenu=new JPopupMenu();
            new MyComponent<>(me).onRightClickEvent(evt -> {
                this.jPopupMenu.show(me, evt.getX(), evt.getY());
            });
            new MyComponent<>(me.getTree()).onRightClickEvent(evt -> {
                PointerInfo a = MouseInfo.getPointerInfo();
                Point b = a.getLocation();
                SwingUtilities.convertPointFromScreen(b, scrollpane);
                this.jPopupMenu.show(me, (int)b.getX(), (int)b.getY());
            });
         }
        JMenuItem jMenuItem=new JMenuItem(title);
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
     * 获取当前选中节点
     * @return
     */
    public Opt<DefaultMutableTreeNode> getNowSelectedNode() {
        Opt<DefaultMutableTreeNode> node=new Opt<>();
        if (tree.getLastSelectedPathComponent()!=null) {
            Object lastSelectedPathComponent = tree.getLastSelectedPathComponent();
            node.of( (DefaultMutableTreeNode)lastSelectedPathComponent);
        }
        return node;
    }


        /**
     * 获取当前选中节点
     * @return
     */
    public List<DefaultMutableTreeNode> getNowSelectedNodes() {
        List<DefaultMutableTreeNode> nodes=new ArrayList<>();
        TreePath[] paths = tree.getSelectionPaths();
            for (TreePath path : paths != null ? paths : new TreePath[0]) {
                nodes.add((DefaultMutableTreeNode)path.getLastPathComponent());
            }
        return nodes;
    }


    

    public FilteredTree(DefaultMutableTreeNode originalRoot) {
        this.me=this;
        this.originalRoot = originalRoot;
        guiLayout();
    }


    /**
     * 渲染渲染的时候可以
     */
    public FilteredTree() {
        this.me=this;
    }

    public void randerTree() {
        if (this.originalTreeModel==null) {
            //代表第一次渲染
            guiLayout();
        }
    }

    public void setRootNode(DefaultMutableTreeNode root) {
        this.originalRoot = root;
        if (this.originalTreeModel==null) {
            //代表第一次渲染
            guiLayout();
        }else{
             //已经渲染过了 直接设置根节点
             this.originalTreeModel.setRoot(originalRoot);
             reloadTree();
        }
     }

    /**
     * @param treeCellRendererCommponentFunction the treeCellRendererCommponentFunction to set
     */
    public void setTreeCellRendererCommponentsFunction(
            Function<TreeCellRendererCommponentArgsWarper, List<Component>> treeCellRendererCommponentFunction) {
        this.treeCellRendererCommponentFunction.of(treeCellRendererCommponentFunction);
    }
    /**
     * @return the treeCellRendererCommponentFunction
     */
    public Opt<Function<TreeCellRendererCommponentArgsWarper, List<Component>>> getTreeCellRendererCommponentFunction() {
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
     * 设置用来显示参数节点文本的函数(注意<这只在TreeCellRendererCommponentFunction未设置或返回null时候调用)
     * @param treeNodeTextFunction the treeNodeTextFunction to set
     */
    public void setTreeNodeTextFunction(Function<F, String> treeNodeTextFunction) {
        this.treeNodeTextFunction.of(treeNodeTextFunction);
    }

    /**
     * 设置过滤函数,如果不设置默认绑定的对象toString
     * @param filterFunction the filterFunction to set
     */
    public void setFilterFunction(Function<F, Boolean> filterFunction) {
        this.filterFunction .of(filterFunction);
    }

    /**
     * @return the filterFunction
     */
    public Opt<Function<F, Boolean>> getFilterFunction() {
        return filterFunction;
    }

    /**
     * 获取过滤的文本输入框
     * @return
     */
    public  JTextField getFIlterTextField() {
        return filtedTextField;
    }

/**
 * @return the scrollpane
 */
public MyJScrollPane getScrollpane() {
    return scrollpane;
}

    private void guiLayout() {
        if (originalRoot==null) {
            return ;
        }
        tree.setCellRenderer(new FiltedTreeRenderer<>(this));
        filtedTextField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent ke) {
                super.keyTyped(ke);
                SwingUtilities.invokeLater(() -> {
                    filterTree(filtedTextField.getText());
                });
            }
        });

        originalTreeModel = new DefaultTreeModel(originalRoot);
        tree.setModel(originalTreeModel);
        this.setLayout(new BorderLayout());

        add(filtedTextField, BorderLayout.NORTH);
        scrollpane.setViewportView(tree);
        scrollpane.getVerticalScrollBar().setUI(new MyScrollBarUI());
        add(scrollpane , BorderLayout.CENTER);
        originalRoot = (DefaultMutableTreeNode) originalTreeModel.getRoot();
        scrollpane.updateUI();
    }

    /**
     *
     * @param text
     */
    private void filterTree(String text) {
        filteredText = text;
        // get a copy
        DefaultMutableTreeNode filteredRoot = copyNode(originalRoot);

        if (text.trim().toString().equals("")) {

            // reset with the original root
            originalTreeModel.setRoot(originalRoot);

            tree.setModel(originalTreeModel);
            tree.updateUI();
            scrollpane.getViewport().setView(tree);

            for (int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }

            return;
        } else {

            TreeNodeFilterBuilder b = new TreeNodeFilterBuilder(text,this);
            filteredRoot = b.prune((DefaultMutableTreeNode) filteredRoot.getRoot());

            originalTreeModel.setRoot(filteredRoot);

            tree.setModel(originalTreeModel);
            tree.updateUI();
            scrollpane.getViewport().setView(tree);
        }

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    /**
     * Clone/Copy a tree node. TreeNodes in Swing don't support deep cloning.
     * 
     * @param orig to be cloned
     * @return cloned copy
     */
    private DefaultMutableTreeNode copyNode(DefaultMutableTreeNode orig) {

        DefaultMutableTreeNode newOne = new DefaultMutableTreeNode();
        newOne.setUserObject(orig.getUserObject());

        Enumeration<TreeNode> enm = orig.children();

        while (enm.hasMoreElements()) {

            DefaultMutableTreeNode child = (DefaultMutableTreeNode) enm.nextElement();
            newOne.add(copyNode(child));
        }
        return newOne;
    }


    /**
     * 展开所有节点.,必须放在swing渲染线程中 否则会出现bug
     */
    public void expandAllNode() {
             TreeNode root = (TreeNode) getTree().getModel().getRoot();
            expandAll(tree, new TreePath(root), true);
    }



    /**
     * 折叠所有节点,必须放在swing渲染线程中 否则会出现bug
     */
    public void collapseAllNode() {
         TreeNode root = (TreeNode) getTree().getModel().getRoot();
        expandAll(tree, new TreePath(root), true);
    }

     public  static void expandAll(JTree tree, TreePath path, boolean expand) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        if (node.getChildCount() >= 0) {
            Enumeration enumeration = node.children();
            while (enumeration.hasMoreElements()) {
                TreeNode n = (TreeNode) enumeration.nextElement();
                TreePath p = path.pathByAddingChild(n);
                expandAll(tree, p, expand);
            }
        }

        if (expand) {
            tree.expandPath(path);
        } else {
            tree.collapsePath(path);
        }
    }

    public JTree getTree() {
        return tree;
    }


    /**
     * 重新加载整个树,在更改了节点后刷新,,必须放在swing渲染线程中 否则会出现bug
     */
    public void reloadTree() {
        ((DefaultTreeModel)getTree().getModel()).reload();
    }
    /**
     * 重新加载某个节点,在更改了节点后刷新,,必须放在swing渲染线程中 否则会出现bug
     * @param node
     */
    public void reloadTreeNode(TreeNode node) {
        ((DefaultTreeModel)getTree().getModel()).reload(node);

    }

    /**
     * 当双击选中节点时候触发
     * @param fun
     */
    public void onDoubleClickNode(Consumer<DefaultMutableTreeNode>  fun) {
        new MyComponent<>(getTree()).onDoubleClick(obj  ->{
            getNowSelectedNode().ifNotNull_(o  ->{
                fun.accept(o);
            });
        });
    }
    

    public void onClickNode(Consumer<DefaultMutableTreeNode> run) {
        new MyComponent<>(getTree()).onClick(obj  ->{
            Opt<DefaultMutableTreeNode> nowSelectedNode = getNowSelectedNode();
            if (nowSelectedNode.notNull_()) {
                run.accept(nowSelectedNode.get());
            }
        });
    }
 

    /**
     *
     * @return
     */
    public Opt<DefaultMutableTreeNode> getRootNode() {
        return new Opt<DefaultMutableTreeNode>(this.originalRoot);
    }

    public static void main(String[] args) {

        DefaultMutableTreeNode n = new DefaultMutableTreeNode("animals");
        {
            DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("bear");
            n.add(n1);
        }
        {
            DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("cat");
            n.add(n1);
        }
        {
            DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("boor");
            n.add(n1);
        }
        {
            DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("dog");
            n.add(n1);
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("billy");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("cassie");
                n1.add(n2);
            }
        }
        {
            DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("bat");
            n.add(n1);
        }

        {
            DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("crow");
            n.add(n1);
        }
        {
            DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("cow");
            n.add(n1);

            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("carp");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("boa constrictor");
                n1.add(n2);

                {
                    DefaultMutableTreeNode n3 = new DefaultMutableTreeNode("cockatoo");
                    n2.add(n3);
                }
                {
                    DefaultMutableTreeNode n3 = new DefaultMutableTreeNode("dragon");
                    n2.add(n3);
                }
                {
                    DefaultMutableTreeNode n3 = new DefaultMutableTreeNode("adder");
                    n2.add(n3);
                }
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("alligator");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("snake");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("spider");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("salamander");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("ant");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("ant");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("ant");
                n1.add(n2);
            }
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("ant");
                n1.add(n2);
            }

        }
        for (int i = 0; i < 1000; i++) {
            {
                DefaultMutableTreeNode n2 = new DefaultMutableTreeNode(i + "ant");
                n.add(n2);
            }

        }
        JFrame jf = new JFrame("adsad");
        FilteredTree<Object> filteredTree = new FilteredTree<>(n);
        filteredTree.addPopMenu("asdad", obj  ->{

        });
        jf.add(filteredTree);
        jf.show();

    }


    /**
     * @return the userObjMap
     */
    public HashMap getUserObjMap() {
        return userObjMap;
    }

    /**
     * @param userObjMap the userObjMap to set
     */
    public void setUserObjMap(HashMap userObjMap) {
        this.userObjMap = userObjMap;
    }
    
}
