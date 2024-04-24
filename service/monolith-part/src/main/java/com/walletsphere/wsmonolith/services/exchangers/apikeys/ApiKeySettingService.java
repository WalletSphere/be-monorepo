package com.walletsphere.wsmonolith.services.exchangers.apikeys;

import com.walletsphere.wsmonolith.model.exchanger.DecryptedApiKeySettingDTO;

import java.util.List;

public interface ApiKeySettingService {
    List<DecryptedApiKeySettingDTO> getDecryptApiKeySettings(long userId);
}
