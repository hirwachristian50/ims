package com.airtel.inventory.repository;

import com.airtel.inventory.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Get current active assignment for an asset
    Optional<Assignment> findByAssetIdAndActiveTrue(Long assetId);

    // Get all active assignments for a user
    List<Assignment> findByAssignedToIdAndActiveTrue(Long userId);

    // Get full assignment history for an asset
    List<Assignment> findByAssetIdOrderByAssignedDateDesc(Long assetId);

    // Get all currently active assignments
    List<Assignment> findByActiveTrue();

    // Get assignments within a date range (for reports)
    @Query("SELECT a FROM Assignment a WHERE a.assignedDate BETWEEN :start AND :end")
    List<Assignment> findByDateRange(LocalDateTime start, LocalDateTime end);

    // Count active assignments
    long countByActiveTrue();

    // Count assignments per user (for reports)
    @Query("SELECT a.assignedTo.fullName, COUNT(a) FROM Assignment a GROUP BY a.assignedTo.id")
    List<Object[]> countAssignmentsPerUser();
}