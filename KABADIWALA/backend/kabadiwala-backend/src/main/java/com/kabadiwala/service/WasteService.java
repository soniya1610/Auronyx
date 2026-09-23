package com.kabadiwala.service;

import com.kabadiwala.dto.WasteCategoryDto;
import com.kabadiwala.dto.WasteItemDto;
import com.kabadiwala.entity.Waste;
import com.kabadiwala.entity.WasteCategory;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.WasteCategoryRepository;
import com.kabadiwala.repository.WasteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WasteService {

    private final WasteCategoryRepository categoryRepository;
    private final WasteRepository wasteRepository;

    public WasteService(WasteCategoryRepository categoryRepository, WasteRepository wasteRepository) {
        this.categoryRepository = categoryRepository;
        this.wasteRepository = wasteRepository;
    }

    @Transactional(readOnly = true)
    public List<WasteCategoryDto> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WasteCategoryDto getCategoryById(Long id) {
        WasteCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteCategory", "id", id));
        return mapCategoryToDto(category);
    }

    @Transactional(readOnly = true)
    public List<WasteItemDto> getAllItems() {
        return wasteRepository.findByActiveTrue().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WasteItemDto getItemById(Long id) {
        Waste item = wasteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteItem", "id", id));
        return mapItemToDto(item);
    }

    @Transactional(readOnly = true)
    public List<WasteItemDto> getItemsByCategoryId(Long categoryId) {
        return wasteRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList());
    }

    public WasteCategoryDto mapCategoryToDto(WasteCategory category) {
        return new WasteCategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getActive(),
                category.getUnit(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    public WasteItemDto mapItemToDto(Waste item) {
        return new WasteItemDto(
                item.getId(),
                item.getCategory().getId(),
                item.getCategory().getName(),
                item.getName(),
                item.getDescription(),
                item.getActive(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
