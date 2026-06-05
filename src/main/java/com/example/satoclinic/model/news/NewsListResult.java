package com.example.satoclinic.model.news;

import java.util.List;

public record NewsListResult(
        List<NewsArticle> newsItems,
        String currentCategory,
        String currentCategoryLabel,
        Integer currentArchiveYear,
        String currentArchiveLabel,
        List<NewsCategoryView> categoryOptions,
        List<NewsArchiveView> archiveOptions,
        List<NewsPageLink> pageLinks,
        boolean hasPreviousPage,
        boolean hasNextPage,
        String previousPageHref,
        String nextPageHref,
        int currentPageNumber,
        int totalPages) {
}
