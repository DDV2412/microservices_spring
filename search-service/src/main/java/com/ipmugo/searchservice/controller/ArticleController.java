package com.ipmugo.searchservice.controller;

import com.ipmugo.searchservice.dto.ResponseData;
import com.ipmugo.searchservice.service.ArticleService;
import com.ipmugo.searchservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<ResponseData<SearchResponse>> getAllArticle (@RequestParam(value = "page", defaultValue = "0", required = false) String page, @RequestParam(value = "size", defaultValue = "25", required = false) String size, @RequestParam(value = "search", required = false) String search, @RequestParam(name = "sort", required = false) String sort, @RequestParam(value = "advanced", required = false) String advanced, @RequestParam(value = "filter", required = false) String filter) {
        ResponseData<SearchResponse> responseData = new ResponseData<>();
        try{
            Sort sortBy = Sort.by(Sort.Direction.DESC, "score");

            if (sort != null && !sort.isEmpty()) {
                String[] sortParams = sort.split(",");
                String field = sortParams[0];
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                sortBy = Sort.by(direction, field);
            }
            Pageable pageable = PageRequest.of(Integer.valueOf(page), Integer.valueOf(size), sortBy);

            responseData.setStatus(true);
            responseData.setData(articleService.search(pageable, search, advanced, filter));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }


}
