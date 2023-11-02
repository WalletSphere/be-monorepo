package com.khomishchak.cryptoportfolio.model.exchanger;

import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DecryptedApiKeySettingDTO {

    private ExchangerCode code;
    private String privateKey;
    private String publicKey;
}
