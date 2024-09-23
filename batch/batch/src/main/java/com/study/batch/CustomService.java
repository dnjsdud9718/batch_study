package com.study.batch;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomService<T> {

    public void customWrite(T item) {
        log.info("item = {}", item);
    }

}
