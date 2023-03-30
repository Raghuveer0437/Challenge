package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.FundTransferReq;
import com.dws.challenge.domain.FundTransferRes;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }

  /** This Method is a post call which helps us to transfer the funds from one account to another account
   *
   * @param fundTransfer - Request to transfer the funds
   * @return
   */
  @PostMapping(path = "/fundTransfer",consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> accountTransfer(@RequestBody @Valid FundTransferReq fundTransfer) {
    log.info("accountTransfer  fromAccount {} toAccount {} ", fundTransfer.getFromAccountId(),fundTransfer.getToAccountId());
    FundTransferRes fundTransferRes = null;
    try {

      fundTransferRes = this.accountsService.moneyTransfer(fundTransfer.getFromAccountId(), fundTransfer.getToAccountId(), fundTransfer.getTransferAmount());
      if(fundTransferRes!=null && fundTransferRes.getMessage().equalsIgnoreCase("Funds transferred successfully")) {
        return new ResponseEntity<>(fundTransferRes,HttpStatus.OK);
      } else {
        fundTransferRes.setMessage("FundTransfer does not happen, please contact the bank");
        return new ResponseEntity<>(fundTransferRes,HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    catch (Exception ex) {
      return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }



  @GetMapping(path = "/test")
  public String helloWorld() {
    return "hello-world";
  }

}
