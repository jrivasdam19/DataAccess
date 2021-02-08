package com.jrivas.FileCreatorApplication.repository;

import com.jrivas.FileCreatorApplication.data.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
Product findByBrand(String brand);
}
