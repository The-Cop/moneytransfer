package ru.thecop.revotest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import ru.thecop.revotest.guice.AppModule;
import ru.thecop.revotest.guice.AppServletModule;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class Application {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);

        ServletContextHandler ctx = new ServletContextHandler(server, "/", ServletContextHandler.NO_SESSIONS);

        Injector injector = Guice.createInjector(new AppModule(), new AppServletModule());

        FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
        ctx.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));

        server.start();
        server.join();
    }
}

