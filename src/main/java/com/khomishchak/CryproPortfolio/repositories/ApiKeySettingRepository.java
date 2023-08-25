package com.khomishchak.CryproPortfolio.repositories;

import com.khomishchak.CryproPortfolio.model.exchanger.ApiKeySetting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiKeySettingRepository extends JpaRepository<ApiKeySetting, Long> {

    List<ApiKeySetting> findAllByUserId(long userId);
}
