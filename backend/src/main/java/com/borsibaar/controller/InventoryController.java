package com.borsibaar.controller;

import com.borsibaar.annotation.CurrentUser;
import com.borsibaar.annotation.IsOnboardedAdmin;
import com.borsibaar.dto.*;
import com.borsibaar.entity.User;
import com.borsibaar.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryResponseDto> getOrganizationInventory(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long organizationId,
            @CurrentUser User user
            ) {
        // If organizationId is provided, use it (for public access)
        // Otherwise, get from authenticated user
        Long orgId;
        if (organizationId != null) {
            orgId = organizationId;
        } else {
            orgId = user.getOrganizationId();
        }
        return inventoryService.getByOrganization(orgId, categoryId);
    }

    @GetMapping("/product/{productId}")
    public InventoryResponseDto getProductInventory(@PathVariable Long productId, @CurrentUser User user) {
        return inventoryService.getByProductAndOrganization(productId, user.getOrganizationId());
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponseDto addStock(@RequestBody @Valid AddStockRequestDto request, @CurrentUser User user) {
        System.out.println("Received request: " + request); // DEBUG
        System.out.println("ProductId: " + request.productId()); // DEBUG
        System.out.println("Quantity: " + request.quantity()); // DEBUG

        return inventoryService.addStock(request, user.getId(), user.getOrganizationId());
    }

    @PostMapping("/remove")
    public InventoryResponseDto removeStock(@RequestBody @Valid RemoveStockRequestDto request, @CurrentUser User user) {
        return inventoryService.removeStock(request, user.getId(), user.getOrganizationId());
    }

    @PostMapping("/adjust")
    public InventoryResponseDto adjustStock(@RequestBody @Valid AdjustStockRequestDto request, @CurrentUser User user) {
        return inventoryService.adjustStock(request, user.getId(), user.getOrganizationId());
    }

    @GetMapping("/product/{productId}/history")
    public List<InventoryTransactionResponseDto> getTransactionHistory(@PathVariable Long productId, @CurrentUser User user) {
        return inventoryService.getTransactionHistory(productId, user.getOrganizationId());
    }

    @GetMapping("/sales-stats")
    public List<UserSalesStatsResponseDto> getUserSalesStats(@CurrentUser User user) {
        return inventoryService.getUserSalesStats(user.getOrganizationId());
    }

    @GetMapping("/station-sales-stats")
    public List<StationSalesStatsResponseDto> getStationSalesStats(@CurrentUser User user) {
        return inventoryService.getStationSalesStats(user.getOrganizationId());
    }
}
