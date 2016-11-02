package com.bookislife.flow.core.exception;

/**
 * FlowNotFoundException
 *
 * @author SidneyXu
 */
public class FlowNotFoundException extends FlowException {

    public FlowNotFoundException(String message) {
        super(OBJECT_NOT_FOUND, message);
    }
}
