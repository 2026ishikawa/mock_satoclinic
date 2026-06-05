package com.example.satoclinic.model.news;

import java.time.LocalDate;

public record NewsArticle(
        String categoryKey,
        String categoryLabel,
        String title,
        LocalDate date,
        String href) {
}
