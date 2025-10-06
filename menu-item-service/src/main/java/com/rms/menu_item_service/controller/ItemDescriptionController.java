package com.rms.menu_item_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.rms.menu_item_service.dto.ItemDescriptionRequestDto;
import com.rms.menu_item_service.dto.ItemDescriptionResponseDto;
import com.rms.menu_item_service.service.ItemDescriptionService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/menu-items/descriptions")
public class ItemDescriptionController {

    private final ItemDescriptionService itemDescriptionService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ItemDescriptionResponseDto> createItemDescription(
            @RequestBody ItemDescriptionRequestDto requestDto) {
        ItemDescriptionResponseDto createdDescription = itemDescriptionService.createItemDescription(requestDto);
        return new ResponseEntity<>(createdDescription, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ItemDescriptionResponseDto> updateItemDescription(
            @PathVariable Integer id,
            @RequestBody ItemDescriptionRequestDto requestDto) {
        ItemDescriptionResponseDto updatedDescription = itemDescriptionService.updateItemDescription(id, requestDto);
        return ResponseEntity.ok(updatedDescription);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteItemDescription(@PathVariable Integer id) {
        itemDescriptionService.deleteItemDescription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDescriptionResponseDto> getItemDescriptionById(@PathVariable Integer id) {
        ItemDescriptionResponseDto description = itemDescriptionService.getItemDescriptionById(id);
        return ResponseEntity.ok(description);
    }

    @GetMapping
    public ResponseEntity<List<ItemDescriptionResponseDto>> getAllItemDescriptions() {
        List<ItemDescriptionResponseDto> descriptions = itemDescriptionService.getAllItemDescriptions();
        return ResponseEntity.ok(descriptions);
    }
}