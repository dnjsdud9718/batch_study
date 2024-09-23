package com.study.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ItemReaderAdaptorConfiguration {

    @Bean
    public Job job(JobRepository jobRepository, Step step1) {

        return new JobBuilder("batchJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(customItemReader())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public ItemReader<String> customItemReader() {
        ItemReaderAdapter<String> adapter = new ItemReaderAdapter<>();
        adapter.setTargetObject(customService());
        adapter.setTargetMethod("customRead");
        return adapter;
    }

    @Bean
    public Object customService() {
        return new CustomService<String>();
    }

    @Bean
    public ItemWriter<String> customItemWriter() {
        return items -> {
            System.out.println("item: " + items);
        };
    }



}
