package com.ecommerce.furniture.controllers;

import com.ecommerce.furniture.models.Category;
import com.ecommerce.furniture.security.services.CategoryService;
import com.ecommerce.furniture.security.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    @PostMapping
    public Category createCategory(@RequestParam("name") String name,
                                   @RequestParam("description") String description,
                                   @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {
        String fileName = (image != null && !image.isEmpty()) ? fileStorageService.saveFile(image) : null;
        Category category = new Category(name, description, fileName);
        return categoryService.saveCategory(category);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

    // ✅ NEW: Endpoint to get product count per category
    @GetMapping("/product-count")
    public List<Map<String, Object>> getCategoryProductCounts() {
        List<Object[]> results = categoryService.getCategoryProductCounts();
        List<Map<String, Object>> response = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", row[0]);
            map.put("productCount", row[1]);
            response.add(map);
        }

        return response;
    }
}
