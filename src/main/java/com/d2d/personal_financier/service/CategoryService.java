package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.categoryDTO.CategoryRequestDto;
import com.d2d.personal_financier.dto.categoryDTO.CategoryResponseDto;
import com.d2d.personal_financier.entity.Category;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.exception.CategoryNotFoundException;
import com.d2d.personal_financier.mapper.CategoryMapper;
import com.d2d.personal_financier.repository.CategoryRepository;
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
    private final HtmlSanitizerService sanitizer;

    public CategoryResponseDto createCategory(CategoryRequestDto dto) {

        Category category = categoryMapper.toEntity(dto);

        category.setName(
            sanitizer.sanitize(dto.name())
        );

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

        User user = securityUtils.getCurrentUser();

        Category category = categoryRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new CategoryNotFoundException(id));

        return categoryMapper.toDto(category);
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {

        User user = securityUtils.getCurrentUser();

        Category category = categoryRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new CategoryNotFoundException(id));

        category.setName(dto.name());
        category.setType(dto.type());

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    public void deleteCategory(Long id) {

        User user = securityUtils.getCurrentUser();

        Category category = categoryRepository.findByIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new CategoryNotFoundException(id));

        categoryRepository.delete(category);
    }
}

