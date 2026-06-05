package com.example.satoclinic.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.satoclinic.model.news.NewsArchiveView;
import com.example.satoclinic.model.news.NewsArticle;
import com.example.satoclinic.model.news.NewsCategoryView;
import com.example.satoclinic.model.news.NewsListResult;
import com.example.satoclinic.model.news.NewsPageLink;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NewsService {

    private static final String ALL_CATEGORY = "all";
    private static final String ALL_LABEL = "すべて";
    private static final List<NewsCategoryDefinition> NEWS_CATEGORY_DEFINITIONS = List.of(
            new NewsCategoryDefinition(ALL_CATEGORY, ALL_LABEL),
            new NewsCategoryDefinition("news", "お知らせ"),
            new NewsCategoryDefinition("urgent", "緊急"),
            new NewsCategoryDefinition("closed", "休診"),
            new NewsCategoryDefinition("column", "コラム"));

    private final List<NewsArticle> newsArticles;

    public NewsService(
            ObjectMapper objectMapper,
            @Value("classpath:data/news-articles.json") Resource newsArticlesResource) {
        this.newsArticles = loadNewsArticles(objectMapper, newsArticlesResource);
    }

    public List<NewsArticle> getLatestPublished(int limit) {
        return newsArticles.stream()
                .limit(limit)
                .toList();
    }

    public NewsListResult getNewsList(String category, Integer year, int page, int pageSize) {
        String normalizedCategory = normalizeCategory(category);
        Integer normalizedYear = normalizeYear(year);
        List<NewsArticle> filteredArticles = filterArticles(normalizedCategory, normalizedYear);
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredArticles.size() / pageSize));
        int currentPage = Math.min(Math.max(page, 1), totalPages);
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredArticles.size());

        return new NewsListResult(
                filteredArticles.subList(startIndex, endIndex),
                normalizedCategory,
                categoryLabel(normalizedCategory),
                normalizedYear,
                archiveLabel(normalizedYear),
                buildCategoryOptions(normalizedCategory, normalizedYear),
                buildArchiveOptions(normalizedCategory, normalizedYear),
                buildPageLinks(normalizedCategory, normalizedYear, totalPages, currentPage),
                currentPage > 1,
                currentPage < totalPages,
                buildNewsHref(normalizedCategory, normalizedYear, currentPage - 1),
                buildNewsHref(normalizedCategory, normalizedYear, currentPage + 1),
                currentPage,
                totalPages);
    }

    public String buildLegacyQuery(String category, Integer year, int page) {
        return buildNewsHref(normalizeCategory(category), normalizeYear(year), page).replaceFirst("^/news", "");
    }

    private List<NewsArticle> loadNewsArticles(ObjectMapper objectMapper, Resource newsArticlesResource) {
        try (InputStream inputStream = newsArticlesResource.getInputStream()) {
            return List.copyOf(objectMapper.readValue(inputStream, new TypeReference<List<NewsArticle>>() {
            }));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load news articles from JSON.", exception);
        }
    }

    private List<NewsArticle> filterArticles(String category, Integer year) {
        return newsArticles.stream()
                .filter(article -> matchesCategory(article, category))
                .filter(article -> matchesArchiveYear(article, year))
                .toList();
    }

    private List<NewsCategoryView> buildCategoryOptions(String currentCategory, Integer currentYear) {
        List<NewsCategoryView> categoryOptions = new ArrayList<>();
        for (NewsCategoryDefinition definition : NEWS_CATEGORY_DEFINITIONS) {
            long count = newsArticles.stream()
                    .filter(article -> matchesCategory(article, definition.key()))
                    .filter(article -> matchesArchiveYear(article, currentYear))
                    .count();
            categoryOptions.add(new NewsCategoryView(
                    definition.key(),
                    definition.label(),
                    count,
                    buildNewsHref(definition.key(), currentYear, 1),
                    definition.key().equals(currentCategory)));
        }
        return categoryOptions;
    }

    private List<NewsArchiveView> buildArchiveOptions(String currentCategory, Integer currentYear) {
        List<NewsArchiveView> archiveOptions = new ArrayList<>();
        long allCount = newsArticles.stream()
                .filter(article -> matchesCategory(article, currentCategory))
                .count();
        archiveOptions.add(new NewsArchiveView(
                null,
                ALL_LABEL,
                allCount,
                buildNewsHref(currentCategory, null, 1),
                currentYear == null));

        newsArticles.stream()
                .map(article -> article.date().getYear())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .forEach(year -> {
                    long count = newsArticles.stream()
                            .filter(article -> matchesCategory(article, currentCategory))
                            .filter(article -> matchesArchiveYear(article, year))
                            .count();
                    archiveOptions.add(new NewsArchiveView(
                            year,
                            year + "年",
                            count,
                            buildNewsHref(currentCategory, year, 1),
                            Integer.valueOf(year).equals(currentYear)));
                });
        return archiveOptions;
    }

    private List<NewsPageLink> buildPageLinks(String category, Integer year, int totalPages, int currentPage) {
        return IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> new NewsPageLink(
                        page,
                        buildNewsHref(category, year, page),
                        page == currentPage))
                .toList();
    }

    private String buildNewsHref(String category, Integer year, int page) {
        List<String> queryParts = new ArrayList<>();
        if (!ALL_CATEGORY.equals(category)) {
            queryParts.add("category=" + category);
        }
        if (year != null) {
            queryParts.add("year=" + year);
        }
        if (page > 1) {
            queryParts.add("page=" + page);
        }
        if (queryParts.isEmpty()) {
            return "/news";
        }
        return "/news?" + String.join("&", queryParts);
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return ALL_CATEGORY;
        }
        return NEWS_CATEGORY_DEFINITIONS.stream()
                .map(NewsCategoryDefinition::key)
                .filter(category::equals)
                .findFirst()
                .orElse(ALL_CATEGORY);
    }

    private Integer normalizeYear(Integer year) {
        if (year == null) {
            return null;
        }
        return newsArticles.stream()
                .map(article -> article.date().getYear())
                .filter(articleYear -> articleYear.equals(year))
                .findFirst()
                .orElse(null);
    }

    private String categoryLabel(String category) {
        return NEWS_CATEGORY_DEFINITIONS.stream()
                .filter(definition -> definition.key().equals(category))
                .findFirst()
                .map(NewsCategoryDefinition::label)
                .orElse(ALL_LABEL);
    }

    private String archiveLabel(Integer year) {
        return year == null ? ALL_LABEL : year + "年";
    }

    private boolean matchesCategory(NewsArticle article, String category) {
        return ALL_CATEGORY.equals(category) || article.categoryKey().equals(category);
    }

    private boolean matchesArchiveYear(NewsArticle article, Integer year) {
        return year == null || article.date().getYear() == year;
    }

    private record NewsCategoryDefinition(String key, String label) {
    }
}
