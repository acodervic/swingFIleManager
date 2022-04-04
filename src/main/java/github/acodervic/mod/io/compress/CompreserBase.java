package github.acodervic.mod.io.compress;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;

import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;

/**
 * Compresser 压缩者,提供基本的压缩功能功能
 * 
 * @param <T>
 */
public abstract class CompreserBase<T> {
    DirRAM rootDir = new DirRAM("/");
    FileRes inputArchiveFIle = null;// 原始输入压缩包文件对象,可以为null,当从内存中构造压缩包的时候此对象为null
    // apche的压缩包顶级输出流,用于写入压缩数据到磁盘
    ArchiveOutputStream archiveOutputStream = null; // 压缩输出流
    ArchiveInputStream archiveInputStream = null;// 解压输出流

    /**
     * 读取压缩包所有的文件夹和文件
     *
     * @return
     */
    public DirRAM getRootDir() {
        return this.rootDir;
    };

    /**
     * 将压缩包内所有的文件夹和文件压缩保存到磁盘
     *
     * @param dirRes
     * @return
     * @throws IOException
     */
    public synchronized Exception packToDisk() {
        try {
            if (this.inputArchiveFIle == null) {
                Exception exp = new FileNotFoundException(
                        "没有指定archiveFIle的路径!,如果没有从构造函数指定打包输出文件路径,请使用packToDisk(FileRes fileRes);函数输出!");
                ;
                exp.printStackTrace();
                ;
                return exp;
            } else {
                syncFilesToArchiveOutputStream();
                archiveOutputStream.finish();
            }

        } catch (Exception e) {
            return e;
        }
        return null;
    }

    /**
     * 将压缩包内所有的文件夹和文件压缩保存到磁盘
     *
     * @param fileRes
     * @return
     * @throws IOException
     */
    public abstract Exception packToDisk(FileRes fileRes);

    /**
     * 将当前压缩包解压到磁盘
     *
     * @param targetDir
     * @return
     */
    public boolean unPackToDir(DirRes targetDir) {
        return unPackToDir(targetDir, null, null);
    };

    /**
     * 将当前压缩包解压到磁盘
     *
     * @param targetDir
     * @return
     */
    public boolean unPackToDirWithFileFilter(DirRes targetDir, Function<FileRAM, Boolean> fileFilter) {
        nullCheck(targetDir, fileFilter);
        return unPackToDir(targetDir, fileFilter, null);
    };

    /**
     * 将当前压缩包解压到磁盘
     *
     * @param targetDir
     * @return
     */
    public boolean unPackToDirWithDirFilter(DirRes targetDir, Function<DirRAM, Boolean> dirFilter) {
        nullCheck(targetDir, dirFilter);
        return unPackToDir(targetDir, null, dirFilter);
    };

    /**
     * 将当前压缩包解压到磁盘
     *
     * @param targetDir
     * @return
     */
    public boolean unPackToDir(DirRes targetDir, Function<FileRAM, Boolean> fileFilter_opt,
            Function<DirRAM, Boolean> dirFilter_opt) {
        nullCheck(targetDir);

        if (targetDir.exists() && !targetDir.canWrite()) {
            return false;
        }
        Opt<DirRes> parentDir = targetDir.getParentDir();
        if (!targetDir.exists() &&parentDir.notNull_() && !parentDir.get().canWrite()) {
            return false;
        }
        if (!targetDir.exists()) {
            targetDir.makeDir();
        }
        getRootDir().IteratorAll(dir -> {
            if ((dirFilter_opt == null) || (dirFilter_opt != null && dirFilter_opt.apply(dir))) {
                // 合并目标目录
                // 将目录转换为磁盘路径
                String conventDiskRootPath = dir.conventDiskRootPath(targetDir);
                DirRes dirRes = new DirRes(conventDiskRootPath);
                if (!dirRes.exists()) {
                    // 写入新目录
                    dirRes.makeDir();
                }
            }

        }, file -> {
            // 进行文件夹过滤
            if ((dirFilter_opt == null) || (file.getParentDir() != null && dirFilter_opt != null
                    && dirFilter_opt.apply(file.getParentDir()))) {

                // 文件夹过滤通过后再进行文件过滤
                if ((fileFilter_opt == null) || (fileFilter_opt != null && fileFilter_opt.apply(file))) {
                    // 合并目标文件
                    // 将文件转换为磁盘路径
                    String conventDiskRootPath = file.conventDiskRootPath(targetDir);
                    FileRes fileRes = new FileRes(conventDiskRootPath);
                    if (!fileRes.exists()) {
                        // 写入新目录
                        fileRes.writeByteArray(file.getByteLIst().toBytes());
                    }
                }
            }

        });
        return true;
    };

    /**
     * 读取压缩包名称
     *
     * @return the archiveFileName
     */
    public String getArchiveFileName() {
        return this.inputArchiveFIle.getFileName();
    }

    /**
     * 设置压缩包名称
     *
     * @param archiveFileName the archiveFileName to set
     */
    public synchronized void setArchiveFileName(String archiveFileName) {
        archiveFileName = archiveFileName;
    }

    /**
     * 此函数用户在压缩之前将rootdir中的树文件资源同步到压缩流中
     *
     * @return 返回同步的文件数量
     */
    public abstract int syncFilesToArchiveOutputStream();

    /**
     * 此函数用户在解压缩之前将rootdir中的树文件资源同步到解压输出流中
     *
     * @return 返回同步的文件数量
     */
    public abstract int syncFilesToArchiveInputStream();

    /**
     * @param archiveFIle
     */
    public CompreserBase(FileRes archiveFIle) {
        this.inputArchiveFIle = archiveFIle;
    }

    /**
     * 构造一个内存中的压缩包对象
     *
     * @param inputArchiveFIle
     */
    public CompreserBase() {
    }

    /**
     * 从一个已知的内存目录构造一个压缩包对象
     *
     * @param inputArchiveFIle
     */
    public CompreserBase(DirRAM dirRAM) {
        nullCheck(dirRAM);
        if (dirRAM.getName().equals("/")) {
            this.rootDir = dirRAM;
        } else {
            this.rootDir.addDir(dirRAM);
        }
    }

    /**
     * 用于在执行压缩之间对archiveOutputStream进行设置操作等
     *
     * @param <T>
     * @param optionsFun
     */

    public abstract void setOption(Consumer<T> optionsFun);

    /**
     * 尝试从磁盘归档中加载压缩树到压缩根
     */
    public abstract void loadCompreserArchiveFIleToRAMTree() throws IOException;

    /**
     * 尝试从磁盘归档中加载压缩树到压缩根
     */
    public abstract void loadCompreserArchiveFIleToRAMTree(String password) throws IOException;

    /**
     * 读取归档大小
     * 
     * @return
     */
    public long getTargetSize() {
        if (this.inputArchiveFIle != null && this.inputArchiveFIle.exists()) {
            return this.inputArchiveFIle.size();
        }
        return 0;
    }

    public void getDataSize() {
        // TODO 读取压缩前的数据大小
    }

    /**
     * 释放资源,包含所绑定的root目录树的所有文件数据
     */
    public void close() {
        if (this.archiveInputStream != null) {
            try {
                this.archiveInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.archiveOutputStream != null) {
            try {
                this.archiveOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 释放所有内存文件树数据
        getRootDir().clear();
    }

    /**
     * 将压缩文件中的单个目录导出到内存压缩树根目录
     *
     * @param archiveEntry
     */
    protected void unPackArchiveEntryFileAndDirToRootDir(ArchiveEntry archiveEntry) {
        String filePath = archiveEntry.getName(); // 这里同样可以获取包括名字在内的许多文件信息
        // 在压缩包的读取是没有/开头的,所以自己补全/开头
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        // 判断是否为文件夹
        if (archiveEntry.isDirectory()) {
            // 创建目录
            getRootDir().createDirByPath(filePath);
        } else {
            // 创建文件
            // 读取文件目录
            FileRes fileRes = new FileRes(filePath);
            DirRes fileDir = fileRes.getDir();
            DirRAM dirByPath = DirRAM.getDirByPath(getRootDir(), fileDir.getAbsolutePath());
            // 如果被写入的虚拟目录不存在则创建目录
            if (dirByPath == null) {
                getRootDir().createDirByPath(fileDir.getAbsolutePath());
            }
            // 重新获取目录
            dirByPath = DirRAM.getDirByPath(getRootDir(), fileDir.getAbsolutePath());
            if (dirByPath != null) {
                FileRAM file = new FileRAM(fileRes.getFileName(), fileRes);
                // 添加到文件树
                dirByPath.addFile(file);
                // 将内存流中的数据填充到问题
                file.loadMemberByteToByteList(this.archiveInputStream);
            } else {
                System.out.println(filePath + "在虚拟内存文件树中不存在!");
            }

        }
    }
}