package com.study.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StartNextConfiguration {

    @Bean
    public Job batchJob(JobRepository jobRepository,
                        Flow flowA, Step step3,
                        Flow flowB, Step step6) {
        return new JobBuilder("batchJob", jobRepository)
                .start(flowA)
                .next(step3)
                .next(flowB)
                .next(step6)
                .end()
                .build();
    }

    @Bean
    public Flow flowA(Step step1, Step step2) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowA");
        flowBuilder.start(step1)
                .next(step2)
                .end();

        return flowBuilder.build();
    }

    @Bean
    public Flow flowB(Step step4, Step step5) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowA");
        flowBuilder.start(step4)
                .next(step5)
                .end();

        return flowBuilder.build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step1 has executed");
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
    }@Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step4", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step4 has executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }@Bean
    public Step step5(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step5", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step5 has executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }@Bean
    public Step step6(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step6", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("step6 has executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager).build();
    }
}
