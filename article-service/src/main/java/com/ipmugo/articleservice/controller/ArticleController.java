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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ResponseData<Page<Article>>> getAllArticles(@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "25", required = false) String size, @RequestParam(value = "search", required = false) String search) {
        ResponseData<Page<Article>> responseData = new ResponseData<>();

        try{

            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size), Sort.by("publishDate").descending());
            responseData.setStatus(true);
            responseData.setData(articleService.getAllArticle(pageable, search));

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
    @GetMapping("/{id}")
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<String>> deleteArticle(@PathVariable("id") String id) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            articleService.deleteArticle(id);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Article deleted successfully");

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
    public ResponseEntity<ResponseData<String>> deleteJournal(@PathVariable("id") UUID id) {

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
    @Retry(name = "journal")
    public ResponseEntity<ResponseData<String>> getOaiDc(@PathVariable("id") UUID id, @PathVariable("start") String start, @PathVariable("until") String until){
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
    @Retry(name = "journal")
    public ResponseEntity<ResponseData<String>> getOaiMarc(@PathVariable("id") UUID id, @PathVariable("start") String start, @PathVariable("until") String until){
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

    @GetMapping("/scopus/{identify}/{doi}")
    public ResponseEntity<ResponseData<String>> citationScopus(@PathVariable("identify") String identify,@PathVariable("doi") String doi) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            String fullDoi = identify + "/" + doi;

            articleService.citationScopus(fullDoi);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Citation by scopus synchronized successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    @GetMapping("/cross-ref/{identify}/{doi}")
    public ResponseEntity<ResponseData<String>> citationCrossRef(@PathVariable("identify") String identify, @PathVariable("doi") String doi) {

        ResponseData<String> responseData = new ResponseData<>();
        try{

            String fullDoi = identify + "/" + doi;

            articleService.citationCrossRef(fullDoi);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Citation by crossRef synchronized successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Get List Article
     * */
    @GetMapping("/featured-articles")
    public ResponseEntity<ResponseData<Iterable<Article>>> featuredArticles() {
        ResponseData<Iterable<Article>> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(articleService.featuredArticles());

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}
