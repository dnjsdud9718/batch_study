package com.study.batch.domain.shop.entity;

import com.study.batch.domain.account.entity.Account;
import com.study.batch.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seeds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@ToString(exclude = {"seedRounds", "user"})
public class Seed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seed_id")
    private Long id;

    @Builder.Default // EXP : warning: @Builder will ignore the initializing expression entirely.
    @OneToMany(mappedBy = "seed")
    private List<SeedRound> seedRounds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_account_id")
    private Account depositAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdrawal_account_id")
    private Account withdrawalAccount;

    private String title;
    private Integer targetAmount;
    private Integer transferAmount;
    private Integer entireRound;

    @Enumerated(EnumType.STRING)
    private PeriodStatus period;

    private LocalDate startDate;
    private LocalDate endDate;


    @Enumerated(EnumType.STRING)
    private SeedStatus status;
}
