package ru.thecop.revotest.service;

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

public class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    //TODO test nulls in account numbers
    //TODO test null in amount
    //TODO test negative amount
    //TODO test zero amount
    //TODO test equal accounts numbers

    @Inject
    private AccountDao dao;

    @Transactional
    public void transfer(String accountNumberFrom, String accountNumberTo, BigDecimal amount) {
        LOGGER.info("Transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);

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

        Account from = findByNumberOrThrow(accountNumberFrom);
        Account to = findByNumberOrThrow(accountNumberTo);

        if (from.getAmount().compareTo(amount) < 0) {
            LOGGER.error("Insufficient funds on account {}: {}, transfer amount {}", accountNumberFrom, from.getAmount(), amount);
            throw new InsufficientFundsException();
        }

        try {
            //clear session cache to get actual data from db
            dao.getSession().clear();

            from = dao.getById(from.getId(), true);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            to = dao.getById(to.getId(), true);
            // TODO remove sleep

            LOGGER.debug("Before From: {}", from.toString());
            LOGGER.debug("Before To: {}", to.toString());

            from.setAmount(from.getAmount().subtract(amount));
            to.setAmount(to.getAmount().add(amount));

            LOGGER.debug("After From: {}", from.toString());
            LOGGER.debug("After To: {}", to.toString());
        } catch (Exception e) {
            // TODO log stacktrace
            LOGGER.error("Failed to transfer from " + accountNumberFrom
                    + " to " + accountNumberTo
                    + " amount " + amount + ": " + e.getMessage());
            throw new TransferException(e);
        }
        LOGGER.info("Successful transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);
    }

    private Account findByNumberOrThrow(String number) {
        Account acc = dao.findByNumber(number);
        if (acc == null) {
            LOGGER.error("Failed to find account {}", number);
            throw new AccountNotFoundException(number);
        }
        return acc;
    }
}
