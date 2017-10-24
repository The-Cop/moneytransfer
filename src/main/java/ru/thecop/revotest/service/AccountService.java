package ru.thecop.revotest.service;

import com.google.inject.persist.Transactional;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import javax.inject.Inject;

public class AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Transactional
    public Account find(String number) {
        return accountDao.findByNumber(number);
    }
}
