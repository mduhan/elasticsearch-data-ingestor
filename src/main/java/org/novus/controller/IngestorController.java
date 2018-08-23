/**
 * Copyright 2018 Manjeet Duhan.
 *  Manjeet Duhan
 *
 **/
package org.novus.controller;

import java.io.File;
import java.io.IOException;

import org.elasticsearch.client.transport.TransportClient;
import org.novus.es.client.ESClient;
import org.novus.service.IngestorServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class IngestorController {
  private Logger log = LoggerFactory.getLogger(getClass());

  @Value("${upload.dir}")
  private String uploadDir;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private IngestorServiceImpl ingestorService;
  @RequestMapping("/")
  public String home(Model model) {
    return "home";
  }

  @RequestMapping(value = "/csv", method = RequestMethod.GET)
  public String uploadingCsv(Model model) {
    File file = new File(uploadDir);
    model.addAttribute("files", file.listFiles());
    return "uploadingcsv";
  }

  @RequestMapping(value = "/json", method = RequestMethod.GET)
  public String uploadingJson(Model model) {
    File file = new File(uploadDir);
    model.addAttribute("files", file.listFiles());
    return "uploadingjson";
  }

  /**
   * 
   * This uses the json passed to populate random predefined values and generate ES schema specific json to
   * feed into ES and ingest it into ES
   * 
   * @param esUrl
   * @param clusterName
   * @param index
   * @param type
   * @param mapping
   * @param sampleJson
   * @param startIndex
   * @param sampleSize
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/json", method = RequestMethod.POST)
  public String uploadingJsonPost(@RequestParam("esUrl") String esUrl,
      @RequestParam(value = "clusterName") String clusterName, @RequestParam(value = "index") String index,
      @RequestParam(value = "type") String type, @RequestParam(value = "mapping") String mapping,
      @RequestParam(value = "json") String sampleJson,
      @RequestParam(value = "startIndex", required = false) int startIndex,
      @RequestParam(value = "sampleSize", required = false) int sampleSize) throws IOException {
    TransportClient client = ESClient.createEsClient(clusterName, esUrl, index, mapping, restTemplate);
    ingestorService.jsonGenerator(client, sampleJson, startIndex, sampleSize, index, type);
    
    return "redirect:/";
  }

  /**
   * controller to upload csv file and ingest data into es
   * 
   * @param uploadingFiles
   * @param esUrl
   * @param clusterName
   * @param index
   * @param type
   * @param mapping
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/csv", method = RequestMethod.POST)
  public String uploadingCsvPost(@RequestParam("uploadingFiles") MultipartFile[] uploadingFiles,
      @RequestParam("esUrl") String esUrl, @RequestParam(value = "clusterName") String clusterName,
      @RequestParam(value = "index") String index, @RequestParam(value = "type") String type,
      @RequestParam(value = "mapping") String mapping) throws IOException {
    TransportClient client = ESClient.createEsClient(clusterName, esUrl, index, mapping, restTemplate);
    ingestorService.csvLoader(index, type, client, uploadingFiles);
    return "redirect:/";
  }
}

