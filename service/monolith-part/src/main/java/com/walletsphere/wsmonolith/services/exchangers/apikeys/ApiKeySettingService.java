package com.walletsphere.wsmonolith.services.exchangers.apikeys;

import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeySetting;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.model.exchanger.DecryptedApiKeySettingDTO;
import com.walletsphere.wsmonolith.model.requests.RegisterApiKeysReq;

import java.util.List;

public interface ApiKeySettingService {
    List<DecryptedApiKeySettingDTO> getDecryptApiKeySettings(long userId);

    ApiKeySetting saveApiKeysSettings(User user, Balance balance, RegisterApiKeysReq apiKeysPair);
}
