package com.bookislife.flow.server.utils;

import com.bookislife.flow.core.domain.BaseEntity;
import com.bookislife.flow.core.exception.FlowException;
import com.bookislife.flow.core.utils.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * ResponseCreator
 *
 * @author SidneyXu
 */
public class ResponseCreator {

    public static final String FIELD_COUNT = "count";
    public static final String FIELD_ERROR_CODE = "error_code";
    public static final String FIELD_ERROR_MESSAGE = "error_message";
    public static final String FIELD_DATA = "data";

    public static String newCreateResponse(BaseEntity entity) {
        return JacksonJsonBuilder.create()
                .put(BaseEntity.FIELD_ID, entity.getId())
                .put(BaseEntity.FIELD_CREATED_AT, entity.getCreatedAt())
                .build();
    }

    public static String newDeleteResponse(int n) throws FlowException {
        if (n == 0) {
            throw new FlowException(FlowException.OBJECT_NOT_FOUND, "Object not found for deleting.");
        }
        return JacksonJsonBuilder.create()
                .put(FIELD_COUNT, n)
                .build();
    }

    public static String newUpdateResponse(Pair<Integer, Long> pair) throws FlowException {
        if (pair.second == 0) {
            throw new FlowException(FlowException.OBJECT_NOT_FOUND, "Object not found for updating.");
        }
        return JacksonJsonBuilder.create()
                .put(FIELD_COUNT, pair.first)
                .put(BaseEntity.FIELD_UPDATED_AT, pair.second)
                .build();
    }

    public static String newCountResponse(long n) {
        return JacksonJsonBuilder.create()
                .put(FIELD_COUNT, n)
                .build();
    }

    public static String newQueryResponse(BaseEntity entity) throws FlowException {
        if (entity == null) {
            throw new FlowException(FlowException.OBJECT_NOT_FOUND, "Object not found.");
        }
        return JacksonJsonBuilder.create()
                .put(BaseEntity.FIELD_DATA, entity.getData())
                .put(FIELD_COUNT, 1)
                .build();
    }

    public static String newQueryResponse(List<? extends BaseEntity> entities) throws FlowException {
        if (null == entities || entities.isEmpty()) {
            throw new FlowException(FlowException.OBJECT_NOT_FOUND, "Object not found.");
        } else {
            Object[] arrays = entities.stream().map(o ->
                    new HashMap<String, Object>() {{
                        put(BaseEntity.FIELD_DATA, o.getData());
                    }}).toArray();
            return JacksonJsonBuilder.create()
                    .put(FIELD_COUNT, entities.size())
                    .put(FIELD_DATA, arrays)
                    .build();
        }
    }

    public static String newErrorResponse(int code, String message) {
        return JacksonJsonBuilder.create()
                .put(FIELD_ERROR_CODE, code)
                .put(FIELD_ERROR_MESSAGE, message)
                .build();
    }

    public static String newErrorResponse(String message) {
        return JacksonJsonBuilder.create()
                .put(FIELD_ERROR_CODE, FlowException.UNKNOWN_ERROR)
                .put(FIELD_ERROR_MESSAGE, message)
                .build();
    }

    public static String newErrorResponse(Throwable cause) {
        return JacksonJsonBuilder.create()
                .put(FIELD_ERROR_CODE, FlowException.UNKNOWN_ERROR)
                .put(FIELD_ERROR_MESSAGE, String.format("%s : %s",
                        cause.getClass().getName(),
                        cause.getMessage()))
                .build();
    }


    public static String newErrorResponse(FlowException e) {
        return JacksonJsonBuilder.create()
                .put(FIELD_ERROR_CODE, e.errorCode)
                .put(FIELD_ERROR_MESSAGE, e.errorMessage)
                .build();
    }

}
