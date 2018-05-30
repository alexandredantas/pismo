package com.pismo.transactions.util;

import java.nio.charset.Charset;

import org.springframework.http.MediaType;

public class TestUtils {

  public static MediaType JSON_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
      MediaType.APPLICATION_JSON.getSubtype(),
      Charset.forName("utf8"));
}
