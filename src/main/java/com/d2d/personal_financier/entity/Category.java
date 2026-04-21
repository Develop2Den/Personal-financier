package com.d2d.personal_financier.entity;

import com.d2d.personal_financier.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
    name = "categories",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_category_per_user", columnNames = {"name", "user_id"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets;
}

