package ru.thecop.revotest.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.thecop.revotest.Application;
import ru.thecop.revotest.api.dto.TransferDto;
import ru.thecop.revotest.model.Account;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static junit.framework.TestCase.assertEquals;

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
    public void testTransfer() {
        BigDecimal initialAmountA = client.getAccount("A").getAmount();
        BigDecimal initialAmountB = client.getAccount("B").getAmount();

        TransferDto transferDto = new TransferDto();
        transferDto.setFrom("A");
        transferDto.setTo("B");
        transferDto.setAmount(BigDecimal.ONE);

        Response response = client.transfer(transferDto);
        Account[] accounts = response.readEntity(Account[].class);

        Account a = findByNumber("A", accounts);
        assertEquals(0, a.getAmount().compareTo(initialAmountA.subtract(BigDecimal.ONE)));

        Account b = findByNumber("B", accounts);
        assertEquals(0, b.getAmount().compareTo(initialAmountB.add(BigDecimal.ONE)));
    }

    @Test
    public void testTransferConcurrentTriple() {
        BigDecimal initialAmountA = client.getAccount("A").getAmount();
        BigDecimal initialAmountB = client.getAccount("B").getAmount();
        BigDecimal initialAmountC = client.getAccount("C").getAmount();
        BigDecimal initialSum = initialAmountA.add(initialAmountB).add(initialAmountC);

        int ab = 13;
        int ba = 7;
        int ac = 10;
        int ca = 12;
        int bc = 15;
        int cb = 9;
        int threadPoolSize = 20;

        List<Callable<Response>> transfers = createTransfers("A", "B", ab);
        transfers.addAll(createTransfers("B", "A", ba));
        transfers.addAll(createTransfers("A", "C", ac));
        transfers.addAll(createTransfers("C", "A", ca));
        transfers.addAll(createTransfers("B", "C", bc));
        transfers.addAll(createTransfers("C", "B", cb));
        Collections.shuffle(transfers);

        ExecutorService es = Executors.newFixedThreadPool(threadPoolSize);
        try {
            List<Future<Response>> futures = es.invokeAll(transfers);
            for (Future<Response> future : futures) {
                future.get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        BigDecimal amountA = client.getAccount("A").getAmount();
        BigDecimal amountB = client.getAccount("B").getAmount();
        BigDecimal amountC = client.getAccount("C").getAmount();
        BigDecimal resultSum = amountA.add(amountB).add(amountC);

        assertEquals(0, amountA.compareTo(initialAmountA.add(new BigDecimal(ba + ca - ab - ac))));
        assertEquals(0, amountB.compareTo(initialAmountB.add(new BigDecimal(ab + cb - ba - bc))));
        assertEquals(0, amountC.compareTo(initialAmountC.add(new BigDecimal(ac + bc - ca - cb))));
        assertEquals(0, resultSum.compareTo(initialSum));
    }

    private List<Callable<Response>> createTransfers(String from, String to, int count) {
        List<Callable<Response>> transfers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            transfers.add(transferCallable(from, to, BigDecimal.ONE));
        }
        return transfers;
    }

    private Callable<Response> transferCallable(String from, String to, BigDecimal amount) {
        return () -> {
            TransferDto transferDto = new TransferDto();
            transferDto.setFrom(from);
            transferDto.setTo(to);
            transferDto.setAmount(amount);
            return client.transfer(transferDto);
        };
    }

    private Account findByNumber(String number, Account[] array) {
        return Arrays.stream(array).filter(acc -> acc.getNumber().equals(number)).findAny().get();
    }
    //TODO test error response
}
