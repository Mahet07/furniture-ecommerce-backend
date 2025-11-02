package com.ecommerce.furniture.controllers;

import com.ecommerce.furniture.models.Category;
import com.ecommerce.furniture.models.Product;
import com.ecommerce.furniture.repository.CategoryRepository;
import com.ecommerce.furniture.security.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    // ✅ Get all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // ✅ Get by category
    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    // ✅ Get single product
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Create new product (with image upload)
    @PostMapping(consumes = {"multipart/form-data"})
    public Product createProduct(
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam Long categoryId,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return productService.saveProductWithImage(name, price, category, image);
    }

    // ✅ Update product
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public Product updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam Long categoryId,
            @RequestParam(required = false) MultipartFile image
    ) throws IOException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return productService.updateProductWithImage(id, name, price, category, image);
    }

    // ✅ Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
