package com.se1889_jv.swp391.swpstart.service;

import com.se1889_jv.swp391.swpstart.domain.Product;
import com.se1889_jv.swp391.swpstart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    public Product getProductById(Long id) {
        return productRepository.findProductById(id);
    }
    public void deleteProduct(Integer id) {
        productRepository.deleteById(Long.valueOf(id));
    }
    public void updateProduct(Product product) {
        productRepository.save(product);
    }
    public List<String> getAllProductName(){
        return productRepository.getAllProductByName();
    }
    public Page<Product> searchProductByAttribute(Long idFrom, Long idTo, String name,
                                           Double priceFrom, Double priceTo, Boolean storageState,
                                           Long category, Pageable pageable) {
        return productRepository.getProductByAttribute(idFrom, idTo, name,
                priceFrom, priceTo, storageState, category, pageable);
    }
    public Page<Product> searchAllProduct(Pageable pageable) {
        return productRepository.getAllProducts(pageable);
    }
}
