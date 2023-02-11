package com.ipmugo.searchservice.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;

    @Value("${spring.elasticsearch.rest.connection-timeout}")
    private long connectionTimeOut;

    @Value("${spring.elasticsearch.rest.read-timeout}")
    private long readTimeOut;

    @Value("${spring.elasticsearch.rest.username}")
    private String username;

    @Value("${spring.elasticsearch.rest.password}")
    private String password;

    @Bean
    RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo(uris)
                        .withBasicAuth(username, password)
                        .withConnectTimeout(connectionTimeOut)
                        .withSocketTimeout(readTimeOut).build();

        return RestClients.create(clientConfiguration).rest();
    }
}
