package com.study.batch;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaConfiguration {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job job(JobRepository jobRepository, Step step1) {
        return new JobBuilder("batchJob", jobRepository)
            .start(step1)
            .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
            .<Customer, Customer2>chunk(3, transactionManager)
            .reader(customItemReader())
            .processor(customItemProcessor())
            .writer(customerItemWriter())
            .build();
    }

    private ItemReader<? extends Customer> customItemReader() {

        return new JpaPagingItemReaderBuilder<Customer>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(3)
            .queryString("select c from Customer c")
            .build();
    }

    @Bean
    public ItemWriter<? super Customer2> customerItemWriter() {

        return new JpaItemWriterBuilder<Customer2>()
            .usePersist(true)
            .entityManagerFactory(entityManagerFactory)
            .build();
    }


    @Bean
    public ItemProcessor<? super Customer, ? extends Customer2> customItemProcessor() {

        return new CustomeItemProcessor();
    }

}
