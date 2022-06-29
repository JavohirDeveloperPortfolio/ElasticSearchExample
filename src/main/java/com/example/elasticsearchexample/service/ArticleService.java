package com.example.elasticsearchexample.service;

import com.example.elasticsearchexample.dto.ArticleDto;
import com.example.elasticsearchexample.dto.SearchRequestDto;
import com.example.elasticsearchexample.dto.util.SearchUtil;
import com.example.elasticsearchexample.entity.ArticleEntity;
import com.example.elasticsearchexample.helper.Indices;
import com.example.elasticsearchexample.repository.ArticleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(ArticleService.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final RestHighLevelClient client;
    private final ArticleRepository articleRepository;

    public List<ArticleEntity> search(final SearchRequestDto dto) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.ARTICLE_INDEX,
                dto
        );

        return searchInternal(request);
    }

    public List<ArticleEntity> getAllArticleCreatedSince(final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.ARTICLE_INDEX,
                "created",
                date
        );

        return searchInternal(request);
    }

    public List<ArticleEntity> searchCreatedSince(final SearchRequestDto dto, final Date date) {
        final SearchRequest request = SearchUtil.buildSearchRequest(
                Indices.ARTICLE_INDEX,
                dto,
                date
        );

        return searchInternal(request);
    }

    private List<ArticleEntity> searchInternal(final SearchRequest request) {
        if (request == null) {
            LOG.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<ArticleEntity> vehicles = new ArrayList<>(searchHits.length);
            for (SearchHit hit : searchHits) {
                vehicles.add(
                        MAPPER.readValue(hit.getSourceAsString(), ArticleEntity.class)
                );
            }

            return vehicles;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Boolean index(final ArticleDto articleDto) {
        ArticleEntity article = new ArticleEntity();
        article.setName(articleDto.getName());
        article.setText(articleDto.getText());
        try {
            article.setCreated(DATE_FORMAT.parse(articleDto.getCreated()));
        } catch (ParseException e) {
            LOG.error(e.getMessage(), e);
        }
        try {
            articleRepository.save(article);
            final String articleAsString = MAPPER.writeValueAsString(article);

            final IndexRequest request = new IndexRequest(Indices.ARTICLE_INDEX);
            request.id(article.getId().toString());
            request.source(articleAsString, XContentType.JSON);
            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public ArticleEntity getById(final String articleId) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(Indices.ARTICLE_INDEX, articleId),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), ArticleEntity.class);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
