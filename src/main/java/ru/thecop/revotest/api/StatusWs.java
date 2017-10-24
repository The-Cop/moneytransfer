package ru.thecop.revotest.api;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.StatusDto;
import ru.thecop.revotest.util.Constants;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

//TODO remove class
@Singleton
@Path(StatusWs.PATH)
@Produces(Constants.MEDIATYPE_JSON_UTF8)
public class StatusWs {

    public static final String PATH = "/status";

    @GET
    public StatusDto getStatus() {
        StatusDto status = new StatusDto();
        status.setStatus("С сервером все хорошо. server ok");
        return status;
    }
}
