package com.example.satoclinic;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SatoclinicApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void newsPageFiltersByYear() throws Exception {
        mockMvc.perform(get("/news").param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2025.12.26")))
                .andExpect(content().string(not(containsString("2026.01.28"))))
                .andExpect(content().string(containsString("2025年")));
    }

    @Test
    void newsPageCombinesCategoryAndYearFilters() throws Exception {
        mockMvc.perform(get("/news").param("category", "urgent").param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("感染症流行に伴う面会制限について")))
                .andExpect(content().string(not(containsString("院内設備点検に伴う一時停電のお知らせ"))))
                .andExpect(content().string(containsString("/news?category=urgent&amp;year=2025")));
    }

    @Test
    void topPageUsesSharedNewsDataSource() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("インフルエンザ予防接種について")))
                .andExpect(content().string(containsString("オンライン診療の導入について")))
                .andExpect(content().string(not(containsString("感染症流行に伴う面会制限について"))));
    }
}
