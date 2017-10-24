package ru.thecop.revotest.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.persist.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thecop.revotest.exception.AccountNotFoundException;
import ru.thecop.revotest.exception.InsufficientFundsException;
import ru.thecop.revotest.exception.TransferException;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);
    private static final long LOCK_WAIT_TIMEOUT_MILLIS = 500;

    private final AccountDao dao;
    private final Cache<String, Lock> locks = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build();

    @Inject
    public TransferService(AccountDao dao) {
        this.dao = dao;
    }

    @Transactional
    public void transfer(String accountNumberFrom, String accountNumberTo, BigDecimal amount) {
        LOGGER.info("Called transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);

        if (StringUtils.isBlank(accountNumberFrom) || StringUtils.isBlank(accountNumberTo)) {
            throw new IllegalArgumentException("Both accounts must be specified");
        }
        if (accountNumberFrom.equals(accountNumberTo)) {
            throw new IllegalArgumentException("Accounts must be different");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount must be specified");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount can not be zero or negative");
        }

        //get or create lock
        String key1;
        String key2;
        if (accountNumberFrom.compareTo(accountNumberTo) < 0) {
            key1 = accountNumberFrom;
            key2 = accountNumberTo;
        } else {
            key1 = accountNumberTo;
            key2 = accountNumberFrom;
        }

        Lock lock1;
        Lock lock2;
        try {
            lock1 = locks.get(key1, ReentrantLock::new);
            lock2 = locks.get(key2, ReentrantLock::new);
        } catch (ExecutionException e) {
            LOGGER.error("ExecutionException trying to acquire locks", e);
            throw new RuntimeException(e);
        }

        try {
            if (lock1.tryLock(LOCK_WAIT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                if (lock2.tryLock(LOCK_WAIT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                    try {
                        doTransfer(accountNumberFrom, accountNumberTo, amount);
                    } finally {
                        lock1.unlock();
                        lock2.unlock();
                    }
                } else {
                    lock1.unlock();
                    throw new TransferException("Timeout while trying to acquire lock on " + key2);
                }
            } else {
                throw new TransferException("Timeout while trying to acquire lock on " + key1);
            }
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException trying to acquire lock with timeout", e);
        }

        LOGGER.info("Successful transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);
    }

    private void doTransfer(String accountNumberFrom, String accountNumberTo, BigDecimal amount) {
        try {
            Account from = dao.getByNaturalId(accountNumberFrom, true);
            checkAndThrowAccountNotFound(from, accountNumberFrom);

            if (from.getAmount().compareTo(amount) < 0) {
                LOGGER.error("Insufficient funds on account {}: {}, transfer amount {}", from.getAmount(), from.getAmount(), amount);
                throw new InsufficientFundsException();
            }

            Account to = dao.getByNaturalId(accountNumberTo, true);
            checkAndThrowAccountNotFound(to, accountNumberTo);

            from.setAmount(from.getAmount().subtract(amount));
            to.setAmount(to.getAmount().add(amount));
        } catch (InsufficientFundsException | AccountNotFoundException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to transfer from " + accountNumberFrom
                    + " to " + accountNumberTo
                    + " amount " + amount, e);
            throw new TransferException(e);
        }
    }

    private void checkAndThrowAccountNotFound(Account acc, String number) {
        if (acc == null) {
            LOGGER.error("Failed to find account {}", number);
            throw new AccountNotFoundException(number);
        }
    }
}
