package com.D2D.personal_financier.entity;

import com.D2D.personal_financier.entity.enums.GoalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Double targetAmount;

    private Double currentAmount = 0.0;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private GoalStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;
}

