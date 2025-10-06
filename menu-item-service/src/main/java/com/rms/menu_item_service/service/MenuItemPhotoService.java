package com.rms.menu_item_service.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.rms.menu_item_service.model.MenuItem;
import com.rms.menu_item_service.repository.MenuItemRepository;
import com.rms.menu_item_service.util.MenuItemUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuItemPhotoService {

    private final MenuItemRepository menuItemRepository;

    private final MenuItemUtil menuItemUtil;

    private final RestTemplate restTemplate;

    @Value("${service.urls.api-gateway}")
    private String mediaServiceUrl;

    
    

    @Transactional
    public String addPhoto(
            Integer menuItemId,
            Integer ownerId,
            MultipartFile file) throws IOException {

        String MEDIA_SERVICE_UPLOAD_URL = mediaServiceUrl + "/media/upload";

        MenuItem existingMenuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu Item not found with id : " + menuItemId));

        menuItemUtil.verifyRestaurantOwner(existingMenuItem.getRestaurantId(), ownerId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(MEDIA_SERVICE_UPLOAD_URL, requestEntity,
                String.class);
        String photoUrl = response.getBody();

        existingMenuItem.setImageUrl(photoUrl);

        menuItemRepository.save(existingMenuItem);

        return photoUrl;
    }

    @Transactional
    public void deletePhoto(
            Integer menuItemId,
            Integer ownerId) {

        String MEDIA_SERVICE_DELETE_URL = mediaServiceUrl + "/media/delete";

        MenuItem existingMenuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu Item not found with id : " + menuItemId));

        menuItemUtil.verifyRestaurantOwner(existingMenuItem.getRestaurantId(), ownerId);

        if(existingMenuItem.getImageUrl() == null) {
            throw new RuntimeException("image does not exist");
        }

        String encodedUrl = Base64.getEncoder().encodeToString(existingMenuItem.getImageUrl().getBytes());

        try {
            restTemplate.delete(MEDIA_SERVICE_DELETE_URL + "/" + encodedUrl);
        } catch (RestClientException e) {
            System.err.println("Failed to delete photo from media-service: " + existingMenuItem.getImageUrl() + ". Error: " + e.getMessage());
        }

        existingMenuItem.setImageUrl(null);

        menuItemRepository.save(existingMenuItem);
    }
}
