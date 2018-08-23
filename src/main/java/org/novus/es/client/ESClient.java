/**
 * Copyright 2018 Manjeet Duhan.
 *  Manjeet Duhan
 *
 **/
package org.novus.es.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.web.client.RestTemplate;

public class ESClient {

  /**
   * 
   * This api will create transport client based on user cluster information
   * 
   * @param clusterName
   * @param host
   * @param index
   * @param mapping
   * @param restTemplate
   * @return
   * @throws IOException
   */
  public static TransportClient createEsClient(String clusterName, String host, String index, String mapping,
      RestTemplate restTemplate) throws IOException {
    Settings settings = Settings.builder().put("cluster.name", clusterName).build();
    TransportClient client = new PreBuiltTransportClient(settings);
    try {
      TransportAddress address = new TransportAddress(new InetSocketAddress(InetAddress.getByName(host), 9300));
      client.addTransportAddress(address);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    if (!StringUtils.isEmpty(mapping)) {
      loadSchema(index, client, mapping);
    }
    return client;
  }

  /**
   * Creates an index for the tenant name or index name inputed.
   *
   * @param indexName
   *          String
   * @param client
   *          TODO
   * @throws IOException
   */
  public static void loadSchema(final String indexName, TransportClient client, String schema)
      throws IOException {
    IndicesExistsResponse res = null;
    res = client.admin().indices().prepareExists(indexName).execute().actionGet();
    if (!res.isExists()) {
      final Settings indexSettings = Settings.builder().put("number_of_shards", 1).build();
      final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
      createIndexRequestBuilder.setSource(schema, XContentType.JSON).setSettings(indexSettings);
      createIndexRequestBuilder.execute().actionGet();
      client.admin().indices().prepareFlush().setForce(true).setWaitIfOngoing(true).execute().actionGet();

    }

  }

}
