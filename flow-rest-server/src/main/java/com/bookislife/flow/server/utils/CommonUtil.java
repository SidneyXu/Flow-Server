package com.bookislife.flow.server.utils;

/**
 * Created by SidneyXu on 2016/11/09.
 */
public class CommonUtil {

    public static String wrapWith(String text, String signal) {
        String result = text;
        if (!text.endsWith(signal)) {
            result = result + signal;
        }
        if (!text.startsWith(signal)) {
            result = signal + result;
        }
        return result;
    }

    public static String addPrefix(String text, String signal) {
        String result = text;
        if (!text.startsWith(signal)) {
            result = signal + result;
        }
        return result;
    }
}
