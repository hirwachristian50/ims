package com.airtel.inventory.repository;

import com.airtel.inventory.model.Asset;
import com.airtel.inventory.enums.AssetStatus;
import com.airtel.inventory.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findByAssetTag(String assetTag);
    Optional<Asset> findBySerialNumber(String serialNumber);
    List<Asset> findByStatus(AssetStatus status);
    List<Asset> findByDeviceType(DeviceType deviceType);
    List<Asset> findByDepartmentId(Long departmentId);
    
    @Query("SELECT a FROM Asset a WHERE " +
           "LOWER(a.assetTag) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.model) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Asset> searchAssets(@Param("keyword") String keyword);
    
    long countByStatus(AssetStatus status);
}