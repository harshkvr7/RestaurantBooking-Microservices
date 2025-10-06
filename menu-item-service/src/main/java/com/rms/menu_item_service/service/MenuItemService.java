package com.rms.menu_item_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.rms.menu_item_service.dto.MenuItemRequestDto;
import com.rms.menu_item_service.dto.MenuItemResponseDto;
import com.rms.menu_item_service.mapper.MenuItemMapper;
import com.rms.menu_item_service.model.ItemCategory;
import com.rms.menu_item_service.model.ItemDescription;
import com.rms.menu_item_service.model.MenuItem;
import com.rms.menu_item_service.repository.ItemCategoryRepository;
import com.rms.menu_item_service.repository.ItemDescriptionRepository;
import com.rms.menu_item_service.repository.MenuItemRepository;
import com.rms.menu_item_service.repository.MenuItemSpecification;
import com.rms.menu_item_service.util.MenuItemUtil;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MenuItemService {

        private final MenuItemRepository menuItemRepository;

        private final MenuItemMapper menuItemMapper;

        private final ItemCategoryRepository itemCategoryRepository;

        private final ItemDescriptionRepository itemDescriptionRepository;

        private final MenuItemUtil menuItemUtil;

        // private final KafkaTemplate<String, Object> kafkaTemplate;

        // private static final String TOPIC = "menu-item-events";

        @Transactional
        @CachePut(value = "menu_items", key = "#result.id")
        @CacheEvict(value = { "menu_items_list", "menu_items_by_restaurant" }, allEntries = true)
        public MenuItemResponseDto createMenuItem(
                        MenuItemRequestDto requestDto,
                        Integer OwnerId) {

                menuItemUtil.verifyRestaurantOwner(requestDto.restaurantId(), OwnerId);

                ItemCategory itemCategory = itemCategoryRepository.findById(requestDto.categoryId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Category not found with id: " + requestDto.categoryId()));

                ItemDescription itemDescription = itemDescriptionRepository.findById(requestDto.descriptionId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Category not found with id: " + requestDto.categoryId()));

                MenuItem menuItem = new MenuItem();

                menuItem.setName(requestDto.name());
                menuItem.setRestaurantId(requestDto.restaurantId());
                menuItem.setCategory(itemCategory);
                menuItem.setDescription(itemDescription);
                menuItem.setPrice(requestDto.price());

                MenuItem savedMenuItem = menuItemRepository.save(menuItem);

                MenuItemResponseDto responseDto = menuItemMapper.toMenuItemResponseDto(savedMenuItem);

                return responseDto;
        }

        @Cacheable(value = "menu_items", key = "#id")
        public MenuItemResponseDto getMenuItemById(Integer id) {
                MenuItem menuItem = menuItemRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Menu Item not found with id: " + id));

                return menuItemMapper.toMenuItemResponseDto(menuItem);
        }

        @Cacheable(value = "menu_items_by_restaurant", key = "#id")
        public List<MenuItemResponseDto> getMenuItemsByRestaurant(Integer restaurantId) {
                return menuItemRepository
                                .findAllByRestaurantId(restaurantId)
                                .stream()
                                .map(menuItemMapper::toMenuItemResponseDto)
                                .collect(Collectors.toList());
        }

        @Cacheable(value = "menu_items_list", key = "'all'")
        public List<MenuItemResponseDto> getAllMenuItems(String name, String category) {
                Specification<MenuItem> spec = Specification.unrestricted();

                if (name != null && !name.isEmpty()) {
                        spec = spec.and(MenuItemSpecification.hasName(name));
                }
                if (category != null && !category.isEmpty()) {
                        spec = spec.and(MenuItemSpecification.hasCategory(category));
                }

                return menuItemRepository.findAll(spec)
                                .stream()
                                .map(menuItemMapper::toMenuItemResponseDto)
                                .collect(Collectors.toList());
        }

        @Transactional
        @Caching(evict = {
                        @CacheEvict(value = "menu_items", key = "#menuItemId"),
                        @CacheEvict(value = "menu_items_list", key = "#requestDto.restaurantId()"),
                        @CacheEvict(value = "menu_items_by_restaurant", allEntries = true)
        })
        public MenuItemResponseDto updateMenuItem(
                        Integer menuItemId,
                        Integer ownerId,
                        MenuItemRequestDto requestDto) {

                MenuItem existingMenuItem = menuItemRepository.findById(menuItemId)
                                .orElseThrow(() -> new RuntimeException("Menu Item not found with id : " + menuItemId));

                menuItemUtil.verifyRestaurantOwner(existingMenuItem.getRestaurantId(), ownerId);

                ItemCategory itemCategory = itemCategoryRepository.findById(requestDto.categoryId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Category not found with id: " + requestDto.categoryId()));

                ItemDescription itemDescription = itemDescriptionRepository.findById(requestDto.descriptionId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Category not found with id: " + requestDto.categoryId()));

                existingMenuItem.setName(requestDto.name());
                existingMenuItem.setRestaurantId(requestDto.restaurantId());
                existingMenuItem.setCategory(itemCategory);
                existingMenuItem.setDescription(itemDescription);
                existingMenuItem.setPrice(requestDto.price());

                MenuItem savedMenuItem = menuItemRepository.save(existingMenuItem);

                MenuItemResponseDto responseDto = menuItemMapper.toMenuItemResponseDto(savedMenuItem);

                return responseDto;
        }

        @Caching(evict = {
                        @CacheEvict(value = "menu_items", key = "#id"),
                        @CacheEvict(value = "menu_items_list", allEntries = true),
                        @CacheEvict(value = "menu_items_by_restaurant", allEntries = true)
        })
        public void deleteMenuItem(Integer id) {
                if (!menuItemRepository.existsById(id)) {
                        throw new RuntimeException("Menu Item not found with id : " + id);
                }

                menuItemRepository.deleteById(id);
        }
}
