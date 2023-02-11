package com.ipmugo.articleservice.controller;

import com.ipmugo.articleservice.service.JournalService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import com.ipmugo.articleservice.dto.ArticleRequest;
import com.ipmugo.articleservice.dto.ResponseData;
import com.ipmugo.articleservice.model.Article;
import com.ipmugo.articleservice.service.ArticleService;
import com.ipmugo.articleservice.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/article")
@RequiredArgsConstructor
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JournalService journalService;

    /**
     * Save Article
     * */
    @PostMapping
    @CircuitBreaker(name = "journal")
    @TimeLimiter(name = "journal")
    @Retry(name = "journal")
    public ResponseEntity<ResponseData<Article>> createArticle(@Valid @RequestBody ArticleRequest articleRequest,
                                                                       Errors errors) {
        ResponseData<Article> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            responseData.setData(articleService.createArticle(articleRequest));
            responseData.setStatus(true);
            return ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Update Article
     * */
    @PutMapping("/{id}")
    @CircuitBreaker(name = "journal")
    @TimeLimiter(name = "journal")
    @Retry(name = "journal")
    public ResponseEntity<ResponseData<Article>> updateArticle(@PathVariable("id") String id, @Valid @RequestBody ArticleRequest articleRequest,
                                                               Errors errors) {
        ResponseData<Article> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            responseData.setData(articleService.updateArticle(id, articleRequest));
            responseData.setStatus(true);
            return ResponseEntity.ok(responseData);

        }catch (CustomException e){
            responseData.getMessages().add(e.getMessage());
            responseData.setData(null);
            responseData.setStatus(false);
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Get List Article
     * */
    @GetMapping
    public ResponseEntity<ResponseData<List<Article>>> getAllArticles() {
        ResponseData<List<Article>> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(articleService.getAllArticle());

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Get Article By ID
     * */
    @GetMapping("{id}")
    public ResponseEntity<ResponseData<Article>> getArticle(@PathVariable("id") String id) {
        ResponseData<Article> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.setData(articleService.getArticle(id));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Delete Article By ID
     * */
    @DeleteMapping("{id}")
    public ResponseEntity<ResponseData<String>> deleteArticle(@PathVariable("id") String id) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            articleService.deleteArticle(id);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Journal deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Delete Journal By ID
     * */
    @DeleteMapping("/delete/journal/{id}")
    public ResponseEntity<ResponseData<String>> deleteJournal(@PathVariable("id") String id) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            journalService.deleteJournal(id);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Journal deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Get Article By Current Issue
     * */
    @GetMapping("/current/issue/{id}")
    public ResponseEntity<ResponseData<Iterable<Article>>> getCurrentIssue(@PathVariable("id") String id) {
        ResponseData<Iterable<Article>> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.setData(articleService.getCurrentIssue(id));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @GetMapping("/oai-pmh/dc/{id}/{start}/{until}")
    @CircuitBreaker(name = "journal")
    @TimeLimiter(name = "journal")
    @Retry(name = "journal")
    public ResponseEntity<ResponseData<String>> getOaiDc(@PathVariable("id") String id, @PathVariable("start") String start, @PathVariable("until") String until){
        ResponseData<String> responseData = new ResponseData<>();
        try{
            articleService.getOaiPmh(id, "oai_dc", start, until);

            responseData.setStatus(true);
            responseData.getMessages().add( "Harvested journal with id " + id + " successfully");
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @GetMapping("/oai-pmh/marc/{id}/{start}/{until}")
    @CircuitBreaker(name = "journal")
    @TimeLimiter(name = "journal")
    @Retry(name = "journal")
    public ResponseEntity<ResponseData<String>> getOaiMarc(@PathVariable("id") String id, @PathVariable("start") String start, @PathVariable("until") String until){
        ResponseData<String> responseData = new ResponseData<>();
        try{
            articleService.getOaiPmh(id, "oai_marc", start, until);

            responseData.setStatus(true);
            responseData.getMessages().add( "Harvested journal with id " + id + " successfully");
            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}
