package com.example.elasticsearchexample.controller;

import com.example.elasticsearchexample.dto.ArticleDto;
import com.example.elasticsearchexample.dto.SearchRequestDto;
import com.example.elasticsearchexample.entity.ArticleEntity;
import com.example.elasticsearchexample.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

//    @PostMapping
//    public void index(@RequestBody final ArticleEntity article) {
//        articleService.index(article);
//    }

    @PostMapping("/insertArticle")
    public ResponseEntity<?> insertDummyData(@RequestBody ArticleDto articleDto) {
        return ResponseEntity.ok(articleService.index(articleDto));
    }

    @GetMapping("/{id}")
    public ArticleEntity getById(@PathVariable final String id) {
        return articleService.getById(id);
    }

    @PostMapping("/search")
    public List<ArticleEntity> search(@RequestBody final SearchRequestDto dto) {
        return articleService.search(dto);
    }

    @GetMapping("/search/{date}")
    public List<ArticleEntity> getAllVehiclesCreatedSince(
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return articleService.getAllArticleCreatedSince(date);
    }

    @PostMapping("/searchcreatedsince/{date}")
    public List<ArticleEntity> searchCreatedSince(
            @RequestBody final SearchRequestDto dto,
            @PathVariable
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        return articleService.searchCreatedSince(dto, date);
    }
}
