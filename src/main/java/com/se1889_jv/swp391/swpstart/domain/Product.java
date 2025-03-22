package com.se1889_jv.swp391.swpstart.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;
    private double price;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String shortDescription;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String detailDescription;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String target;
    private boolean storage;
    private String image;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private List<CartItem> cartItems;
}
