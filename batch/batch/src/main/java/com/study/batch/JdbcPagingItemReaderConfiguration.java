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
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class JdbcPagingItemReaderConfiguration {

    private final DataSource dataSource;

    @Bean
    public Job job(JobRepository jobRepository, Step step1) {
        return new JobBuilder("batchJob", jobRepository)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(3, transactionManager)
                .reader(customItemReader())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    protected ItemReader<? extends Customer> customItemReader() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("firstname", "L%");
        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcPagingItemReader")
                .pageSize(3)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .queryProvider(createQueryProvider())
                .parameterValues(params)
                .build();
    }

    @Bean
    protected PagingQueryProvider createQueryProvider() throws Exception {

        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setSelectClause("id, firstname, lastname, birthdate");
        factoryBean.setFromClause("from customer");
        factoryBean.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);
        factoryBean.setSortKeys(sortKeys);

        return factoryBean.getObject();
    }

    @Bean
    protected ItemWriter<? super Customer> customItemWriter() {
        return items -> {
            for(Customer customer : items) {
                log.info(customer.toString());
            }
        };
    }

}
