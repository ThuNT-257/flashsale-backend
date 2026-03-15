package com.sapotest.flashsale.controller;

import com.sapotest.flashsale.model.dto.ProductDTO;
import com.sapotest.flashsale.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing and retrieving product information.
 */
@RestController
@RequestMapping("/api/flash-sale-products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Retrieve a list of all products currently in the flash sale.
     * * @return List of Flash Sale products with their discounted prices.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getFlashSaleProducts() {
        return ResponseEntity.ok(productService.getAllFlashSaleProducts());
    }
}