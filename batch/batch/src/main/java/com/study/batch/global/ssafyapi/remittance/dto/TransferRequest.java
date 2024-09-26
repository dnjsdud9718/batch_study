package com.study.batch.global.ssafyapi.remittance.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.batch.global.ssafyapi.RequestHeader;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    @JsonProperty("Header")
    private RequestHeader requestHeader;
    private String depositAccountNo;
    private String depositTransactionSummary;
    private Long transactionBalance;
    private String withdrawalAccountNo;
    private String withdrawalTransactionSummary;
}
