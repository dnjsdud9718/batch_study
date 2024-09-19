package com.study.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class ChunkConfiguration {

    @Bean
    public Job job(JobRepository jobRepository, Step step1, Step step2) {

        return new JobBuilder("batchJob", jobRepository)
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<String, String>chunk(2, transactionManager)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        Thread.sleep(300);
                        log.info("item={}", item);
                        return "my item: " + item;
                    }
                })
                .writer(new ListItemWriter<String>(){
                    @Override
                    public void write(Chunk<? extends String> items) throws Exception {
                        Thread.sleep(300);
                        log.info("items={}", items);
                    }
                })
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .<Customer, Customer>chunk(2, transactionManager)
                .reader(
                        new CustomItemReader(Arrays.asList(new Customer("A"), new Customer("B")))
                )
                .processor(new CustomerItemProcessor())
                .writer(new CustomerWriter())
                .build();

    }


}
