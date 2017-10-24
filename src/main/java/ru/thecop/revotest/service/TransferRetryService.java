package ru.thecop.revotest.service;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class TransferRetryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferRetryService.class);

    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(Exception.class)
//            .withDelay(2, TimeUnit.SECONDS)
            .withDelay(2, TimeUnit.MILLISECONDS)
            .withMaxRetries(3);

    @Inject
    private TransferService transferService;

    public void transferWithRetry(String accountNumberFrom, String accountNumberTo, double value) {
        Failsafe.with(RETRY_POLICY)
                .onFailedAttempt((throwable) -> {
                    LOGGER.error("Failed attempt to transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, value);
                })
                .run(() -> {
                    LOGGER.info("Trying to transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, value);
                    transferService.transfer(accountNumberFrom, accountNumberTo, value);
                });
    }
}