package com.ipmugo.searchservice.controller;


import com.ipmugo.searchservice.dto.ResponseData;
import com.ipmugo.searchservice.service.ArticleService;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<ResponseData<HashMap<String, Object>>> getAllArticle (@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "30", required = false) String size, @RequestParam(value = "query", required = false) String search, @RequestParam(name = "sort", required = false) String sort, @RequestParam(value = "singleYear", required = false) String singleYear,  @RequestParam(value = "rangeYear", required = false) String rangeYear, @RequestParam(value = "publicationName", required = false) String publicationName, @RequestParam(value = "topic", required = false) String topic, @RequestParam(value = "aff", required = false) String aff, @RequestParam(value = "author", required = false) String author, @RequestParam(value = "q", required = false) String query) {
        ResponseData<HashMap<String, Object>> responseData = new ResponseData<>();
        try{

            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size));

            SearchResponse searchResponse = articleService.search(pageable, search, sort, singleYear, rangeYear, publicationName, topic, author, aff, query);

            HashMap<String, Object> response = new HashMap<>();

            Map<String, Aggregation> results = searchResponse.getAggregations()
                    .asMap();

            Map<String, ArrayList<Map<String, Object>>> aggregation = new HashMap<>();
            ArrayList<Map<String, Object>> journalList = new ArrayList<>();

            Terms journalTerms = searchResponse.getAggregations().get("journal");
            for (Terms.Bucket bucket : journalTerms.getBuckets()) {
                String journal = bucket.getKeyAsString();
                long docCount = bucket.getDocCount();
                Map<String, Object> journalAgg = new HashMap<>();
                journalAgg.put("name", journal);
                journalAgg.put("count", docCount);
                journalList.add(journalAgg);
            }

            aggregation.put("journal", journalList);

            ArrayList<Map<String, Object>> yearList = new ArrayList<>();
            Terms yearTerms = searchResponse.getAggregations().get("year");
            for (Terms.Bucket bucket : yearTerms.getBuckets()) {
                String year = bucket.getKeyAsString();
                long docCount = bucket.getDocCount();
                Map<String, Object> yearAgg = new HashMap<>();
                yearAgg.put("list", year);
                yearAgg.put("count", docCount);
                yearList.add(yearAgg);
            }

            aggregation.put("year", yearList);

            ArrayList<Map<String, Object>> keywordList = new ArrayList<>();
            Terms keywordsTerms = searchResponse.getAggregations().get("keywords");
            for (Terms.Bucket bucket : keywordsTerms.getBuckets()) {
                String keyword = bucket.getKeyAsString();
                long docCount = bucket.getDocCount();
                Map<String, Object> keywordAgg = new HashMap<>();
                keywordAgg.put("name", keyword);
                keywordAgg.put("count", docCount);
                keywordList.add(keywordAgg);
            }

            aggregation.put("keyword", keywordList);

            ArrayList<Map<String, Object>> authorList = new ArrayList<>();
            Nested authorsNested = searchResponse.getAggregations().get("authors");
            Terms authorsTerms = authorsNested.getAggregations().get("authors");
            for (Terms.Bucket bucket : authorsTerms.getBuckets()) {
                String authorName = bucket.getKeyAsString();
                long docCount = bucket.getDocCount();
                Map<String, Object> authorAgg = new HashMap<>();
                authorAgg.put("name", authorName);
                authorAgg.put("count", docCount);
                authorList.add(authorAgg);
            }

            aggregation.put("author", authorList);

            ArrayList<Map<String, Object>> affiliationList = new ArrayList<>();
            Terms affiliationTerms = authorsNested.getAggregations().get("affiliation");
            for (Terms.Bucket bucket : affiliationTerms.getBuckets()) {
                String affiliation = bucket.getKeyAsString();
                long docCount = bucket.getDocCount();
                Map<String, Object> affiliationAgg = new HashMap<>();
                affiliationAgg.put("name", affiliation);
                affiliationAgg.put("count", docCount);
                affiliationList.add(affiliationAgg);
            }

            aggregation.put("affiliation", affiliationList);

            response.put("aggregations", aggregation);
            response.put("totalHits", searchResponse.getHits().getTotalHits());
            response.put("content", searchResponse.getHits().getHits());


            responseData.setStatus(true);
            responseData.setData(response);

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}
