package github.acodervic.filemanager.model;

import java.nio.file.WatchKey;

public class WatchKeyWallper {
    WatchKey key;
    RESWallper watchedDir;
    /**
     * @param key
     * @param watchedDir
     */
    public WatchKeyWallper(WatchKey key, RESWallper watchedDir) {
        this.key = key;
        this.watchedDir = watchedDir;
    }

    /**
     * @return the key
     */
    public WatchKey getKey() {
        return key;
    }

    /**
     * @return the watchedDir
     */
    public RESWallper getWatchedDir() {
        return watchedDir;
    }
    
}
