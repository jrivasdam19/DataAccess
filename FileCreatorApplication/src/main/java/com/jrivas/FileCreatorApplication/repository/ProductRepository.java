package com.jrivas.FileCreatorApplication.repository;

import com.jrivas.FileCreatorApplication.data.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
List<Product> findByBrand(String brand);
}
