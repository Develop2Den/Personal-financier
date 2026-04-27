package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByOwnerId(Long ownerId);
    Page<Category> findByOwnerId(Long ownerId, Pageable pageable);
    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);
}
