package com.github.bhlangonijr.chesslib;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CONFIG {
  private Properties properties;

  public CONFIG() {
    properties = new Properties();
    try (InputStream input = new FileInputStream("C:\\Users\\User\\Desktop\\MasterPreper\\MasterConfig.txt")) {
      properties.load(input);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }
}