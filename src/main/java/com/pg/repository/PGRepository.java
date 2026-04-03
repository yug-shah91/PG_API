package com.pg.repository;

import com.pg.entity.PG;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PGRepository extends JpaRepository<PG, Long> {

    List<PG> findByOwnerId(Long ownerId);

    Optional<PG> findByOwnerIdAndIsActiveTrue(Long ownerId);

    boolean existsByOwnerId(Long ownerId);
}
