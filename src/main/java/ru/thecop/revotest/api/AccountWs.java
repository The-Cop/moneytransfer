package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.service.AccountService;
import ru.thecop.revotest.service.TransferRetryService;
import ru.thecop.revotest.service.TransferService;
import ru.thecop.revotest.util.Constants;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

@Singleton
@Path(AccountWs.PATH)
@Produces(Constants.MEDIATYPE_JSON_UTF8)
public class AccountWs {

    public static final String PATH = "/accounts";

    @Inject
    private AccountService accountService;

    @Inject
    private TransferService transferService;
    @Inject
    private TransferRetryService transferRetryService;

    @GET
    @Path("/new")
    public Account createAcccount() {
        return accountService.createAcccount();
    }

    @GET
    @Path("/ab")
    public List<Account> ab() {
        transferRetryService.transferWithRetry("A", "B", 1);
        return accountService.all();
    }

    @GET
    @Path("/ba")
    public List<Account> ba() {
        transferRetryService.transferWithRetry("B", "A", 1);
        return accountService.all();
    }

    @GET
    @Path("/{from}/{to}")
    public List<Account> ab(@PathParam("from") String from, @PathParam("to") String to) {
        transferRetryService.transferWithRetry(from, to, 1);
        return accountService.all();
    }

    //
    @GET
    @Path("/")
    public List<Account> all() {
        return accountService.all();
    }
//
//    @GET
//    @Path("/{accNum}")
//    public Account all(@PathParam("accNum") String accountNumber) {
//        return inTransactionExecutor.executeInTransaction(() -> accountDao.findByNumber(accountNumber));
//    }
}
