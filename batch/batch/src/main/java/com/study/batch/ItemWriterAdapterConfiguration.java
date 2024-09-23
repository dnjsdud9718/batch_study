package com.study.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class ItemWriterAdapterConfiguration {

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
            .reader(new ItemReader<String>(){
                int i = 0;
                
                @Override
                public String read()
                    throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                    i++;
                    return i > 10 ? null : "item" + i;
                }
            })
            .writer(customItemWriter())
            .build();
    }

    @Bean
    public ItemWriter<? super String> customItemWriter() {
        ItemWriterAdapter<String> adapter = new ItemWriterAdapter<>();

        adapter.setTargetObject(customService());
        adapter.setTargetMethod("customWrite");

        return adapter;
    }

    @Bean
    public CustomService customService() {
        return new CustomService();
    }


}
