package com.ipmugo.searchservice.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String uris;
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration client = ClientConfiguration.builder()
                .connectedTo(uris)
                .withConnectTimeout(600000)
                .build();

        return RestClients.create(client).rest();
    }
}