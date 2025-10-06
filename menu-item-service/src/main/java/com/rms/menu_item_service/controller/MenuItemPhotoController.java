package com.rms.menu_item_service.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rms.menu_item_service.service.MenuItemPhotoService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/menu-items/photos/{menuItemId}") 
@AllArgsConstructor
public class MenuItemPhotoController {

    private final MenuItemPhotoService menuItemPhotoService;

    @PostMapping
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<String> addPhoto(
            @PathVariable Integer menuItemId,
            @RequestHeader("X-User-Id") Integer ownerId,
            @RequestParam("file") MultipartFile file) { 
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File cannot be empty.");
            }

            String imageUrl = menuItemPhotoService.addPhoto(menuItemId, ownerId, file);

            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file.");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Integer menuItemId,
            @RequestHeader("X-User-Id") Integer ownerId) {
        try {
            menuItemPhotoService.deletePhoto(menuItemId, ownerId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
