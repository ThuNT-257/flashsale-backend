package com.sapotest.flashsale.service.impl;

import com.sapotest.flashsale.model.dto.ProductDTO;
import com.sapotest.flashsale.model.entity.FlashSaleProduct;
import com.sapotest.flashsale.model.entity.Product;
import com.sapotest.flashsale.repository.FlashSaleProductRepository;
import com.sapotest.flashsale.repository.ProductRepository;
import com.sapotest.flashsale.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing products and flash sale catalog.
 * Handles synchronization between base products and flash sale specific details.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FlashSaleProductRepository flashSaleProductRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              FlashSaleProductRepository flashSaleProductRepository) {
        this.productRepository = productRepository;
        this.flashSaleProductRepository = flashSaleProductRepository;
    }

    /**
     * Retrieves all standard products from the database.
     * @return List of Product entities
     */
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Finds a single product by its unique identifier.
     * @param id The UUID of the product
     * @return The found Product entity
     * @throws RuntimeException if product does not exist
     */
    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    /**
     * Persists a new product into the system.
     * @param product The product entity to save
     * @return The persisted Product entity
     */
    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Fetches all active flash sale products and merges them with their base product information.
     * This method transforms internal entities into DTOs suitable for the frontend.
     * * @return List of ProductDTO containing combined product and flash sale data
     */
    @Override
    public List<ProductDTO> getAllFlashSaleProducts() {
        /*
         * 1. Retrieve all flash sale entries
         */
        List<FlashSaleProduct> fsProducts = flashSaleProductRepository.findAll();

        /*
         * 2. Efficiently fetch base products in bulk to avoid N+1 query problem
         */
        List<UUID> productIds = fsProducts.stream()
                .map(FlashSaleProduct::getProductId)
                .collect(Collectors.toList());
        List<Product> baseProducts = productRepository.findAllById(productIds);

        /*
         * 3. Map entities to ProductDTO for API response
         */
        return fsProducts.stream().map(fsp -> {
            // Find the corresponding base product details (name, original price)
            Product base = baseProducts.stream()
                    .filter(p -> p.getId().equals(fsp.getProductId()))
                    .findFirst()
                    .orElse(null);

            ProductDTO dto = new ProductDTO();
            dto.setId(fsp.getProductId());

            if (base != null) {
                dto.setName(base.getName());
                dto.setBasePrice(base.getBasePrice());
            }

            // Apply flash sale specific pricing and stock levels
            dto.setSalePrice(fsp.getSalePrice());
            dto.setStock(fsp.getStock());

            /*
             * IMPORTANT: Attach FlashSaleId so the Frontend can include it
             * in the purchase request payload.
             */
            if (fsp.getFlashSale() != null) {
                dto.setFlashSaleId(fsp.getFlashSale().getId());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}