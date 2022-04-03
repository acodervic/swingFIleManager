package github.acodervic.filemanager.treetable;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.mod.data.DirRes;

public class SeachFileSystemModel extends FileSystemModel {

    public SeachFileSystemModel(FileObject dirRes) throws FileSystemException {
        super(dirRes);
    }
     
    public SeachFileSystemModel(RESWallper dirRes) {
        super(dirRes);
    }

}
