package ru.thecop.revotest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.h2.jdbcx.JdbcConnectionPool;
import ru.thecop.revotest.guice.AppModule;
import ru.thecop.revotest.guice.AppServletModule;

import javax.servlet.DispatcherType;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

public class Application {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);

        ServletContextHandler ctx = new ServletContextHandler(server, "/", ServletContextHandler.NO_SESSIONS);

        Injector injector = Guice.createInjector(new AppModule(), new AppServletModule());

        FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
        ctx.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));

//        h2();

        server.start();
        server.join();
    }

    private static void h2() {
        try {
            DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "password");
            Connection conn = ds.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("Select 1");
//            conn.createStatement().executeUpdate("CREATE TABLE data ("
//                    + " key VARCHAR(255) PRIMARY KEY,"
//                    + " value VARCHAR(1023) )");
            // ... populate with data, test etc
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
