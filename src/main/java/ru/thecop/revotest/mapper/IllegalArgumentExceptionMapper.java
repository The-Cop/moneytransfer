package ru.thecop.revotest.mapper;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.ErrorDto;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        ErrorDto dto = new ErrorDto();
        dto.setError("Illegal argument: " + exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(dto).build();
    }
}
