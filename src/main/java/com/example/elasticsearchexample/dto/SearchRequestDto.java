package com.example.elasticsearchexample.dto;

import lombok.*;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto extends PagedRequestDto{
    private List<String> fields;
    private String searchTerm;
    private String sortBy;
    private SortOrder order;
}
