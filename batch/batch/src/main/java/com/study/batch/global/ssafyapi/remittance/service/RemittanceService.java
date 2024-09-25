package com.study.batch.global.ssafyapi.remittance.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.batch.global.config.ApiConfig;
import com.study.batch.global.ssafyapi.RequestHeader;
import com.study.batch.global.ssafyapi.remittance.dto.RemittanceRequest;
import com.study.batch.global.ssafyapi.remittance.dto.RemittanceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemittanceService {
    private final WebClient webClient;
    private final ApiConfig apiConfig;
    private final ObjectMapper objectMapper;

    public RemittanceResponse getAccount(
            String userKey,
            String depositAccountNumber,
            String withdrawalAccountNumber,
            Long money
    ) {
        log.info("getAccount");
        LocalDateTime now = LocalDateTime.now();

        RequestHeader header = RequestHeader.builder()
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

        RemittanceRequest remittanceRequest = RemittanceRequest
                .builder()
                .transactionBalance(money)
                .depositAccountNo(depositAccountNumber)
                .depositTransactionSummary("(수시입출금) : 입금(이체)")
                .requestHeader(header)
                .withdrawalAccountNo(withdrawalAccountNumber)
                .withdrawalTransactionSummary("(수시입출금) : 출금(이체)")
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try{
            objectMapper.writeValueAsString(remittanceRequest);
        }catch (Exception e){
            e.printStackTrace();
        }

        String apiUserResponse = "";
        try{
            apiUserResponse = webClient.post()
                    .uri("/edu/demandDeposit/updateDemandDepositAccountTransfer")
                    .bodyValue(remittanceRequest)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            log.info("연결 잘 됨");
                        }
                        return response.bodyToMono(String.class);
                    }).block();

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(apiUserResponse, RemittanceResponse.class);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
