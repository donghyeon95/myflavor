package com.myflavor.myflavor.common.configuration.elasticsearch;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticSearchConfig {
	@Value("${es.url}")
	private String hostname;

	@Value("${es.port}")
	private int port;

	@Value("${es.username}")
	private String userName;

	@Value("${es.password}")
	private String password;

	@Value("${es.keystore.path}")
	private String keyStorePath;

	@Value("${es.keystore.password}")
	private String keyStorePassword;

	@Bean
	public ElasticsearchClient esClient() throws Exception {
		KeyStore truststore = KeyStore.getInstance("PKCS12");
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(keyStorePath)) {
			truststore.load(is, keyStorePassword.toCharArray());
		}

		SSLContext sslContext = SSLContextBuilder
			.create()
			.loadTrustMaterial(truststore, null)
			.build();

		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostname, port, "https"))
			.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
				.setDefaultCredentialsProvider(credentialsProvider)
				.setSSLContext(sslContext));

		RestClient restClient = restClientBuilder.build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

		return new ElasticsearchClient(transport);
	}

}
