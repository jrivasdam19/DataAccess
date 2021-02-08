package com.jrivas.FileCreatorApplication.service;

import com.jrivas.FileCreatorApplication.data.Product;
import com.jrivas.FileCreatorApplication.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public Product saveProduct(Product product){
        return repository.save(product);
    }

    public List<Product> saveProducts(List<Product> products){
        return repository.saveAll(products);
    }

    public List<Product>getProducts(){
        return repository.findAll();
    }

    public Product getProductById(int id){
        return repository.findById(id).orElse(null);
    }

    public Product getProductByBrand(String brand){
        return repository.findByBrand(brand);
    }

    public String deleteProduct(int id){
        repository.deleteById(id);
        return "Successfully deleted!";
    }

    public Product updateProduct(Product product){
        Product existingProduct=repository.findById(product.getId()).orElse(null);
        existingProduct.setPrice(product.getPrice());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setUrl(product.getUrl());
        existingProduct.setOnDiscount(product.isOnDiscount());
        return repository.save(existingProduct);
    }
}
