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
public class BasicConfiguration {

    @Bean
    Job batchJob3(JobRepository jobRepository, Step step9, Step step2) {
        return new JobBuilder("batchJob3", jobRepository)
                .start(step9)
                .on("FAILED")
                .to(step2)
                .end().build();
    }

    @Bean
    public Step step9(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step9", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info(">> step9 has executed");
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                } ), transactionManager).build();
    }
}
