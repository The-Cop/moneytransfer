package ru.thecop.revotest.provider;

import com.google.inject.Singleton;
import ru.thecop.revotest.api.dto.ErrorDto;
import ru.thecop.revotest.exception.AccountNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class AccountNotFoundExceptionMapper implements ExceptionMapper<AccountNotFoundException> {

    @Override
    public Response toResponse(AccountNotFoundException exception) {
        ErrorDto dto = new ErrorDto();
        dto.setError("Account not found: " + exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(dto).build();
    }
}
