package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransferRes;
import com.dws.challenge.exception.AccountTranferException;
import com.dws.challenge.exception.OverdraftNotAllowedException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  private final  NotificationService notificationService;


  @Autowired
  public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
    this.accountsRepository = accountsRepository;
    this.notificationService = notificationService;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  /**
   * transfers the money from FromAccount to ToAccount if it is a validRequest/validTransaction
   * @param fromAccountId - Account Id from which amount need to be transferred
   * @param toAccountId - Account Id to which amount need to be credited
   * @param transferAmount - Amount to be transferred
   * @return FunTransferRes - Response object to showcase to the end user
   */
  public synchronized FundTransferRes moneyTransfer(String fromAccountId, String toAccountId, BigDecimal transferAmount) {
    isValidRequest(fromAccountId, toAccountId);
    Account fromAccount = accountsRepository.getAccount(fromAccountId);
    Account toAccount   = accountsRepository.getAccount(toAccountId);
    FundTransferRes response = new FundTransferRes();
    if(fromAccount == null) {
      accountsRepository.createAccount(new Account(fromAccountId,new BigDecimal(5000.00)));
      fromAccount = accountsRepository.getAccount(fromAccountId);
    }
    if(toAccount == null) {
      accountsRepository.createAccount(new Account(toAccountId,new BigDecimal(10000.00)));
      toAccount   = accountsRepository.getAccount(toAccountId);
    }

    if (isValidTransaction(fromAccount, toAccount, transferAmount)) {
      accountsRepository.moneyTransfer(fromAccount,toAccount,transferAmount);
      fromAccount = withDrawFromAccount(fromAccount, transferAmount);
      notificationService.notifyAboutTransfer(fromAccount, "Debited the amount " + transferAmount + " from your account.");
      toAccount = transferToAccount(toAccount, transferAmount);
      notificationService.notifyAboutTransfer(toAccount, "Credited the amount " + transferAmount + " to your account.");
      accountsRepository.moneyTransfer(fromAccount, toAccount, transferAmount);
      response.setMessage("Funds transferred successfully");
    }else {
       response.setMessage("Is not a valid transaction");
    }
    return response;
  }

  /**
   * checks whether given request is valid
   * @param fromAccountId - Account Id from which amount need to be transferred
   * @param toAccountId - Account Id to which amount need to be credited
   */
  public synchronized void isValidRequest(String fromAccountId, String toAccountId) {
    if(fromAccountId.equalsIgnoreCase(toAccountId)){
        throw new AccountTranferException("fromAccountId  and toAccountId should not be same");
    }
  }

  /**
   *  Checks initiated transaction is valid
   * @param fromAccount - Account Id from which amount need to be transferred
   * @param toAccount  - Account Id to which amount need to be credited
   * @param transferAmount - Amount to be transferred
   * @return true if it is valid Transaction
   */
  public synchronized boolean isValidTransaction(Account fromAccount, Account toAccount, BigDecimal transferAmount) {
    BigDecimal accountBalance = fromAccount.getBalance();
    if (transferAmount.compareTo(accountBalance) == 1) {
         throw new OverdraftNotAllowedException("Transfer Amount should not be greater than current account balance");
    }
    return true;
  }

  /**
   * Withdraw the transferAmount from the FromAccount
   * @param account - Here it is FromAccount from where transaction amount is debited
   * @param transferAmount - Amount to be transferred/debited from FromAccount
   * @return - Returns FromAccount Object after deducting the transfer amount.
   */
  public synchronized Account withDrawFromAccount(Account account, BigDecimal transferAmount) {
    BigDecimal currentBalance = account.getBalance();
    account.setBalance(currentBalance.subtract(transferAmount));
    return account;
  }

  /**
   * Deposits the transferAmount into the ToAccount
   * @param account - Here it is ToAccount to which transferAmount is credited
   * @param transferAmount - Amount to depostied/credited to ToAccount
   * @return Returns TopAccount Object after adding the transfer amount
   */
  public synchronized Account transferToAccount(Account account, BigDecimal transferAmount) {
    BigDecimal currentBalance = account.getBalance();
    account.setBalance(currentBalance.add(transferAmount));
    return account;
  }

}
