package com.ecommerce.furniture.security.services;

import com.ecommerce.furniture.models.Category;
import com.ecommerce.furniture.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // ✅ NEW: Fetch categories with product count
    public List<Object[]> getCategoryProductCounts() {
        return categoryRepository.findCategoryProductCounts();
    }
}
