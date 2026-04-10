package com.D2D.personal_financier.service;

import com.D2D.personal_financier.config.security.SecurityUtils;
import com.D2D.personal_financier.dto.categoryDTO.CategoryRequestDto;
import com.D2D.personal_financier.dto.categoryDTO.CategoryResponseDto;
import com.D2D.personal_financier.entity.Category;
import com.D2D.personal_financier.entity.User;
import com.D2D.personal_financier.mapper.CategoryMapper;
import com.D2D.personal_financier.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SecurityUtils securityUtils;
    private final CategoryMapper categoryMapper;

    public CategoryResponseDto createCategory(CategoryRequestDto dto) {

        Category category = categoryMapper.toEntity(dto);

        User user = securityUtils.getCurrentUser();

        category.setOwner(user);

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    public List<CategoryResponseDto> getAllCategories() {

        User user = securityUtils.getCurrentUser();

        return categoryRepository.findByOwnerId(user.getId())
                .stream()
                .map(categoryMapper::toDto)
                .toList();
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

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }
}

