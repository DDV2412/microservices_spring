package com.ipmugo.searchservice.controller;

import com.ipmugo.searchservice.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class ArticleController {
    @Autowired
    private ArticleService articleService;

}
