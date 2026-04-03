package com.pg.repository;

import com.pg.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByUserId(Long userId);

    @Query("SELECT t FROM Tenant t WHERE t.user.email = :email")
    Optional<Tenant> findByUserEmail(@Param("email") String email);

    List<Tenant> findByPgIdAndIsActiveTrue(Long pgId);

    Page<Tenant> findByPgIdAndIsActiveTrue(Long pgId, Pageable pageable);

    List<Tenant> findByRoomIdAndIsActiveTrue(Long roomId);

    long countByPgIdAndIsActiveTrue(Long pgId);

    boolean existsByIdAndPgId(Long tenantId, Long pgId);

    @Query("SELECT t FROM Tenant t WHERE t.pg.id = :pgId " +
           "AND t.isActive = true " +
           "AND LOWER(t.user.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tenant> searchByNameInPg(@Param("pgId") Long pgId, @Param("name") String name);

    @Query("SELECT t FROM Tenant t WHERE t.isActive = true AND t.room IS NOT NULL")
    List<Tenant> findAllActiveTenatsWithRoom();
}
