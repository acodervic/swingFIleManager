package github.acodervic.mod.io;

import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;

import static java.nio.file.StandardWatchEventKinds.*;
import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

import github.acodervic.mod.data.str;

/**
 * 用来监控文件或目录
 */
public class FileSystemWatcher {
    private String fileAndDirFilterNameRegex_opt;// 用于过滤的表达式,匹配的目标则会被监控,为null则是所有目标
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;// 出现新的目标是否监听,默认true
    private boolean log = true;

    /**
     * 启用追逐模式,出现新的目录之后继续监听
     */
    public void enableTrace() {
        this.trace = true;
    }

    /**
     * 关闭追踪模式(不监听新创建的目录)
     */
    public void disableTrace() {
        this.trace = false;
    }

    /**
     * 开启日志
     */
    public void enableLog() {
        this.log = true;
    }

    /**
     * 关闭日志
     */
    public void disableLog() {
        this.log = false;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        if (fileAndDirFilterNameRegex_opt == null
                || new str(dir.toFile().getName()).hasRegex(fileAndDirFilterNameRegex_opt)) {
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            if (trace) {
                Path prev = keys.get(key);
                if (prev == null) {
                    System.out.format("注册: %s\n", dir);
                } else {
                    if (!dir.equals(prev)) {
                        System.out.format("更新: %s -> %s\n", prev, dir);
                    }
                }
            }
            keys.put(key, dir);
        }
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 创建监控
     */
    FileSystemWatcher(Path dir, boolean recursive, String fileAndDirFilterNameRegex_opt) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;
        this.fileAndDirFilterNameRegex_opt = fileAndDirFilterNameRegex_opt;
        if (recursive) {
            System.out.format("扫描目录 %s ...\n", dir);
            registerAll(dir);
            System.out.println("目录监听完成.");
        } else {
            register(dir);
        }
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents(BiConsumer<Kind, File> onChange) {
        nullCheck(onChange);
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("监控key没有同步!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);
                File changeFile = child.toFile();
                if (this.log) {
                    System.out.println(changeFile.getAbsolutePath() + "  " + kind.name());
                }
                try {
                    onChange.accept(kind, changeFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

}