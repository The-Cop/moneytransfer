package ru.thecop.revotest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thecop.revotest.guice.AppServletModule;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static Server server;

    public static void main(String[] args) {
        try {
            start();
            server.join();
        } catch (Exception e) {
            LOGGER.error("Error running server", e);
        } finally {
            stop();
        }
    }

    public static void start() throws Exception {
        server = new Server(8081);

        ServletContextHandler ctx = new ServletContextHandler(server, "/", ServletContextHandler.NO_SESSIONS);

        Injector injector = Guice.createInjector(new AppServletModule());

        FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
        ctx.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));

        server.start();
    }

    public static void stop() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                LOGGER.error("Error stopping server", e);
            }
        }
    }
}

