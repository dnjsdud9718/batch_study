package com.study.batch.global.ssafyapi.account.service;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.batch.global.config.ApiConfig;
import com.study.batch.global.ssafyapi.AccountResponse;
import com.study.batch.global.ssafyapi.RequestHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiAccountService {
    private final WebClient webClient;
    private final ApiConfig apiConfig;

    /**
     * parameter로 userKey를 받아오던가, userDetail에서 꺼내 써야함.
     *
     * @return
     */
    public AccountResponse getAccount() {
        LocalDateTime now = LocalDateTime.now();

        RequestHeader header = RequestHeader.builder()
                .apiKey(apiConfig.getApiKey())
                .apiName("inquireDemandDepositAccountList")
                .apiServiceCode("inquireDemandDepositAccountList")
                .transmissionDate(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .transmissionTime(now.format(DateTimeFormatter.ofPattern("HHmmss")))
                .institutionCode(apiConfig.getInstitutionCode())
                .fintechAppNo(apiConfig.getFintechAppNo())
                .institutionTransactionUniqueNo(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS")))
                .userKey("f8fef7ab-71e7-479a-a98e-a48e6b36a4bb")
//                .userKey(AuthUtil.getCustomUserDetails().getUserKey   );
                .build();

        log.info("header: {}", header);

        Map<String, RequestHeader> bodyMap = new HashMap<>();
        bodyMap.put("Header", header);
        String apiUserResponse = "";
        try {
            apiUserResponse = webClient.post()
                    .uri("/edu/demandDeposit/inquireDemandDepositAccountList")
                    .bodyValue(bodyMap)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            log.info("연결 잘 됨");
                            return response.bodyToMono(String.class);
                        } else {
                            log.info("에러코드 : {}", response.statusCode());
                            log.info("header: {}", response.request().getHeaders());
                            log.info("uri: {}", response.request().getURI());


                            return response.bodyToMono(String.class);
                        }
                    }).block();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            log.info("Response: {}", objectMapper.readValue(apiUserResponse, AccountResponse.class));

            return objectMapper.readValue(apiUserResponse, AccountResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("apiUserResponse: {}", apiUserResponse);
            return null;
        }
    }
}
