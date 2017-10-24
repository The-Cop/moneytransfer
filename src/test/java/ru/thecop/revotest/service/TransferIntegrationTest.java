package ru.thecop.revotest.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.thecop.revotest.Application;
import ru.thecop.revotest.api.dto.StatusDto;
import ru.thecop.revotest.api.dto.TransferDto;
import ru.thecop.revotest.model.Account;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class TransferIntegrationTest {
    RestClient client = new RestClient();

    @Before
    public void startup() throws Exception {
        Application.start();
    }

    @After
    public void tearDown() throws Exception {
        Application.stop();
    }

    @Test
    public void testStatus() {
        StatusDto statusDto = client.getStatus();
        assertNotNull(statusDto);
    }

    @Test
    public void testTransfer() {
        TransferDto transferDto = new TransferDto();
        transferDto.setFrom("A");
        transferDto.setTo("B");
        transferDto.setAmount(BigDecimal.ONE);
        Response response = client.transfer(transferDto);
        Account[] accounts = response.readEntity(Account[].class);
        Arrays.stream(accounts).forEach(System.out::println);

        Account a = findByNumber("A", accounts);
        assertEquals(0, a.getAmount().compareTo(new BigDecimal(999)));

        Account b = findByNumber("B", accounts);
        assertEquals(0, b.getAmount().compareTo(new BigDecimal(2001)));
    }

    private Account findByNumber(String number, Account[] array) {
        return Arrays.stream(array).filter(acc -> acc.getNumber().equals(number)).findAny().get();
    }

    //TODO test parallel transfers
    //TODO test error response
}
