package com.study.batch;

import com.study.batch.domain.account.entity.Account;
import com.study.batch.domain.alarm.Alarm;
import com.study.batch.domain.alarm.TypeStatus;
import com.study.batch.domain.alarm.repository.JpaAlarmRepository;
import com.study.batch.domain.shop.entity.Seed;
import com.study.batch.domain.shop.entity.SeedRound;
import com.study.batch.domain.shop.entity.SeedStatus;
import com.study.batch.domain.shop.entity.TransferStatus;
import com.study.batch.domain.user.entity.User;
import com.study.batch.global.alarm.dto.FcmSendDto;
import com.study.batch.global.alarm.service.FcmService;
import com.study.batch.global.common.CommonResponse;
import com.study.batch.global.ssafyapi.remittance.dto.RemittanceResponse;
import com.study.batch.global.ssafyapi.remittance.service.RemittanceService;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.study.batch.domain.shop.entity.TransferStatus.*;
import static com.study.batch.domain.shop.entity.TransferStatus.FAIL;
import static com.study.batch.domain.shop.entity.TransferStatus.NONE;
import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    private static final String FCM_TOKEN = "es2ey8msSVW9Miu7mgGQZD:APA91bHmWrxfuNR_hN03WE7UNpO8iN-30M70YBi8ese7Vsj3RzO0cyyMbRZmeyDQMFK-rVDEbQa4uVu9dz8vHLE2lKJOBXzKSR3LM42J2YMxmaqvSP4MwPgkDmlXulD-Ka8sdDbzgKh6";
    public static final String TITLE = "종잣돈 모으기 송금 알림";
    private final int CHUNK_SIZE = 3;

    private final EntityManagerFactory entityManagerFactory;
    private final FcmService fcmService;
    private final RemittanceService remittanceService;
    private final JpaAlarmRepository alarmRepository;

    @Bean
    public Job job(JobRepository jobRepository, Step step1) {

        return new JobBuilder("batchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step1", jobRepository)
                .<SeedRound, SeedRound>chunk(CHUNK_SIZE, transactionManager)
                .reader(itemReader(null))
                .processor(itemProcessor())
                .writer(items -> log.info("items = {}", items))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<SeedRound> itemReader(
            @Value("#{jobParameters['createDate']}") String createDate
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("createDate", LocalDate.parse(createDate, ofPattern("yyyy-MM-dd")));

        return new JpaPagingItemReaderBuilder<SeedRound>()
                .name("itemReader")
                .pageSize(CHUNK_SIZE)
                .queryString("select r from SeedRound r where r.transferDate = :createDate order by r.id")
                .parameterValues(params)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public ItemProcessor<? super SeedRound, ? extends SeedRound> itemProcessor() {

        return (ItemProcessor<SeedRound, SeedRound>) item -> {
            if (!NONE.equals(item.getStatus())) {
                return item;
            }

            Seed seed = item.getSeed();
            User user = seed.getUser();
            Account depositAccount = seed.getDepositAccount();
            Account withdrawalAccount = seed.getWithdrawalAccount();
            Long transferBalance = Long.valueOf(seed.getTransferAmount());
            // 1. 송금
            RemittanceResponse response = remittanceService.getAccount(user.getUserKey(), depositAccount.getAccountNumber(), withdrawalAccount.getAccountNumber(), transferBalance);
            // 2. 송금 결과 저장
            if (response.getHeader() == null) {
                // FAIL
                item.transferFailed();
            } else {
                // SUCCESS
                item.transferSucceed();
            }
            // 3. 알림 전송
            String message = seed.getTitle() + " 모으기" + (item.getStatus().equals(FAIL) ? "실패" : "성공");

            FcmSendDto fcm = FcmSendDto.builder()
                    .title(TITLE)
                    .body(message)
                    .token(FCM_TOKEN)
                    .build();

            CommonResponse commonResponse = fcmService.sendMessageTo(fcm);
            if ("SUCCESS".equals(commonResponse.getResult())) {
                Alarm alarm = Alarm.of(user.getId(), TITLE, message, TypeStatus.SEED_SEND, seed.getId());
                alarmRepository.save(alarm);
            }
            return item;
        };
    }

}
