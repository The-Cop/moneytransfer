package ru.thecop.revotest.guice;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import ru.thecop.revotest.api.Status2Ws;
import ru.thecop.revotest.api.StatusWs;

public class AppServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        super.configureServlets();
        bind(GuiceContainer.class);
        bind(StatusWs.class);
        bind(Status2Ws.class);
        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
        serve("/api/*").with(GuiceContainer.class);
    }
}
