package com.se1889_jv.swp391.swpstart.service;

import com.se1889_jv.swp391.swpstart.domain.Category;
import com.se1889_jv.swp391.swpstart.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public List<Category> getAllCategories(){
        return categoryRepository.searchAll();
    }
}
