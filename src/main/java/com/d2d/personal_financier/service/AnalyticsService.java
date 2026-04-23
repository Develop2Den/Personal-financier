package com.d2d.personal_financier.service;

import com.d2d.personal_financier.config.security.utils.SecurityUtils;
import com.d2d.personal_financier.dto.analytics.CategoryBreakdownDto;
import com.d2d.personal_financier.dto.analytics.DashboardDto;
import com.d2d.personal_financier.dto.analytics.MonthlyCashflowDto;
import com.d2d.personal_financier.dto.analytics.MonthlyExpenseDto;
import com.d2d.personal_financier.entity.Account;
import com.d2d.personal_financier.entity.Transaction;
import com.d2d.personal_financier.entity.User;
import com.d2d.personal_financier.repository.AccountRepository;
import com.d2d.personal_financier.repository.GoalRepository;
import com.d2d.personal_financier.repository.TransactionRepository;
import com.d2d.personal_financier.entity.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AccountRepository accountRepository;
    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityUtils securityUtils;

    public List<MonthlyExpenseDto> getMonthlyExpenses() {

        User user = securityUtils.getCurrentUser();

        return buildMonthlyCashflow(user.getId())
            .stream()
            .map(item -> new MonthlyExpenseDto(item.month(), item.expenses()))
            .toList();
    }

    public List<MonthlyCashflowDto> getMonthlyCashflow() {

        User user = securityUtils.getCurrentUser();

        return buildMonthlyCashflow(user.getId());
    }

    public List<CategoryBreakdownDto> getTopCategories(String month) {

        User user = securityUtils.getCurrentUser();
        YearMonth selectedMonth = parseMonthOrCurrent(month);

        return buildCategoryBreakdown(user.getId(), selectedMonth);
    }

    public DashboardDto getDashboard(String month) {

        User user = securityUtils.getCurrentUser();
        YearMonth selectedMonth = parseMonthOrCurrent(month);
        List<Transaction> userTransactions = transactionRepository.findByOwnerId(user.getId());
        List<Transaction> monthlyTransactions = filterTransactionsByMonth(userTransactions, selectedMonth);
        List<CategoryBreakdownDto> topCategories = buildCategoryBreakdown(monthlyTransactions);

        BigDecimal totalBalance =
                accountRepository.findByOwnerId(user.getId())
                        .stream()
                        .map(Account::getBalance)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal monthlyIncome = sumByType(monthlyTransactions, TransactionType.INCOME);
        BigDecimal monthlyExpenses = sumByType(monthlyTransactions, TransactionType.EXPENSE);
        BigDecimal netCashflow = monthlyIncome.subtract(monthlyExpenses);

        String topCategory =
                topCategories
                        .stream()
                        .findFirst()
                        .map(CategoryBreakdownDto::category)
                        .orElse("None");

        Long activeGoals =
                goalRepository.findByOwnerId(user.getId())
                        .stream()
                        .count();

        return new DashboardDto(
                totalBalance,
                monthlyIncome,
                monthlyExpenses,
                netCashflow,
                topCategory,
                activeGoals,
                (long) monthlyTransactions.size()
        );
    }

    private List<MonthlyCashflowDto> buildMonthlyCashflow(Long userId) {

        Map<YearMonth, List<Transaction>> transactionsByMonth = transactionRepository.findByOwnerId(userId)
            .stream()
            .collect(Collectors.groupingBy(
                transaction -> YearMonth.from(transaction.getDate()),
                LinkedHashMap::new,
                Collectors.toList()
            ));

        return transactionsByMonth.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                BigDecimal income = sumByType(entry.getValue(), TransactionType.INCOME);
                BigDecimal expenses = sumByType(entry.getValue(), TransactionType.EXPENSE);

                return new MonthlyCashflowDto(
                    entry.getKey().toString(),
                    income,
                    expenses,
                    income.subtract(expenses)
                );
            })
            .toList();
    }

    private List<CategoryBreakdownDto> buildCategoryBreakdown(Long userId, YearMonth selectedMonth) {

        List<Transaction> monthlyTransactions = filterTransactionsByMonth(
            transactionRepository.findByOwnerId(userId),
            selectedMonth
        );

        return buildCategoryBreakdown(monthlyTransactions);
    }

    private List<CategoryBreakdownDto> buildCategoryBreakdown(List<Transaction> monthlyTransactions) {

        Map<String, List<Transaction>> transactionsByCategory = monthlyTransactions
            .stream()
            .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
            .collect(Collectors.groupingBy(
                this::resolveCategoryName,
                LinkedHashMap::new,
                Collectors.toList()
            ));

        BigDecimal totalExpenses = sumByType(monthlyTransactions, TransactionType.EXPENSE);

        return transactionsByCategory.entrySet()
            .stream()
            .map(entry -> {
                BigDecimal amount = entry.getValue()
                    .stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal percentage = BigDecimal.ZERO;

                if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                    percentage = amount
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalExpenses, 2, RoundingMode.HALF_UP);
                }

                return new CategoryBreakdownDto(
                    entry.getKey(),
                    amount,
                    percentage,
                    (long) entry.getValue().size()
                );
            })
            .sorted(Comparator.comparing(CategoryBreakdownDto::amount).reversed())
            .limit(5)
            .toList();
    }

    private List<Transaction> filterTransactionsByMonth(List<Transaction> transactions, YearMonth selectedMonth) {

        return transactions.stream()
            .filter(transaction -> YearMonth.from(transaction.getDate()).equals(selectedMonth))
            .toList();
    }

    private BigDecimal sumByType(List<Transaction> transactions, TransactionType type) {

        return transactions.stream()
            .filter(transaction -> transaction.getType() == type)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private YearMonth parseMonthOrCurrent(String month) {

        if (month == null || month.isBlank()) {
            return YearMonth.now();
        }

        try {
            return YearMonth.parse(month);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid month format. Expected YYYY-MM");
        }
    }

    private String resolveCategoryName(Transaction transaction) {

        if (transaction.getCategory() == null || transaction.getCategory().getName() == null) {
            return "Uncategorized";
        }

        return transaction.getCategory().getName();
    }
}
