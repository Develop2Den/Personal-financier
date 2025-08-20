package com.D2D.personal_financier.service;

import com.D2D.personal_financier.dto.categoryDTO.CategoryRequestDto;
import com.D2D.personal_financier.dto.categoryDTO.CategoryResponseDto;
import com.D2D.personal_financier.entity.Category;
import com.D2D.personal_financier.mapper.CategoryMapper;
import com.D2D.personal_financier.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category category = categoryMapper.toEntity(dto);
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return categoryMapper.toDto(category);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.name());
        category.setType(dto.type());
        categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}

