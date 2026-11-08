package com.payflow.repository;

import com.payflow.entity.MerchantSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantSettingsRepository extends JpaRepository<MerchantSettings, Long> {
}
