package com.dws.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * POJO class to handle the Request
 */
@Data
public class FundTransferReq {

    @NotNull
    private Long id;

    @NotNull
    @Min(value = 0, message = "account number must be positive.")
    private String fromAccountId;

    @NotNull
    @Min(value = 0, message = "account number must be positive.")
    private String toAccountId;

    @NotNull
    @Min(value = 1, message = "Transfer Amount should not be in negative as well as Zero.")
    private BigDecimal transferAmount;

    @JsonCreator
    public FundTransferReq(@JsonProperty("fromAccountId") String fromAccountId, @JsonProperty("toAccountId") String toAccountId, @JsonProperty("transferAmount") BigDecimal transferAmount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.transferAmount = transferAmount;
    }
}
