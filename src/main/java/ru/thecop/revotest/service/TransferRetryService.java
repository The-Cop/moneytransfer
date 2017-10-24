package ru.thecop.revotest.service;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class TransferRetryService {

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
                    System.err.println("Failed attempt to transfer");
                })
                .run(() -> {
                    System.out.println("TransferService: trying transfer " + accountNumberFrom + " -> " + accountNumberTo);
                    transferService.transfer2(accountNumberFrom, accountNumberTo, value);
                });
    }
}
