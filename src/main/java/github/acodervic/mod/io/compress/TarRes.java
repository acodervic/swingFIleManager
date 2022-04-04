package github.acodervic.mod.io.compress;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.TimeUtil;

/**
 * 想对于zipResTarRes还能自动存储文件时间和权限
 */
public class TarRes extends CompreserBase<TarArchiveOutputStream> {

    public static void main(String[] args) throws IOException {
        // DirRAM dirRAM = new DirRAM(new
        //// DirRes("/home/w/Downloads/111/apache-tomcat-8"));
        // dirRAM.fixChirdenFileAndDirFullPath();// 将磁盘文件树的绝对路径转换为相对路径
        // TarRes tarRes = new TarRes(dirRAM);
        // tarRes.packToDisk(new FileRes("/home/w/Downloads/111/test2.tar"));
        TarRes tarRes = new TarRes(new FileRes("/home/w/Downloads/111/test2.tar"));
        tarRes.loadCompreserArchiveFIleToRAMTree();
        tarRes.unPackToDir(new DirRes("/home/w/Downloads/112"));
        // TODO TarRes解压targ到磁盘的时候的文件权限修改

    }

    /**
     * 构 构造一个压缩包对象,
     *
     * @param archiveFIle
     */
    public TarRes(FileRes archiveFIle) {
        super(archiveFIle);
        try {
            // archiveFIle可能是已经存在的压缩文件,或者是不存在的压缩文件
            if (archiveFIle.exists()) {
                // 如果已存在,则打开输入流,并等待将磁盘数据读取到内存
                // archiveInputStream = new ZipArchiveInputStrea
            } else {
                // 如果不存在,则打开输出流,等待将压缩数据写入磁盘
                archiveOutputStream = new TarArchiveOutputStream(new FileOutputStream((archiveFIle.getFile())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构 构造一个压缩包对象,
     *
     * @param inputArchiveFIle
     */
    public TarRes() {
    }

    /**
     * 从一个已知的目录节点为根构造一个压缩包对象
     *
     * @param dirRAM
     */
    public TarRes(DirRAM dirRAM) {
        super(dirRAM);
    }

    @Override
    public Exception packToDisk(FileRes file) {
        nullCheck(file);
        try {
            // 构造新的输出流
            TarArchiveOutputStream newTarArchiveOutputStream = new TarArchiveOutputStream(
                    new FileOutputStream(file.getFile()));
            syncFilesToArchiveOutputStream(newTarArchiveOutputStream);
            newTarArchiveOutputStream.finish();// 写入磁盘
        } catch (IOException e) {
            e.printStackTrace();
            return e;
        }
        return null;
    }

    /**
     * 遍历被压缩的文件夹,并将数据追加到指定输出流
     *
     * @param zArchiveOutputStream
     * @return
     */
    public int syncFilesToArchiveOutputStream(TarArchiveOutputStream tarArchiveOutputStream) {
        // 便利被压缩的文件夹
        getRootDir().IteratorAll(dir -> {
        }, file -> {
            try {

                TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file.getFullPath());
                tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
                // 设置长文件模式否则超过100个字符会报错!
                byte[] bytes = file.getByteLIst().toBytes();
                // 必须在putArchiveEntry之前 设置好文件大小!
                tarArchiveEntry.setSize(bytes.length);
                if (file.getDiskFile() != null && file.getDiskFile().exists()) {
                    // 如果本地文件存在则尝试设置权限
                    try {
                        int uid = (int) Files.getAttribute(file.getDiskFile().getFile().toPath(), "unix:uid",
                                LinkOption.NOFOLLOW_LINKS);
                        int groupid = (int) Files.getAttribute(file.getDiskFile().getFile().toPath(), "unix:gid",
                                LinkOption.NOFOLLOW_LINKS);
                        long modTime = Files.getLastModifiedTime(file.getDiskFile().getFile().toPath())
                                .to(TimeUnit.MILLISECONDS);
                        // long ctime = (long) Files.getAttribute(file.getDiskFile().getFile().toPath(),
                        // "unix:ctime",
                        // LinkOption.NOFOLLOW_LINKS);
                         tarArchiveEntry.setModTime(TimeUtil.cTime(modTime));
                        tarArchiveEntry.setUserId(uid);
                        tarArchiveEntry.setGroupId(groupid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ;
                    }

                }
                // 放入输出流
                tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
                // print("文件:" + file.getFullPath() + "加入压缩," + bytes.length + "字节");
                tarArchiveOutputStream.write(bytes);
                tarArchiveOutputStream.closeArchiveEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return 0;
    }

    /**
     * 读取压缩输出流
     *
     * @return
     */
    private TarArchiveOutputStream getTarArchiveOutputStream() {
        return (TarArchiveOutputStream) this.archiveOutputStream;
    }

    /**
     * 读取压缩输入流
     *
     * @return
     */
    private TarArchiveInputStream getTarArchiveIntputStream() {
        return (TarArchiveInputStream) this.archiveInputStream;
    }

    @Override
    public int syncFilesToArchiveOutputStream() {
        // 遍历被压缩的文件夹,并将数据追加到原始输出流
        syncFilesToArchiveOutputStream(getTarArchiveOutputStream());
        return 0;
    }

    @Override
    public int syncFilesToArchiveInputStream() {
        return 0;
    }

    @Override
    public void setOption(Consumer<TarArchiveOutputStream> optionsFun) {
        optionsFun.accept(getTarArchiveOutputStream());

    }

    @Override
    public void loadCompreserArchiveFIleToRAMTree() throws IOException {
        if (inputArchiveFIle != null && inputArchiveFIle.exists() && inputArchiveFIle.canRead()) {
            // 读取压缩文件到内存
            this.archiveInputStream = new TarArchiveInputStream(
                    new BufferedInputStream(new FileInputStream(inputArchiveFIle.getFile()), 4096), "utf-8");
            TarArchiveInputStream tarArchiveIntputStream = getTarArchiveIntputStream();
            TarArchiveEntry entry = null;
            // 循环压缩包内文件
            while ((entry = tarArchiveIntputStream.getNextTarEntry()) != null) {
                unPackArchiveEntryFileAndDirToRootDir(entry);
            }
        }
    }

    @Override
    public void loadCompreserArchiveFIleToRAMTree(String password) throws IOException {

	}


}