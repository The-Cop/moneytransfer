package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.util.Constants;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.math.BigDecimal;

@Singleton
@Path(AccountWs.PATH)
@Produces(Constants.MEDIATYPE_JSON_UTF8)
public class AccountWs {

    public static final String PATH = "/accounts";

    @Inject
    private EntityManager em;

    @GET
    @Path("/new")
    public Account createAcccount() {
        Account a = new Account();
        a.setAmount(BigDecimal.valueOf(666.13));
        a.setNumber("01234");

        em.persist(a);
        System.out.println("Persisted: " + a);
        return a;
    }
}
