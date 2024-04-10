package com.khomishchak.ws.services.exchangers.apikeys;

import com.khomishchak.ws.model.exchanger.DecryptedApiKeySettingDTO;

import java.util.List;

public interface ApiKeySettingService {
    List<DecryptedApiKeySettingDTO> getDecryptApiKeySettings(long userId);
}
