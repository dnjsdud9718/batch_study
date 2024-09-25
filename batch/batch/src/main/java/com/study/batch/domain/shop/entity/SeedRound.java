package com.study.batch.domain.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "seed_rounds")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
@Builder
@Getter
@ToString(exclude = {"seed"})
public class SeedRound {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "seed_round_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "seed_id")
    private Seed seed;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;
    private LocalDate transferDate;

    public void transferSucceed() {
        status = TransferStatus.SUCCESS;
    }
    public void transferFailed() {
        status = TransferStatus.FAIL;
    }
}
