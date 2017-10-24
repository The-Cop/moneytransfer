package ru.thecop.revotest.service;

import com.google.inject.persist.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import javax.inject.Inject;
import java.util.List;

public class AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    private AccountDao accountDao;

    @Inject
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Transactional
    public Account find(String number) {
        return accountDao.findByNumber(number);
    }

    @Transactional
    public List<Account> all() {
        return accountDao.findAll();
    }
}
