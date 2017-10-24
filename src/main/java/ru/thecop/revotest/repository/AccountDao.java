package ru.thecop.revotest.repository;

import org.hibernate.criterion.Restrictions;
import ru.thecop.revotest.model.Account;

public class AccountDao extends AbstractHibernateGenericDao<Account, Long> {
    public Account findByNumber(String number) {
        return (Account) createCriteria()
                .add(Restrictions.eq("number", number.trim()))
                .uniqueResult();
    }
}
