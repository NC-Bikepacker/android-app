package ru.netcracker.bikepacker.sorting;

import java.util.HashMap;
import java.util.Map;

public enum TypeOfSorting {
    BEST_CHOICE, BY_DISTANCE, BY_COMPLEXITY, BY_TIME;
    public static Map<Integer,TypeOfSorting> integerTypeOfSortingMap() {
        Map<Integer,TypeOfSorting> map = new HashMap<Integer, TypeOfSorting>();
        map.put(1,TypeOfSorting.BEST_CHOICE);
        map.put(2,TypeOfSorting.BY_DISTANCE);
        map.put(3,TypeOfSorting.BY_COMPLEXITY);
        map.put(4,TypeOfSorting.BY_TIME);
        return map;
    }
}
