package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.TransferDto;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.service.AccountService;
import ru.thecop.revotest.service.TransferRetryService;
import ru.thecop.revotest.util.Constants;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Singleton
@Path(AccountWs.PATH)
@Produces(Constants.MEDIATYPE_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountWs {

    public static final String PATH = "/accounts";

    private AccountService accountService;
    private TransferRetryService transferRetryService;

    @Inject
    public AccountWs(AccountService accountService,
                     TransferRetryService transferRetryService) {
        this.accountService = accountService;
        this.transferRetryService = transferRetryService;
    }

    // TODO remove unnecessary methods
    // TODO do not return all accounts

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

    @GET
    @Path("/info/{number}")
    public Account info(@PathParam("number") String number) {
        return accountService.find(number);
    }
}
