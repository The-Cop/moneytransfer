package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.hibernate.SomeBean;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Singleton
@Path(StatusWs.PATH)
@Produces(MediaType.TEXT_PLAIN)
public class StatusWs {

    public static final String PATH = "/status";

    private SomeBean someBean;

    @Inject
    public StatusWs(SomeBean someBean) {
        this.someBean = someBean;
    }

    @GET
    public String getStatus(){
        return "server ok: " + someBean.say();
    }
}
