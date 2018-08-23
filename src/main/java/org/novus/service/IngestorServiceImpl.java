/**
 * Copyright 2018 Manjeet Duhan.
 *  Manjeet Duhan
 *
 **/
package org.novus.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vincentrussell.json.datagenerator.JsonDataGenerator;
import com.github.vincentrussell.json.datagenerator.impl.JsonDataGeneratorImpl;

@Service("ingestorService")
public class IngestorServiceImpl {

  private Logger log = LoggerFactory.getLogger(getClass());

  @Value("${upload.dir}")
  private String uploadDir;

  /**
   * This uses the json passed to populate random predefined values and generate ES schema specific json to
   * feed into ES.
   * 
   * @param client
   * @param uploadingFiles
   * @param startIndex
   * @param sampleSize
   * @throws IllegalStateException
   * @throws IOException
   */
  public void jsonGenerator(TransportClient client, String sampleJson, int startIndex, int sampleSize, String index,
      String type) throws IllegalStateException, IOException {
    int endIndex = startIndex + sampleSize;
    BulkRequestBuilder bulkRequest = client.prepareBulk();
    JsonDataGenerator jsonDataGenerator = new JsonDataGeneratorImpl();
    IntStream.range(startIndex, endIndex).parallel().boxed().forEach(count -> {
      try {
        try (InputStream inputStream = new ByteArrayInputStream(sampleJson.getBytes())) {
          ByteArrayOutputStream bis = new ByteArrayOutputStream();
          jsonDataGenerator.generateTestDataJson(inputStream, bis);
          if (System.getProperty("debug", "false").equals("true")) {
            log.debug(bis.toString());
            log.debug("==========");
          }

          String json = bis.toString();
          bulkRequest.add(client.prepareIndex(index, type, count.toString()).setSource(json));
          bis.close();
        } catch (Exception ex) {
          log.error("Error while generating json and feeding to ES", ex);
        }

      } catch (Exception ex) {
        log.error("Error while generating json and feeding to ES", ex);
      }
    });
    bulkRequest.setRefreshPolicy("true");
    final BulkResponse bulkResponse = bulkRequest.execute().actionGet();
    if (bulkResponse.hasFailures()) {
      log.error("============================== failures " + bulkResponse.buildFailureMessage());
    }

  }

  /**
   * This method parses CSV and creates a json out of it and feeds to ES CSV Should follow same order for
   * columns - As this is for Demo, there is no validation handled.
   * 
   * @param client
   * @param uploadingFiles
   * @throws IllegalStateException
   * @throws IOException
   */
  public void csvLoader(String index, String type, TransportClient client, MultipartFile[] uploadingFiles)
      throws IllegalStateException, IOException {
    BulkRequestBuilder bulkRequest = client.prepareBulk();
    for (MultipartFile uploadedFile : uploadingFiles) {
      File file = new File(uploadDir + File.separator + uploadedFile.getOriginalFilename());
      if (!file.getName().endsWith("csv")) {
        throw new IllegalArgumentException("Invalid csv");
      }

      uploadedFile.transferTo(file);
      String line;
      int lineNumber = 1;
      String delimiter = ",";
      boolean isHeader = true;
      Map<Integer, Object> headers = new HashMap<>();
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        while ((line = br.readLine()) != null) {
          String[] tokens = line.split(delimiter);
          Map<String, String> values = new HashMap<>();

          if (isHeader) {
            for (int i = 0; i < tokens.length; i++) {
              headers.put(i, tokens[i]);
            }
            isHeader = false;
          } else {
            for (int i = 0; i < tokens.length; i++) {
              try {
                values.put(headers.get(i).toString(), tokens[i]);
              } catch (Exception ex) {
                System.out.println(" Please check line number " + lineNumber
                    + " for comma or any ther format error with data " + tokens);
                // ex.printStackTrace();
              }

            }
          }

          // System.out.println(line);

          if (!CollectionUtils.isEmpty(values)) {

            String json = new ObjectMapper().writeValueAsString(values);
            bulkRequest.add(client.prepareIndex(index, type, tokens[0].toString()).setSource(json, XContentType.JSON));
          }
          lineNumber++;
          if ((lineNumber % 5000) == 0) {
            System.out.println("executing bulk updates lineNumber " + lineNumber);
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (bulkResponse.hasFailures()) {
              System.out.println(bulkResponse.buildFailureMessage());
              log.error("============================== failures");
            }
            bulkRequest = client.prepareBulk();
          }
        }
        bulkRequest.setRefreshPolicy("true");
        final BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
          log.error("============================== failures");
        }

      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
