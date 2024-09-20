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
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JpaCursorConfiguration {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("batchJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(2, transactionManager)
                .reader(customerItemReader())
                .writer(customerItemWriter())
                .build();
    }

    private ItemWriter<? super Customer> customerItemWriter() {
        return items -> {
            for (Customer customer : items) {
                log.info(">> customer = {}", customer.toString());
            }
        };
    }

    private ItemReader<? extends Customer> customerItemReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", "L%");

        return new JpaCursorItemReaderBuilder<Customer>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from Customer c where c.firstName like :firstName")
                .parameterValues(parameters)
                .build();
    }

}
