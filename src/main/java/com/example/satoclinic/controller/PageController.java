package com.example.satoclinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

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
    public String news() {
        return "news";
    }
}
