package com.ercanbeyen.movieapplication.constant.enums;

public enum OrderBy {
    ASC("ascending"),
    DESC("descending");

    private final String label;

    OrderBy(String label) {
        this.label = label;
    }

    public String getOrderByInfo() {
        return label;
    }
}
