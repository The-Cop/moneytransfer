package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.TransferDto;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.service.AccountService;
import ru.thecop.revotest.service.TransferRetryService;
import ru.thecop.revotest.service.TransferService;
import ru.thecop.revotest.util.Constants;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

@Singleton
@Path(AccountWs.PATH)
@Produces(Constants.MEDIATYPE_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON)
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
        transferRetryService.transferWithRetry("A", "B", BigDecimal.ONE);
        return accountService.all();
    }

    @GET
    @Path("/ba")
    public List<Account> ba() {
        transferRetryService.transferWithRetry("B", "A", BigDecimal.ONE);
        return accountService.all();
    }

    @GET
    @Path("/{from}/{to}")
    public List<Account> ab(@PathParam("from") String from, @PathParam("to") String to) {
        transferRetryService.transferWithRetry(from, to, BigDecimal.ONE);
        return accountService.all();
    }

    @POST
    @Path("/transfer")
    public List<Account> transfer(TransferDto transferDto) {
        transferRetryService.transferWithRetry(transferDto.getFrom(), transferDto.getTo(), transferDto.getAmount());
        return accountService.all();
    }

    @GET
    @Path("/")
    public List<Account> all() {
        return accountService.all();
    }
}
