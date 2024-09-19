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
@RequiredArgsConstructor
@Configuration
public class TransitionConfiguration {

    @Bean
    public Job batchJob4(JobRepository jobRepository, Step step1, Step step2, Step step3, Step step4, Step step5) {
        return new JobBuilder("batchJob4", jobRepository)
                .start(step1)
                    .on("FAILED")
                    .to(step2)
                    .on("FAILED")
                    .stop()
                .from(step1)
                    .on("*")
                    .to(step3)
                    .next(step4)
                .from(step2)
                    .on("*")
                    .to(step5)
                    .end()
                .build();
    }

    @Bean
    public Step step01(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSourceTransactionManager transactionManager) {

        return new StepBuilder("step01")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">> step01 has executed");

                    return RepeatStatus.FINISHED;
                } ), transactionManager).build();
    }@Bean
    public Step step01(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step01", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">> step01 has executed");

                    return RepeatStatus.FINISHED;
                } ), transactionManager).build();
    }
    @Bean
    public Step step02(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step02", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">> step02 has executed");

                    return RepeatStatus.FINISHED;
                } ), transactionManager).build();
    }@Bean
    public Step step03(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step03", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">> step03 has executed");

                    return RepeatStatus.FINISHED;
                } ), transactionManager).build();
    }
    @Bean
    public Step step04(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step04")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">> step04 has executed");

                    return RepeatStatus.FINISHED;
                } ), transactionManager).build();
    }
    @Bean
    public Step step05(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step05")
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">> step05 has executed");

                    return RepeatStatus.FINISHED;
                } ), transactionManager).build();
    }


}
