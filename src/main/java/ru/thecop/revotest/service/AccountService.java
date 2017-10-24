package ru.thecop.revotest.service;

import com.google.inject.persist.Transactional;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    @Inject
    private AccountDao accountDao;

    @Transactional
    public Account createAcccount() {
        System.out.println("" + getClass().getName());
//        return inTransactionExecutor.executeInTransaction(() -> {
        Account a = new Account();
        a.setAmount(BigDecimal.valueOf(666.13));
        a.setNumber(System.currentTimeMillis()+"");

        accountDao.create(a);
        System.out.println("Persisted: " + a);
        return a;
//        });
    }

    @Transactional
    public List<Account> all() {
        System.out.println("" + getClass().getName());
        return accountDao.findAll();
    }
}
