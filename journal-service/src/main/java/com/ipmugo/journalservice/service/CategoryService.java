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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get Category by Name
     * */
    public Category getCategory(UUID id) throws CustomException{
        try{
            Optional<Category> category = categoryRepository.findById(id);

            if (category.isEmpty()){
                throw new CustomException("Category with id "+ id + "not found", HttpStatus.NOT_FOUND);
            }


            return category.get();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Create Category
     * */
    public Category createCategory(Category category) throws CustomException {
        try{
            Optional<Category> category1 = categoryRepository.findByName(category.getName());

            if (category1.isPresent()){
                throw new CustomException("Category with name "+ category.getName() + "is ready", HttpStatus.FOUND);
            }

            Category category2 = Category.builder()
                    .name(category.getName())
                    .illustration(category.getIllustration())
                    .build();
            return categoryRepository.save(category2);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Update Category
     * */
    public Category updateCategory(UUID id, Category category) throws CustomException {
        try{
            Category category1 = this.getCategory(id);

            category1.setName(category.getName());
            category1.setIllustration(category.getIllustration());

            return categoryRepository.save(category1);
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
    public void deleteCategory(UUID id) throws CustomException{
        try{
            this.getCategory(id);

            categoryRepository.deleteById(id);

        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


}
