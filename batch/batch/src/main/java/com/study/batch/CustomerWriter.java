package com.study.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class CustomerWriter implements ItemWriter<Customer> {

    @Override
    public void write(Chunk<? extends Customer> items) throws Exception {
        items.forEach(System.out::println);
    }
}
