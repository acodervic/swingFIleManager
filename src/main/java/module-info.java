module filemanager{
    requires mod;
    requires  java.base;
    requires java.desktop;
    requires com.formdev.flatlaf;
    requires miglayout.swing;
    requires hutool.core;
    requires  hutool.json;
    requires jediterm.pty;
    requires pty4j;
    requires zip4j;
    requires nanohttpd;
    requires thumbnailator;
    requires commons.vfs2;
    requires jsch;
    requires java.prefs;
    requires swingbits;
    requires org.xerial.sqlitejdbc;
    requires javacv;
    requires opencv;
    
    
    exports github.acodervic.filemanager.theme;
    opens github.acodervic.filemanager.theme;
    opens github.acodervic.filemanager.model;
}
