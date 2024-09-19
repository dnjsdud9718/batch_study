package com.study.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchStatusExitStatusConfiguration {

    @Bean
    public Job batchJob2(JobRepository jobRepository,
                         Step step7, Step step8) {
        return new JobBuilder("batchJob2", jobRepository)
                .start(step7)
                .next(step8)
                .build();
    }


    @Bean
    public Step step7(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step7", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step7 has executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
    @Bean
    public Step step8(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step8", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step8 has executed");
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
}
