/**
 * Copyright 2018 Manjeet Duhan.
 *  Manjeet Duhan
 *
 **/
package org.novus;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { CassandraDataAutoConfiguration.class, MongoDataAutoConfiguration.class,
    MongoRepositoriesAutoConfiguration.class, MongoAutoConfiguration.class })
public class ESDataIngestorApplication {

  public static void main(String[] args) throws IOException {
    SpringApplication.run(ESDataIngestorApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setReadTimeout(60000);
    requestFactory.setConnectTimeout(10000);
    return new RestTemplate(requestFactory);
  }

}