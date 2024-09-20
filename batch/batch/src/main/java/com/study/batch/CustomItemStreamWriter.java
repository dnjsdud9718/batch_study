package com.study.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

@Slf4j
public class CustomItemStreamWriter implements ItemStreamWriter<String> {
    @Override
    public void write(Chunk<? extends String> items) throws Exception {
      log.info("write");
        items.getItems().forEach(item -> {
            log.info("item={}", item);
        });
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("open");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("update");
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("close");
    }
}
