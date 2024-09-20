package com.study.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcCursorConfiguration {

    private final int chunkSize = 10;
    private final DataSource dataSource;

    @Bean
    public Job job(JobRepository jobRepository, Step step1){
        return new JobBuilder("batchJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {

        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(chunkSize, transactionManager)
                .reader(customItemReader())
                .writer(customItemWriter())
                .build();
    }

    private ItemWriter<? super Customer> customItemWriter() {
        return items -> {
            for (Customer customer : items) {
                log.info(">> customer :  {}", customer.toString());
            }
        };
    }

    private ItemReader<Customer> customItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("jdbcCursorItemReader")
                .fetchSize(chunkSize)
                .sql("select * from customer where firstName like ? order by lastName, firstName")
                .beanRowMapper(Customer.class)
                .queryArguments("L%")
                .dataSource(dataSource)
                .build();
    }

}
