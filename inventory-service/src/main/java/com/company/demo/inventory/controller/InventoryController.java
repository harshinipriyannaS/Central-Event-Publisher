package com.company.demo.inventory.controller;

import com.company.demo.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/update")
    public String updateInventory(@RequestParam String productId, @RequestParam int quantity) {
        inventoryService.updateInventory(productId, quantity);
        return "Inventory updated and event published";
    }

    @GetMapping("/health")
    public String health() {
        return "Inventory Service is running on port 8081";
    }
}
