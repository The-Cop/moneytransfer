package ru.thecop.revotest.service;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thecop.revotest.exception.TransferException;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class TransferRetryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferRetryService.class);

    // TODO set normal delay
    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(TransferException.class)
//            .withDelay(2, TimeUnit.SECONDS)
            .withDelay(2, TimeUnit.MILLISECONDS)
            .withMaxRetries(3);

    @Inject
    private TransferService transferService;

    public void transferWithRetry(String accountNumberFrom, String accountNumberTo, BigDecimal amount) {
        Failsafe.with(RETRY_POLICY)
                .onFailedAttempt((throwable) -> {
                    LOGGER.error("Failed attempt to transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);
                })
                .run(() -> {
                    LOGGER.info("Trying to transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);
                    transferService.transfer(accountNumberFrom, accountNumberTo, amount);
                });
    }
}
