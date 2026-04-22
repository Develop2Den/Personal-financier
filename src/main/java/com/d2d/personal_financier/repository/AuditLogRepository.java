package com.d2d.personal_financier.repository;

import com.d2d.personal_financier.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
