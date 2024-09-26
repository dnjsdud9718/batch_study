package com.study.batch;

import com.study.batch.domain.account.entity.Account;
import com.study.batch.domain.alarm.Alarm;
import com.study.batch.domain.alarm.TypeStatus;
import com.study.batch.domain.alarm.repository.JpaAlarmRepository;
import com.study.batch.domain.shop.entity.Seed;
import com.study.batch.domain.shop.entity.SeedRound;
import com.study.batch.domain.user.entity.User;
import com.study.batch.global.alarm.dto.FcmSendDto;
import com.study.batch.global.alarm.service.FcmService;
import com.study.batch.global.common.CommonResponse;
import com.study.batch.global.ssafyapi.remittance.dto.TransferResponse;
import com.study.batch.global.ssafyapi.remittance.service.TransferService;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.study.batch.domain.shop.entity.TransferStatus.FAIL;
import static com.study.batch.domain.shop.entity.TransferStatus.NONE;
import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    private static final String FCM_TOKEN = "f1Td0XrURgOZol8QqyeeWi:APA91bFvtcEh5-CojATEfMnFKPKEl4fk_9wQvKgwN_eSwFNvm4ixt8MZHThMKvTnzZ4V-qtR8d0_Qs7_xuq1n0cK1rYSuWEgMhVe5iRMK0t0m2eb91PS-g4htLtNJ8Y62WkJwxi0vH1v";
    public static final String TITLE = "종잣돈 모으기 송금 알림";
    private final int CHUNK_SIZE = 3;

    private final EntityManagerFactory entityManagerFactory;
    private final FcmService fcmService;
    private final TransferService transferService;
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
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super SeedRound> itemWriter() {

        return new JpaItemWriterBuilder<>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(false)
                .clearPersistenceContext(true)
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
                log.info("item = {}", item);
                return null;
            }

            Seed seed = item.getSeed();
            User user = seed.getUser();
            Account depositAccount = seed.getDepositAccount();
            Account withdrawalAccount = seed.getWithdrawalAccount();
            Long transferBalance = Long.valueOf(seed.getTransferAmount());
            // 1. 송금
            TransferResponse response = transferService.transfer(user.getUserKey(), depositAccount.getAccountNumber(), withdrawalAccount.getAccountNumber(), transferBalance);
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
//                    .token(user.getFCM())
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
