package github.acodervic.filemanager;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

/**
 * VFS
 */
public class VFSTest {

    public static void main(String[] args) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
FileObject jarFile = fsManager.resolveFile("/");
for (FileObject fileObject : jarFile.getChildren()) {
    fileObject.isFolder();

    System.out.println(jarFile.getChildren());
    
}
    }
}