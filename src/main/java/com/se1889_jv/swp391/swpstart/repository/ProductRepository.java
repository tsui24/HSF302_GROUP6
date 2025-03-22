package com.se1889_jv.swp391.swpstart.repository;

import com.se1889_jv.swp391.swpstart.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Product findProductById(@Param("id") Long id);

    @Query("SELECT distinct p.name FROM Product p")
    List<String> getAllProductByName();

    @Query("SELECT p FROM Product p WHERE (:idFrom IS NULL OR p.id >= :idFrom) " +
            " AND (:idTo IS NULL OR p.id <= :idTo) " +
            " AND (:priceFrom IS NULL OR p.price >= :priceFrom) " +
            " AND (:priceTo IS NULL OR p.price <= :priceTo) " +
            " AND (:storageState IS NULL OR p.storage = :storageState)" +
            " AND (:category IS NULL OR p.category.id = :category)" +
            " AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> getProductByAttribute(@Param("idFrom") Long idFrom,
                                   @Param("idTo") Long idTo,
                                   @Param("name") String name, @Param("priceFrom") Double priceFrom,
                                   @Param("priceTo") Double priceTo, @Param("storageState") Boolean storageState,
                                   @Param("category") Long category, Pageable pageable);

    @Query("SELECT p FROM Product p")
    Page<Product> getAllProducts(Pageable pageable);
}
