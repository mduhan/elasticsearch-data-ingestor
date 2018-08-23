/**
 * Copyright 2018 Manjeet Duhan.
 *  Manjeet Duhan
 *
 **/
package org.novus.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IngestorErrorController implements ErrorController {

  public static final String PATH = "/error";

  @RequestMapping(value = PATH)
  public String error() {
    return "error";
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }

}

