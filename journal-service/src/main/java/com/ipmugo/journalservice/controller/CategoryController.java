package com.ipmugo.journalservice.controller;

import com.ipmugo.journalservice.dto.JournalRequest;
import com.ipmugo.journalservice.dto.ResponseData;
import com.ipmugo.journalservice.model.Category;
import com.ipmugo.journalservice.model.Journal;
import com.ipmugo.journalservice.service.CategoryService;
import com.ipmugo.journalservice.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Create Category
     * */
    @PostMapping
    public ResponseEntity<ResponseData<Category>> createCategory(@Valid @RequestBody Category category,
                                                                       Errors errors) {
        ResponseData<Category> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            responseData.setData(categoryService.createCategory(category));
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
     * Update Category
     * */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseData<Category>> updateCategory(@PathVariable("id") String id, @Valid @RequestBody Category category,
                                                                 Errors errors) {
        ResponseData<Category> responseData = new ResponseData<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()){
                responseData.getMessages().add(error.getDefaultMessage());
            }

            responseData.setStatus(false);

            return ResponseEntity.badRequest().body(responseData);
        }

        try{
            responseData.setData(categoryService.updateCategory(id, category));
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
     * Get List Category
     * */
    @GetMapping
    public ResponseEntity<ResponseData<Iterable<Category>>> getAllCategory() {
        ResponseData<Iterable<Category>> responseData = new ResponseData<>();

        try{
            responseData.setStatus(true);
            responseData.setData(categoryService.getAllCategory());

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());

            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Get Category By Name
     * */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Category>> getCategory(@PathVariable("id") String id) {
        ResponseData<Category> responseData = new ResponseData<>();
        try{
            responseData.setStatus(true);
            responseData.setData(categoryService.getCategory(id));

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }

    /**
     * Delete Journal By Name
     * */
    @DeleteMapping("/{name}")
    public ResponseEntity<ResponseData<String>> deleteCategory(@PathVariable("name") String name) {

        ResponseData<String> responseData = new ResponseData<>();
        try{
            categoryService.deleteCategory(name);

            responseData.setStatus(true);
            responseData.setData(null);
            responseData.getMessages().add("Category deleted successfully");

            return ResponseEntity.ok(responseData);
        }catch (CustomException e){
            responseData.setStatus(true);
            responseData.getMessages().add(e.getMessage());
            return ResponseEntity.status(e.getStatusCode()).body(responseData);
        }
    }
}
