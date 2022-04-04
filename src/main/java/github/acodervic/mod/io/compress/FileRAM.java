package github.acodervic.mod.io.compress;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import github.acodervic.mod.data.ByteLIst;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.str;

/**
 * 代表压缩文件中的临时文件
 */
public class FileRAM {
    String name;
    String fullPath;// 全路径
    DirRAM parentDir;// 上层dir对象
    FileRes diskFile;// 和本地文件的磁盘资源映射
    // 字节数组
    ByteLIst byteLIst = new ByteLIst();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the byteLIst
     */
    public ByteLIst getByteLIst() {
        if (byteLIst.size() == 0 && this.diskFile != null && this.diskFile.exists()) {
            loadDiskBytesToByteList();// 从磁盘加载文件
        }
        return byteLIst;
    }

    /**
     * @param byteLIst the byteLIst to set
     */
    public void setByteLIst(ByteLIst byteLIst) {
        this.byteLIst = byteLIst;
    }

    /**
     * @param name
     * @param byteLIst
     */
    public FileRAM(String name, ByteLIst byteLIst) {
        this.name = name;
        this.byteLIst = byteLIst;
    }

    /**
     * @param name
     * @param byteLIst
     */
    public FileRAM(String name, FileRes file) {
        nullCheck(file);
        this.name = name;
        this.diskFile = file;
    }

    /**
     * @param name
     * @param byteLIst
     */
    public FileRAM(String name, byte[] bytes) {
        this.name = name;
        this.byteLIst = new ByteLIst(bytes);
    }

    /**
     * @return the fullPath
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * @param fullPath the fullPath to set
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    /**
     * @return the parentDir
     */
    public DirRAM getParentDir() {
        return parentDir;
    }

    /**
     * @param parentDir the parentDir to set
     */
    public void setParentDir(DirRAM parentDir) {
        this.parentDir = parentDir;
        if (parentDir.getFullPath().equals("/")) {
            this.fullPath = "/" + this.name;

        } else {
            this.fullPath = parentDir.getFullPath() + "/" + this.name;

        }
    }

    /**
     * 如果文件是和磁盘进行映射则可以从磁盘加载文件到bytelist中
     * 在进行getBytelis的时候会进行一次自动加载,如果本地文件发生了更改请重新调用此方法
     */
    public void loadDiskBytesToByteList() {
        if (this.diskFile != null) {
            this.byteLIst.clearAllBytes();
            this.byteLIst.put(diskFile.readBytes());
        }
    }

    /**
     * 从内存中加载数据并填充到文件中
     *
     * @param archiveInputStream
     */
    public void loadMemberByteToByteList(ArchiveInputStream archiveInputStream) {
        try {
            this.byteLIst.clearAllBytes();
            this.byteLIst.put(IOUtils.toByteArray(archiveInputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("从流中读取" + this.byteLIst.size() + "字节数据到,目录树" +
        // this.fullPath);
    }

    /**
     * 将内存虚拟路径转换为磁盘路径
     *
     * @param dirRes
     * @return
     */
    public String conventDiskRootPath(DirRes dirRes) {
        dirRes.getAbsolutePath();
        String newpath = this.getFullPath();
        if (this.getFullPath().startsWith("/")) {
            // 删除 虚拟路径的/
            newpath = this.getFullPath().replaceFirst("/", "");
        }
        // 合并 新目录的 路径
        if (!newpath.equals("")) {
            newpath = dirRes.getAbsolutePath() + newpath;

        } else {
            newpath = dirRes.getAbsolutePath();
        }
        return newpath;
    }

    /**
     * 读取虚拟内存文件和磁盘影映射的文件对象,如果没有映射则返回null
     * 
     * @return the diskFile
     */
    public FileRes getDiskFile() {
        return diskFile;
    }

    /**
     * @param diskFile the diskFile to set
     */
    public void setDiskFile(FileRes diskFile) {
        this.diskFile = diskFile;
    }

    /**
     * 修复文件路径
     *
     * @return
     */
    public String resetFullPath() {
        str fullpath = new str("");
        DirRAM nowDir = this.getParentDir();
        if (nowDir != null) {
            if (!nowDir.getName().equals("/")) {
                fullpath.insertStrToHead(nowDir.getName() + "/");
            }
            while ((nowDir = nowDir.getParentDir()) != null) {
                if (nowDir != null) {
                    if (!nowDir.getName().equals("/")) {
                        fullpath.insertStrToHead(nowDir.getName() + "/");
                    }
                } else {
                    fullpath.insertStrToHead(nowDir.getName());
                }
            }
        } else {
            fullpath.insertStrToHead(nowDir.getName());
        }

        fullpath.insertStrToEnd(this.getName());
        if (!fullpath.startWith("/")) {
            // 修复路径
            fullpath.insertStrToHead("/");
        }
        this.setFullPath(fullpath.to_s());
        return getFullPath();
    }
}
