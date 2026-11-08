package com.payflow.repository;

import com.payflow.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByTargetTypeAndTargetId(String targetType, Long targetId);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findAllByOrderByTimestampDesc();
}
