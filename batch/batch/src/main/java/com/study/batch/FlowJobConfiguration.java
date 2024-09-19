package com.study.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlowJobConfiguration {

    @Bean
    public Job batchJob(JobRepository jobRepository, Step step1, Step step2, Step step3) {
        /**
         * 만약 스텝1이 성공하면, 스텝3으로
         * 만약 스텔1이 실패하며, 스텝2로
         */
        return new JobBuilder("batchJob", jobRepository)
                .start(step1)
                .on("COMPLETED")
                .to(step3)
                .from(step1)
                .on("FAILED")
                .to(step2)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step1 has executed");
//                     throw new RuntimeException("step1 has failed");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step2 has executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step3 has executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
}
