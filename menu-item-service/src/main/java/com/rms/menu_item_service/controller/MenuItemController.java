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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rms.menu_item_service.dto.MenuItemRequestDto;
import com.rms.menu_item_service.dto.MenuItemResponseDto;
import com.rms.menu_item_service.service.MenuItemService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/menu-items")
@AllArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<MenuItemResponseDto> createMenuItem(
            @RequestBody MenuItemRequestDto requestDto,
            @RequestHeader("X-User-Id") Integer ownerId) {
        try {
            MenuItemResponseDto responseDto = menuItemService.createMenuItem(requestDto, ownerId);
            return ResponseEntity.ok(responseDto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            System.out.print(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponseDto> getMenuItemById(@PathVariable Integer id) {
        try {
            MenuItemResponseDto responseDto = menuItemService.getMenuItemById(id);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemResponseDto>> getAllMenuItemsByRestaurant(@PathVariable Integer restaurantId) {
        try {
            List<MenuItemResponseDto> responseDto = menuItemService.getMenuItemsByRestaurant(restaurantId);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponseDto>> getAllMenuItems(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
                
        List<MenuItemResponseDto> menuItems = menuItemService.getAllMenuItems(name, category);
        return ResponseEntity.ok(menuItems);
    }
    

    @PutMapping("/{menuItemId}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<MenuItemResponseDto> updateMenuItem(
            @PathVariable Integer menuItemId,
            @RequestBody MenuItemRequestDto requestDto,
            @RequestHeader("X-User-Id") Integer ownerID) {

        try {
            MenuItemResponseDto responseDto = menuItemService.updateMenuItem(menuItemId, ownerID, requestDto);
            return ResponseEntity.ok(responseDto);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @DeleteMapping("/{menuItemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Integer menuItemId) {
        menuItemService.deleteMenuItem(menuItemId);

        return ResponseEntity.noContent().build();
    }
}
