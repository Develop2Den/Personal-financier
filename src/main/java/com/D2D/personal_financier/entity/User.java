package com.D2D.personal_financier.entity;

import com.D2D.personal_financier.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Category> categories;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Account> accounts;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Goal> goals;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Budget> budgets;
}

