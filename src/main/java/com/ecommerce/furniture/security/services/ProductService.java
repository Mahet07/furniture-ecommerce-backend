package com.ecommerce.furniture.security.services;

import com.ecommerce.furniture.models.Category;
import com.ecommerce.furniture.models.Product;
import com.ecommerce.furniture.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final Path uploadDir;

    public ProductService(ProductRepository productRepository,
                          @Value("${file.upload-dir:uploads/products}") String uploadDirStr) {
        this.productRepository = productRepository;
        this.uploadDir = Paths.get(uploadDirStr).toAbsolutePath().normalize();
    }

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProductWithImage(String name, Double price, Category category, MultipartFile image) throws IOException {
        String imageFileName = null;

        if (image != null && !image.isEmpty()) {
            imageFileName = saveImageFile(image);
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setImage(imageFileName); // ✅ Only store file name

        return productRepository.save(product);
    }

    public Product updateProductWithImage(Long id, String name, Double price, Category category, MultipartFile image) throws IOException {
        return productRepository.findById(id).map(existing -> {
            existing.setName(name);
            existing.setPrice(price);
            existing.setCategory(category);

            if (image != null && !image.isEmpty()) {
                if (existing.getImage() != null) {
                    deleteFileIfExists(existing.getImage());
                }
                String imageFileName = saveImageFile(image);
                existing.setImage(imageFileName);
            }

            return productRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            if (product.getImage() != null) {
                deleteFileIfExists(product.getImage());
            }
            productRepository.deleteById(id);
        });
    }

    private String saveImageFile(MultipartFile imageFile) {
        try {
            String originalFileName = Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
            String fileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("\\s+", "_");
            Path target = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // ✅ Only return the filename (not path)
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    private void deleteFileIfExists(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete image: " + filename);
        }
    }
}
