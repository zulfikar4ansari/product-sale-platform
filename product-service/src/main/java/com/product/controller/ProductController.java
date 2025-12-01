package com.product.controller;


import com.product.dto.ProductRequest;
import com.product.dto.ProductResponse;
import com.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Helper to check if header role is ADMIN
    private boolean isAdmin(String roleHeader) {
        return roleHeader != null && roleHeader.equalsIgnoreCase("ADMIN");
    }

    // -------------------- PUBLIC (any authenticated user via gateway) --------------------

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getByCategory(@PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchByName(@RequestParam("q") String query) {
        List<ProductResponse> products = productService.searchProductsByName(query);
        return ResponseEntity.ok(products);
    }

    // -------------------- ADMIN ONLY --------------------

    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestHeader(value = "X-Auth-Role", required = false) String role,
            @Valid @RequestBody ProductRequest request) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: ADMIN role required");
        }

        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @RequestHeader(value = "X-Auth-Role", required = false) String role,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: ADMIN role required");
        }

        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @RequestHeader(value = "X-Auth-Role", required = false) String role,
            @PathVariable Long id) {

        if (!isAdmin(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: ADMIN role required");
        }

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
