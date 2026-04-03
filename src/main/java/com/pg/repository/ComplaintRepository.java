package com.pg.repository;

import com.pg.entity.Complaint;
import com.pg.entity.enums.ComplaintCategory;
import com.pg.entity.enums.ComplaintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Page<Complaint> findByPgIdOrderByCreatedAtDesc(Long pgId, Pageable pageable);

    List<Complaint> findByPgIdAndStatusOrderByCreatedAtDesc(Long pgId, ComplaintStatus status);

    List<Complaint> findByPgIdAndCategoryOrderByCreatedAtDesc(Long pgId, ComplaintCategory category);

    List<Complaint> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    long countByPgIdAndStatus(Long pgId, ComplaintStatus status);

    boolean existsByIdAndPgId(Long complaintId, Long pgId);

    boolean existsByIdAndTenantId(Long complaintId, Long tenantId);

    @Query("SELECT c.category, COUNT(c) FROM Complaint c " +
           "WHERE c.pg.id = :pgId GROUP BY c.category")
    List<Object[]> countByCategory(@Param("pgId") Long pgId);
}
