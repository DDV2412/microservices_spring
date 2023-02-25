package com.ipmugo.searchservice.service;

import com.ipmugo.searchservice.model.Article;
import com.ipmugo.searchservice.repository.ArticleRepository;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private RestHighLevelClient client;


    public void saveArticle(Article article) throws CustomException{
        try{
            articleRepository.save(article);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public SearchResponse search(Pageable pageable, String searchTerm, String advancedQuery, String filter) throws CustomException{
        try{
            SearchRequest searchRequest = new SearchRequest(Article.INDEX);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            if(searchTerm == null) {
                boolQueryBuilder.should(QueryBuilders.matchAllQuery());
            }else if(StringUtils.isBlank(searchTerm)) {
                boolQueryBuilder.should(QueryBuilders.matchAllQuery());
            }else if(advancedQuery != null && StringUtils.isBlank(advancedQuery)){
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(advancedQuery));
            }else{
                boolQueryBuilder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("title", searchTerm))
                        .should(QueryBuilders.termQuery("abstractText", searchTerm));
            }

            if (filter != null && !filter.isEmpty()) {
                String[] filterParams = filter.split(",");
                String filterType = filterParams[0];
                String filterValue = filterParams[1];

                // Cek tipe filter yang diminta
                switch (filterType) {
                    case "single":
                        // Filter berdasarkan satu tahun
                        boolQueryBuilder.filter(QueryBuilders.rangeQuery("date").gte(filter).lte(filter));
                        break;
                    case "range":
                        // Filter berdasarkan rentang tahun
                        String[] yearRange = filter.split("-");
                        String startYear = yearRange[0];
                        String endYear = yearRange[1];
                        boolQueryBuilder.filter(QueryBuilders.rangeQuery("date").gte(startYear).lte(endYear));
                        break;
                    case "journal.name":
                        // Filter berdasarkan nama jurnal
                        boolQueryBuilder.filter(QueryBuilders.matchQuery("journal.name", filter));
                        break;
                    case "keywords":
                        // Filter berdasarkan keywords
                        boolQueryBuilder.filter(QueryBuilders.matchQuery("keywords.name", filter));
                        break;
                    case "authors":
                        String[] authorParams = filter.split("\\+");
                        String firstName = authorParams[0];
                        String lastName = authorParams[1];
                        // Filter berdasarkan nama dan affiliasi author
                        boolQueryBuilder.filter(
                                QueryBuilders.boolQuery()
                                        .must(QueryBuilders.matchQuery("authors.firstName", firstName))
                                        .must(QueryBuilders.matchQuery("authors.lastName", lastName))
                        );
                        break;
                    case "affiliation":
                        // Filter berdasarkan affiliation
                        boolQueryBuilder.filter(QueryBuilders.matchQuery("authors.affiliation", filter));
                        break;
                    default:
                        break;
                }
            }

            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.from(pageable.getPageNumber()).size(pageable.getPageSize());
            searchSourceBuilder.trackTotalHits(true);

            searchSourceBuilder.aggregation(AggregationBuilders.terms("journal").field("journal.name"));
            searchSourceBuilder.aggregation(AggregationBuilders.terms("year").field("publishYear"));
            searchSourceBuilder.aggregation(AggregationBuilders.nested("keywords", "keywords")
                    .subAggregation(AggregationBuilders.terms("keyword_name_agg").field("keywords.name")));
            searchSourceBuilder.aggregation(AggregationBuilders.nested("authors_agg", "authors")
                    .subAggregation(AggregationBuilders.terms("author_name_agg")
                            .script(new Script(ScriptType.INLINE, "painless", "doc['authors.firstName'].value + ' ' + doc['authors.lastName'].value", Collections.emptyMap()))));
            searchSourceBuilder.aggregation(AggregationBuilders.nested("authors", "authors")
                    .subAggregation(AggregationBuilders.terms("authorAffiliation").field("authors.affiliation.keyword")));

            pageable.getSort().forEach(sortField -> {
                FieldSortBuilder sortBuilder = SortBuilders.fieldSort(sortField.getProperty())
                        .order(sortField.getDirection().isDescending() ? SortOrder.DESC : SortOrder.ASC);
                searchSourceBuilder.sort(sortBuilder);
            });

            searchRequest.source(searchSourceBuilder);

            return client.search(searchRequest, RequestOptions.DEFAULT);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


}
