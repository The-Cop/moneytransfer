package ru.thecop.revotest.guice;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Scopes;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.slf4j.bridge.SLF4JBridgeHandler;
import ru.thecop.revotest.api.AccountWs;
import ru.thecop.revotest.api.StatusWs;
import ru.thecop.revotest.provider.AccountNotFoundExceptionMapper;
import ru.thecop.revotest.provider.IllegalArgumentExceptionMapper;
import ru.thecop.revotest.provider.InsufficientFundsExceptionMapper;
import ru.thecop.revotest.provider.TransferExceptionMapper;

import java.util.HashMap;
import java.util.Map;

public class AppServletModule extends ServletModule {

    private void bindServlets() {
        bind(StatusWs.class);
        bind(AccountWs.class);
    }

    @Override
    protected void configureServlets() {
        super.configureServlets();
        //Requst logging JUL -> Slf4j bridge
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        bind(GuiceContainer.class);
        bind(TransferExceptionMapper.class);
        bind(AccountNotFoundExceptionMapper.class);
        bind(InsufficientFundsExceptionMapper.class);
        bind(IllegalArgumentExceptionMapper.class);
        bindServlets();

        //persistence
        install(new JpaPersistModule("default"));
        final Map<String, String> params = new HashMap<String, String>();
//        params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, LoggingFilter.class.getName());
//        params.put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, LoggingFilter.class.getName());
//        bind(LoggingFilter.class);
        serve("/api/*").with(GuiceContainer.class, params);


        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);
        filter("/api/*").through(PersistFilter.class);
//        filter("/api/*").through(LoggingFilter.class);
    }
}
