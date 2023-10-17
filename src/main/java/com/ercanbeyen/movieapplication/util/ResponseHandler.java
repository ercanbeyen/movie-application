package com.ercanbeyen.movieapplication.util;

import com.ercanbeyen.movieapplication.constant.names.FilterNames;
import com.ercanbeyen.movieapplication.constant.names.ParameterNames;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public class ResponseHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new Gson();

    public static ResponseEntity<Object> generateResponse(HttpStatus httpStatus, String message, Object data) {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put(ParameterNames.STATUS, httpStatus);
            response.put(ParameterNames.MESSAGE, message);
            response.put(ParameterNames.DATA, data);
            response.put(ParameterNames.TIMESTAMP, LocalDateTime.now());
        } catch (Exception exception) {
            response.clear();
            response.put(ParameterNames.STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put(ParameterNames.MESSAGE, exception.getMessage());
            response.put(ParameterNames.DATA, null);
            response.put(ParameterNames.TIMESTAMP, LocalDateTime.now());
        }

        return new ResponseEntity<>(response, httpStatus);
    }

    public static Map<String, ?> getSerializedPartialData(Object response, final String ...fields) throws JsonProcessingException {
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter(FilterNames.PARTIAL_RESPONSE_FILTER, SimpleBeanPropertyFilter.filterOutAllExcept(fields));
        objectMapper.setFilterProvider(filterProvider);

        String jsonString = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(response);

        return gson.fromJson(jsonString, Map.class);
    }

    public static Map<String, ?> getFilteredPartialData(Object response, final String ...fields) throws JsonProcessingException {
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter(FilterNames.PARTIAL_RESPONSE_FILTER, SimpleBeanPropertyFilter.serializeAllExcept(fields));
        objectMapper.setFilterProvider(filterProvider);

        String jsonString = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(response);

        return gson.fromJson(jsonString, Map.class);
    }

    public static Map<String, ?> getAllSerializedData(Object response) throws JsonProcessingException {
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter(FilterNames.PARTIAL_RESPONSE_FILTER, SimpleBeanPropertyFilter.serializeAll());
        objectMapper.setFilterProvider(filterProvider);

        String jsonString = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(response);

        return gson.fromJson(jsonString, Map.class);
    }
}
