package com.example.satoclinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.satoclinic.model.news.NewsListResult;
import com.example.satoclinic.service.NewsService;

@Controller
public class PageController {

    private static final int NEWS_PAGE_SIZE = 5;
    private static final int HOME_NEWS_LIMIT = 5;

    private final NewsService newsService;

    public PageController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping({"/", "/index.html"})
    public String index(Model model) {
        model.addAttribute("homeNewsItems", newsService.getLatestPublished(HOME_NEWS_LIMIT));
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
        NewsListResult result = newsService.getNewsList(category, year, page, NEWS_PAGE_SIZE);

        model.addAttribute("newsItems", result.newsItems());
        model.addAttribute("currentCategory", result.currentCategory());
        model.addAttribute("currentCategoryLabel", result.currentCategoryLabel());
        model.addAttribute("currentArchiveYear", result.currentArchiveYear());
        model.addAttribute("currentArchiveLabel", result.currentArchiveLabel());
        model.addAttribute("categoryOptions", result.categoryOptions());
        model.addAttribute("archiveOptions", result.archiveOptions());
        model.addAttribute("pageLinks", result.pageLinks());
        model.addAttribute("hasPreviousPage", result.hasPreviousPage());
        model.addAttribute("hasNextPage", result.hasNextPage());
        model.addAttribute("previousPageHref", result.previousPageHref());
        model.addAttribute("nextPageHref", result.nextPageHref());
        model.addAttribute("currentPageNumber", result.currentPageNumber());
        model.addAttribute("totalPages", result.totalPages());
        return "news";
    }

    @GetMapping({"/news-page2", "/news-page2.html"})
    public String newsPage2(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "year", required = false) Integer year) {
        return "redirect:/news" + newsService.buildLegacyQuery(category, year, 2);
    }

    @GetMapping({"/news-page3", "/news-page3.html"})
    public String newsPage3(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "year", required = false) Integer year) {
        return "redirect:/news" + newsService.buildLegacyQuery(category, year, 3);
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
}
