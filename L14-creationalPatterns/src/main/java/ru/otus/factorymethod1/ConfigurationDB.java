package ru.otus.factorymethod1;

/**
 * @author sergey
 * created on 19.09.18.
 */
public class ConfigurationDB implements Configuration {
  @Override
  public String params() {
    return "params from DB";
  }
}
