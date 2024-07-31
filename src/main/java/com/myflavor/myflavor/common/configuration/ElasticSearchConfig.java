package com.myflavor.myflavor.common.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
  @Value("${es.url}")
  private String hostname;

  @Value("${es.port}")
  private int port;

  @Bean
  public ElasticsearchClient restClient() {
    RestClient restClient = RestClient.builder(HttpHost.create("http://" + hostname + ":" + port)).build();
    ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

    return new ElasticsearchClient(transport);
  }

}
