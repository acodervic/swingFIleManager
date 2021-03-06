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
	Opt<List<RESWallper>> lastSelectedRes=new Opt<>();;//?????????????????????????????????
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
	 * ????????????????????????
	 * 
	 * @param res
	 */
	public void startEditRes(RESWallper res) {
		int indexOfChild = getTreeFileSystemModel().getIndexOfChild(res.getParentResWallper(), res);
		if (indexOfChild>0) {
			nowEditingRes.of(res);
			nowEditIndex = indexOfChild + 1;
			editCellAt(nowEditIndex, 1);// rotIndex+1?????????????????????????????????,???????????????
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
			

			if(e.getKeyCode()==113){//f2 ?????????
				List<RESWallper> selectedResWallper = getSelectedResWallper();
				if(selectedResWallper.size()==1){
					RESWallper resWallper = selectedResWallper.get(0);
					if(!resWallper.isWritable()){
						MessageBox.showErrorMessageDialog(mainFrame, "??????", "???"+resWallper.getName()+"???????????????!");
						return  ;
					}
					startEditRes(resWallper);
					//getSelectionModel().addListSelectionListener(removeEditListener);
					
					/**
					 * 					str newName = str(MessageBox.showInputDialog(mainFrame, "???????????????", "????????????????????????", resWallper.getName(), 0));
					if(newName.trim().notEmpty()&&!newName.eq(resWallper.getName())){
						if(resWallper.isFile()){
							//resWallper.toFileRes().rename(newName.to_s());
						}else{
							//resWallper.toDir().rename(newName.to_s());
						}
						if(!resWallper.exists()){
							//???????????????????????????
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

			//???ctrl??????
			if (e.isControlDown()&&!e.isAltDown()) {
				print(e.getKeyCode());
				if (e.getKeyCode() == 70) {// ctrl+f
					if(fileTableTab.inSearchModel()){
						if(stopSearch()){
							MessageBox.showErrorMessageDialog(mainFrame, "??????", "??????????????????!");
						}
						fileTableTab.switchToFileModel(fileTreeTablePanel);
					}else{
						fileTableTab.switchToSearchModel(getFileTreeTablePanel(),(searchText,isRegex)  ->{
							// ??????????????????
							try {
								if(isRegex){
									//???????????? searchText
									try {
										Pattern.compile(searchText.toString());
									} catch (Exception e1) {
										MessageBox.showErrorMessageDialog(null, "????????????","????????????regex:"+searchText);
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
								// ???List??????,???????????????????????????????????????
								Collections.synchronizedList(list);
								logInfo("??????");
								DeepSreachThread deepSreachThread = new DeepSreachThread(startDir.getFileObj(), res -> {
									searchRoot.addChild(res);
									list.add(res);
									//logInfo("?????????" + res.getAbsolutePath());
									CountDownLatch countDownLatch=new CountDownLatch(1);
	
									// ??????ui
									SwingUtilities.invokeLater(() -> {
										// swing ???????????????
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
									logInfo("??????????????????:" + searchRoot.getName());
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
								//??????????????????????????????????????????????????????
								if (treePathByNodeObj.notNull_()) {
									tree.expandPath(treePathByNodeObj.get());
								}
	
								deepSreachThread.startSearch();
								// ?????????????????????1???????????????,??????????????????fireTreeNodesInserted????????????????????????
	 
	
								logInfo("???????????????");
							} catch (Exception e1) {
								e1.printStackTrace();
							}						
						});//?????????????????????
					}

 
					// DeepSreachThread
				} else if (e.getKeyCode() == 83) {// ctrl+s ???????????????????????????
					str search = str(MessageBox.showInputDialog(me, "??????????????????????????????", "?????????????????????", null, -1));
					if (search.trim().notEmpty()) {
						me.fileSystemmodel.nowSearchTextBuffer = search.to_s();
						PublicThreadPool.FilePorcessPool_Exec("searchAndSelectedNode", ()  ->{
							searchAndSelectedNode(true);
						});
					}
				} else if (e.getKeyCode() == 82) {// ctrl+r ????????????
					updateTable();
				}else if(e.getKeyCode()==67){//ctrl+c
					List<RESWallper> fileOperationTargetRes = tempFileOperation.getFileOperationTargetRes();
					List<RESWallper> copyTargetRes = fileOperationTargetRes;
					copyTargetRes.clear();
					 copyTargetRes.addAll( getSelectedResWallper());
					 tempFileOperation.setFileOperation(FileOperation.COPY);
					 tempFileOperation.setTargetFileOrDirsSouceDir(getRootDir().get());
					 logInfo("??????????????????:"+copyTargetRes.size()+"?????????.");
					setRessToClipboardString(copyTargetRes);

				} else if (e.getKeyCode() == 86) {// ctrl+v
					List<RESWallper> copyOrMoveTargetRes = tempFileOperation.getFileOperationTargetRes();
					List<FileOperationResult> copyOrMoveFIleResList = new ArrayList<>();
					if (copyOrMoveTargetRes.size() == 0) {
						return;
					}

					PublicThreadPool.FilePorcessPool_Exec("copyOrMoveFIle", () -> {
						// ????????????????????????????????????
						Opt<RESWallper> rootDir = getRootDir();
						if (copyOrMoveTargetRes.size() > 0) {
							// asdasdsad

							List<RESWallper> selectedResWallper = getSelectedResWallper();
							FileObject targetDir = getRootDir().get().getFileObj();// ?????????????????????root??????

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
							 * MessageBox.showInfoMessageDialog(me, "??????", "??????????????????????????????/???,??????????????????");
							 * }
							 */

							if (notNull(targetDir)) {
								try {
									if (targetDir.exists()) {

										if (targetDir.isWriteable()) {
											// ????????????
											CopyOrMoveFIleThread copyOrMoveFIleThread = new CopyOrMoveFIleThread(
													tempFileOperation.getFileOperation(),
													tempFileOperation.getTargetFileOrDirsSouceDir().getFileObj(), targetDir, false,copyOrMoveTargetRes);
											copyOrMoveFIleThread.setResultProcessFun(result -> {
												// print("????????????" + result.getTargetDir().getAbsolutePath());
												copyOrMoveFIleResList.add(result);
											});
											// ????????????
											mainFrame.getBackgroundTaskManager().execTask(copyOrMoveFIleThread);
											// try {
											// copyOrMoveFIleThread.syncWaitFinish();
											// } catch (Exception e1) {
											// }
											// /print(needCopyFIles);

										} else {
											MessageBox
													.showErrorMessageDialog(me, "??????",
															"??????" + targetDir.toString() + "?????????!");
										}

									} else {
										MessageBox.showErrorMessageDialog(me, "??????",
												"??????" + targetDir.toString() + "?????????!");
									}
								} catch (FileSystemException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}

							}

						}
					});

					// ???????????????????????????
					TempFileOperation clone = tempFileOperation.clone();
					/**
					 * clone.getRecoverFun().of(() ->{
					 * //????????????
					 * for(int i=0;i<copyOrMoveFIleResList.size();i++){
					 * FileOperationResult fileOperationResult = copyOrMoveFIleResList.get(i);
					 * File distResources = fileOperationResult.getDistResources();
					 * if(fileOperationResult.isSucess()&&notNull(distResources)){
					 * if(fileOperationResult.getOperation()==FileOperation.MOVE){
					 * //???????????????????????????
					 * //???????????????????????????
					 * move(fileOperationResult.getDistResources(),
					 * fileOperationResult.getSourceFile());
					 * }else if(fileOperationResult.getOperation()==FileOperation.COPY){
					 * //???????????????????????????
					 * //????????????????????????
					 * //logInfo("??????"+distResources.getAbsolutePath());
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
					logInfo("??????????????????:"+copyTargetRes.size()+"?????????.");
				}else if (e.getKeyCode() == 68){//ctrl+d.???????????????
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
								Boolean delete = MessageBox.showConfirmDialog(mainFrame, "????????????", "?????? ?????????????", 1);
								if (delete) {
									Opt<Boolean> sucess=res.delete();
									if(sucess.get()){
										needrefUi.setValue(true);
									}else{
										 MessageBox.showConfirmErrorDialog(mainFrame, "????????????", "????????????"+res.getAbsolutePath()+"   Exception="+sucess.getException());
									}
								}
							}
						});
						if(needrefUi.get(Boolean.class)){
							updateTable();
						}
						// ?????????
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

				}else if(e.getKeyCode()==84){//ctrl+t ?????????????????????????????????
					
					getRootDir().ifNotNull_(root  ->{
						PublicThreadPool.FilePorcessPool_Exec("copyOrMoveFIle", ()  ->{
							getFileTableTab().getTabsPanel().getMainFrame().getMainPanel().getCenterTabsPanel().addFilesTableTab(root, true);
							 });
					});
				}else if(e.getKeyCode()==87){//ctrl+W ??????????????????
					getFileTableTab().getTabsPanel().getMainFrame().getMainPanel().getCenterTabsPanel().removeFilesTableTab(fileTableTab);
					//
					fileTableTab.freeMe();
				}else if(e.getKeyCode()==69){//ctrl+e ????????? ?????????/????????????
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
					//??????????????????

				}else if(e.getKeyCode()==90){//ctrl+z ??????
					if(tempFileOperationStack.size()>0){
						TempFileOperation pop = tempFileOperationStack.pop();
						if(pop.getRecoverFun().notNull_()){
							PublicThreadPool.FilePorcessPool_Exec("recover", ()  ->{
								pop.getRecoverFun().get().run();
							});
						}
					}else{
						 logInfo("??????????????????????????????!");
					}
					
				}
			} else if (e.isAltDown()&&!e.isControlDown()) {			//???alt????????????
				
				print(e.getKeyCode());
				Container parent = fileTableTab.getParent();
				if(e.getKeyCode()==90){//alt+z ???????????????tab
					if (parent instanceof DraggableTabbedPane dtp) {
						int selectedIndex = dtp.getSelectedIndex();
						int tabCount = dtp.getTabCount();
						if (tabCount == 1) {
							return;
						}
						if ((selectedIndex - 1) < 0) {
							// ????????????????????????,??????????????????
							dtp.setSelectedIndex(tabCount-1);
						} else {
							dtp.setSelectedIndex(selectedIndex -1);
						}
					}

				} else if (e.getKeyCode() == 67) {// alt+c ???????????????tab
					if (parent instanceof DraggableTabbedPane dtp) {
						int selectedIndex = dtp.getSelectedIndex();
						int tabCount = dtp.getTabCount();
						if (tabCount == 1) {
							return;
						}
						if ((selectedIndex + 1) > (tabCount - 1)) {
							// ????????????????????????,?????????0index
							dtp.setSelectedIndex(0);
						} else {
							dtp.setSelectedIndex(selectedIndex + 1);
						}
					} 
					
					print("lastSearchResultes");
				}else if (e.getKeyCode() == 10) {// alt+enter ???tab?????????????????????dir
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
							// ??????????????????
							stopSearch();
							MessageBox.showInfoMessageDialog(null, "??????", "??????????????????");
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

				// ??????alt+ctrl+key?????????
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
				//??????????????????
				RESWallper resWallper = getSelectedResWallper().get(0);
				fileTableTab.navigationToDIr(resWallper, true);
				return  ;
			}else if(e.getKeyCode()==40){//?????????
					searchAndSelectedNode(true);
				return ;
			}else if(e.getKeyCode()==38){//?????????
					searchAndSelectedNode(false);
					repaint();
				return ;
			}else if(e.getKeyCode()==39){//?????????
				//????????????
					RESWallper resWallper = getSelectedResWallper().get(0);
					if(resWallper.isDir()){
						Opt<TreePath> treePathByNodeObj = fileSystemmodel.getTreePathByNodeObj(resWallper);
						if (treePathByNodeObj.notNull_()) {
							tree.expandPath(treePathByNodeObj.get());
							// ?????????????????????
							setSelectedResWallpers(newList(resWallper));
						}
					}
					return ;
			}else if(e.getKeyCode()==37){//?????????
				//????????????
				RESWallper resWallper = getSelectedResWallper().get(0);
				if(resWallper.isDir()){
					Opt<TreePath> treePathByNodeObj = fileSystemmodel.getTreePathByNodeObj(resWallper);
					if(treePathByNodeObj.notNull_()){
						tree.collapsePath(treePathByNodeObj.get());
						//?????????????????????
						setSelectedResWallpers(newList(resWallper));
					}
				}

				return ;
			}else if(e.getKeyCode()==8){//?????? ???????????????
				List<RESWallper> selectedResWallper = getSelectedResWallper();
				if(selectedResWallper.size()==0){
					return ;
				}
				RESWallper root= getRootDir().get();
				if(root.isLocalFile()){
					//??????????????????
					for(int i=0;i<selectedResWallper.size();i++){						 
					   selectedResWallper.get(i).putToTrash();
					}
				}else{
					 //????????????
					 Boolean showConfirmDialog = MessageBox.showConfirmDialog(editorComp, "????????????", "???????????????"+selectedResWallper.size()+"?????????,????????????", 1);
					 if(showConfirmDialog){
						 

					 }

				}

				return ;
			}
			if (e.isAltDown() || e.isControlDown()) {
				return;// ?????????????????????
			}
			if (e.getKeyCode() == 8) {// ????????????
				if(me.fileSystemmodel.nowSearchTextBuffer.length()>0){
					me.fileSystemmodel.nowSearchTextBuffer = me.fileSystemmodel.nowSearchTextBuffer.substring(0, me.fileSystemmodel.nowSearchTextBuffer.length() - 1);
					print("??????search:" + me.fileSystemmodel.nowSearchTextBuffer);
					PublicThreadPool.FilePorcessPool_Exec("searchAndSelectedNode", ()  ->{
						searchAndSelectedNode(true);
					});
				}else{
					setSelectedResWallpers(null);
				}
			} else if (e.getKeyCode() == 32) {
				// ??????????????????
				searchAndSelectedNode(null);
			} else {
				
				char c=e.getKeyChar();
				if(Character.isLetterOrDigit(c)){
					me.fileSystemmodel.nowSearchTextBuffer +=c;
					print("??????search:" + me.fileSystemmodel.nowSearchTextBuffer);
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
		// ??????????????????
		tree.setModel(fileSystemmodel);
		fileTableTab.switchToFileModel(fileTreeTablePanel);
		fileTableTab.randerTabHeaderPanel();
	}

	/**
	 * ????????????
	 * @return 
	 */

	public  boolean stopSearch() {
		if (inSearchModel()) {
			if (inSearching()) {
				// ????????????????????????????????????
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
	 * ??????????????????????????????
	 */
	public void searchAndSelectedNode(Boolean down) {
		if (str(me.fileSystemmodel.nowSearchTextBuffer).trim().isEmpty()) {
			return;
		}
		// ????????????????????????????????????
		new Thread(() -> {

			if (down!=null&& me.fileSystemmodel.nowSearchTextBuffer.equals(me.fileSystemmodel.lastSearchText) && me.fileSystemmodel.getLastSearchResultes().size() > 0) {
				// ???????????????,?????????????????????????????????

				// ?????????????????????
				//setSelectedResWallpers(lastSearchResultes);

				SwingUtilities.invokeLater(() -> {
					int lastResSelectedIndex = -1;
					if (me.fileSystemmodel.getLastSearchScrolReswalleper().notNull_()) {
						lastResSelectedIndex = me.fileSystemmodel.getLastSearchResultes().indexOf(me.fileSystemmodel.getLastSearchScrolReswalleper().get());// ?????????????????????????????????
					}
					if (lastResSelectedIndex == -1) {
						if (down) {
							RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(0);
							setSelectedResWallpers(newList(res));
							// ???????????????
							scrollToRes(res);
						}else{
							 //??????????????????
							 RESWallper res = me.fileSystemmodel.getLastSearchResultes().get( me.fileSystemmodel.getLastSearchResultes().size()-1);
							 setSelectedResWallpers(newList(res));
							 // ???????????????
							 scrollToRes(res);
						}

					} else {
						if (lastResSelectedIndex < (me.fileSystemmodel.getLastSearchResultes().size() - 1)) {// ??????????????????
							if (down) {
								// ???????????????
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(lastResSelectedIndex + 1);
								scrollToRes(res);
								setSelectedResWallpers(newList(res));
							} else {
								if (lastResSelectedIndex==0) {
																		 								// ???????????????
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(me.fileSystemmodel.getLastSearchResultes().size()-1);
								scrollToRes(res);
								setSelectedResWallpers(newList(res));
								}else{
									 								// ???????????????
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(lastResSelectedIndex - 1);
								scrollToRes(res);
								setSelectedResWallpers(newList(res));
								}

							}

						} else {
							// ????????????????????????
							if (down) {
								RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(0);
								setSelectedResWallpers(newList(res));
								// ???????????????
								scrollToRes(res);
							}else{
								if (me.fileSystemmodel.getLastSearchResultes().size()>2) {
									RESWallper res = me.fileSystemmodel.getLastSearchResultes().get(me.fileSystemmodel.getLastSearchResultes().size()-2);
									setSelectedResWallpers(newList(res));
									// ???????????????
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
						// ?????????????????????
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
						// ????????????????????????
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
	 * ????????????
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
	 * ????????????????????????
	 * 
	 * @param res
	 */
	public void scrollToRes(RESWallper res) {
		me.fileSystemmodel.getLastSearchScrolReswalleper().of(res);
		Opt<TreePath> treePathByNodeObj = getTreeFileSystemModel().getTreePathByNodeObj(res);
		if (treePathByNodeObj.notNull_()) {
			System.out.println("??????:" + treePathByNodeObj.get());
			Rectangle bounds = tree.getPathBounds(treePathByNodeObj.get());
			// set the height to the visible height to force the node to top
			bounds.height = getVisibleRect().height;
			scrollRectToVisible(bounds);// ?????????????????????
		}

	}

	public JTreeTable(TreeTableModel treeTableModel, FileTableTab fileTbaleTab) {
		super();
		this.fileTableTab = fileTbaleTab;
		getTableHeader().setPreferredSize(new Dimension(100, 30));// ??????tableheader????????????30px
		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer(treeTableModel, this); // tree?????????
		tree.setToggleClickCount(0);//??????????????????
		

		me = this;
		setRowHeight(24);//????????????
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
						//????????????
						if (inSearchModel()) {
							setSelectedResWallpersByRow(r);
							//????????????????????????tab???????????????
							OpenContainingFolder.openNewTab(new ArrayList<>(getSelectedResWallper()), fileTbaleTab.  getTabsPanel().getMainFrame().getMainPanel(),false);
							return ;
						} else {
							// ???????????? ????????????/????????????
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
						// ???????????????
						return;
					}
	
					int rowindex = getSelectedRow();
					// if (rowindex < 0)
					// return;
				});
			}


		});
		//??????????????????
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
			setRowSelectionInterval(r, r);// ?????????????????????
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
		// ??????????????????
		for (int i = 0; i < Columns.ColumnNamesArray.length; i++) {
			Column c = Columns.ColumnNamesArray[i];
			if (c != Columns.Name) {// name?????????,??????TreeTableCellRenderer??????
				getColumnModel().getColumn(i).setCellRenderer(c.getRander());
			}
		}
	}

	/**
	 * ?????????????????????
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
	 * ??????????????????
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
	// return 0;// ??????????????????,??????????????????????????????
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
					// ????????????????????????????????????????????????Ui
					if (lastSelectedIndex > -1) {
						// ???????????????????????????
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
						// System.out.println("??????-" + (System.currentTimeMillis() - currentTimeMillis) +
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
					//System.out.println("??????-" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
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
	 * ??????ui
	 */
	public  void refreshUi() {
		if (!fireTableDataChangeding) {
			List<RESWallper> selectedResWallper = getSelectedResWallper();
			getTreeTableModelAdapter().fireTableDataChanged();
			System.out.println("??????ui");
			fireTableDataChangeding=false;
			if (selectedResWallper.size()>0) {
				setSelectedResWallpers(selectedResWallper);
			}
		}
	}

	/**
	 * ????????????????????????
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
			// renderer.setBackground(Color.LIGHT_GRAY); //???????????????
			TableColumn column = model.getColumn(columnIndex);
			// column.setHeaderRenderer(renderer);
			column.setPreferredWidth((int) (percentages[columnIndex] * factor));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
	}


	    /**
     * ??????????????????????????????,????????????setColumnWidth??????
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
                    // ????????????????????????????????????
                    double a = ((double) column.getWidth()) / ((double) getWidth());
                    d += (a * 1000);
                    // ????????????????????????,??????????????????100???????????????????????????
                    if (i == Columns.ColumnNamesArray .length - 1) {
                        if ((100 - d) > 0) {
                            double b = ((double) (1000 - d)) / 1000;
                            a += b;
                        }
                    }
                    widths += ("," + df.format(a));
                }
                // ?????????
                print("??????" + tableName + "  ????????????????????????  " + widths);
            });
        
    }

 
 
}
