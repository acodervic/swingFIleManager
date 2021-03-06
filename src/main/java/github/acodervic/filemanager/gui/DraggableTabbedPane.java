package github.acodervic.filemanager.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JWindow;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * 支持拖动的tabPanle
 */
public class DraggableTabbedPane extends JTabbedPane {
	private static Set panes = new HashSet();
	private boolean dragging = false;
	private int draggedTabIndex = 0;
	private Container paneParent;
	private Map focusListeners = new HashMap();
	private static FocusListener lastFocusListener = null;
	private static JWindow window;
	private final ChangeListener chListener;
	DraggableTabbedPane me;
	static {
		// Set up placeholder window. (Shows when moving tabs)
		window = new JWindow();
		JLabel lab = new JLabel("Moving", JLabel.CENTER);
		lab.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				window.setVisible(false);
			}

			public void mouseDragged(MouseEvent e) {
				window.setVisible(false);
			}
		});
		window.getContentPane().add(lab, java.awt.BorderLayout.CENTER);
		window.setSize(300, 300);
	}

	/**
	 * Finds the parent tab of the component given in c.
	 * 
	 * @param c The component whose tab is to be obtained
	 */
	public static DraggableTabbedPane getTabPane(Component c) {
		Component subParent = c, par;
		for (par = subParent.getParent(); !(par instanceof DraggableTabbedPane); par = par.getParent())
			subParent = par;
		return (DraggableTabbedPane) par;
	}

	/**
	 * 请求焦点通过title
	 */
	public void requestFouceByTitle (String title){
		for (int i = 0; i < getTabCount(); i++) {
			String titleAt = getTitleAt(i);
			Component tabComponentAt = getComponents()[i];
			if (titleAt.equals(title)) {
				setSelectedIndex(i);
			}else if(tabComponentAt instanceof  JSplitPane){
				requestFouceByTitle(title, ((JSplitPane)tabComponentAt));
			}
		}
	}

	public void requestFouceByTitle(String title,JSplitPane jsp) {
		Component leftComponent = jsp.getLeftComponent();
		Component rightComponent = jsp.getRightComponent();
		if (leftComponent instanceof DraggableTabbedPane) {
			((DraggableTabbedPane) leftComponent).requestFouceByTitle(title);
		} else if (leftComponent instanceof JSplitPane) {
			requestFouceByTitle(title, (JSplitPane) leftComponent);
		}
		if (rightComponent instanceof DraggableTabbedPane) {
			((DraggableTabbedPane) rightComponent).requestFouceByTitle(title);
		} else if (rightComponent instanceof JSplitPane) {
			requestFouceByTitle(title, (JSplitPane) rightComponent);
		}
	}

	/**
	 * Returns a object composed of nested arrays & strings representing the layout
	 * of splitpanes and tabs in the given object
	 * 
	 * @param component The root component
	 * @return
	 */
	public static Object getSplitLayout(Object component) {
		if (component instanceof javax.swing.JPanel) {
			return getSplitLayout(((javax.swing.JPanel) component).getComponent(0));
		} else if (component instanceof javax.swing.JSplitPane) {
			javax.swing.JSplitPane split = (javax.swing.JSplitPane) component;
			ArrayList list = new ArrayList();
			list.add(split.getOrientation());
			list.add(getSplitLayout(split.getLeftComponent()));
			list.add(split.getDividerLocation());
			list.add(getSplitLayout(split.getRightComponent()));
			return list;
		} else if (component instanceof DraggableTabbedPane) {
			ArrayList list = new ArrayList();
			DraggableTabbedPane pane = (DraggableTabbedPane) component;
			for (int i = 0; i < pane.getTabCount(); i++)
				list.add(pane.getTitleAt(i));
			return list;
		}
		return null;
	}

	/**
	 * Restores a layout of splitpanes and tabs to the given container from an
	 * object composed of nested arrays & strings representing the layout
	 *
	 * @param component The root component
	 * @return
	 */
	public static Component restoreSplitLayout(Object component, Container parent, DraggableTabbedPane root) {
		if (!(component instanceof ArrayList))
			return null;
		ArrayList list = (ArrayList) component;
		// Split pane
		if (list.size() == 4 && ((ArrayList) component).get(0) instanceof Integer) {
			JSplitPane split = new JSplitPane();
			Component left = restoreSplitLayout(list.get(1), split, root);
			if (left == null) // If the only tabs here are not present (meterp, file, etc)
				return restoreSplitLayout(list.get(3), parent, root); // return other side
			// Get right
			Component right = restoreSplitLayout(list.get(3), split, root);
			if (right == null) {
				// uhoh. now we told left the wrong parent. Fix.
				if (left instanceof DraggableTabbedPane)
					((DraggableTabbedPane) left).paneParent = parent;
				return left;
			}
			left.setMaximumSize(new Dimension(0, 0));
			right.setMaximumSize(new Dimension(0, 0));

			// Ok! both sides are good. Plug 'em in and we'll go
			split.setOrientation((Integer) list.get(0));
			split.setLeftComponent(left);

			split.setRightComponent(right);
			//设置分割比例
			split.setDividerLocation(Integer.parseInt(list.get(2).toString()));
			return split;
		}
		DraggableTabbedPane pane = new DraggableTabbedPane(parent);
		for (Object o : list)
			if (root.indexOfTab(o.toString()) != -1)
				root.moveTabTo(root.indexOfTab(o.toString()), pane);
		if (pane.getTabCount() == 0)
			return null;
		root.paneParent = null; // we're disconnecting you
		return pane;
	}

	/**
	 * Finds the parent tab of the component given in c, and dis/enables it.
	 * 
	 * @param c       The component whose tab is to be dis/enabled
	 * @param enabled The new enabled state of the tab
	 */
	public static void setTabComponentEnabled(Component c, boolean enabled) {
		Component subParent = c, par;
		for (par = subParent.getParent(); !(par instanceof DraggableTabbedPane) && par != null; par = par.getParent())
			subParent = par;
		if (par == null)
			throw new RuntimeException("Error in DraggableTabbedPane.show; no parent is a DraggableTabbedPane!");
		DraggableTabbedPane pane = (DraggableTabbedPane) par;
		for (int i = 0; i < pane.getTabCount(); i++)
			if (pane.getComponentAt(i).equals(subParent))
				pane.setEnabledAt(i, enabled);
	}

	/**
	 * Adds a listener which will be notified when the given tab receives or loses
	 * focus
	 *
	 * @param listener
	 */
	public void setTabFocusListener(int tabIndex, FocusListener listener) {
		focusListeners.put(getComponentAt(tabIndex), listener);
	}

	/**
	 * Moves the given tab to the destination DraggableTabbedPane.
	 *
	 * @param sourceIndex
	 * @param destinationPane
	 */
	public void moveTabTo(int sourceIndex, DraggableTabbedPane destinationPane) {
		moveTabTo(sourceIndex, destinationPane, destinationPane.getTabCount());
	}

	@Override
	public void addTab(String title, Component component) {
		if (component==null) {
			throw new RuntimeException("tab组件不能为Null!否则布局配置无法运行!");
		}
		super.addTab(title, component);
	}
	/**
	 * Moves the given tab to the destination DraggableTabbedPane at the destination
	 * index
	 *
	 * @param sourceIndex
	 * @param destinationPane
	 * @param destinationIndex
	 */
	public void moveTabTo(int sourceIndex, DraggableTabbedPane destinationPane, int destinationIndex) {
		// First save tab information
		Component comp = getComponentAt(sourceIndex);
		Icon iconAt = this.getIconAt(sourceIndex);
		String title = getTitleAt(sourceIndex);
		boolean enabled = isEnabledAt(sourceIndex);
		comp.setMinimumSize(new  Dimension(1,1));
		// Then move tab and restore information
		removeTabAt(sourceIndex);
		destinationPane.insertTab(title, iconAt, comp, null, destinationIndex);
		destinationPane.setEnabledAt(destinationIndex, enabled);
		destinationPane.setSelectedIndex(destinationIndex);
		destinationPane.focusListeners.put(comp, focusListeners.get(comp));
		destinationPane.setMinimumSize(new  Dimension(1,1));
		// If we got rid of the last tab, close this window, unless it's the main window
		if (getTabCount() < 1) {
			panes.remove(this);
			if (paneParent instanceof JPanel)
				paneParent = ((JPanel) paneParent).getTopLevelAncestor();
			// If parent is a frame, just close it

			if (paneParent instanceof JFrame) {
				paneParent.setVisible(false);
				((JFrame) paneParent).dispose();
				// If it's a split pane, replace with other side
			} else if (paneParent instanceof JSplitPane) {
				JSplitPane split = (JSplitPane) paneParent;
				Component replacement;
				if (split.getRightComponent() == this)
					replacement = split.getLeftComponent();
				else if (((JSplitPane) paneParent).getLeftComponent() == this)
					replacement = split.getRightComponent();
				else
					throw new RuntimeException("不是分裂的任何一面？ 这永远都不会发生");
				Container parent = split.getParent();
				parent.remove(split);
				parent.add(replacement);
				// If the other side is a DraggableTabbedPane, update its parent
				for (Container c = parent; c != null; c = c.getParent()) {
					if ((c instanceof JSplitPane || c instanceof JFrame)
							&& replacement instanceof DraggableTabbedPane) {
						((DraggableTabbedPane) replacement).paneParent = c;
						break;
					}
				}
				((Window) ((javax.swing.JComponent) replacement).getTopLevelAncestor()).pack();
			}
		}
	}

	/**
	 * Finds the parent tab of the component given in c, and makes it visible.
	 * 
	 * @param c The component whose tab is to be made visible
	 */
	public static boolean isVisible(Component c) {
		Component subParent = c, par;
		for (par = subParent.getParent(); !(par instanceof Window); par = par.getParent())
			if (par == null || !par.isVisible())
				return false;
		return true;
	}

	/**
	 * Finds the parent tab of the component given in c, and makes it visible.
	 * 
	 * @param c The component whose tab is to be made visible
	 */
	public static void show(Component c) {
	c.setMaximumSize(new Dimension(0, 0));
		// Find containing tab pane
		Component subParent = c, par;
		for (par = subParent.getParent(); !(par instanceof DraggableTabbedPane) && par != null; par = par.getParent())
			subParent = par;
		if (par == null)
			throw new RuntimeException("Error in DraggableTabbedPane.show; no parent is a DraggableTabbedPane!");
		DraggableTabbedPane pane = (DraggableTabbedPane) par;
		// Show this tab
		for (int i = 0; i < pane.getTabCount(); i++)
			if (pane.getComponentAt(i).equals(subParent))
				pane.setSelectedIndex(i);
		lastFocusListener = (FocusListener) pane.focusListeners.get(pane.getSelectedComponent());
		try {
			// Also make containing window show up
			for (par = pane.getParent(); !(par instanceof Window); par = par.getParent())
				;
			((Window) par).setVisible(true);
		} catch (NullPointerException nex) { // If it is not associated with a window, make one
			pane.moveTabToNewFrame(pane.getSelectedIndex(), 0, 0).setSize(400, 300);
		}
	}

	/**
	 * Tells this DraggableTabbedPane to listen for focus events on the parent
	 * window.
	 */
	public void addWindowFocusListener() {
		Window win = (Window) getTopLevelAncestor();
		// Notify on focus changes
		win.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				chListener.stateChanged(new ChangeEvent(getSelectedComponent()));
			}

			public void windowLostFocus(WindowEvent e) {
			}
		});
	}


	/**
	 * Constructs a new DraggableTabbedPane with a parent
	 *
	 * @param parent
	 */
	public DraggableTabbedPane(Container parent) {
		paneParent = parent;
		me=this;
		putClientProperty("JTabbedPane.tabHeight", 20);//设置tab的最大高度 详情 https://www.formdev.com/flatlaf/client-properties/#JTabbedPane

		// Set up right-click menu
		final JPopupMenu tabPopupMenu = new JPopupMenu();
		JMenuItem closeTabItem = new JMenuItem("关闭当前标签");
		closeTabItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int indx = getSelectedIndex();
				if (indx != -1) {
					JFrame newFrame = moveTabToNewFrame(indx, 0, 0);
					newFrame.setVisible(false);
					newFrame.dispose();
				}
			}
		});
		tabPopupMenu.add(closeTabItem);
		JMenuItem newWinItem = new JMenuItem("新窗口打开");
		newWinItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int indx = getSelectedIndex();
				if (indx == -1)
					return;
				moveTabToNewFrame(indx, 0, 0);
			}
		});
		tabPopupMenu.add(newWinItem);
		JMenuItem splitVerticalItem = new JMenuItem("上下分割");
		splitVerticalItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int indx = getSelectedIndex();
				if (indx == -1)
					return;
				addSplit(indx, JSplitPane.VERTICAL_SPLIT);
			}
		});
		tabPopupMenu.add(splitVerticalItem);
		JMenuItem splitHorizontalItem = new JMenuItem("左右分割");
		splitHorizontalItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int indx = getSelectedIndex();
				if (indx == -1)
					return;
				addSplit(indx, JSplitPane.HORIZONTAL_SPLIT);
			}
		});
		tabPopupMenu.add(splitHorizontalItem);
		addMouseListener(new PopupMouseListener() {
			public void showPopup(MouseEvent e) {
				if (me.getParent() instanceof MainCenterTabsPanel  ) {
					tabPopupMenu.show(DraggableTabbedPane.this, e.getX(), e.getY());
					return ;
				}
				System.out.println("adssad");
			}
		});
		// Set up dragging listener
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (!dragging && ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0)) {
					// Gets the tab index based on the mouse position
					int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());
					if (tabNumber < 0)
						return;
					draggedTabIndex = tabNumber;
					dragging = true;
					window.setVisible(true);
				} else {
					window.setLocation(e.getXOnScreen(), e.getYOnScreen());
				}
				super.mouseDragged(e);
			}
		});
		// Set up tab change focus listener
		chListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				FocusEvent event = new FocusEvent((Component) e.getSource(), getSelectedIndex());
				FocusListener listener = (FocusListener) focusListeners.get(getSelectedComponent());
				// If focus has been lost, trigger lost focus event
				if (lastFocusListener != null && lastFocusListener != listener)
					lastFocusListener.focusLost(event);
				// If we got focus, trigger gained focus event
				if (listener != null && lastFocusListener != listener) { // If we have a new tab
					listener.focusGained(event);
					lastFocusListener = listener;
				}
			}
		};

		/**
		 * 分割后复制焦点监听器
		 */
		this.addChangeListener(chListener);
		 
 
	 
		// Set up drop handler
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (!dragging)
					return;
				// We are done dragging
				dragging = false;
				window.setVisible(false);
				boolean moved = false;

				// Find out what pane this tab has been dragged to.
				for (Object tabo : panes) {
					DraggableTabbedPane pane = (DraggableTabbedPane) tabo;
					try {
						Point ptabo = pane.getLocationOnScreen();
						int x = e.getXOnScreen() - ptabo.x;
						int y = e.getYOnScreen() - ptabo.y;
						int tabNumber = pane.getUI().tabForCoordinate(pane, x, y);

						// If it's not on one of the tabs, but it's still in the tab bar, make a new tab
						int paneW = pane.getWidth();
						int paneH = pane.getHeight();
						if (tabNumber < 0 && x > 0 && y > 0 && x <= paneW && y <= paneH)
							tabNumber = pane.getTabCount() - 1;

						// We found it!
						if (tabNumber >= 0) {
							moveTabTo(draggedTabIndex, pane, tabNumber);
							return;
						}
					} catch (java.awt.IllegalComponentStateException icse) {
					} // This is fired for non-visible windows. Can be safely ignored
				}
				// Not found. Must create new frame
				moveTabToNewFrame(draggedTabIndex, e.getXOnScreen(), e.getYOnScreen());
			}
		});
		panes.add(this);
	}

	/**
	 * Splits the current tabbed pane, adding indx to the new split
	 * 
	 * @param indx
	 */
	private void addSplit(int indx, int orientation) {
		// Sanity check
		if (getTabCount() < 2)
			throw new RuntimeException("需要多个标签才能拆分视图!");
		// Make split pane
		JSplitPane split = new javax.swing.JSplitPane();
		split.setOrientation(orientation);
		split.setLeftComponent(DraggableTabbedPane.this);
		// make new tabbedpane
		final DraggableTabbedPane newTabs = new DraggableTabbedPane(split);
		// 将当前的监听器复制上去
		for (int i = 0; i < getFocusListeners().length; i++) {
			newTabs.addFocusListener(getFocusListeners()[i]);
		}
		for (int i = 0; i < getChangeListeners().length; i++) {
			newTabs.addChangeListener(getChangeListeners()[i]);
		}
		//for (int i = 0; i < getMouseListeners().length; i++) {
		//	newTabs.addMouseListener(getMouseListeners()[i]);
		//}
		//for (int i = 0; i < getKeyListeners().length; i++) {
		//	newTabs.addKeyListener(getKeyListeners()[i]);
		//}
		for (int i = 0; i < getMouseMotionListeners().length; i++) {
			newTabs.addMouseMotionListener(getMouseMotionListeners()[i]);
		}
		for (int i = 0; i < getMouseWheelListeners().length; i++) {
			newTabs.addMouseWheelListener(getMouseWheelListeners()[i]);
		}
		
		moveTabTo(indx, newTabs, 0);
		split.setRightComponent(newTabs);
		if (paneParent instanceof JFrame) {
			Dimension size = paneParent.getSize();
			((JFrame) paneParent).getContentPane().removeAll();
			((JFrame) paneParent).getContentPane().setLayout(new java.awt.GridLayout());
			((JFrame) paneParent).getContentPane().add(split);
			((JFrame) paneParent).pack();
			if ((((JFrame) paneParent).getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0)
				paneParent.setSize(size);
		} else if (paneParent instanceof JSplitPane) {
			JSplitPane splitParent = (JSplitPane) paneParent;
			if (splitParent.getRightComponent() == null) {
				splitParent.setRightComponent(split);
			} else if (splitParent.getLeftComponent() == null) {
				splitParent.setLeftComponent(split);
			}
			splitParent.setDividerLocation(0.5);
		} else if (paneParent instanceof JPanel) {
			paneParent.removeAll();
			paneParent.setLayout(new java.awt.GridLayout());
			paneParent.add(split);
			((JPanel) paneParent).validate();
		}
		split.setDividerLocation(0.5);
		paneParent = split;
	}

	/**
	 * Creates a new frame, and places the given tab in it
	 */
	private JFrame moveTabToNewFrame(int tabNumber, int x, int y) throws HeadlessException {
		final JFrame newFrame = new JFrame("_");
		newFrame.setSize(DraggableTabbedPane.this.getSize());
		newFrame.setLocation(x, y);
		// Make tabs to go in the frame
		final DraggableTabbedPane tabs = new DraggableTabbedPane(newFrame);
		moveTabTo(tabNumber, tabs, 0);
		newFrame.add(tabs);
		newFrame.setVisible(true);
		// Clean up on exit
		newFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				panes.remove(tabs);
				if (panes.size() < 1)
					System.exit(0);
			}
		});
		tabs.addWindowFocusListener();
		return newFrame;
	}
}
