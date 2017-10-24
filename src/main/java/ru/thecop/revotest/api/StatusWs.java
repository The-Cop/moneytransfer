package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.StatusDto;
import ru.thecop.revotest.hibernate.SomeBean;
import ru.thecop.revotest.util.Constants;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Singleton
@Path(StatusWs.PATH)
@Produces(Constants.MEDIATYPE_JSON_UTF8)
public class StatusWs {

    public static final String PATH = "/status";

    private SomeBean someBean;

    @Inject
    public StatusWs(SomeBean someBean) {
        this.someBean = someBean;
    }

    @GET
    public StatusDto getStatus() {
        StatusDto status = new StatusDto();
        status.setStatus("С сервером все хорошо. server ok: " + someBean.say());
        return status;
    }
}
