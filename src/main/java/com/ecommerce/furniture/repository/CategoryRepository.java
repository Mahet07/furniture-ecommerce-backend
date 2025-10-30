package com.ecommerce.furniture.repository;

import com.ecommerce.furniture.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ✅ NEW: Query to count products in each category
    @Query("SELECT c.name, COUNT(p.id) FROM Category c LEFT JOIN c.products p GROUP BY c.id, c.name")
    List<Object[]> findCategoryProductCounts();
}
