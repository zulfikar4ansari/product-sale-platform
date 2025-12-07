package com.inventory.service;


import com.inventory.entity.Inventory;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repo;

    @Override
    public Integer getStock(Long productId) {
        return repo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory for product " + productId))
                .getStockQty();
    }

    @Override
    public void increaseStock(Long productId, int qty) {
        Inventory inv = repo.findByProductId(productId)
                .orElse(Inventory.builder().productId(productId).stockQty(0).build());

        inv.setStockQty(inv.getStockQty() + qty);
        repo.save(inv);
    }

    @Override
    public void reduceStock(Long productId, int qty) {
        Inventory inv = repo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory for product " + productId));

        if (inv.getStockQty() < qty)
            throw new RuntimeException("Insufficient stock");

        inv.setStockQty(inv.getStockQty() - qty);
        repo.save(inv);
    }
}
