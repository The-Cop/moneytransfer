package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.hibernate.SomeBean;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path(Status2Ws.PATH)
@Produces(MediaType.TEXT_PLAIN)
public class Status2Ws {

    public static final String PATH = "/status2";

    private SomeBean someBean;

    @Inject
    public Status2Ws(SomeBean someBean) {
        this.someBean = someBean;
    }

    @GET
    public String getStatus() {
        return "server ok 2: " + someBean.say();
    }
}
