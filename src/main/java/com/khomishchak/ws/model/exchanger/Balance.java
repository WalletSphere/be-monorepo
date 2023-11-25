package com.khomishchak.ws.model.exchanger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String balanceName;

    private LocalDateTime lastTimeWasUpdated;

    @Enumerated(value = EnumType.STRING)
    private ExchangerCode code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    private List<Currency> currencies;

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonProperty("user_id")
    public Long getUserId() {
        return user.getId();
    }
}
