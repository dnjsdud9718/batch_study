package com.study.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class RepeatConfiguration {

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
                .<String, String>chunk(3, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;

                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 3 ? null : "item" + i;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    RepeatTemplate repeatTemplate = new RepeatTemplate();

                    @Override
                    public String process(String item) throws Exception {

//                        repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
                        CompositeCompletionPolicy completionPolicy = new CompositeCompletionPolicy();
                        CompletionPolicy[] completionPolicies = {
                                new SimpleCompletionPolicy(3),
                                new TimeoutTerminationPolicy(3000)
                        };
                        completionPolicy.setPolicies(completionPolicies);

                        repeatTemplate.setExceptionHandler(simpleExceptionHandler());

                        repeatTemplate.iterate(new RepeatCallback() {

                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                log.info("RepeatTemplate is testing");
                                throw new RuntimeException("Exception is occurred");
//                                return RepeatStatus.CONTINUABLE;
                            }
                        });

                        return item;
                    }
                })
                .writer(items -> log.info("item = {}", items))
                .build();

    }

    @Bean
    public ExceptionHandler simpleExceptionHandler() {
        return new SimpleLimitExceptionHandler(3);
    }
}
