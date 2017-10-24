package ru.thecop.revotest.api;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.TransferDto;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.service.AccountService;
import ru.thecop.revotest.service.TransferRetryService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static ru.thecop.revotest.api.AccountWs.MEDIATYPE_JSON_UTF8;

@Singleton
@Path(AccountWs.PATH)
@Produces(MEDIATYPE_JSON_UTF8)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountWs {

    public static final String MEDIATYPE_JSON_UTF8 = MediaType.APPLICATION_JSON + ";charset=utf-8";

    public static final String PATH = "/accounts";

    private AccountService accountService;
    private TransferRetryService transferRetryService;

    @Inject
    public AccountWs(AccountService accountService,
                     TransferRetryService transferRetryService) {
        this.accountService = accountService;
        this.transferRetryService = transferRetryService;
    }

    @POST
    @Path("/transfer")
    public List<Account> transfer(TransferDto transferDto) {
        transferRetryService.transferWithRetry(transferDto.getFrom(), transferDto.getTo(), transferDto.getAmount());
        return Lists.newArrayList(accountService.find(transferDto.getFrom()), accountService.find(transferDto.getTo()));
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
