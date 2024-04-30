package com.walletsphere.wsmonolith.model.exchanger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_key_settings")
public class ApiKeySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private User user;

    @OneToOne
    @JoinColumn(name = "balance_id")
    private Balance balance;

    @Enumerated(value = EnumType.STRING)
    private ExchangerCode code;

    @Embedded
    private ApiKeysPair apiKeys;
}