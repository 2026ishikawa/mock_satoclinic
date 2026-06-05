package com.example.satoclinic.model.news;

public record NewsArchiveView(
        Integer year,
        String label,
        long count,
        String href,
        boolean current) {
}
