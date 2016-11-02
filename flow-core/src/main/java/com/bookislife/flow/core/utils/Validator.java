package com.bookislife.flow.core.utils;

import com.bookislife.flow.core.exception.FlowNotFoundException;

import java.util.Iterator;

/**
 * Validator
 * <p>
 * Util to validate the input.
 *
 * @author SidneyXu
 */
public class Validator {

    public static void assertNotNull(Object object, String message) {
        if (null == object)
            throw new IllegalArgumentException(message);
    }

    public static void assertHasNext(Iterator<?> iterator, String message) throws FlowNotFoundException {
        if (!iterator.hasNext())
            throw new FlowNotFoundException(message);
    }
}
