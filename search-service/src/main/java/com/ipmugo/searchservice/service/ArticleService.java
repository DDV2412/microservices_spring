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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


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
            Optional<Article> article1 = articleRepository.findById(article.getId());

            if(article1.isEmpty()){
                articleRepository.save(article);
            }else{
                Article update = Article.builder()
                        .id(article.getId())
                        .journal(article.getJournal())
                        .ojsId(article.getOjsId())
                        .title(article.getTitle())
                        .pages(article.getPages())
                        .publishYear(article.getPublishYear())
                        .lastModifier(article.getLastModifier())
                        .publishDate(article.getPublishDate())
                        .doi(article.getDoi())
                        .volume(article.getVolume())
                        .issue(article.getIssue())
                        .publishStatus(article.getPublishStatus())
                        .abstractText(article.getAbstractText())
                        .articlePdf(article.getArticlePdf())
                        .keywords(article.getKeywords())
                        .authors(article.getAuthors())
                        .citationByScopus(article.getCitationByScopus())
                        .citationByCrossRef(article.getCitationByCrossRef())
                        .figures(article.getFigures())
                        .viewsCount(article.getViewsCount())
                        .downloadCount(article.getDownloadCount())
                        .build();

                articleRepository.save(update);
            }


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public void updateCounter(Article article) throws CustomException{
        try{
            Optional<Article> article1 = articleRepository.findById(article.getId());

            if(article1.isPresent()){
                Article update = Article.builder()
                        .id(article.getId())
                        .citationByScopus(article.getCitationByScopus())
                        .citationByCrossRef(article.getCitationByCrossRef())
                        .viewsCount(article.getViewsCount())
                        .downloadCount(article.getDownloadCount())
                        .build();

                articleRepository.save(update);
            }


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public SearchResponse search(Pageable pageable, String searchTerm, String sort, String singleYear, String rangeYear, String publicationName, String topic, String author, String aff, String query) throws CustomException{
        try{
            SearchRequest searchRequest = new SearchRequest(Article.INDEX);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            if (StringUtils.isNotBlank(query)) {
                boolQueryBuilder.should(QueryBuilders.queryStringQuery(query));
            }else if(StringUtils.isNotBlank(searchTerm)){
                boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchTerm + "~"));
                boolQueryBuilder.should(QueryBuilders.matchQuery("abstractText", searchTerm + "~"));
                boolQueryBuilder.should(QueryBuilders.matchQuery("keywords.name", searchTerm + "~"));
            }else{
                boolQueryBuilder.should(QueryBuilders.matchAllQuery());
            }

            /**
             * Filter
             * */

            if(singleYear != null && !StringUtils.isBlank(singleYear)){
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishYear").gte(singleYear).lte(singleYear));
            }

            if(rangeYear != null && !StringUtils.isBlank(rangeYear)){
                String[] yearRange = rangeYear.split("-");
                String startYear = yearRange[0];
                String endYear = yearRange[1];
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishYear").gte(startYear).lte(endYear));
            }

            if(publicationName != null && !StringUtils.isBlank(publicationName)){
                boolQueryBuilder.filter(QueryBuilders.matchQuery("journal.name", publicationName));
            }

            if(topic != null && !StringUtils.isBlank(topic)){
                boolQueryBuilder.filter(QueryBuilders.matchQuery("keywords.name", topic));
            }

            if(author != null && !StringUtils.isBlank(author)){
                String[] authorParams = author.split(", ");
                String firstName = authorParams[1];
                String lastName = authorParams[0];

                boolQueryBuilder.filter(
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.matchQuery("authors.firstName", firstName))
                                .must(QueryBuilders.matchQuery("authors.lastName", lastName))
                );
            }

            if(aff != null && !StringUtils.isBlank(aff)){
                boolQueryBuilder.filter(QueryBuilders.matchQuery("authors.affiliation", aff));
            }

            if(sort != null && !StringUtils.isBlank(sort)){
                if(sort.equals("publishDate:desc")){
                    searchSourceBuilder.sort(new FieldSortBuilder("publishDate").order(SortOrder.DESC));
                }else if(sort.equals("publishDate:asc")){
                    searchSourceBuilder.sort(new FieldSortBuilder("publishDate").order(SortOrder.ASC));
                }else if(sort.equals("publicationTitle:desc")){
                    searchSourceBuilder.sort(new FieldSortBuilder("title").order(SortOrder.DESC));
                }else if(sort.equals("publicationTitle:asc")){
                    searchSourceBuilder.sort(new FieldSortBuilder("title").order(SortOrder.ASC));
                }else if(sort.equals("popularity:desc")){
                    searchSourceBuilder.sort(new FieldSortBuilder("viewsCount").order(SortOrder.DESC));
                }else if(sort.equals("popularity:asc")){
                    searchSourceBuilder.sort(new FieldSortBuilder("viewsCount").order(SortOrder.ASC));
                }
            }else{
                searchSourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
            }

            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.from(pageable.getPageNumber()).size(pageable.getPageSize());
            searchSourceBuilder.trackTotalHits(true);

            searchSourceBuilder.aggregation(
                    AggregationBuilders.terms("journal").field("journal.name")
            );
            searchSourceBuilder.aggregation(
                    AggregationBuilders.terms("year").field("publishYear")
            );
            searchSourceBuilder.aggregation(
                    AggregationBuilders.terms("keywords").field("keywords.name")
            );

            searchSourceBuilder.aggregation(
                    AggregationBuilders.nested("authors", "authors")
                            .subAggregation(
                                    AggregationBuilders.terms("authors")
                                            .script(
                                                    new Script(
                                                            "doc['authors.lastName'].value + ', '+ doc['authors.firstName'].value"
                                                    )
                                            )
                            )
                            .subAggregation(
                                    AggregationBuilders.terms("affiliation").field("authors.affiliation")
                            )
            );


            searchSourceBuilder.from(pageable.getPageNumber());
            searchSourceBuilder.size(pageable.getPageSize());
            searchRequest.source(searchSourceBuilder);

            return client.search(searchRequest, RequestOptions.DEFAULT);


        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


}
