package ru.thecop.revotest.provider;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.ErrorDto;
import ru.thecop.revotest.exception.TransferException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class TransferExceptionMapper implements ExceptionMapper<TransferException> {

    @Override
    public Response toResponse(TransferException exception) {
        ErrorDto dto = new ErrorDto();
        dto.setError("Failed to transfer: possibly another transaction pending, try again later.");
        return Response.status(Response.Status.CONFLICT).entity(dto).build();
    }
}
