package com.uade.tpo.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> getCategories(PageRequest pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }
 

    public Category createCategory(String name, String description, long parent_Id) throws CategoryDuplicateException {
    List<Category> categories = categoryRepository.findByname(name);
    if (categories.isEmpty()) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
         Category parent = categoryRepository.findById( parent_Id).orElse(null);
         category.setParent(parent);
        return categoryRepository.save(category);
    }
    throw new CategoryDuplicateException();
}


    public List<Category> getCategorysByIdParent(Long parentId) throws CategoryNotFoundException {
        
        Category parent = categoryRepository.findById(parentId).orElse(null);
        if (parent != null ){
            List<Category> categories = categoryRepository.findByParent_id(parent);
            return categories;
        }
        throw new CategoryNotFoundException("La categoria" + parentId +" no existe");

    }
}
