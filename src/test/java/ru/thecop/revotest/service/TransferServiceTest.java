package ru.thecop.revotest.service;

import org.hibernate.Session;
import org.junit.Test;
import ru.thecop.revotest.exception.AccountNotFoundException;
import ru.thecop.revotest.exception.InsufficientFundsException;
import ru.thecop.revotest.model.Account;
import ru.thecop.revotest.repository.AccountDao;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransferServiceTest {

    private final static String ACCOUNT_1_NUMBER = "A";
    private final static String ACCOUNT_2_NUMBER = "B";
    private final static BigDecimal ACCOUNT_1_AMOUNT = new BigDecimal(100);
    private final static BigDecimal ACCOUNT_2_AMOUNT = new BigDecimal(200);

    @Test
    public void testTransfer() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        BigDecimal transferAmount = new BigDecimal(13);
        transferService.transfer(ACCOUNT_1_NUMBER, ACCOUNT_2_NUMBER, transferAmount);
        assertEquals(dao.findByNumber(ACCOUNT_1_NUMBER).getAmount(), ACCOUNT_1_AMOUNT.subtract(transferAmount));
        assertEquals(dao.findByNumber(ACCOUNT_2_NUMBER).getAmount(), ACCOUNT_2_AMOUNT.add(transferAmount));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferNullAccount() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        BigDecimal transferAmount = new BigDecimal(13);
        transferService.transfer(null, null, transferAmount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferNullAmount() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        transferService.transfer(ACCOUNT_1_NUMBER, ACCOUNT_2_NUMBER, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferNegativeAmount() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        BigDecimal transferAmount = new BigDecimal(-13);
        transferService.transfer(ACCOUNT_1_NUMBER, ACCOUNT_2_NUMBER, transferAmount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferZeroAmount() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        BigDecimal transferAmount = new BigDecimal(0);
        transferService.transfer(ACCOUNT_1_NUMBER, ACCOUNT_2_NUMBER, transferAmount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTransferSameAccount() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        BigDecimal transferAmount = new BigDecimal(10);
        transferService.transfer(ACCOUNT_1_NUMBER, ACCOUNT_1_NUMBER, transferAmount);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testTransferInsufficientFunds() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        BigDecimal transferAmount = ACCOUNT_1_AMOUNT.add(BigDecimal.ONE);
        transferService.transfer(ACCOUNT_1_NUMBER, ACCOUNT_2_NUMBER, transferAmount);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferAccountNotFound() {
        AccountDao dao = mockDao();
        TransferService transferService = new TransferService(dao);
        BigDecimal transferAmount = new BigDecimal(13);
        transferService.transfer("no such account", ACCOUNT_2_NUMBER, transferAmount);
    }

    private AccountDao mockDao() {
        AccountDao dao = mock(AccountDao.class);
        Account a = new Account();
        a.setId(1L);
        a.setNumber(ACCOUNT_1_NUMBER);
        a.setAmount(ACCOUNT_1_AMOUNT);

        Account b = new Account();
        b.setId(2L);
        b.setNumber(ACCOUNT_2_NUMBER);
        b.setAmount(ACCOUNT_2_AMOUNT);

        when(dao.findByNumber(ACCOUNT_1_NUMBER)).thenReturn(a);
        when(dao.findByNumber(ACCOUNT_2_NUMBER)).thenReturn(b);
        when(dao.getById(eq(1L), anyBoolean())).thenReturn(a);
        when(dao.getById(eq(2L), anyBoolean())).thenReturn(b);
        when(dao.getSession()).thenReturn(mock(Session.class));

        return dao;
    }
}
