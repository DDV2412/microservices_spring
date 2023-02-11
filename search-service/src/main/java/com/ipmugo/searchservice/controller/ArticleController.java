package com.ipmugo.searchservice.controller;

import com.ipmugo.searchservice.model.Article;
import com.ipmugo.searchservice.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<Iterable<Article>> getAllArticle(){
        return ResponseEntity.ok(articleService.getAllArticle());
    }
}
