package com.example.satoclinic.model.news;

public record NewsCategoryView(
        String key,
        String label,
        long count,
        String href,
        boolean current) {
}
