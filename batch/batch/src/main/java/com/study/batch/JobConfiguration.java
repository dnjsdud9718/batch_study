package com.study.batch;

import com.study.batch.domain.shop.entity.SeedRound;
import com.study.batch.domain.shop.entity.TransferStatus;
import com.study.batch.global.alarm.dto.FcmSendDto;
import com.study.batch.global.alarm.service.FcmService;
import com.study.batch.global.common.CommonResponse;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
    private final int CHUNK_SIZE = 2;
    private final EntityManagerFactory entityManagerFactory;
    private final FcmService fcmService;

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
                .<String, String>chunk(CHUNK_SIZE, transactionManager)
//                .reader(itemReader(null))
                .reader(new ItemReader<String>() {
                    int i=0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > CHUNK_SIZE ? null : "item" + i;
                    }
                })
                .processor(itemProcessor(fcmService))
                .writer(items -> log.info("items = {}", items))
                .build();
    }

    @Bean
    public ItemProcessor<? super String, ? extends String> itemProcessor(FcmService fcmService) {

        return (ItemProcessor<String, String>) item -> {
            // 1. 송금
            // 2. 송금 결과 저장
            // 3. 알림 전송
            // 4. 알림 내력 저장

            FcmSendDto fcm = FcmSendDto.builder()
                    .title("sibal")
                    .body("이현규 바보")
                    .token("es2ey8msSVW9Miu7mgGQZD:APA91bHmWrxfuNR_hN03WE7UNpO8iN-30M70YBi8ese7Vsj3RzO0cyyMbRZmeyDQMFK-rVDEbQa4uVu9dz8vHLE2lKJOBXzKSR3LM42J2YMxmaqvSP4MwPgkDmlXulD-Ka8sdDbzgKh6")
                    .build();
            CommonResponse commonResponse = fcmService.sendMessageTo(fcm);
            log.info("result = {}", commonResponse);
//            Seed seed = item.getSeed();
//            User user = seed.getUser();
//
//            log.info("seed = {}", seed);
            return item;
        };
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<SeedRound> itemReader(
            @Value("#{jobParameters['createDate']}") String createDate
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", TransferStatus.NONE);
        params.put("createDate", LocalDate.parse(createDate, ofPattern("yyyy-MM-dd")));

        return new JpaPagingItemReaderBuilder<SeedRound>()
                .name("itemReader")
                .pageSize(CHUNK_SIZE)
                .queryString("select r from SeedRound r where r.status = :status and r.transferDate = :createDate order by r.id")
                .parameterValues(params)
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}
