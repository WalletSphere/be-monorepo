package com.khomishchak.cryptoportfolio.repositories;

import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeySetting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiKeySettingRepository extends JpaRepository<ApiKeySetting, Long> {

    List<ApiKeySetting> findAllByUserId(long userId);
}
