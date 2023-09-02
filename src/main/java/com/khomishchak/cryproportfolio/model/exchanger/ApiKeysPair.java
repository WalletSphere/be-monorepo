package com.khomishchak.cryproportfolio.model.exchanger;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Embeddable
public class ApiKeysPair {

    private String publicApi;
    private String privateKey;
}
