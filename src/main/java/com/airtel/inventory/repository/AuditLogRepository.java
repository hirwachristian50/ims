package com.airtel.inventory.repository;

import com.airtel.inventory.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // For dashboard – recent 20 logs
    List<AuditLog> findTop20ByOrderByTimestampDesc();

    // For reports – all logs sorted newest first
    List<AuditLog> findAllByOrderByTimestampDesc();

    // Filter by date range (reports)
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    // Filter by action (reports)
    List<AuditLog> findByActionOrderByTimestampDesc(String action);

    // Filter by user (reports)
    List<AuditLog> findByPerformedByOrderByTimestampDesc(String email);

    // All logs for a specific record (e.g. asset history, optionally used)
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);
}