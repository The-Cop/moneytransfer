package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;
import ru.thecop.revotest.repository.InTransactionExecutor;
import ru.thecop.revotest.util.Constants;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.math.BigDecimal;
import java.util.List;

@Singleton
@Path(AccountWs.PATH)
@Produces(Constants.MEDIATYPE_JSON_UTF8)
public class AccountWs {

    public static final String PATH = "/accounts";

    @Inject
    private AccountDao accountDao;

    @Inject
    private InTransactionExecutor inTransactionExecutor;

    @GET
    @Path("/new")
    @Transactional
    public Account createAcccount() {
        return inTransactionExecutor.executeInTransaction(() -> {
            Account a = new Account();
            a.setAmount(BigDecimal.valueOf(666.13));
            a.setNumber(System.currentTimeMillis()+"");

            accountDao.create(a);
            System.out.println("Persisted: " + a);
            return a;
        });
    }

    @GET
    @Path("/")
    public List<Account> all() {
        return inTransactionExecutor.executeInTransaction(() -> accountDao.findAll());
    }

    @GET
    @Path("/{accNum}")
    public Account all(@PathParam("accNum") String accountNumber) {
        return inTransactionExecutor.executeInTransaction(() -> accountDao.findByNumber(accountNumber));
    }
}
