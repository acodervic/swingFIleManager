package github.acodervic.filemanager.treetable;
/*
 * %W% %E%
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.FSUtil;
import github.acodervic.filemanager.gui.DraggableTabbedPane;
import github.acodervic.filemanager.gui.FileTableTab;
import github.acodervic.filemanager.gui.FileTreeTablePanel;
import github.acodervic.filemanager.gui.LocationToolPanel;
import github.acodervic.filemanager.gui.MainFrame;
import github.acodervic.filemanager.gui.MainTopToolsPanel;
import github.acodervic.filemanager.gui.popmenus.OpenContainingFolder;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.model.SearchRootRESWallper;
import github.acodervic.filemanager.model.TempFileOperation;
import github.acodervic.filemanager.thread.CopyOrMoveFIleThread;
import github.acodervic.filemanager.thread.DeepSreachThread;
import github.acodervic.filemanager.thread.FileOperation;
import github.acodervic.filemanager.thread.PublicThreadPool;
import github.acodervic.filemanager.thread.CopyOrMoveFIleThread.FileOperationResult;
import github.acodervic.filemanager.treetable.renderer.TreeNameColumnRenderer;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.data.str;
import github.acodervic.mod.io.IoType;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.thread.TimePool;
 
/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class JTreeTable extends JTable implements GuiUtil {
	protected TreeTableCellRenderer tree;
	JTreeTable me;
	FileTableTab fileTableTab;
	FileTreeTablePanel fileTreeTablePanel;
	Opt<List<RESWallper>> lastSelectedRes=new Opt<>();;//最后一次选中的资源结果
	SeachFileSystemModel searchModel;
	FileSystemModel fileSystemmodel;
	int nowEditIndex=-1;
	Opt<RESWallper> nowEditingRes=new Opt<>();

 
	MouseListener mouseListener=new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			List<RESWallper> selectedResWallper = getSelectedResWallper();
			lastSelectedRes.of(selectedResWallper);
			String text="";
			if(selectedResWallper.size()==1){
				RESWallper resWallper = selectedResWallper.get(0);
				text="selected "+resWallper.getName();
			}else{
				text="selected"+ selectedResWallper.size()+" items";
			}
			fileTableTab.getTabsPanel().getMainFrame().getMainPanel().getBottomInfoPanel().setInfText(text);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//print("getGraphics()");

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			//print("getGraphics()");

		}

		@Override
		public void mouseExited(MouseEvent e) {
			//print("getGraphics()");
		}
		
	};

	/**
	 * 开始编辑某个资源
	 * 
	 * @param res
	 */
	public void startEditRes(RESWallper res) {
		int indexOfChild = getTreeFileSystemModel().getIndexOfChild(res.getParentResWallper(), res);
		if (indexOfChild>0) {
			nowEditingRes.of(res);
			nowEditIndex = indexOfChild + 1;
			editCellAt(nowEditIndex, 1);// rotIndex+1下标因为第一个是父目录,开始编辑列
		}
	}

	public void stopEditRes() {
		removeEditor();
	}
	/**
	 * @return the nowEditingRes
	 */
	public Opt<RESWallper> getNowEditingRes() {
		return nowEditingRes;
	}
	
	KeyListener hotKeyListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			MainFrame mainFrame = fileTableTab.getTabsPanel().getMainFrame();
			TempFileOperation tempFileOperation = mainFrame.getTempFileOperation();
			Stack<TempFileOperation> tempFileOperationStack = mainFrame.getBackgroundTaskManager().getTempFileOperationStack();
			

			if(e.getKeyCode()==113){//f2 重命名
				List<RESWallper> selectedResWallper = getSelectedResWallper();
				if(selectedResWallper.size()==1){
					RESWallper resWallper = selectedResWallper.get(0);
					if(!resWallper.isWritable()){
						MessageBox.showErrorMessageDialog(mainFrame, "错误", "对"+resWallper.getName()+"没有写权限!");
						return  ;
					}
					startEditRes(resWallper);
					//getSelectionModel().addListSelectionListener(removeEditListener);
					
					/**
					 * 					str newName = str(MessageBox.showInputDialog(mainFrame, "输入新名称", "输入新文件的名称", resWallper.getName(), 0));
					if(newName.trim().notEmpty()&&!newName.eq(resWallper.getName())){
						if(resWallper.isFile()){
							//resWallper.toFileRes().rename(newName.to_s());
						}else{
							//resWallper.toDir().rename(newName.to_s());
						}
						if(!resWallper.exists()){
							//说明已经成功被删除
							//resWallper.removeChildFsromParent();
							fireTreeNodesRemoved(newList(resWallper));
							refreshUi();

						}
					}
					 */
				}
				return  ;
			}
			if(e.isControlDown()&&e.isAltDown()){
				print("alt+ctrl+"+e.getKeyCode());
				if(e.getKeyCode()==81){//alt+ctrl+q
					PublicThreadPool.FilePorcessPool_Exec("back", ()  ->{
						fileTableTab.back();
					});
				}else if(e.getKeyCode()==69){//alt+ctrl+e
					PublicThreadPool.FilePorcessPool_Exec("go", ()  ->{
						fileTableTab.go();
					});
				} 
				return  ;
			}

			//单ctrl按下
			if (e.isControlDown()&&!e.isAltDown()) {
				print(e.getKeyCode());
				if (e.getKeyCode() == 70) {// ctrl+f
					if(fileTableTab.inSearchModel()){
						if(stopSearch()){
							MessageBox.showErrorMessageDialog(mainFrame, "错误", "查询已经停止!");
						}
						fileTableTab.switchToFileModel(fileTreeTablePanel);
					}else{
						fileTableTab.switchToSearchModel(getFileTreeTablePanel(),(searchText,isRegex)  ->{
							// 递归搜索文件
							try {
								if(isRegex){
									//尝试编译 searchText
									try {
										Pattern.compile(searchText.toString());
									} catch (Exception e1) {
										MessageBox.showErrorMessageDialog(null, "查询错误","无法编译regex:"+searchText);
										return ;
									}
								}else{
									searchText=(".*"+searchText+".*");
								}
								RESWallper startDir = getRootDir().get();
								if(startDir instanceof SearchRootRESWallper rrew){
									startDir=rrew.getDir();
								}
								SearchRootRESWallper searchRoot = new SearchRootRESWallper(startDir, searchText);
								Vector<RESWallper> list = new Vector<>();
								// 给List加锁,因为另外一个线程要定时拿出
								Collections.synchronizedList(list);
								logInfo("搜索");
								DeepSreachThread deepSreachThread = new DeepSreachThread(startDir.getFileObj(), res -> {
									searchRoot.addChild(res);
									list.add(res);
									//logInfo("搜索到" + res.getAbsolutePath());
									CountDownLatch countDownLatch=new CountDownLatch(1);
	
									// 通知ui
									SwingUtilities.invokeLater(() -> {
										// swing 线程中处理
										fireTreeNodesInserted(searchRoot, newList(res));
										refreshUi();
										countDownLatch.countDown();
									});
									try {
										countDownLatch.await();
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
	
								}, searchText);
								searchRoot.setStartSearchTime(System.currentTimeMillis());
								deepSreachThread.setOnFinishCall(resultes -> {
									searchRoot.setEndSearchTime(System.currentTimeMillis());
									logInfo("搜索已经完成:" + searchRoot.getName());
									refreshUi();
									fileTableTab.cancelLoadIng();
								});
	
								searchRoot.setSearchThread(deepSreachThread);
								fileTableTab.loadIng();
								fileTableTab.getTabsPanel().resetThisTabheaderWidth();
						
	 
							
								searchModel = new SeachFileSystemModel(searchRoot);
								searchModel.setTree(tree);
								tree.setModel(searchModel);
								tree.updateUI();

								// Install a tableModel representing the visible rows in the tree.
								getTreeTableModelAdapter().setTreeTableModel(searchModel);
								Opt<TreePath> treePathByNodeObj = fileSystemmodel.getTreePathByNodeObj(searchRoot);
								//必须展开节点，否则搜索的内容不会显示
								if (treePathByNodeObj.notNull_()) {
									tree.expandPath(treePathByNodeObj.get());
								}
	
								deepSreachThread.startSearch();
								// 进度进度线程每1秒刷新一次,避免大量调用fireTreeNodesInserted导致线程被阻塞死
	 
	
								logInfo("正在搜索中");
							} catch (Exception e1) {
								e1.printStackTrace();
							}						
						});//切换到查询模式
					}

 
					// DeepSreachThread
				} else if (e.getKeyCode() == 83) {// ctrl+s 在当前目录搜索文件
					str search = str(MessageBox.showInputDialog(me, "在当前表格内容中搜索", "输入搜索字符串", null, -1));
					if (search.trim().notEmpty()) {
						me.fileSystemmodel.nowSearchTextBuffer = search.to_s();
						PublicThreadPool.FilePorcessPool_Exec("searchAndSelectedNode", ()  ->{
							searchAndSelectedNode(true);
						});
					}
				} else if (e.getKeyCode() == 82) {// ctrl+r 刷新目录
					updateTable();
				}else if(e.getKeyCode()==67){//ctrl+c
					List<RESWallper> fileOperationTargetRes = tempFileOperation.getFileOperationTargetRes();
					List<RESWallper> copyTargetRes = fileOperationTargetRes;
					copyTargetRes.clear();
					 copyTargetRes.addAll( getSelectedResWallper());
					 tempFileOperation.setFileOperation(FileOperation.COPY);
					 tempFileOperation.setTargetFileOrDirsSouceDir(getRootDir().get());
					 logInfo("当前准备复制:"+copyTargetRes.size()+"个文件.");
					setRessToClipboardString(copyTargetRes);

				} else if (e.getKeyCode() == 86) {// ctrl+v
					List<RESWallper> copyOrMoveTargetRes = tempFileOperation.getFileOperationTargetRes();
					List<FileOperationResult> copyOrMoveFIleResList = new ArrayList<>();
					if (copyOrMoveTargetRes.size() == 0) {
						return;
					}

					PublicThreadPool.FilePorcessPool_Exec("copyOrMoveFIle", () -> {
						// 判断当前执行目录是否可写
						Opt<RESWallper> rootDir = getRootDir();
						if (copyOrMoveTargetRes.size() > 0) {
							// asdasdsad

							List<RESWallper> selectedResWallper = getSelectedResWallper();
							FileObject targetDir = getRootDir().get().getFileObj();// 只能复制到当前root目录

							/**
							 * if (selectedResWallper.size() == 1) {
							 * RESWallper target = selectedResWallper.get(0);
							 * if (target.isDir()) {
							 * targetDir = target.getFileObj();
							 * } else {
							 * try {
							 * targetDir = target.getFileObj().getParent();
							 * } catch (FileSystemException e1) {
							 * e1.printStackTrace();
							 * }
							 * }
							 * 
							 * } else if (selectedResWallper.size() > 1) {
							 * MessageBox.showInfoMessageDialog(me, "错误", "请选中要复制到的文件/夹,只能选择一个");
							 * }
							 */

							if (notNull(targetDir)) {
								try {
									if (targetDir.exists()) {

										if (targetDir.isWriteable()) {
											// 开始复制
											CopyOrMoveFIleThread copyOrMoveFIleThread = new CopyOrMoveFIleThread(
													tempFileOperation.getFileOperation(),
													tempFileOperation.getTargetFileOrDirsSouceDir().getFileObj(), targetDir, false,copyOrMoveTargetRes);
											copyOrMoveFIleThread.setResultProcessFun(result -> {
												// print("操作完成" + result.getTargetDir().getAbsolutePath());
												copyOrMoveFIleResList.add(result);
											});
											// 执行任务
											mainFrame.getBackgroundTaskManager().execTask(copyOrMoveFIleThread);
											// try {
											// copyOrMoveFIleThread.syncWaitFinish();
											// } catch (Exception e1) {
											// }
											// /print(needCopyFIles);

										} else {
											MessageBox
													.showErrorMessageDialog(me, "错误",
															"目标" + targetDir.toString() + "不可写!");
										}

									} else {
										MessageBox.showErrorMessageDialog(me, "错误",
												"目标" + targetDir.toString() + "不存在!");
									}
								} catch (FileSystemException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}

						}
					});

					// 保存操作记录到堆栈
					TempFileOperation clone = tempFileOperation.clone();
					/**
					 * clone.getRecoverFun().of(() ->{
					 * //撤回配置
					 * for(int i=0;i<copyOrMoveFIleResList.size();i++){
					 * FileOperationResult fileOperationResult = copyOrMoveFIleResList.get(i);
					 * File distResources = fileOperationResult.getDistResources();
					 * if(fileOperationResult.isSucess()&&notNull(distResources)){
					 * if(fileOperationResult.getOperation()==FileOperation.MOVE){
					 * //移动动作的撤回操作
					 * //将文件重新移动回去
					 * move(fileOperationResult.getDistResources(),
					 * fileOperationResult.getSourceFile());
					 * }else if(fileOperationResult.getOperation()==FileOperation.COPY){
					 * //复制动作的撤回操作
					 * //删除被复制的内容
					 * //logInfo("删除"+distResources.getAbsolutePath());
					 * delete(distResources);
					 * }
					 * }
					 * 
					 * }
					 * 
					 * 
					 * });
					 */
					tempFileOperationStack.push(clone);
				}else if (e.getKeyCode() == 88){//ctrl + x

					List<RESWallper> fileOperationTargetRes = tempFileOperation.getFileOperationTargetRes();
					List<RESWallper> copyTargetRes = fileOperationTargetRes;
					copyTargetRes.clear();
					 copyTargetRes.addAll( getSelectedResWallper());
					 tempFileOperation.setFileOperation(FileOperation.MOVE);
					 tempFileOperation.setTargetFileOrDirsSouceDir(getRootDir().get());
					logInfo("当前准备移动:"+copyTargetRes.size()+"个文件.");
				}else if (e.getKeyCode() == 68){//ctrl+d.放进垃圾箱
					List<RESWallper> selectedResWallper = getSelectedResWallper();
					if(selectedResWallper.get(0).getAbsolutePath().startsWith(FileTableTab.trashPath)){
						return ;
					}

					int selectedRow = getSelectedRow();
					PublicThreadPool.FilePorcessPool_Exec("putToTrash", () -> {
						Value needrefUi=new Value(false);
						selectedResWallper.forEach(res -> {
							if(res.isLocalFile()){
								res.putToTrash();
							} else {
								//
								Boolean delete = MessageBox.showConfirmDialog(mainFrame, "是否删除", "执行 永久删除?", 1);
								if (delete) {
									Opt<Boolean> sucess=res.delete();
									if(sucess.get()){
										needrefUi.setValue(true);
									}else{
										 MessageBox.showConfirmErrorDialog(mainFrame, "删除失败", "删除失败"+res.getAbsolutePath()+"   Exception="+sucess.getException());
									}
								}
							}
						});
						if(needrefUi.get(Boolean.class)){
							updateTable();
						}
						// 设选中
						int rowCount = getRowCount();
						if (rowCount > selectedRow) {
							getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
						} else {
							getSelectionModel().setSelectionInterval(rowCount - 1, rowCount - 1);
						}
						//updateUI();
					});

					TempFileOperation tmo=new TempFileOperation();
					tmo.getFileOperationTargetRes().addAll(selectedResWallper);
					tmo.setFileOperation(FileOperation.PUTTOTRASH);
					tempFileOperationStack.push(tmo);

				}else if(e.getKeyCode()==84){//ctrl+t 在当前位置打开新的标签
					
					getRootDir().ifNotNull_(root  ->{
						PublicThreadPool.FilePorcessPool_Exec("copyOrMoveFIle", ()  ->{
							getFileTableTab().getTabsPanel().getMainFrame().getMainPanel().getCenterTabsPanel().addFilesTableTab(root, true);
							 });
					});
				}else if(e.getKeyCode()==87){//ctrl+W 关闭当前标签
					getFileTableTab().getTabsPanel().getMainFrame().getMainPanel().getCenterTabsPanel().removeFilesTableTab(fileTableTab);
					//
					fileTableTab.freeMe();
				}else if(e.getKeyCode()==69){//ctrl+e 切换到 按钮组/地址模式
					MainTopToolsPanel topToolsPanel = getFileTableTab().getTabsPanel().getMainFrame().getMainPanel().getTopToolsPanel();
					LocationToolPanel tp =topToolsPanel.getLocationToolPanel();
					if(tp.isButtonGroupModel()){
						tp.changeToLocationEditModel();
						Component editorComponent = tp.getLocationEditableCombox().getEditor().getEditorComponent();
						if(editorComponent instanceof JTextField text){
								SwingUtilities.invokeLater(()  ->{
									text.requestFocus();
									text.selectAll();
								});
						}
					}else{
						tp.changeToButtonGroupModel();
					}
					tp.updateUI();
					//选中文本内容

				}else if(e.getKeyCode()==90){//ctrl+z 撤回
					if(tempFileOperationStack.size()>0){
						TempFileOperation pop = tempFileOperationStack.pop();
						if(pop.getRecoverFun().notNull_()){
							PublicThreadPool.FilePorcessPool_Exec("recover", ()  ->{
								pop.getRecoverFun().get().run();
							});
						}
					}else{
						 logInfo("没有可以撤回的的动作!");
					}
					
				}
			} else if (e.isAltDown()&&!e.isControlDown()) {			//单alt按下按下
				
				print(e.getKeyCode());
				Container parent = fileTableTab.getParent();
				if(e.getKeyCode()==90){//alt+z 移动到左侧tab
					if (parent instanceof DraggableTabbedPane dtp) {
						int selectedIndex = dtp.getSelectedIndex();
						int tabCount = dtp.getTabCount();
						if (tabCount == 1) {
							return;
						}
						if ((selectedIndex - 1) < 0) {
							// 说明已经在最左侧,移动到最右侧
							dtp.setSelectedIndex(tabCount-1);
						} else {
							dtp.setSelectedIndex(selectedIndex -1);
						}
					}

				} else if (e.getKeyCode() == 67) {// alt+c 移动到右侧tab
					if (parent instanceof DraggableTabbedPane dtp) {
						int selectedIndex = dtp.getSelectedIndex();
						int tabCount = dtp.getTabCount();
						if (tabCount == 1) {
							return;
						}
						if ((selectedIndex + 1) > (tabCount - 1)) {
							// 说明已经在最右侧,移动到0index
							dtp.setSelectedIndex(0);
						} else {
							dtp.setSelectedIndex(selectedIndex + 1);
						}
					} 
					
					print("lastSearchResultes");
				}else if (e.getKeyCode() == 10) {// alt+enter 新tab打开当前选中的dir
					List<RESWallper> selectedResWallper = getSelectedResWallper();
					PublicThreadPool.FilePorcessPool_Exec("openNewTasb", ()  ->{
						for (int i = 0; i < selectedResWallper.size(); i++) {
							RESWallper resWallper = selectedResWallper.get(i);
							if (resWallper.isDir() && resWallper.exists()) {
								fileTableTab.getTabsPanel().addFilesTableTab(resWallper, true);
							}
						}

					});
						 

				}
			} else {
				if (e.getKeyCode() == 27) {// esc
					if (inSearchModel()) {
						if (inSearching()) {
							// 杀死查询线程
							stopSearch();
							MessageBox.showInfoMessageDialog(null, "通知", "查询已经停止");
						} else {
							switchToFileModel();
						}
					} else {
						me.fileSystemmodel.nowSearchTextBuffer = "";
						fileTableTab.getTabsPanel().getMainFrame().getMainPanel().getBottomInfoPanel()
								.getRightInfoLable().setText("Search: " + me.fileSystemmodel.nowSearchTextBuffer);
						me.me.fileSystemmodel.getLastSearchResultes().clear();
								setSelectedResWallpers(null);
					}
				}

				// 处理alt+ctrl+key的情况
				if (e.isAltDown() && e.isControlDown()) {
	
				}
			}
		}


 

		

 
		private void setRessToClipboardString(List<RESWallper> copyTargetRes) {
			str clipboardString = new str("");
			for (int i = 0; i < copyTargetRes.size(); i++) {
				RESWallper resWallper = copyTargetRes.get(i);
				try {
					if(resWallper.isLocalFile()){
						if ("".equals(clipboardString)) {
							if((i +1)<copyTargetRes.size() ){
								clipboardString.setString(resWallper.getAbsolutePathWithoutProtocol() + "\n");
							}else{
								clipboardString.setString(resWallper.getAbsolutePathWithoutProtocol() );
							}
						} else {
							if((i +1)<copyTargetRes.size() ){
								clipboardString.insertStrToEnd(resWallper.getAbsolutePathWithoutProtocol() + "\n");
							}else{
								clipboardString.insertStrToEnd(resWallper.getAbsolutePathWithoutProtocol() );
							}
						}
					}else{
						if ("".equals(clipboardString)) {
							if((i +1)<copyTargetRes.size() ){
								clipboardString.setString(resWallper.getAbsolutePath() + "\n");
							}else{
								clipboardString.setString(resWallper.getAbsolutePath() );
							}
						} else {
							if((i +1)<copyTargetRes.size() ){
								clipboardString.insertStrToEnd(resWallper.getAbsolutePath() + "\n");
							}else{
								clipboardString.insertStrToEnd(resWallper.getAbsolutePath() );
							}
						}
					}

				} catch (Exception ed) {

				}

			}
			if (!"".equals(clipboardString)) {

				TimePool.getStaticTimePool().setTimeOut(250, ()  ->{
					setClipboardString(clipboardString.to_s());
				});
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

	};

	KeyListener searchKeyListener = new KeyListener() {

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			PublicThreadPool.SwingEventPool_Exec(TOOL_TIP_TEXT_KEY, () -> {
			
			if(e.getKeyCode()==10){
				//回车进入目录
				RESWallper resWallper = getSelectedResWallper().get(0);
				fileTableTab.navigationToDIr(resWallper, true);
				return  ;
			}else if(e.getKeyCode()==40){//下箭头
					searchAndSelectedNode(true);
				return ;
			}else if(e.getKeyCode()==38){//上箭头
					searchAndSelectedNode(false);
					repaint();
				return ;
			}else if(e.getKeyCode()==39){//右箭头
				//展开节点
					RESWallper resWallper = getSelectedResWallper().get(0);
					if(resWallper.isDir()){
						Opt<TreePath> treePathByNodeObj = fileSystemmodel.getTreePathByNodeObj(resWallper);
						if (treePathByNodeObj.notNull_()) {
							tree.expandPath(treePathByNodeObj.get());
							// 选中原来的位置
							setSelectedResWallpers(newList(resWallper));
						}
					}
					return ;
			}else if(e.getKeyCode()==37){//左箭头
				//折叠节点
				RESWallper resWallper = getSelectedResWallper().get(0);
				if(resWallper.isDir()){
					Opt<TreePath> treePathByNodeObj = fileSystemmodel.getTreePathByNodeObj(resWallper);
					if(treePathByNodeObj.notNull_()){
						tree.collapsePath(treePathByNodeObj.get());
						//选中原来的位置
						setSelectedResWallpers(newList(resWallper));
					}
				}

				return ;
			}else if(e.getKeyCode()==8){//退格 放入回收站
				List<RESWallper> selectedResWallper = getSelectedResWallper();
				if(selectedResWallper.size()==0){
					return ;
				}
				RESWallper root= getRootDir().get();
				if(root.isLocalFile()){
					//本地文件系统
					for(int i=0;i<selectedResWallper.size();i++){						 
					   selectedResWallper.get(i).putToTrash();
					}
				}else{
					 //直接删除
					 Boolean showConfirmDialog = MessageBox.showConfirmDialog(editorComp, "删除警告", "将永久删除"+selectedResWallper.size()+"个资源,是否继续", 1);
					 if(showConfirmDialog){
						 

					 }

				}

				return ;
			}
			if (e.isAltDown() || e.isControlDown()) {
				return;// 不要做任何操作
			}
			if (e.getKeyCode() == 8) {// 删除操作
				if(me.fileSystemmodel.nowSearchTextBuffer.length()>0){
					me.fileSystemmodel.nowSearchTextBuffer = me.fileSystemmodel.nowSearchTextBuffer.substring(0, me.fileSystemmodel.nowSearchTextBuffer.length() - 1);
					print("当前search:" + me.fileSystemmodel.nowSearchTextBuffer);
					PublicThreadPool.FilePorcessPool_Exec("searchAndSelectedNode", ()  ->{
						searchAndSelectedNode(true);
					});
				}else{
					setSelectedResWallpers(null);
				}
			} else if (e.getKeyCode() == 32) {
				// 空格开始搜索
				searchAndSelectedNode(null);
			} else {
				
				char c=e.getKeyChar();
				if(Character.isLetterOrDigit(c)){
					me.fileSystemmodel.nowSearchTextBuffer +=c;
					print("当前search:" + me.fileSystemmodel.nowSearchTextBuffer);
					PublicThreadPool.FilePorcessPool_Exec("searchAndSelectedNode", ()  ->{
						searchAndSelectedNode(true);
					});
				}
			}
			fileTableTab.getTabsPanel().getMainFrame().getMainPanel().getBottomInfoPanel().getRightInfoLable().setText("Search: "+me.fileSystemmodel.nowSearchTextBuffer);
		});

		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

	};

	public void switchToFileModel() {
		// 取消查询模式
		tree.setModel(fileSystemmodel);
		fileTableTab.switchToFileModel(fileTreeTablePanel);
		fileTableTab.randerTabHeaderPanel();
	}

	/**
	 * 停止查询
	 * @return 
	 */

	public  boolean stopSearch() {
		if (inSearchModel()) {
			if (inSearching()) {
				// 停止查询并切换到普通模式
				Object root = getTreeFileSystemModel().getRoot();
				if (root instanceof SearchRootRESWallper srr) {
					srr.stopSearch();
					fileTableTab.cancelLoadIng();
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * @param fileTreeTablePanel the fileTreeTablePanel to set
	 */
	public void setFileTreeTablePanel(FileTreeTablePanel fileTreeTablePanel) {
		this.fileTreeTablePanel = fileTreeTablePanel;
	}

	/**
	 * @return the fileTreeTablePanel
	 */
	public FileTreeTablePanel getFileTreeTablePanel() {
		return fileTreeTablePanel;
	}
	/**
	 * @return the hotKeyListener
	 */
	public KeyListener getHotKeyListener() {
		return hotKeyListener;
	}
	/**
	 * 在新的线程中启动查询
	 */
	public void searchAndSelectedNode(Boolean down) {
		if (str(me.fileSystemmodel.nowSearchTextBuffer).trim().isEmpty()) {
			return;
		}
		// 搜索当前展开的所有树节点
		new Thread(() -> {

			if (down!=null&& me.fileSystemmodel.nowSearchTextBuffer.equals(me.fileSystemmodel.lastSearchText) && me.fileSystemmodel.getLastSearchResultes().size() > 0) {
				// 重复的搜索,滚动滚动条到下一个搜索

				// 先继续设置选中
				//setSelectedResWallpers(lastSearchResultes);

				SwingUtilities.invokeLater(() -> {
					int lastResSelectedIndex = -1;
					if (me.fileSystemmodel.getLastSearchScrolReswalleper().notNull_()) {
						lastResSelectedIndex = me.fileSystemmodel.getLastSearchResultes().indexOf(me.fileSystemmodel.getLastSearchScrolReswalleper().get());// 资源在上次结果中的下标
					}
					if (lastResSelectedIndex == -1) {
						if (down) {
							RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(0);
							setSelectedResWallpers(newList(res));
							// 选中第一个
							scrollToRes(res);
						}else{
							 //选中最后一个
							 RESWallper res = me.fileSystemmodel.getLastSearchResultes().get( me.fileSystemmodel.getLastSearchResultes().size()-1);
							 setSelectedResWallpers(newList(res));
							 // 选中第一个
							 scrollToRes(res);
						}

					} else {
						if (lastResSelectedIndex < (me.fileSystemmodel.getLastSearchResultes().size() - 1)) {// 不是最后一个
							if (down) {
								// 跳到下一个
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(lastResSelectedIndex + 1);
								scrollToRes(res);
								setSelectedResWallpers(newList(res));
							} else {
								if (lastResSelectedIndex==0) {
																		 								// 跳到上一个
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(me.fileSystemmodel.getLastSearchResultes().size()-1);
								scrollToRes(res);
								setSelectedResWallpers(newList(res));
								}else{
									 								// 跳到上一个
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(lastResSelectedIndex - 1);
								scrollToRes(res);
								setSelectedResWallpers(newList(res));
								}

							}

						} else {
							// 已经是最后一个了
							if (down) {
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(0);
								setSelectedResWallpers(newList(res));
								// 跳到第一个
								scrollToRes(res);
							}else{
								if (me.fileSystemmodel.getLastSearchResultes().size()>2) {
									RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(me.fileSystemmodel.getLastSearchResultes().size()-2);
									setSelectedResWallpers(newList(res));
									// 跳到第一个
									scrollToRes(res);
								}else{
		 
								}

							}
						}
					}
				});

			} else {
				int rowCount = getModel().getRowCount();
				List<RESWallper> needSelected = new ArrayList<>();
				for (int i = 0; i < rowCount; i++) {
					RESWallper res = (RESWallper) getTreeTableModelAdapter().nodeForRow(i);
					if (getTreeFileSystemModel().getRoot() == res) {
						// 跳过搜索跟节点
						continue;
					} else {
						str macthString = str(res.getMatchString());
						if (macthString.isEmpty()) {
							continue;
						} else {
							if (macthString.hasNoCase(me.fileSystemmodel.nowSearchTextBuffer)) {
								needSelected.add(res);
							}
						}
					}

				}
				me.fileSystemmodel.getLastSearchResultes().clear();
				if (needSelected.size() > 0) {
					SwingUtilities.invokeLater(() -> {
						setSelectedResWallpers(needSelected);
						// 滚动到地一个选中
						scrollToRes(needSelected.get(0));
						setLastSearchResultes(needSelected);
					});
				}
				me.fileSystemmodel.lastSearchText = me.fileSystemmodel.nowSearchTextBuffer;
			}
		}).start();

	}

	/**
	 * @param lastSearchResultes the lastSearchResultes to set
	 */
	void setLastSearchResultes(List<RESWallper> lastSearchResultes) {
		me.fileSystemmodel.setLastSearchResultes(lastSearchResultes);
	}

	/**
	 * 设置选中
	 * 
	 * @param res
	 * @return
	 */
	public List<TreePath> setSelectedResWallpers(List<RESWallper> res) {
		if (isNull(res)) {
			tree.getSelectionModel().setSelectionPaths(null);
			return null;
		}else{
		
			List<TreePath> paths = getTreeFileSystemModel().getTreePatshByNodsObjs((List) res);
			if (paths.size() > 0) {
				tree.getSelectionModel().setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
			}
			return paths;	 
		}
	}

	public TreeTableModelAdapter getTreeTableModelAdapter() {
		return (TreeTableModelAdapter) getModel();
	}

	public FileSystemModel getTreeFileSystemModel() {
		return (FileSystemModel) tree.getModel();
	}

	/**
	 * 滚动滚动条到资源
	 * 
	 * @param res
	 */
	public void scrollToRes(RESWallper res) {
		me.fileSystemmodel.getLastSearchScrolReswalleper().of(res);
		Opt<TreePath> treePathByNodeObj = getTreeFileSystemModel().getTreePathByNodeObj(res);
		if (treePathByNodeObj.notNull_()) {
			System.out.println("选中:" + treePathByNodeObj.get());
			Rectangle bounds = tree.getPathBounds(treePathByNodeObj.get());
			// set the height to the visible height to force the node to top
			bounds.height = getVisibleRect().height;
			scrollRectToVisible(bounds);// 当表格滚动选中
		}

	}

	public JTreeTable(TreeTableModel treeTableModel, FileTableTab fileTbaleTab) {
		super();
		this.fileTableTab = fileTbaleTab;
		getTableHeader().setPreferredSize(new Dimension(100, 30));// 设置tableheader的高度为30px
		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer(treeTableModel, this); // tree渲染器
		tree.setToggleClickCount(0);//点击不要展开
		

		me = this;
		setRowHeight(24);//设置行高
		// Install a tableModel representing the visible rows in the tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree, this));
		fileSystemmodel = (FileSystemModel) treeTableModel;
		initColumRander();

		// Force the JTable and JTree to share their row selection models.
		tree.setSelectionModel(new DefaultTreeSelectionModel() {
			// Extend the implementation of the constructor, as if:
			/* public this() */ {
				setSelectionModel(listSelectionModel);
			}
		});
		// Make the tree and table row heights the same.
		tree.setRowHeight(getRowHeight());

		tree.setCellRenderer(new TreeNameColumnRenderer(this));

		

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		TreeTableCellEditor editor = new TreeTableCellEditor(this);
		//editor.setClickCountToStart(3);
		setDefaultEditor(TreeTableModel.class, editor);

		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				PublicThreadPool.SwingEventPool_Exec("MouseAdapter", ()  ->{
					int r = rowAtPoint(e.getPoint());
					if (e.getButton() == 2) {
						//选中位置
						if (inSearchModel()) {
							setSelectedResWallpersByRow(r);
							//查询模式则在新的tab中打开目标
							OpenContainingFolder.openNewTab(new ArrayList<>(getSelectedResWallper()), fileTbaleTab.  getTabsPanel().getMainFrame().getMainPanel(),false);
							return ;
						} else {
							// 鼠标中键 自动折叠/展开节点
							if (getModel() instanceof TreeTableModelAdapter tmap) {
								TreePath treePathForRow = tmap.getTreePathForRow(r);
								if (notNull(treePathForRow)) {
									boolean collapsed = tree.isCollapsed(treePathForRow);
									if (collapsed) {
										tree.expandPath(treePathForRow);
									} else {
										tree.collapsePath(treePathForRow);
									}
									setSelectedResWallpersByRow(r);
								}
								return;
							}
						}
					}
					// else {
					// clearSelection();
					// }
	
					if (e.getButton() != 3) {
						// 只右键弹出
						return;
					}
	
					int rowindex = getSelectedRow();
					// if (rowindex < 0)
					// return;
				});
			}


		});
		//表头向左对齐
		((DefaultTableCellRenderer)getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.LEFT);
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				"Enter");
		getActionMap().put("Enter", new AbstractAction() {//
			@Override
			public void actionPerformed(ActionEvent ae) {
			}
		});
		addKeyListener(searchKeyListener);
		addKeyListener(hotKeyListener);
		tree.addMouseListener(mouseListener);
		fileTbaleTab.addKeyListener(getHotKeyListener());
		//autoPrintTableColumnWidth("TOOL_TIP_TEXT_KEY");
		setColumnWidth();
	}
	private void setSelectedResWallpersByRow(int r) {
		if (r >= 0 && r < getRowCount()) {
			setRowSelectionInterval(r, r);// 选中鼠标在的行
			List<RESWallper> selectedResWallper = getSelectedResWallper();
			setSelectedResWallpers(selectedResWallper);
		}
	}
	public Opt<RESWallper> getRootDir() {
		Opt<RESWallper> ret = new Opt<>();
		if (tree.getModel() instanceof FileSystemModel rsm) {
			ret.of((RESWallper) rsm.getRoot());
		}
		return ret;
	}

	public List<RESWallper> getSelectedResWallper() {
		List<RESWallper> selectedItems = new ArrayList<>();
		int[] selectedRows = getSelectedRows();
		if (selectedRows.length > 0) {
			if (getModel() instanceof TreeTableModelAdapter tmap) {
				for (int i = 0; i < selectedRows.length; i++) {
					Object rowValueAt = tmap.getRowValueAt(selectedRows[i]);
					if (rowValueAt instanceof RESWallper res) {
							selectedItems.add(res);
					}
				}
			}
		}
		return selectedItems;
	}

	private void initColumRander() {
		// 设置列渲染器
		for (int i = 0; i < Columns.ColumnNamesArray.length; i++) {
			Column c = Columns.ColumnNamesArray[i];
			if (c != Columns.Name) {// name列是树,使用TreeTableCellRenderer渲染
				getColumnModel().getColumn(i).setCellRenderer(c.getRander());
			}
		}
	}

	/**
	 * 是否在查询状态
	 * 
	 * @return
	 */
	public boolean inSearchModel() {
		return tree.getModel() instanceof SeachFileSystemModel;
	}

	public void changeToFileSystemModel() {
		tree.setModel(fileSystemmodel);
		updateUI();
	}

	/**
	 * 是否正在搜索
	 * 
	 * @return
	 */
	public boolean inSearching() {
		if (inSearchModel()) {
			return ((SearchRootRESWallper) ((SeachFileSystemModel) tree.getModel()).getRoot()).inSearching();
		}
		return false;
	}

	public void updateTable() {
		fileTableTab.getNowShowingTable().updateAllTable();
		fileTableTab.getNowShowingTable().requestFocus();
		
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to
	 * paint the renderers and editors and overriding setBounds() below
	 * is not the right thing to do for an editor. Returning -1 for the
	 * editing row in this case, ensures the editor is never painted.
	 */
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}

	// @Override
	// public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
	// int direction) {
	// return 0;// 启用动画时候,由动画来处理滚动像素
	// }

	/**
	 * @return the tree
	 */
	public TreeTableCellRenderer getTree() {
		return tree;
	}
	//
	// The editor used to interact with tree nodes, a JTree.
	//

 

	public void fireTreeNodesRemoved(List<RESWallper> wallpers) {
		if (getModel() instanceof TreeTableModelAdapter ttmap) {
			// ttmap.fireTableDataChangzed();
			if (tree.getModel() instanceof FileSystemModel rsm) {
					int[] selectedRows = getSelectedRows();
					int lastSelectedIndex = -1;
					if (selectedRows.length > 0) {
						lastSelectedIndex = selectedRows[0];
					}
					List<TreePath> treePatshByNodsObjs = rsm.getTreePatshByNodsObjs((List) wallpers);
						rsm.fireTreeNodesRemoved(treePatshByNodsObjs);
					// 一百毫秒后删除，方便刷新线程刷新Ui
					if (lastSelectedIndex > -1) {
						// 还是选中原来的位置
						getTree().setSelectionInterval(lastSelectedIndex, lastSelectedIndex);
					}

			}
		}
	}

	public void fireTreeNodesInserted(RESWallper parentDir, List<RESWallper> childs) {
		if (getModel() instanceof TreeTableModelAdapter ttmap) {
			// ttmap.fireTableDataChanged();
			if (tree.getModel() instanceof FileSystemModel rsm) {
					List<TreePath> treePatshByNodsObjs = rsm.getTreePatshByNodsObjs((List) newList(parentDir));
					if (treePatshByNodsObjs.size() > 0) {
						// long currentTimeMillis = System.currentTimeMillis();
						int selectedRow = getSelectedRow();
						rsm.fireTreeNodesInserted(treePatshByNodsObjs.get(0), childs);
						// System.out.println("耗时-" + (System.currentTimeMillis() - currentTimeMillis) +
						// "ms");
						if (selectedRow>-1) {
							setSelectedResWallpersByRow(selectedRow);
						}
						
					}
			}
		}
	}

	public void fireTreeNodesChanged( List<RESWallper> childs) {
		if (getModel() instanceof TreeTableModelAdapter ttmap) {
			// ttmap.fireTableDataChanged();
			if (tree.getModel() instanceof FileSystemModel rsm) {
				List<TreePath> treePatshByNodsObjs = rsm.getTreePatshByNodsObjs(newList(childs.get(0).getParentResWallper()));
				if (treePatshByNodsObjs.size() > 0) {
					SwingUtilities.invokeLater(() -> {
						rsm.fireTreeNodesChanged(treePatshByNodsObjs.get(0), childs);
					});
					//long currentTimeMillis = System.currentTimeMillis();
					//System.out.println("耗时-" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
				}
			}
		}
	}

	/**
	 * @return the fileTbaleTab
	 */
	public FileTableTab getFileTableTab() {
		return fileTableTab;
	}


	Boolean fireTableDataChangeding=false;


	/**
	 * 刷新ui
	 */
	public  void refreshUi() {
		if (!fireTableDataChangeding) {
			List<RESWallper> selectedResWallper = getSelectedResWallper();
			getTreeTableModelAdapter().fireTableDataChanged();
			System.out.println("刷新ui");
			fireTableDataChangeding=false;
			if (selectedResWallper.size()>0) {
				setSelectedResWallpers(selectedResWallper);
			}
		}
	}

	/**
	 * 重新刷新整个表格
	 */
	public void updateAllTable() {
		SwingUtilities.invokeLater(()  ->{
			RESWallper root = getRootDir().get();
			root.resetChildsRes();
			FileSystemModel fileSystemModel=new FileSystemModel(root);
			setModel(new TreeTableModelAdapter(fileSystemModel, tree, this));
			tree.setModel(fileSystemModel);
			refreshUi();
			setColumnWidth();
			initColumRander();
		});
	}

	double[] percentages={0.057,0.403,0.189,0.088,0.088,0.088};

	public void setColumnWidth() {
		final double factor = 10000;
		TableColumnModel model = this.getColumnModel();
		for (int columnIndex = 0; columnIndex < percentages.length; columnIndex++) {
			try {
			// renderer.setBackground(Color.LIGHT_GRAY); //不设置颜色
			TableColumn column = model.getColumn(columnIndex);
			// column.setHeaderRenderer(renderer);
			column.setPreferredWidth((int) (percentages[columnIndex] * factor));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}


	    /**
     * 对表格列的比进行输出,便于设置setColumnWidth函数
     */
    public void autoPrintTableColumnWidth(String tableName) {
		getModel();
            TableColumnModel columnModel = getColumnModel();
            TimePool.getStaticTimePool().Interval(3000, () -> {
                String widths = "";
                DecimalFormat df = new DecimalFormat("0.000");
                int d = 0;
                for (int i = 0; i < Columns.ColumnNamesArray.length; i++) {
                    TableColumn column = columnModel.getColumn(i);
                    // 计算当前列和表格宽度的比
                    double a = ((double) column.getWidth()) / ((double) getWidth());
                    d += (a * 1000);
                    // 如果是最后一个列,则将所以不足100的补足到最后一列上
                    if (i == Columns.ColumnNamesArray .length - 1) {
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
