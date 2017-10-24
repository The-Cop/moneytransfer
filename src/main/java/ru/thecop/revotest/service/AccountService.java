package ru.thecop.revotest.service;

import com.google.inject.persist.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    @Inject
    private AccountDao accountDao;

    @Transactional
    public Account createAcccount() {
        Account a = new Account();
        a.setAmount(BigDecimal.valueOf(666.13));
        a.setNumber(System.currentTimeMillis() + "");

        accountDao.create(a);
        LOGGER.debug("Created account: {}", a);
        return a;
    }

    @Transactional
    public List<Account> all() {
        return accountDao.findAll();
    }
}
