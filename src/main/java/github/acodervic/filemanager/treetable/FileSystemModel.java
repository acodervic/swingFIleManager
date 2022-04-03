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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.exception.NotFoundResWallperExecption;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.model.SearchRootRESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.Opt;

/**
 * FileSystemModel is a TreeTableModel representing a hierarchical file
 * system. Nodes in the FileSystemModel are FileNodes which, when they
 * are directory nodes, cache their children to avoid repeatedly querying
 * the real file system.
 * 
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */

public class FileSystemModel extends AbstractTreeTableModel
        implements TreeTableModel,GuiUtil {
            String nowSearchTextBuffer = "";
            String lastSearchText = "";// 最后一次搜索的字符串
            Opt<RESWallper> lastSearchScrolReswalleper = new Opt<>();// 最后一个查询和设置选中的值节点
            List<RESWallper> lastSearchResultes = new ArrayList<>();// 最后一次搜索的结果
    JTree tree;// 作用的tree
    //Boolean showHiden=false;

    // The the returned file length for directories.
    public static final Integer ZERO = new Integer(0);

    /**
     * @return the lastSearchResultes
     */
    public List<RESWallper> getLastSearchResultes() {
        return lastSearchResultes;
    }

    /**
     * @param lastSearchResultes the lastSearchResultes to set
     */
    public void setLastSearchResultes(List<RESWallper> lastSearchResultes) {
        this.lastSearchResultes = lastSearchResultes;
    }

    /**
     * @param lastSearchScrolReswalleper the lastSearchScrolReswalleper to set
     */
    public void setLastSearchScrolReswalleper(Opt<RESWallper> lastSearchScrolReswalleper) {
        this.lastSearchScrolReswalleper = lastSearchScrolReswalleper;
    }
    /**
     * @return the lastSearchScrolReswalleper
     */
    public Opt<RESWallper> getLastSearchScrolReswalleper() {
        return lastSearchScrolReswalleper;
    }
    public FileSystemModel(FileObject dirRes) throws FileSystemException {
        super(new RESWallper(dirRes, null, null));
    }

    public FileSystemModel(RESWallper dirRes) {
        super(dirRes);
    }

    /**
     * @param tree the tree to set
     */
    public void setTree(JTree tree) {
        this.tree = tree;
    }


    /**
     * @return the tree
     */
    public JTree getTree() {
        return tree;
    }
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof RESWallper resDir && child instanceof RESWallper res) {
            int indexOf = resDir.getChildResList().indexOf(res);
            if (indexOf==-1) {
                Opt<RESWallper> resWallperByAbsolutePath = resDir.getChilds().getRESWallperByAbsolutePath(res.getAbsolutePath());
                if (resWallperByAbsolutePath.notNull_()) {
                    RESWallper resWallper = resWallperByAbsolutePath.get();
                    return resDir.getChildResList().indexOf( resWallper);
                    
                }
                
            }else{
                 return indexOf;
            }
        }
        return -1;
    }
 

    public Opt<RESWallper> getRESWallperByIndex(Object parent, int index) {
        Opt<RESWallper> ret=new Opt<>();
        if (parent instanceof RESWallper resDir ) {
            ret.of( (RESWallper)resDir.getChildResList().get(index));
        }
        return ret;
    }

 
    //
    // Some convenience methods.
    //

    protected FileObject getFile(Object node) {
        RESWallper res = ((RESWallper) node);
        return res.getFileObj();
    }

    protected List<RESWallper> getChildren(Object node) {
        RESWallper res = ((RESWallper) node);
        return res.getChildResList();
    }

    //
    // The TreeModel interface
    //

    public int getChildCount(Object node) {
        List<RESWallper> children = getChildren(node);
        return (children == null) ? 0 : children.size();
    }

    public Object getChild(Object node, int i) {
        return getChildren(node).get(i);
    }

    // The superclass's implementation would work, but this is more efficient.
    public boolean isLeaf(Object node) {
        if (node instanceof SearchRootRESWallper) {
            return false;
        }
        try {
            if (node instanceof RESWallper res) {
                return res.isFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 通过File来找到当前root节点下的 RESWallper
     * @param file
     * @return
     */
    public Opt<RESWallper> searchNodeByFile(RESWallper file,Boolean searchSubDir) {
        return getNodeByFile((RESWallper)root, file,searchSubDir);
    }
    /**
     * 通过路径
     * @param file /hom/w/
     * @return /home/w/ RESWallper
     */
    public Opt<RESWallper> getNodeByFile (RESWallper dir,RESWallper file,Boolean searchSubDir) {
        Opt<RESWallper> ret=new Opt<>();
        if (dir.isFile()) {
            return ret;
        }
        if (dir instanceof SearchRootRESWallper) {
            return ret;
        }
        FileObject dirFile = dir.getFileObj();
        String absolutePath = file.getAbsolutePath();
        //判断目录是否就是file
        if (dirFile.toString().equals(absolutePath)) {
            ret.of(dir);
            return  ret;
        }

        //从dir下的目录找
        if (absolutePath.startsWith(dirFile.toString() )) {//确保他们起始路径一支
            Opt<RESWallper> resWallperByAbsolutePath = dir.getRESWallperByAbsolutePath(absolutePath);
            if (resWallperByAbsolutePath.notNull_()) {
                return resWallperByAbsolutePath;
            }
            //查询文件夹
            if( searchSubDir){
                for (int i = 0; i < dir.getChildDirList().size(); i++) {
                    RESWallper res = (RESWallper)dir.getChildDirList().get(i);
                    if (res.toString().equals(absolutePath)) {
                        ret.of(res);
                        break;
                    }else{
                        Opt<RESWallper> nodeByFile = getNodeByFile(res, file,true);
                        if (nodeByFile.notNull_()) {
                            ret.of(nodeByFile.get());
                            break;
                        }
                    }
                }
                
             }
        }

        return ret;
    }

    //
    // The TreeTableNode interface.
    //

    public int getColumnCount() {
        return Columns.ColumnNamesArray.length;
    }

    @Override
    public String getColumnName(int column) {
        return Columns.getColumnByIndex(column).getName();
    }

    @Override
    public Class getColumnClass(int column) {
        return Columns.getColumnByIndex(column).getClassType();
    }

    @Override
    public Object getValueAt(Object node, int column) {
        RESWallper res=(RESWallper)node;
        return Columns.getColumnByIndex(column).getValueByRESWallper(res);
    }


    public void fireTreeStructureChanged(List<TreePath> paths) {
        TreePath fristPath = paths.get(0);
        Object parent = fristPath.getParentPath().getLastPathComponent();
        Object[] childs=new Object[paths.size()];
        int[] childIndecies = new int[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            TreePath treePath = paths.get(i);
            Object child = treePath.getLastPathComponent();
            childs[i]=child;
            childIndecies[0] = this.getIndexOfChild(parent, child);
        }
        TreeModelEvent event = new TreeModelEvent(this,
        fristPath.getParentPath(),
                childIndecies,
                childs);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            ((TreeModelListener) listeners[i + 1]).treeStructureChanged(event);
        }
    }
 

    public synchronized void fireTreeNodesChanged(TreePath parentDir,List<RESWallper> childsRes) {
        if (childsRes.size()==0) {
            return ;
        }
        //System.out.println("fireTreeNodesInserted");
        Object parent = parentDir.getLastPathComponent();
        Object[] childs=new Object[childsRes.size()];
        int[] childIndecies = new int[childsRes.size()];
        for (int i = 0; i < childsRes.size(); i++) {
            RESWallper resWallper = childsRes.get(i);
            childs[i]=resWallper;
            childIndecies[i] = this.getIndexOfChild(parent, resWallper);
            getRESWallperByIndex(parent, childIndecies[i]).ifNotNull_(res  ->{
                res.reloadTreeNameColumnPanel();
            });
        }
        TreeModelEvent event = new TreeModelEvent(this,
        parentDir,
        childIndecies,
                childs);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            ((TreeModelListener) listeners[i + 1]).treeNodesChanged(event);
        }
    }
     



    public synchronized void fireTreeNodesInserted(TreePath parentDir,List<RESWallper> childsRes) {
        if (childsRes.size() == 0) {
            return;
        }
        try {
            // System.out.println("fireTreeNodesInserted");
            Object parent = parentDir.getLastPathComponent();
            Object[] childs = new Object[childsRes.size()];
            int[] childIndecies = new int[childsRes.size()];
            for (int i = 0; i < childsRes.size(); i++) {
                RESWallper resWallper = childsRes.get(i);
                childs[i] = resWallper;
                childIndecies[i] = this.getIndexOfChild(parent, resWallper);
            }
            TreeModelEvent event = new TreeModelEvent(this,
                    parentDir,
                    childIndecies,
                    childs);
            Object[] listeners = listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                try {
                    ((TreeModelListener) listeners[i + 1]).treeNodesInserted(event);
                } catch (Exception e) {
                    logInfo("fireTreeNodesInserted error=" + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     

    public synchronized void fireTreeNodesRemoved(List<TreePath> paths) {
        if (paths.size()==0) {
            return ;
        }
        TreePath fristPath = paths.get(0);
        TreePath parentPath = fristPath.getParentPath();
        Object parent =null;
        if (isNull(parentPath)) {
            parent=((RESWallper)fristPath.getLastPathComponent()).getParentResWallper();
        }else{
            parent = parentPath.getLastPathComponent();
        }
        Object[] childs=new Object[paths.size()];
        int[] childIndecies = new int[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            TreePath treePath = paths.get(i);
            Object child = treePath.getLastPathComponent();
            childs[i]=child;
            int indexOfChild = this.getIndexOfChild(parent, child);
            if(indexOfChild==-1){
                throw new NotFoundResWallperExecption("没有找到子节点!");
            }
            childIndecies[i] =indexOfChild ;
        }
        TreeModelEvent event = new TreeModelEvent(this,
        parentPath,
                childIndecies,
                childs);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            try {
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(event);
            } catch (Exception e) {
                logInfo("fireTreeNodesRemoved error="+e.getMessage());
            }
        }
        for (int i = 0; i < paths.size(); i++) {
            TreePath treePath = paths.get(i);
            //确认子节点从父节点资源中被删除
            if (treePath.getParentPath().getLastPathComponent() instanceof RESWallper parentDir && treePath.getLastPathComponent() instanceof RESWallper delete) {
                try {
                    parentDir.removeFIleObjFromThis(delete);
                } catch (Exception e) {
                    logInfo("fireTreeNodesRemoved 后 删除  removeFIleObjFromThis失败!"+delete.getAbsolutePath());
                }
            }
        }

        //System.out.println("123213");
    }


    @Override
    public Opt<TreePath> getTreePathByNodeObj(Object object) {
        Opt<TreePath> path=new Opt<>();
        if (tree==null) {
            return path;
        }
        int rowCount = tree.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            TreePath pathForRow = getTree().getPathForRow(i);
            RESWallper lastPathComponent = (RESWallper)pathForRow.getLastPathComponent();
            RESWallper target= (RESWallper)object;
            if (sameRef(lastPathComponent, target)) {
                path.of(pathForRow);
                return path;
            }
            if (lastPathComponent.getAbsolutePath().equals(target.getAbsolutePath()) ) {
                path.of(pathForRow);
                return path;
            }            
        }
        return  path;
    }

    @Override
    public List<TreePath> getTreePatshByNodsObjs(List<Object> objects) {
        List<TreePath>  paths=new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            Object object = objects.get(i);
            Opt<TreePath> treePathByNodeObj = getTreePathByNodeObj(object);
            if (treePathByNodeObj.notNull_()) {
                paths.add(treePathByNodeObj.get());
            }
        }
        return paths;
    }

 
}