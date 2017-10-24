package ru.thecop.revotest.guice;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Scopes;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import ru.thecop.revotest.api.AccountWs;
import ru.thecop.revotest.api.StatusWs;
import ru.thecop.revotest.provider.TransferExceptionMapper;

public class AppServletModule extends ServletModule {

    private void bindServlets() {
        bind(StatusWs.class);
        bind(AccountWs.class);
    }

    @Override
    protected void configureServlets() {
        super.configureServlets();
        bind(GuiceContainer.class);
        bind(TransferExceptionMapper.class);
        bindServlets();

        //persistence
        install(new JpaPersistModule("default"));
        serve("/api/*").with(GuiceContainer.class);

        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
        filter("/api/*").through(PersistFilter.class);
    }
}
