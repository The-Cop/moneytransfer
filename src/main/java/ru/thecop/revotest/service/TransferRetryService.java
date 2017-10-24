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

    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
            .retryOn(TransferException.class)
            .withDelay(300, TimeUnit.MILLISECONDS)
            .withMaxRetries(3);

    private final TransferService transferService;

    @Inject
    public TransferRetryService(TransferService transferService) {
        this.transferService = transferService;
    }

    public void transferWithRetry(String accountNumberFrom, String accountNumberTo, BigDecimal amount) {
        Failsafe.with(RETRY_POLICY)
                .onFailedAttempt((throwable) -> {
                    LOGGER.error("Failed attempt to transfer from {} to {} amount {}, message: {}",
                            accountNumberFrom, accountNumberTo, amount, throwable.getMessage());
                })
                .run(() -> {
                    LOGGER.info("Trying to transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);
                    transferService.transfer(accountNumberFrom, accountNumberTo, amount);
                });
    }
}
