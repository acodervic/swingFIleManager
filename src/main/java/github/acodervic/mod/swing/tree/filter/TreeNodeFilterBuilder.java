package github.acodervic.mod.swing.tree.filter;

import java.util.function.Function;

import javax.swing.tree.DefaultMutableTreeNode;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.tree.FilteredTree;


/**
 * 树节点过滤器
 */
public class TreeNodeFilterBuilder {

    private String textToMatch;
    FilteredTree filteredTree;

    public TreeNodeFilterBuilder(String textToMatch,FilteredTree filteredTree) {
        this.textToMatch = textToMatch.toLowerCase();
        this.filteredTree=filteredTree;
    }

    /**
     * 修剪
     * @param root
     * @return
     */
    public DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {

        boolean badLeaves = true;

        // keep looping through until tree contains only leaves that match
        while (badLeaves) {
            badLeaves = removeBadLeaves(root);
        }
        return root;
    }

    /**
     *
     * @param root
     * @return boolean bad leaves were returned
     */
    private boolean removeBadLeaves(DefaultMutableTreeNode root) {

        // no bad leaves yet
        boolean badLeaves = false;

        // reference first leaf
        DefaultMutableTreeNode leaf = root.getFirstLeaf();

        // if leaf is root then its the only node
        if (leaf.isRoot())
            return false;

        int leafCount = root.getLeafCount(); // this get method changes if in for loop so have to define outside of
                                             // it
        for (int i = 0; i < leafCount; i++) {

            DefaultMutableTreeNode nextLeaf = leaf.getNextLeaf();


            Boolean match = false;
            Object userObject = leaf.getUserObject();
            //进行过滤
            Opt<Function>     filterFunction = filteredTree.getFilterFunction();
            if (filterFunction.notNull_()&&userObject!=null&&!(userObject instanceof String))  {
                try {
                    match= (Boolean)filterFunction.get().apply(userObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } 
            if (!match) {
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) leaf.getParent();
                if (parent != null)
                    parent.remove(leaf);

                badLeaves = true;
            }
            leaf = nextLeaf;
        }
        return badLeaves;
    }
}
