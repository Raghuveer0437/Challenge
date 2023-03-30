package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransferRes;
import com.dws.challenge.exception.AccountTranferException;
import com.dws.challenge.exception.OverdraftNotAllowedException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    @InjectMocks
    AccountsService accountsService;

    @Mock
    AccountsRepository accountsRepository;

//    @Mock
//    AccountsRepositoryInMemory accountsRepositoryInMemory;

    @Mock
    NotificationService notificationService;

    @BeforeEach
    public void initMocks(){
        MockitoAnnotations.openMocks(this);
    }


    /**
     * Positive test scenario need to be succcessful
     */
    @Test
    public void moneyTransferTest() {
        Account fromAccount = new Account("1234",new BigDecimal(5000));
        Account toAccount = new Account("5678",new BigDecimal(10000));
        Mockito.when(accountsRepository.getAccount("1234")).thenReturn(fromAccount);
        Mockito.when(accountsRepository.getAccount("5678")).thenReturn(toAccount);
        Mockito.doNothing().when(notificationService)
                .notifyAboutTransfer(fromAccount,"Amount debited from your Account");
        FundTransferRes response = accountsService.moneyTransfer("1234","5678",new BigDecimal(200));
        assertEquals("Funds transferred successfully", response.getMessage());

    }

    /**
     * Negative Test Scenario to throw Exception when fromAccountId and toAccountId are same
     * Excpetion - AccountTranferException
     */
    @Test
    public void moneyTransferDuplicateAccountIds() {

        Exception exception = assertThrows(AccountTranferException.class, () -> {
            Account fromAccount = new Account("1234",new BigDecimal(5000));
            Account toAccount = new Account("5678",new BigDecimal(10000));
            Mockito.when(accountsRepository.getAccount("1234")).thenReturn(fromAccount);
            Mockito.when(accountsRepository.getAccount("5678")).thenReturn(toAccount);
            Mockito.doNothing().when(notificationService)
                    .notifyAboutTransfer(fromAccount,"Amount debited from your Account");
            accountsService.moneyTransfer("1234","1234",new BigDecimal(200));
        });
        String expectedMessage = "fromAccountId  and toAccountId should not be same";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Negative Test Scenario to throw Exception when transferAmount  is  greater than fromAccount Current Balance
     * Excpetion - AccountTranferException
     */
    @Test
    public void testOverDraftNotAllowedException() {

        Exception exception = assertThrows(OverdraftNotAllowedException.class, () -> {
            Account fromAccount = new Account("1234",new BigDecimal(5000));
            Account toAccount = new Account("5678",new BigDecimal(10000));
            Mockito.when(accountsRepository.getAccount("1234")).thenReturn(fromAccount);
            Mockito.when(accountsRepository.getAccount("5678")).thenReturn(toAccount);
            Mockito.doNothing().when(notificationService)
                    .notifyAboutTransfer(fromAccount,"Amount debited from your Account");
            accountsService.moneyTransfer("1234","5678",new BigDecimal(20000));
        });
        String expectedMessage = "Transfer Amount should not be greater than current account balance";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }


}
