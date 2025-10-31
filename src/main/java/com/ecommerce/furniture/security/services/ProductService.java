package com.ecommerce.furniture.security.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.furniture.models.Category;
import com.ecommerce.furniture.models.Product;
import com.ecommerce.furniture.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    public ProductService(ProductRepository productRepository,
                          @Value("${cloudinary.cloud_name}") String cloudName,
                          @Value("${cloudinary.api_key}") String apiKey,
                          @Value("${cloudinary.api_secret}") String apiSecret) {
        this.productRepository = productRepository;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
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

    // ✅ Create new product and upload image to Cloudinary
    public Product saveProductWithImage(String name, Double price, Category category, MultipartFile image) throws IOException {
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = uploadToCloudinary(image);
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setImage(imageUrl);

        return productRepository.save(product);
    }

    // ✅ Update product and replace Cloudinary image if needed
    public Product updateProductWithImage(Long id, String name, Double price, Category category, MultipartFile image) throws IOException {
        return productRepository.findById(id).map(existing -> {
            existing.setName(name);
            existing.setPrice(price);
            existing.setCategory(category);

            try {
                if (image != null && !image.isEmpty()) {
                    // Delete old image from Cloudinary if exists
                    if (existing.getImage() != null && existing.getImage().contains("cloudinary.com")) {
                        deleteFromCloudinary(existing.getImage());
                    }
                    String newImageUrl = uploadToCloudinary(image);
                    existing.setImage(newImageUrl);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }

            return productRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    // ✅ Delete product (and Cloudinary image)
    public void deleteProduct(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            if (product.getImage() != null && product.getImage().contains("cloudinary.com")) {
                deleteFromCloudinary(product.getImage());
            }
            productRepository.deleteById(id);
        });
    }

    // ✅ Upload file to Cloudinary
    private String uploadToCloudinary(MultipartFile imageFile) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
                ObjectUtils.asMap("folder", "furniture_products"));
        return uploadResult.get("secure_url").toString();
    }

    // ✅ Delete file from Cloudinary
    private void deleteFromCloudinary(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to delete image from Cloudinary: " + e.getMessage());
        }
    }

    // ✅ Extract Cloudinary public ID from URL
    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            String fileWithExt = parts[parts.length - 1]; // e.g. abc123.jpg
            String publicId = fileWithExt.substring(0, fileWithExt.lastIndexOf(".")); // abc123
            return "furniture_products/" + publicId; // include folder name
        } catch (Exception e) {
            return null;
        }
    }
}
