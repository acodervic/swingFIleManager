package github.acodervic.mod.io.compress;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.str;
import github.acodervic.mod.data.list.ListUtil;

/**
 * 代表内存中的文件系统的临时文件树目录,在构建之后通过如果构建的文件目录对象和磁盘对应则可以通过loadDiskFilesAndDirs()函数进行映射加载所有层级子目录和文件,
 * 如果要求转换磁盘树目录结构则通过fixChirdenFileAndDirFullPath()函数来更改全路径
 */
public class DirRAM {
    String name;// dirName
    String fullPath;// 全路径
    DirRAM parentDir;// 上层dir对象
    DirRes diskDir;// 磁盘目录,如果目录对象和磁盘目录进行关联的话
    /**
     * 当前文件夹下的文件
     */
    List<FileRAM> files = new LinkedList<FileRAM>();
    /**
     * 当前文件夹下的文件夹
     */
    List<DirRAM> dirs = new LinkedList<DirRAM>();

    /**
     * @return the files
     */
    public List<FileRAM> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<FileRAM> files) {
        this.files = files;
    }

    /**
     * @param files the files to set
     * @return
     */
    public DirRAM addFile(FileRAM file) {
        nullCheck(file);
        file.setParentDir(this);
        this.files.add(file);
        return this;
    }

    /**
     * 转为磁盘目录
     * 
     * @return
     */
    public DirRes toDiskDirRes() {
        return new DirRes(getFullPath());
    }

    /**
     * 添加一个dir对象到当前目录,返回被添加的dir对象
     *
     * @param files the files to set
     * @return
     */
    public DirRAM addDir(DirRAM dir) {
        nullCheck(dir);
        dir.setParentDir(this);
        this.dirs.add(dir);
        return dir;
    }

    /**
     * 添加一个dir对象到当前目录,返回被添加的dir对象
     *
     * @param files the files to set
     * @return
     */
    public DirRAM addDir(String newDirName) {
        nullCheck(newDirName);
        DirRAM dir = new DirRAM(newDirName);
        dir.setParentDir(this);
        this.dirs.add(dir);
        return dir;
    }

    /**
     * @return the dirs
     */
    public List<DirRAM> getDirs() {
        return dirs;
    }

    /**
     * @param dirs the dirs to set
     */
    public void setDirs(List<DirRAM> dirs) {
        nullCheck(dirs);
        this.dirs = dirs;
    }

    /**
     * ; 一般顶层dir的名称为/
     *
     * @param name
     */
    public DirRAM(String name) {
        this.name = name;
        this.fullPath = name;// 顶层dir以name作为路径
    }

    /**
     * ; 一般顶层dir的名称为/
     *
     * @param name
     */
    public DirRAM(DirRes dirRes) {
        nullCheck(dirRes);
        this.name = dirRes.getDirBaseName();
        this.fullPath = dirRes.getAbsolutePath();
        this.diskDir = dirRes;
        loadDiskFilesAndDirs();// 进行映射
    }

    /**
     * 如果当前dir和磁盘进行了映射,则从磁盘加载所有的子/目录和子/文件,之包含当前层 默认在构造dir的时候初始调用
     */
    public void loadDiskFilesAndDirs() {
        if (this.diskDir != null && this.diskDir.exists() && this.diskDir.canRead()) {
            // 加载所有目录和文件
            this.diskDir.getFiles().forEach((file) -> {
                FileRAM file2 = new FileRAM(file.getFileName(), file);
                addFile(file2);
            });
            // 目录
            this.diskDir.getDirs().forEach(dir -> {
                DirRAM dir2 = new DirRAM(dir);
                addDir(dir2);
            });
        }
    }

    /**
     * @param name
     */
    public DirRAM(String name, DirRAM parentDir) {
        this.name = name;
        if (parentDir != null) {
            this.parentDir = parentDir;
            this.fullPath = this.getParentDir().getFullPath() + "/" + name;
        } else {
            this.fullPath = name;// 顶层dir以name作为路径
        }

    }

    /**
     * 通过子目录名读取目录对象,没有此目录则返回null
     * 
     * @param dirName
     * @return
     */
    public DirRAM getDirByName(String dirName) {
        nullCheck(dirName);
        for (int i = 0; i < this.dirs.size(); i++) {
            if (dirs.get(i).name.equals(dirName)) {
                return dirs.get(i);
            }
        }
        return null;
    }

    /**
     * 在此dir上创建子目录,返回创建之后的dir
     *
     * @param name
     * @return
     */
    public DirRAM newDir(String name) {
        nullCheck(name);
        DirRAM newDir = new DirRAM(name);
        newDir.setParentDir(this);
        this.dirs.add(newDir);
        return newDir;
    }

    /**
     * 在此dir上创建和磁盘映射的子目录,返回创建之后的dir
     *
     * @param name
     * @return
     */
    public DirRAM addDir(DirRes diskDir) {
        nullCheck(diskDir);
return addDir(diskDir, true);
    }

    /**
     * 在此dir上创建和磁盘映射的子目录,返回创建之后的dir
     * 
     * @param diskDir           磁盘目录
     * @param createFullPathDir 是否使用原始的绝对目录路径防止到目录中
     * @return
     */
    public DirRAM addDir(DirRes diskDir, boolean createFullPathDir) {
        nullCheck(diskDir);
        DirRAM newDir = new DirRAM(diskDir);
        if (!createFullPathDir) {
            // 如果选择不使用完全路径将文件加入到压缩包,则修复所有来自磁盘的文件夹内存对象为虚拟路径
            newDir.setName(diskDir.getDirBaseName());
        }
        // 防止重复
        for (int i = 0; i < this.getDirs().size(); i++) {
            DirRAM dir = this.getDirs().get(i);
            if (dir.getName().equals(newDir.getName())) {
                return dir;
            }
        }
        newDir.setParentDir(this);
        if (!createFullPathDir) {
            // 修复磁盘路径和内存路径的转换
            newDir.fixChirdenFileAndDirFullPath();
        }
        this.dirs.add(newDir);
        return newDir;
    }

    /**
     * 检查修复当前目录下的所有 fullname和dir/filename,
     */
    public void fixChirdenFileAndDirFullPath() {
        this.IteratorAll(dir -> {
            // 检查目录是否和上级目录的fullpath合法,一般是在将从磁盘导入的绝对路径转换为相对路径
            DirRes diskDirRes = dir.getDiskDir();
            if (diskDirRes != null && dir.getParentDir() != null) {
                // 修复fullpath 将磁盘的绝对路径转换为内存压缩文件树的相对路径
                dir.resetFullPath();// 主要是i修复resetFullPath
            }
            // 目录中不能存在 /
            if (dir.getName().indexOf("/") != -1) {
            }
        }, file -> {
            // 检查目录是否和上级目录的fullpath合法,一般是在将从磁盘导入的绝对路径转换为相对路径
            FileRes diskFileRes = file.getDiskFile();
            if (diskFileRes != null) {
                // 修复fullpath 将磁盘的绝对路径转换为内存压缩文件树的相对路径
                file.resetFullPath();
            }
        });
    }

    /**
     * 迭代当前文件夹下所有资源
     */
    public void IteratorAll(Consumer<DirRAM> onDir_opt, Consumer<FileRAM> onFile_opt) {
        // 先处理自身
        onDir_opt.accept(this);
        // 先处理子文件
        if (this.files.size() != 0 && onFile_opt != null) {
            for (int i = 0; i < files.size(); i++) {
                FileRAM file = files.get(i);
                onFile_opt.accept(file);
            }
        }
        // 再处理子目录
        if (this.dirs.size() != 0 && onDir_opt != null) {
            for (int i = 0; i < dirs.size(); i++) {
                DirRAM dir = dirs.get(i);
                onDir_opt.accept(dir);
                // 迭代子目录
                IteratorDir(dir, onDir_opt, onFile_opt);
            }
        }
    }

    /**
     * 自动修正fullpath
     *
     * @return
     */
    public String resetFullPath() {
        str fullpath = new str("");
        DirRAM nowDir = this;
        while ((nowDir = nowDir.getParentDir()) != null) {
            if (nowDir != null) {
                if (!nowDir.getName().equals("/")) {
                    fullpath.insertStrToHead(nowDir.getName() + "/");
                }
            } else {
                fullpath.insertStrToHead(nowDir.getName());
            }
        }
        fullpath.insertStrToEnd(this.getName());
        if (!fullpath.startWith("/")) {
            // 修复路径
            fullpath.insertStrToHead("/");
        }
        this.setFullPath(fullpath.to_s());
        return getFullPath();
    }

    /**
     * 迭代子目录
     *
     * @param startDir
     * @param onDir_opt
     * @param onFile_opt
     */
    private void IteratorDir(DirRAM startDir, Consumer<DirRAM> onDir_opt, Consumer<FileRAM> onFile_opt) {
        // 先处理子文件
        if (startDir.getFiles().size() != 0 && onFile_opt != null) {
            for (int i = 0; i < startDir.getFiles().size(); i++) {
                FileRAM file = startDir.getFiles().get(i);
                onFile_opt.accept(file);
            }
        }
        // 再处理子目录
        if (startDir.getDirs().size() != 0 && onDir_opt != null) {
            for (int i = 0; i < startDir.getDirs().size(); i++) {
                DirRAM dir = startDir.getDirs().get(i);
                onDir_opt.accept(dir);
                // 迭代
                IteratorDir(dir, onDir_opt, onFile_opt);
            }
        }
    }

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
     * 在当前目录上通过path来创建新的文件夹,如当前文件夹为/home,path=/home/1/2/3则会创建出/home/1,/home/2,/home/1/2/3
     * 然后返回/home/1/2/3,可以通过getParentDir来读取上层目录
     * 
     * @param path
     */
    public DirRAM createDirByPath(String path) {
        String fullPath2 = getFullPath();
        String replaceFirst = path.replaceFirst(fullPath2, "");
        if (replaceFirst.length() == path.length()) {
            // 说明,当前文件夹并没有处于被创建的路径内,返回null
            return null;
        }
        if (!replaceFirst.startsWith("/")) {
            replaceFirst = "/" + replaceFirst;
        }
        // 创建子文件夹
        DirRes dirRes = new DirRes(replaceFirst);
        // 读取父文件夹
        List<DirRes> dirs = dirRes.getParentDirs();
        // 反转list
        ListUtil.reverseSortList(dirs);
        // 添加当前子文件夹
        dirs.add(new DirRes(path));

        DirRAM nowDir = this;
        // 循环创建子文件夹
        for (int i = 0; i < dirs.size(); i++) {
            DirRes dirRes2 = dirs.get(i);
            if (dirRes2.getDirBaseName().equals("/")) {
                // 不处理根目录
                continue;
            }
            // 如果文件夹已经存在则不重复创建
            if (nowDir.getName().equals(dirRes2.getDirBaseName())) {

            } else {
                // 如果存在目录则直接读取子目录,避免重复创建
                DirRAM dirByName = nowDir.getDirByName(dirRes2.getDirBaseName());
                if (dirByName == null) {
                    nowDir = nowDir.addDir(dirRes2.getDirBaseName());
                } else {
                    nowDir = dirByName;
                }
            }
        }
        // 返回最终dir
        return nowDir;
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

    public static DirRAM getDirByPath(DirRAM startDir, String dirPath) {
        nullCheck(startDir, dirPath);
        List<DirRes> parentDirs = new DirRes(dirPath).getParentDirs();
        ListUtil.reverseSortList(parentDirs);
        parentDirs.add(new DirRes(dirPath));
        DirRAM nowDir = startDir;
        if (parentDirs.get(0).getDirBaseName().equals("/")) {
            // 处理绝对路径
            for (int i = 1; i < parentDirs.size(); i++) {
                DirRes dirRes = parentDirs.get(i);
                DirRAM dirByName = nowDir.getDirByName(dirRes.getDirBaseName());
                if (dirByName == null) {
                    // 说明dir在路径中不存在
                    return null;
                } else {
                    // 继续往下寻找
                    nowDir = dirByName;
                }
            }
        }
        return nowDir;
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
     * 读取磁盘映射,未和磁盘映射的时候此值为null
     * 
     * @return the diskDir
     */
    public DirRes getDiskDir() {
        return diskDir;
    }

    /**
     * 释放当前目录所有文件中的数据
     */
    public void clear() {
        IteratorAll(dir -> {

        }, file -> {
            file.getByteLIst().clearAllBytes();
        });

    }

}
