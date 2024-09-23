package com.study.batch;

import org.springframework.batch.item.ItemProcessor;

public class CustomeItemProcessor implements ItemProcessor<Customer,Customer2> {



    @Override
    public Customer2 process(Customer customer) throws Exception {
        return Customer2.builder()
            .firstname(customer.getFirstname())
            .lastname(customer.getLastname())
            .birthdate(customer.getBirthdate())
            .build();
    }
}
