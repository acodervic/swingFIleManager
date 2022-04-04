package github.acodervic.mod.db;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.sql.Connection;
import java.sql.DriverManager;

import github.acodervic.mod.data.FileRes;

/**
 * DBUtil数据库常用工具
 */
public class DBUtil {
    /**
     * 创建一个数据库文件,如果存在则不会创建,
     *
     * @param file
     * @return
     */
    public static boolean createSqlLiteDBFile(FileRes file) {
        nullCheck(file);
        try {
            if (file.exists()) {
                return false;
            }
            // 连接SQLite的JDBC
            Class.forName("org.sqlite.JDBC");
            // 建立一个数据库名zieckey.db的连接，如果不存在就在当前目录下创建之
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            conn.createStatement().executeUpdate("create table test(name varchar(20), age int);");// 创建一个表，两列

            conn.createStatement();
            conn.close(); // 结束数据库的连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.exists();
    }

}