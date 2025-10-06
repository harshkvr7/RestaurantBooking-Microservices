package com.rms.menu_item_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rms.menu_item_service.dto.ItemDescriptionRequestDto;
import com.rms.menu_item_service.dto.ItemDescriptionResponseDto;
import com.rms.menu_item_service.mapper.ItemDescriptionMapper;
import com.rms.menu_item_service.model.ItemDescription;
import com.rms.menu_item_service.repository.ItemDescriptionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemDescriptionService {

    private final ItemDescriptionRepository itemDescriptionRepository;
    private final ItemDescriptionMapper itemDescriptionMapper;

    public ItemDescriptionResponseDto createItemDescription(ItemDescriptionRequestDto requestDto) {
        ItemDescription itemDescription = new ItemDescription();

        itemDescription.setName(requestDto.name());
        itemDescription.setDescription(requestDto.description());
        
        ItemDescription savedDescription = itemDescriptionRepository.save(itemDescription);
        
        return itemDescriptionMapper.toItemDescriptionResponseDto(savedDescription);
    }

    public ItemDescriptionResponseDto getItemDescriptionById(Integer id) {
        ItemDescription description = itemDescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ItemDescription not found with id: " + id));
        
        return itemDescriptionMapper.toItemDescriptionResponseDto(description);
    }

    public List<ItemDescriptionResponseDto> getAllItemDescriptions() {
        return itemDescriptionRepository.findAll()
                .stream()
                .map(itemDescriptionMapper::toItemDescriptionResponseDto)
                .collect(Collectors.toList());
    }

    public ItemDescriptionResponseDto updateItemDescription(Integer id, ItemDescriptionRequestDto requestDto) {
        ItemDescription existingDescription = itemDescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ItemDescription not found with id: " + id));

        existingDescription.setName(requestDto.name());
        existingDescription.setDescription(requestDto.description());
        
        ItemDescription updatedDescription = itemDescriptionRepository.save(existingDescription);
        
        return itemDescriptionMapper.toItemDescriptionResponseDto(updatedDescription);
    }

    public void deleteItemDescription(Integer id) {
        if (!itemDescriptionRepository.existsById(id)) {
            throw new RuntimeException("ItemDescription not found with id: " + id);
        }
        itemDescriptionRepository.deleteById(id);
    }
}