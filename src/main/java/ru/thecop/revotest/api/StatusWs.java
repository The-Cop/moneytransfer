package ru.thecop.revotest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status")
@Produces(MediaType.TEXT_PLAIN)
public class StatusWs {

    @GET
    public String getStatus(){
        return "server ok";
    }
}
