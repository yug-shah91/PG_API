package com.pg.repository;

import com.pg.entity.RentRecord;
import com.pg.entity.enums.RentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentRecordRepository extends JpaRepository<RentRecord, Long> {

    boolean existsByTenantIdAndMonthAndYear(Long tenantId, Integer month, Integer year);

    Optional<RentRecord> findByTenantIdAndMonthAndYear(Long tenantId, Integer month, Integer year);

    List<RentRecord> findByTenantIdOrderByYearDescMonthDesc(Long tenantId);

    List<RentRecord> findByPgIdAndStatusOrderByDueDateAsc(Long pgId, RentStatus status);

    List<RentRecord> findByPgIdAndMonthAndYear(Long pgId, Integer month, Integer year);

    @Query("SELECT r FROM RentRecord r WHERE r.status = 'PENDING' AND r.dueDate < :today")
    List<RentRecord> findOverdueRecords(@Param("today") LocalDate today);

    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM RentRecord r " +
           "WHERE r.pg.id = :pgId AND r.status IN ('PENDING', 'OVERDUE')")
    java.math.BigDecimal getTotalOutstandingForPg(@Param("pgId") Long pgId);

    long countByPgIdAndMonthAndYearAndStatus(Long pgId, Integer month, Integer year, RentStatus status);
}
