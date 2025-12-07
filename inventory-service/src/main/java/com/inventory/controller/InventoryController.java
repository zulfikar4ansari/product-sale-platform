package com.inventory.controller;

import com.inventory.dto.StockRequest;
import com.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getStock(productId));
    }

    @PutMapping("/increase/{productId}")
    public ResponseEntity<String> increase(
            @PathVariable Long productId, @RequestBody StockRequest req) {

        service.increaseStock(productId, req.getQuantity());
        return ResponseEntity.ok("Stock increased");
    }

    @PutMapping("/reduce/{productId}")
    public ResponseEntity<String> reduce(
            @PathVariable Long productId, @RequestBody StockRequest req) {

        service.reduceStock(productId, req.getQuantity());
        return ResponseEntity.ok("Stock reduced");
    }

    @GetMapping("/health")
    public String health() { return "Inventory service running"; }
}
