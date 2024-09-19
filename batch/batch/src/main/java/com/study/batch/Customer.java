package com.study.batch;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {

    private String name;

    public void toUpperName() {
        this.name = name.toUpperCase();
    }
}
