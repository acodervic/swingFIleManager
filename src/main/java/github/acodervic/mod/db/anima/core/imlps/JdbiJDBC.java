package github.acodervic.mod.db.anima.core.imlps;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
//import org.jdbi.v3.postgres.PostgresPlugin;

import github.acodervic.mod.db.anima.core.Connection;
import github.acodervic.mod.db.anima.core.JDBC;

public class JdbiJDBC extends JDBC {
    Jdbi jdbi;

    @Override
    public Boolean open(String url, String user, String pass) throws Exception {
        jdbi=Jdbi.create(url, user, pass);
        //.installPlugin(new PostgresPlugin());

        return true;
    }

    @Override
    public Connection newConnection() {
        return new JdbiConnection(jdbi.open());
    }

    @Override
    public Boolean open(DataSource dataSource) throws Exception {
        jdbi=Jdbi.create(dataSource);
        return true;
    }

    /**
     * @return the jdbi
     */
    public Jdbi getJdbi() {
        return jdbi;
    }

}
