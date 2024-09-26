package com.study.batch.global.ssafyapi.remittance.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.batch.global.config.ApiConfig;
import com.study.batch.global.ssafyapi.RequestHeader;
import com.study.batch.global.ssafyapi.remittance.dto.TransferRequest;
import com.study.batch.global.ssafyapi.remittance.dto.TransferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransferService {

    private final WebClient webClient;
    private final ApiConfig apiConfig;
    private final ObjectMapper objectMapper;

    public TransferResponse transfer(
            String userKey,
            String depositAccountNumber,
            String withdrawalAccountNumber,
            Long money
    ) {

        RequestHeader header = getRequestHeader(userKey);
        TransferRequest transferRequest = getTransferRequest(depositAccountNumber, withdrawalAccountNumber, money, header);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try{
            objectMapper.writeValueAsString(transferRequest);
        }catch (Exception e){
            e.printStackTrace();
        }

        String apiUserResponse = "";
        try{
            apiUserResponse = webClient.post()
                    .uri("/edu/demandDeposit/updateDemandDepositAccountTransfer")
                    .bodyValue(transferRequest)
                    .exchangeToMono(response -> {
                        return response.bodyToMono(String.class);
                    }).block();

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(apiUserResponse, TransferResponse.class);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static TransferRequest getTransferRequest(String depositAccountNumber, String withdrawalAccountNumber, Long money, RequestHeader header) {
        return TransferRequest
                .builder()
                .transactionBalance(money)
                .depositAccountNo(depositAccountNumber)
                .depositTransactionSummary("(수시입출금) : 입금(이체)")
                .requestHeader(header)
                .withdrawalAccountNo(withdrawalAccountNumber)
                .withdrawalTransactionSummary("(수시입출금) : 출금(이체)")
                .build();
    }

    private RequestHeader getRequestHeader(String userKey) {
        LocalDateTime now = LocalDateTime.now();

        return RequestHeader.builder()
                .apiKey(apiConfig.getApiKey())
                .apiName("updateDemandDepositAccountTransfer")
                .apiServiceCode("updateDemandDepositAccountTransfer")
                .transmissionDate(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .transmissionTime(now.format(DateTimeFormatter.ofPattern("HHmmss")))
                .institutionCode(apiConfig.getInstitutionCode())
                .fintechAppNo(apiConfig.getFintechAppNo())
                .institutionTransactionUniqueNo(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS")))
                .userKey(userKey)
                .build();
    }
}

