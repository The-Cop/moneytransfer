package ru.thecop.revotest.service;

import ru.thecop.revotest.api.dto.StatusDto;
import ru.thecop.revotest.api.dto.TransferDto;
import ru.thecop.revotest.model.Account;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestClient {
    private static final String STATUS_URI = "http://localhost:8081/api/status";
    private static final String TRANSFER_URI = "http://localhost:8081/api/accounts/transfer";
    private static final String INFO_URI = "http://localhost:8081/api/accounts/info";

    private Client client = ClientBuilder.newClient();

    public StatusDto getStatus() {
        return client
                .target(STATUS_URI)
                .request(MediaType.APPLICATION_JSON)
                .get(StatusDto.class);
    }

    public Account getAccount(String number) {
        return client
                .target(INFO_URI)
                .path(number)
                .request(MediaType.APPLICATION_JSON)
                .get(Account.class);
    }

    public Response transfer(TransferDto transferDto) {
        return client
                .target(TRANSFER_URI)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(transferDto, MediaType.APPLICATION_JSON));
    }
}
