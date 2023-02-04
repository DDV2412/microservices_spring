package com.ipmugo.journalservice.service;

import com.ipmugo.journalservice.model.Category;
import com.ipmugo.journalservice.repository.CategoryRepository;
import com.ipmugo.journalservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get Category by Name
     * */
    public Category getCategory(String name) throws CustomException{
        try{
            Optional<Category> category = categoryRepository.findByName(name);

            if (category.isEmpty()){
                throw new CustomException("Category with name "+ name + "not found", HttpStatus.NOT_FOUND);
            }


            return category.get();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Save or Update Category
     * */
    public Category saveOrUpdate(Category category) throws CustomException {
        try{
            Optional<Category> category1 = categoryRepository.findByName(category.getName());

            if (category1.isEmpty()){
                Category category2 = Category.builder()
                        .name(category.getName())
                        .illustration(category.getIllustration())
                        .build();
                return categoryRepository.save(category2);
            }

            category1.get().setName(category.getName());
            category1.get().setIllustration(category.getIllustration());

            return categoryRepository.save(category1.get());
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Get Category by Name
     * */
    public Iterable<Category> getAllCategory() throws CustomException{
        try{
            return categoryRepository.findAll();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Delete Category by Name
     * */
    public void deleteCategory(String name) throws CustomException{
        try{
            Category category = this.getCategory(name);

            categoryRepository.deleteById(category.getId());

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


}
