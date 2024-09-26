package com.study.batch.global.ssafyapi.remittance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.batch.global.ssafyapi.ResponseHeader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferResponse {
    @JsonProperty("Header")
    private ResponseHeader header;
    @JsonProperty("REC")
    private List<Record> rec;
    private String responseCode;
    private String responseMessage;
}
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class Record{
    private Long transactionUniqueNo;
    private String accountNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    private LocalDate transactionDate;
    private Long transactionType;
    private String transactionTypeName;
    private String transactionAccountNo;
}
