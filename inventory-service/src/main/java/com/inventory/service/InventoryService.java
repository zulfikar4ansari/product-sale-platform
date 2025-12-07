package com.inventory.service;

public interface InventoryService {
    Integer getStock(Long productId);
    void increaseStock(Long productId, int qty);
    void reduceStock(Long productId, int qty);
}
