package com.bookislife.flow.core.utils;

/**
 * JsonBuilder
 * <p>
 * Util interface to build a Json Object.
 *
 * @author SidneyXu
 */
public interface JsonBuilder {

    String build();

    JsonBuilder put(String key, Object object);

    JsonBuilder putIfAbsent(String key, Object object);

    JsonBuilder putIfNotEmpty(String key, Object object);

    JsonBuilder putString(String key, String value);

    JsonBuilder putNumber(String key, Number number);

}
