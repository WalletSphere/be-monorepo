package com.khomishchak.cryproportfolio.model;

import com.khomishchak.cryproportfolio.model.enums.UserRole;
import com.khomishchak.cryproportfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;
import com.khomishchak.cryproportfolio.model.goals.CryptoGoalsTable;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    private LocalDateTime createdTime;
    private LocalDateTime lastLoginTime;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToOne(fetch = FetchType.LAZY)
    private CryptoGoalsTable cryptoGoalsTable;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ApiKeySetting> apiKeysSettings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Balance> balances;
}
