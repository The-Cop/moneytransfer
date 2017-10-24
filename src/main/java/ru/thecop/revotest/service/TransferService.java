package ru.thecop.revotest.service;

import com.google.inject.persist.Transactional;
import ru.thecop.revotest.exception.TransferException;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import javax.inject.Inject;
import java.math.BigDecimal;

public class TransferService {

    @Inject
    private AccountDao dao;

    @Transactional
    public void transfer2(String accountNumberFrom, String accountNumberTo, double value) {
        System.out.println("Transferring from "
                + accountNumberFrom
                + " to "
                + accountNumberTo
                + ", session = ");

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
            from.setAmount(from.getAmount().subtract(BigDecimal.valueOf(value)));
            to.setAmount(to.getAmount().add(BigDecimal.valueOf(value)));
            dao.update(from);
            dao.update(to);
        } catch (Exception e) {
            System.out.println("Failed to transfer: " + e.getMessage());
            e.printStackTrace();
            throw new TransferException(e);
        }
    }
}
