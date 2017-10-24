package ru.thecop.revotest.service;

import com.google.inject.persist.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thecop.revotest.exception.TransferException;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import javax.inject.Inject;
import java.math.BigDecimal;

public class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    @Inject
    private AccountDao dao;

    @Transactional
    public void transfer(String accountNumberFrom, String accountNumberTo, double amount) {
        LOGGER.info("Transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);

        Account from = dao.findByNumber(accountNumberFrom);

        try {
            Account to = dao.findByNumber(accountNumberTo);
            from = dao.getById(from.getId(), true);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            to = dao.getById(to.getId(), true);
            //TODO check if accounts not null
            // TODO check if account has enough money
            from.setAmount(from.getAmount().subtract(BigDecimal.valueOf(amount)));
            to.setAmount(to.getAmount().add(BigDecimal.valueOf(amount)));
            dao.update(from);
            dao.update(to);
        } catch (Exception e) {
            LOGGER.error("Failed to transfer from " + accountNumberFrom
                    + " to " + accountNumberTo
                    + " amount " + amount, e);
            throw new TransferException(e);
        }
        LOGGER.info("Successful transfer from {} to {} amount {}", accountNumberFrom, accountNumberTo, amount);
    }
}
