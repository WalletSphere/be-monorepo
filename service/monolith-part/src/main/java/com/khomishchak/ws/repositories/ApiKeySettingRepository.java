package com.khomishchak.ws.repositories;

import com.khomishchak.ws.model.exchanger.ApiKeySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiKeySettingRepository extends JpaRepository<ApiKeySetting, Long> {

    List<ApiKeySetting> findAllByUserId(long userId);
}
