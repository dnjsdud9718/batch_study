package com.study.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomJpaPagingItemConfiguration {

    private final EntityManagerFactory factory;

    @Bean
    public Job job(JobRepository jobRepository, Step step1) {

        return new JobBuilder("batchJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(3, transactionManager)
                .reader(customReader())
                .writer(customWriter())
                .build();
    }

    @Bean
    protected ItemReader<? extends Customer> customReader() {

        Map<String, Object> params = new HashMap<>();
        params.put("firstname", "L%");

        return new JpaPagingItemReaderBuilder<Customer>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(factory)
                .pageSize(3)
                .queryString("select c from Customer c join fetch c.address")
                .build();
    }

    @Bean
    protected ItemWriter<? super Customer> customWriter() {
        return items -> {
            for (Customer item : items) {
                log.info(">>> item = {}", item.toString());
            }
        };
    }
}
