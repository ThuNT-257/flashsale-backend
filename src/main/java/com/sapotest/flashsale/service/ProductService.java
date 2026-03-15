package com.sapotest.flashsale.service;

import com.sapotest.flashsale.model.dto.ProductDTO;
import com.sapotest.flashsale.model.entity.Product;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing product catalog and flash sale listings.
 * Provides methods for standard product CRUD and specialized flash sale retrieval.
 */
public interface ProductService {

    /**
     * Retrieves all standard products available in the system.
     * @return List of all products
     */
    List<Product> getAllProducts();

    /**
     * Finds a specific product by its unique identifier.
     * @param id The UUID of the product to retrieve
     * @return The found product entity
     * @throws RuntimeException if the product is not found
     */
    Product getProductById(UUID id);

    /**
     * Creates and persists a new product entry.
     * @param product The product entity to be created
     * @return The newly created product entity
     */
    Product createProduct(Product product);

    /**
     * Retrieves a specialized list of products currently participating in flash sales.
     * Maps base product information with specific flash sale attributes like sale price and stock.
     * @return List of ProductDTOs containing merged flash sale data
     */
    List<ProductDTO> getAllFlashSaleProducts();
}