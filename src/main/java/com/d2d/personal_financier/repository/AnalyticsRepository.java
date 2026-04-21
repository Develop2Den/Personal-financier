package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.dto.analytics.CategoryReportDto;
import com.d2d.personal_financier.dto.analytics.MonthlyExpenseDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnalyticsRepository {

    private final EntityManager entityManager;

    public List<MonthlyExpenseDto> getMonthlyExpenses(Long userId) {

        String jpql = """
                SELECT new com.d2d.personal_financier.dto.analytics.MonthlyExpenseDto(
                    FUNCTION('TO_CHAR', t.date, 'YYYY-MM'),
                    SUM(t.amount)
                )
                FROM Transaction t
                WHERE t.owner.id = :userId
                AND t.type = 'EXPENSE'
                GROUP BY FUNCTION('TO_CHAR', t.date, 'YYYY-MM')
                ORDER BY FUNCTION('TO_CHAR', t.date, 'YYYY-MM')
                """;

        return entityManager.createQuery(jpql, MonthlyExpenseDto.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<CategoryReportDto> getTopCategories(Long userId) {

        String jpql = """
                SELECT new com.d2d.personal_financier.dto.analytics.CategoryReportDto(
                    c.name,
                    SUM(t.amount)
                )
                FROM Transaction t
                JOIN t.category c
                WHERE t.owner.id = :userId
                AND t.type = 'EXPENSE'
                GROUP BY c.name
                ORDER BY SUM(t.amount) DESC
                """;

        return entityManager.createQuery(jpql, CategoryReportDto.class)
                .setParameter("userId", userId)
                .setMaxResults(5)
                .getResultList();
    }

}
