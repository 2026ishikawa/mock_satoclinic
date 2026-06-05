package com.example.satoclinic.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private static final int NEWS_PAGE_SIZE = 5;

    private static final List<NewsArticle> NEWS_ARTICLES = List.of(
            new NewsArticle("news", "お知らせ", "インフルエンザ予防接種について", LocalDate.of(2026, 1, 28), "#"),
            new NewsArticle("news", "お知らせ", "年末年始の休診について", LocalDate.of(2026, 1, 25), "#"),
            new NewsArticle("news", "お知らせ", "健康診断のご案内", LocalDate.of(2026, 1, 20), "#"),
            new NewsArticle("news", "お知らせ", "新型コロナウイルス対策について", LocalDate.of(2026, 1, 18), "#"),
            new NewsArticle("news", "お知らせ", "オンライン診療の導入について", LocalDate.of(2026, 1, 15), "#"),
            new NewsArticle("column", "コラム", "花粉症シーズン前の受診について", LocalDate.of(2026, 1, 12), "#"),
            new NewsArticle("closed", "休診", "2月臨時休診日のお知らせ", LocalDate.of(2026, 1, 10), "#"),
            new NewsArticle("news", "お知らせ", "発熱外来の受付時間について", LocalDate.of(2026, 1, 8), "#"),
            new NewsArticle("urgent", "緊急", "院内設備点検に伴う一時停電のお知らせ", LocalDate.of(2026, 1, 6), "#"),
            new NewsArticle("column", "コラム", "年内最終診療日のお知らせ", LocalDate.of(2025, 12, 26), "#"),
            new NewsArticle("closed", "休診", "1月祝日診療の休診案内", LocalDate.of(2025, 12, 22), "#"),
            new NewsArticle("column", "コラム", "冬の乾燥対策と体調管理", LocalDate.of(2025, 12, 18), "#"),
            new NewsArticle("urgent", "緊急", "感染症流行に伴う面会制限について", LocalDate.of(2025, 12, 12), "#"),
            new NewsArticle("news", "お知らせ", "予防接種予約枠の追加について", LocalDate.of(2025, 12, 5), "#"),
            new NewsArticle("news", "お知らせ", "年末の診療体制について", LocalDate.of(2025, 12, 1), "#"));

    private static final List<NewsCategoryDefinition> NEWS_CATEGORY_DEFINITIONS = List.of(
            new NewsCategoryDefinition("all", "すべて"),
            new NewsCategoryDefinition("news", "お知らせ"),
            new NewsCategoryDefinition("urgent", "緊急"),
            new NewsCategoryDefinition("closed", "休診"),
            new NewsCategoryDefinition("column", "コラム"));

    @GetMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"/faq", "/faq.html"})
    public String faq() {
        return "faq";
    }

    @GetMapping({"/medical-info", "/medical-info.html"})
    public String medicalInfo() {
        return "medical-info";
    }

    @GetMapping({"/news", "/news.html"})
    public String news(
            @RequestParam(value = "category", defaultValue = "all") String category,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model) {
        String normalizedCategory = normalizeCategory(category);
        Integer normalizedYear = normalizeYear(year);
        List<NewsArticle> filteredArticles = filterArticles(normalizedCategory, normalizedYear);
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredArticles.size() / NEWS_PAGE_SIZE));
        int currentPage = Math.min(Math.max(page, 1), totalPages);
        int startIndex = (currentPage - 1) * NEWS_PAGE_SIZE;
        int endIndex = Math.min(startIndex + NEWS_PAGE_SIZE, filteredArticles.size());

        model.addAttribute("newsItems", filteredArticles.subList(startIndex, endIndex));
        model.addAttribute("currentCategory", normalizedCategory);
        model.addAttribute("currentCategoryLabel", categoryLabel(normalizedCategory));
        model.addAttribute("currentArchiveYear", normalizedYear);
        model.addAttribute("currentArchiveLabel", archiveLabel(normalizedYear));
        model.addAttribute("categoryOptions", buildCategoryOptions(normalizedCategory, normalizedYear));
        model.addAttribute("archiveOptions", buildArchiveOptions(normalizedCategory, normalizedYear));
        model.addAttribute("pageLinks", buildPageLinks(normalizedCategory, normalizedYear, totalPages, currentPage));
        model.addAttribute("hasPreviousPage", currentPage > 1);
        model.addAttribute("hasNextPage", currentPage < totalPages);
        model.addAttribute("previousPageHref", buildNewsHref(normalizedCategory, normalizedYear, currentPage - 1));
        model.addAttribute("nextPageHref", buildNewsHref(normalizedCategory, normalizedYear, currentPage + 1));
        model.addAttribute("currentPageNumber", currentPage);
        model.addAttribute("totalPages", totalPages);
        return "news";
    }

    @GetMapping({"/news-page2", "/news-page2.html"})
    public String newsPage2(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "year", required = false) Integer year) {
        return "redirect:/news" + buildLegacyQuery(category, year, 2);
    }

    @GetMapping({"/news-page3", "/news-page3.html"})
    public String newsPage3(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "year", required = false) Integer year) {
        return "redirect:/news" + buildLegacyQuery(category, year, 3);
    }

    @GetMapping("/admin/login")
    public String adminLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        model.addAttribute("hasError", error != null);
        model.addAttribute("loggedOut", logout != null);
        return "admin-login";
    }

    private List<NewsArticle> filterArticles(String category, Integer year) {
        return NEWS_ARTICLES.stream()
                .filter(article -> matchesCategory(article, category))
                .filter(article -> matchesArchiveYear(article, year))
                .toList();
    }

    private List<NewsCategoryView> buildCategoryOptions(String currentCategory, Integer currentYear) {
        List<NewsCategoryView> categoryOptions = new ArrayList<>();
        for (NewsCategoryDefinition definition : NEWS_CATEGORY_DEFINITIONS) {
            long count = NEWS_ARTICLES.stream()
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
        long allCount = NEWS_ARTICLES.stream()
                .filter(article -> matchesCategory(article, currentCategory))
                .count();
        archiveOptions.add(new NewsArchiveView(
                null,
                "すべて",
                allCount,
                buildNewsHref(currentCategory, null, 1),
                currentYear == null));

        NEWS_ARTICLES.stream()
                .map(article -> article.date().getYear())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .forEach(year -> {
                    long count = NEWS_ARTICLES.stream()
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
        if (!"all".equals(category)) {
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

    private String buildLegacyQuery(String category, Integer year, int page) {
        return buildNewsHref(normalizeCategory(category), normalizeYear(year), page).replaceFirst("^/news", "");
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return "all";
        }
        return NEWS_CATEGORY_DEFINITIONS.stream()
                .map(NewsCategoryDefinition::key)
                .filter(category::equals)
                .findFirst()
                .orElse("all");
    }

    private Integer normalizeYear(Integer year) {
        if (year == null) {
            return null;
        }
        return NEWS_ARTICLES.stream()
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
                .orElse("すべて");
    }

    private String archiveLabel(Integer year) {
        return year == null ? "すべて" : year + "年";
    }

    private boolean matchesCategory(NewsArticle article, String category) {
        return "all".equals(category) || article.categoryKey().equals(category);
    }

    private boolean matchesArchiveYear(NewsArticle article, Integer year) {
        return year == null || article.date().getYear() == year;
    }

    private record NewsArticle(String categoryKey, String categoryLabel, String title, LocalDate date, String href) {
    }

    private record NewsCategoryDefinition(String key, String label) {
    }

    private record NewsCategoryView(
            String key,
            String label,
            long count,
            String href,
            boolean current) {
    }

    private record NewsArchiveView(
            Integer year,
            String label,
            long count,
            String href,
            boolean current) {
    }

    private record NewsPageLink(int page, String href, boolean active) {
    }
}
