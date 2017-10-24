package ru.thecop.revotest.guice;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Scopes;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.slf4j.bridge.SLF4JBridgeHandler;
import ru.thecop.revotest.api.AccountWs;
import ru.thecop.revotest.mapper.AccountNotFoundExceptionMapper;
import ru.thecop.revotest.mapper.IllegalArgumentExceptionMapper;
import ru.thecop.revotest.mapper.InsufficientFundsExceptionMapper;
import ru.thecop.revotest.mapper.TransferExceptionMapper;

public class AppServletModule extends ServletModule {

    private void bindServlets() {
        bind(AccountWs.class);
    }

    @SuppressWarnings("PointlessBinding")//exception mappers need to be bound explicitly
    @Override
    protected void configureServlets() {
        super.configureServlets();
        //Request logging JUL -> Slf4j bridge
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        bind(GuiceContainer.class);
        bind(TransferExceptionMapper.class);
        bind(AccountNotFoundExceptionMapper.class);
        bind(InsufficientFundsExceptionMapper.class);
        bind(IllegalArgumentExceptionMapper.class);
        bindServlets();

        install(new JpaPersistModule("default"));

        serve("/api/*").with(GuiceContainer.class);

        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
        filter("/api/*").through(PersistFilter.class);
    }
}
