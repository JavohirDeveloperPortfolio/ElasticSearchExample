package com.example.elasticsearchexample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedRequestDto {
    private static final int DEFAULT_SIZE = 100;

    private int page;
    private int size;

    public int getPage() {
        return page;
    }

    public int getSize() {
        return (size != 0) ? size : DEFAULT_SIZE;
    }
}
