package com.rms.menu_item_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rms.menu_item_service.dto.ItemCategoryRequestDto;
import com.rms.menu_item_service.dto.ItemCategoryResponseDto;
import com.rms.menu_item_service.mapper.ItemCategoryMapper;
import com.rms.menu_item_service.model.ItemCategory;
import com.rms.menu_item_service.repository.ItemCategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemCategoryService {

        private final ItemCategoryRepository itemCategoryRepository;

        private final ItemCategoryMapper itemCategoryMapper;

        public ItemCategoryResponseDto createItemCategory(ItemCategoryRequestDto requestDto) {
                ItemCategory itemCategory = new ItemCategory();

                itemCategory.setCategory(requestDto.category());

                return itemCategoryMapper
                                .toItemCategoryResponseDto(
                                                itemCategoryRepository
                                                                .save(itemCategory));
        }

        public ItemCategoryResponseDto getItemCategoryById(Integer id) {
                ItemCategory category = itemCategoryRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Item Category not found with id: " + id));

                return itemCategoryMapper
                                .toItemCategoryResponseDto(category);
        }

        public List<ItemCategoryResponseDto> getAllCategories() {
                return itemCategoryRepository
                                .findAll()
                                .stream()
                                .map(itemCategoryMapper::toItemCategoryResponseDto)
                                .collect(Collectors.toList());
        }

        public ItemCategoryResponseDto updateItemCategory(
                        Integer id,
                        ItemCategoryRequestDto categoryRequestDto) {
                ItemCategory category = itemCategoryRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Item Category not found with id: " + id));

                category.setCategory(categoryRequestDto.category());

                return itemCategoryMapper
                                .toItemCategoryResponseDto(
                                                itemCategoryRepository
                                                                .save(category));
        }

        public void deleteItemCategory(Integer id) {
                if (!itemCategoryRepository.existsById(id)) {
                        throw new RuntimeException("Item Category not found with id: " + id);
                }

                itemCategoryRepository.deleteById(id);
        }
}
