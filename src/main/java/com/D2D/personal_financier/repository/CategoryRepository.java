package com.D2D.personal_financier.repository;

import com.D2D.personal_financier.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

