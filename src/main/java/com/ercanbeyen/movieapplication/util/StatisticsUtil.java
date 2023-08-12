package com.ercanbeyen.movieapplication.util;

import com.ercanbeyen.movieapplication.constant.message.StatisticsMessages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsUtil {
    private static <T> Map<T, Integer> calculateOccurrenceMap(List<T> itemList) {
        Map<T, Integer> occurrenceMap = new HashMap<>();

        for (T item : itemList) {
            if (!occurrenceMap.containsKey(item)) {
                occurrenceMap.put(item, 0);
            } else {
                Integer occurrence = occurrenceMap.get(item);
                occurrence++;
                occurrenceMap.put(item, occurrence);
            }
        }

        return occurrenceMap;
    }

    public static <T> T calculateMostOccurred(List<T> itemList) {
        Map<T, Integer> occurrenceMap = calculateOccurrenceMap(itemList);

        T maximumItem = null;
        Integer maximumOccurrence = 0;

        for (T item : occurrenceMap.keySet()) {
            if (occurrenceMap.get(item) > maximumOccurrence) {
                maximumItem = item;
                maximumOccurrence = occurrenceMap.get(item);
            }
        }

        return maximumItem;
    }

    public static <T> T calculateLeastOccurred(List<T> itemList) {
        Map<T, Integer> occurrenceMap = calculateOccurrenceMap(itemList);

        T minimumItem = null;
        Integer minimumOccurrence = Integer.MAX_VALUE;

        for (T item : occurrenceMap.keySet()) {
            if (occurrenceMap.get(item) < minimumOccurrence) {
                minimumItem = item;
                minimumOccurrence = occurrenceMap.get(item);
            }
        }

        return minimumItem;
    }

    public static String valueAssignmentToStringItem(String returnedValue) {
        return returnedValue != null ? returnedValue : StatisticsMessages.NOT_EXISTS;
    }
}
