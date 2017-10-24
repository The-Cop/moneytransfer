package ru.thecop.revotest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class Application {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8081);

        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ctx.setContextPath("/");

        server.setHandler(ctx);

        ServletHolder holder = ctx.addServlet(ServletContainer.class, "/api/*");

        holder.setInitOrder(1);
        holder.setInitParameter("jersey.config.server.provider.packages","ru.thecop.revotest.api");


        server.start();
        server.join();
    }
}
