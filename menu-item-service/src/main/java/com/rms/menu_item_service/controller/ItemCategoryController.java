package com.rms.menu_item_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rms.menu_item_service.dto.ItemCategoryRequestDto;
import com.rms.menu_item_service.dto.ItemCategoryResponseDto;
import com.rms.menu_item_service.service.ItemCategoryService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/menu-items/categories")
public class ItemCategoryController {

        private final ItemCategoryService itemCategoryService;

        @PostMapping
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ItemCategoryResponseDto> createItemCategory(
                        @RequestBody ItemCategoryRequestDto requestDto) {
                return ResponseEntity.ok(
                                itemCategoryService
                                                .createItemCategory(requestDto));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<ItemCategoryResponseDto> updateItemCategory(
                        @PathVariable Integer id,
                        @RequestBody ItemCategoryRequestDto categoryRequestDto) {

                return ResponseEntity.ok(
                                itemCategoryService
                                                .updateItemCategory(id, categoryRequestDto));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('ADMIN')")
        public ResponseEntity<Void> deleteItemCategory(@PathVariable Integer id) {
                itemCategoryService.deleteItemCategory(id);

                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}")
        public ResponseEntity<ItemCategoryResponseDto> getItemCategoryById(@PathVariable Integer id) {
                return ResponseEntity.ok(
                                itemCategoryService
                                                .getItemCategoryById(id));
        }

        @GetMapping
        public ResponseEntity<List<ItemCategoryResponseDto>> getAllItemCategories() {
                return ResponseEntity.ok(
                                itemCategoryService
                                                .getAllCategories());
        }
}
