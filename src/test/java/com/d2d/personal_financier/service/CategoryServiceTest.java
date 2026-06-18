package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.HtmlSanitizerService;
import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.category_dto.CategoryRequestDto;
import com.d2d.personal_financier.dto.category_dto.CategoryResponseDto;
import com.d2d.personal_financier.entity.Category;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.entity.enums.TransactionType;
import com.d2d.personal_financier.exception.CategoryAlreadyExistsException;
import com.d2d.personal_financier.mapper.CategoryMapper;
import com.d2d.personal_financier.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private HtmlSanitizerService sanitizer;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void deleteCategoryShouldArchiveWithoutPhysicalDelete() {
        User user = User.builder().id(1L).build();
        Category category = new Category();
        category.setId(20L);
        category.setOwner(user);
        category.setActive(true);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findByIdAndOwnerIdAndActiveTrue(20L, 1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(20L);

        assertEquals(false, category.getActive());
        verify(categoryRepository).save(category);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void createCategoryShouldRestoreArchivedCategory() {
        User user = User.builder().id(1L).build();
        Category archivedCategory = new Category();
        archivedCategory.setId(20L);
        archivedCategory.setName("Food");
        archivedCategory.setType(TransactionType.EXPENSE);
        archivedCategory.setOwner(user);
        archivedCategory.setActive(false);

        CategoryRequestDto request = new CategoryRequestDto("Food", TransactionType.EXPENSE);
        CategoryResponseDto expected = new CategoryResponseDto(20L, "Food", TransactionType.EXPENSE, true);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sanitizer.sanitize("Food")).thenReturn("Food");
        when(categoryRepository.findByNameAndOwnerId("Food", 1L)).thenReturn(Optional.of(archivedCategory));
        when(categoryMapper.toDto(archivedCategory)).thenReturn(expected);

        CategoryResponseDto response = categoryService.createCategory(request);

        assertEquals(true, archivedCategory.getActive());
        assertSame(expected, response);
        verify(categoryRepository).save(archivedCategory);
    }

    @Test
    void createCategoryShouldNotCreateNewEntityWhenRestoringArchivedCategory() {
        User user = User.builder().id(1L).build();
        Category archivedCategory = new Category();
        archivedCategory.setId(20L);
        archivedCategory.setName("Food");
        archivedCategory.setType(TransactionType.EXPENSE);
        archivedCategory.setOwner(user);
        archivedCategory.setActive(false);

        CategoryRequestDto request = new CategoryRequestDto("Food", TransactionType.EXPENSE);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sanitizer.sanitize("Food")).thenReturn("Food");
        when(categoryRepository.findByNameAndOwnerId("Food", 1L)).thenReturn(Optional.of(archivedCategory));

        categoryService.createCategory(request);

        verify(categoryMapper, never()).toEntity(any(CategoryRequestDto.class));
        verify(categoryRepository).save(archivedCategory);
    }

    @Test
    void createCategoryShouldRejectExistingActiveCategory() {
        User user = User.builder().id(1L).build();
        Category activeCategory = new Category();
        activeCategory.setId(20L);
        activeCategory.setName("Food");
        activeCategory.setType(TransactionType.EXPENSE);
        activeCategory.setOwner(user);
        activeCategory.setActive(true);

        CategoryRequestDto request = new CategoryRequestDto("Food", TransactionType.EXPENSE);

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(sanitizer.sanitize("Food")).thenReturn("Food");
        when(categoryRepository.findByNameAndOwnerId("Food", 1L)).thenReturn(Optional.of(activeCategory));

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(request));
        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toEntity(any(CategoryRequestDto.class));
    }
}
