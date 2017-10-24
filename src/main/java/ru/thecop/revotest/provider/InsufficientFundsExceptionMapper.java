package ru.thecop.revotest.provider;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.ErrorDto;
import ru.thecop.revotest.exception.InsufficientFundsException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class InsufficientFundsExceptionMapper implements ExceptionMapper<InsufficientFundsException> {

    @Override
    public Response toResponse(InsufficientFundsException exception) {
        ErrorDto dto = new ErrorDto();
        dto.setError("Can not transfer: insufficient funds");
        return Response.status(Response.Status.BAD_REQUEST).entity(dto).build();
    }
}
