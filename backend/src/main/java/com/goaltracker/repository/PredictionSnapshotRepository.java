package com.goaltracker.repository;

import com.goaltracker.domain.PredictionSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PredictionSnapshotRepository extends JpaRepository<PredictionSnapshot, Long> {

    @Query("select p from PredictionSnapshot p where p.userId = :userId and p.trackId = :trackId")
    Optional<PredictionSnapshot> findByUserIdAndTrackId(@Param("userId") Long userId, @Param("trackId") Long trackId);

    @Query("select p from PredictionSnapshot p where p.userId = :userId and p.trackId is null")
    Optional<PredictionSnapshot> findOverallByUserId(@Param("userId") Long userId);
}
