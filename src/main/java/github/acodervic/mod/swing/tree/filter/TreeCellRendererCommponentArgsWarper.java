package github.acodervic.mod.swing.tree.filter;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import github.acodervic.mod.data.Opt;

/**
 * TreeCellRendererCommponentArgsWarper
 */
public class TreeCellRendererCommponentArgsWarper {

    JTree tree;
    Object value;
    boolean selected;
    boolean expanded;
    boolean leaf;
    int row;
    boolean hasfocus;
	Opt<Object> userObject=new Opt();
	/**
	 * @param tree
	 * @param value
	 * @param selected
	 * @param expanded
	 * @param leaf
	 * @param row
	 * @param hasfocus
	 */
	public TreeCellRendererCommponentArgsWarper(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasfocus) {
		this.tree = tree;
		this.value = value;
		if (value!=null&&value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode val=(DefaultMutableTreeNode)value;
			userObject.of( val.getUserObject());
		}
		this.selected = selected;
		this.expanded = expanded;
		this.leaf = leaf;
		this.row = row;
		this.hasfocus = hasfocus;
	}

	/**
	 * @return the tree
	 */
	public JTree getTree() {
		return tree;
	}

	/**
	 * @param tree the tree to set
	 */
	public void setTree(JTree tree) {
		this.tree = tree;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the expanded
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * @param expanded the expanded to set
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	/**
	 * @return the leaf
	 */
	public boolean isLeaf() {
		return leaf;
	}

	/**
	 * @param leaf the leaf to set
	 */
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the hasfocus
	 */
	public boolean isHasfocus() {
		return hasfocus;
	}

	/**
	 * @param hasfocus the hasfocus to set
	 */
	public void setHasfocus(boolean hasfocus) {
		this.hasfocus = hasfocus;
	}

	/**
	 * @return the userObject
	 */
	public Opt<Object> getUserObject() {
		return userObject;
	}
	/**
	 * @param userObject the userObject to set
	 */
	public void setUserObject(Opt<Object> userObject) {
		this.userObject = userObject;
	}
}