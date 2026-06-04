package com.example.satoclinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
