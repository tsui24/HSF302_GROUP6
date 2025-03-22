package com.se1889_jv.swp391.swpstart.controller;

import com.se1889_jv.swp391.swpstart.domain.Category;
import com.se1889_jv.swp391.swpstart.domain.Product;
import com.se1889_jv.swp391.swpstart.service.CategoryService;
import com.se1889_jv.swp391.swpstart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        List<String> nameList = productService.getAllProductName();
        model.addAttribute("nameList", nameList);
        return "admin/product/add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(Model model, @RequestParam String name, @RequestParam Double price,
                             @RequestParam String shortDesc, @RequestParam String detailDesc,
//                             @RequestParam String target,
                             @RequestParam String category, @RequestParam String storage,
                             @RequestParam MultipartFile productFile, RedirectAttributes redirectAttributes) {
        String imagePath = null;
        if (!productFile.isEmpty()) {
            try {
                String uploadDir = "uploads/notes/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID().toString().substring(0, 8) + "_" + productFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(productFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = fileName;
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("messageType", "fail");
                redirectAttributes.addFlashAttribute("message", "Error uploading image!");
                return "redirect:/product/add-product";
            }
        }
        boolean bl_storage = Boolean.valueOf(storage);

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setShortDescription(shortDesc);
        product.setDetailDescription(detailDesc);
//        product.setTarget(target);

        Category category1 = new Category();
        category1.setId(Long.parseLong(category));
        product.setCategory(category1);
        product.setStorage(bl_storage);
        product.setImage(imagePath);
        productService.createProduct(product);

        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addFlashAttribute("message", "Product created successfully!");
        return "redirect:/product/list-product-admin";
    }
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get("uploads/notes/").resolve(filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(imagePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/list-product-admin")
    public String listProduct(Model model,
                              @RequestParam("page") Optional<String> pageOptional,
                              @RequestParam(required = false) String idFrom, @RequestParam(required = false) String idTo,
                              @RequestParam(required = false) String name, @RequestParam(required = false) String priceFrom,
                              @RequestParam(required = false) String priceTo,
                              @RequestParam(required = false) String storageState, @RequestParam(required = false) String category) {
        int page = 1;
        try{
            if(pageOptional.isPresent()){
                page = Integer.parseInt(pageOptional.get());
            }else{
                // page = 1;
            }
        }catch (Exception e){
            //
        }
        Pageable pageable = PageRequest.of(page-1,5, Sort.by("id").ascending());
        Page<Product> productPage;
        if ((idFrom != null && !idFrom.isEmpty() && !idFrom.matches("\\d+")) ||
                (idTo != null && !idTo.isEmpty() && !idTo.matches("\\d+")) ||
                (priceFrom != null && !priceFrom.isEmpty() && !priceFrom.matches("\\d+")) ||
                (priceTo != null && !priceTo.isEmpty() && !priceTo.matches("\\d+"))) {

            model.addAttribute("errorMessage", "Id, Price fields should be positive number.");
            productPage = Page.empty();
            model.addAttribute("products", productPage.getContent());

            model.addAttribute("idFrom", idFrom);
            model.addAttribute("idTo", idTo);
            model.addAttribute("name", name);
            model.addAttribute("priceFrom", priceFrom);
            model.addAttribute("priceTo", priceTo);
            model.addAttribute("storageState", storageState);
            model.addAttribute("category", category);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            return "admin/product/list-product-admin";
        }
        else {
            if ((idFrom != null && !idFrom.isEmpty()) || (idTo != null && !idTo.isEmpty())
                    || (name != null && !name.isEmpty())
                    || (priceFrom != null && !priceFrom.isEmpty()) || (priceTo != null && !priceTo.isEmpty())
                    || (storageState != null && !storageState.isEmpty()) || (category != null && !category.isEmpty())) {


                Long req_idFrom = (idFrom != null && !idFrom.isBlank() && idFrom.matches("\\d+")) ? Long.valueOf(idFrom) : null;
                Long req_idTo = (idTo != null && !idTo.isBlank() && idTo.matches("\\d+")) ? Long.valueOf(idTo) : null;

                Double req_priceFrom = (priceFrom != null && !priceFrom.isBlank() && priceFrom.matches("\\d+")) ? Double.valueOf(priceFrom) : null;
                Double req_priceTo = (priceTo != null && !priceTo.isBlank() && priceTo.matches("\\d+")) ? Double.valueOf(priceTo) : null;

                Boolean req_storageState = (storageState == null || storageState.isEmpty()) ? null : Boolean.parseBoolean(storageState);
                Long req_category = (category == null || category.isEmpty()) ? null : Long.valueOf(category);
                String req_name = (name == null || name.isEmpty()) ? null : name;


                productPage = productService.searchProductByAttribute(req_idFrom, req_idTo, req_name,
                        req_priceFrom, req_priceTo, req_storageState, req_category, pageable);

            } else {

                productPage = productService.searchAllProduct(pageable);

            }
        }

        List<Category> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("products", productPage.getContent());

        model.addAttribute("idFrom", idFrom);
        model.addAttribute("idTo", idTo);
        model.addAttribute("name", name);
        model.addAttribute("priceFrom", priceFrom);
        model.addAttribute("priceTo", priceTo);
        model.addAttribute("storageState", storageState);
        model.addAttribute("category", category);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        return "admin/product/list-product-admin";
    }
    @GetMapping("/detail/{id}")
    public String detailProduct(@PathVariable String id, Model model) {
        List<Category> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        Long productId = Long.parseLong(id);
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "admin/product/detail-product";
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id, Model model) {
        Long productId = Long.parseLong(id);
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        List<Category> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "admin/product/delete-product";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam String productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(Integer.parseInt(productId));
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
            return "redirect:/product/list-product-admin";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "Product is not deleted successfully!");
            return "redirect:/product/list-product-admin";
        }
    }
    @GetMapping("/update/{id}")
    public String updateProduct(@PathVariable String id, Model model) {
        Long productId = Long.parseLong(id);
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        String foodName = product.getName();
        List<String> nameList = productService.getAllProductName();
        model.addAttribute("nameList", nameList);
        model.addAttribute("foodName", foodName);
        return "admin/product/update-product";
    }
    @PostMapping("/update")
    public String updateNewState(@RequestParam (required = false) String productID, @RequestParam (required = false) String productName, @RequestParam (required = false) Double price,
                                 @RequestParam (required = false) String shortDetail, @RequestParam (required = false) String detailDesc,
//                             @RequestParam String target,
                                 @RequestParam (required = false) String category, @RequestParam (required = false) String storage,
                                 @RequestParam (required = false) MultipartFile productFile,
                                 @RequestParam(value = "oldImage", required = false) String oldImage,
                                 RedirectAttributes redirectAttributes){

        String imagePath = null;
        if (productFile != null && !productFile.isEmpty()) {
            try {
                String uploadDir = "uploads/notes/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID().toString().substring(0, 8) + "_" + productFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(productFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                imagePath = fileName;
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("messageType", "fail");
                redirectAttributes.addFlashAttribute("message", "Error uploading image!");
                return "redirect:/product/list-product-admin";
            }
        }else{
            imagePath = oldImage;
        }
        boolean bl_storage = Boolean.valueOf(storage);
        Product product = new Product();
        Long id = Long.parseLong(productID);
        product.setId(id);
        product.setName(productName);
        product.setPrice(price);
        product.setShortDescription(shortDetail);
        product.setDetailDescription(detailDesc);
//        product.setTarget(target);
        product.setStorage(bl_storage);
        Category category1 = new Category();
        category1.setId(Long.parseLong(category));
        product.setCategory(category1);
        product.setImage(imagePath);
        try {
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
            return "redirect:/product/list-product-admin";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("messageType", "fail");
            redirectAttributes.addFlashAttribute("message", "Product is not updated successfully!");
            return "redirect:/product/list-product-admin";
        }

    }
}
