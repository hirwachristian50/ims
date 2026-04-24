package com.airtel.inventory.service;

import com.airtel.inventory.enums.AssetStatus;
import com.airtel.inventory.model.Asset;
import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.repository.AssetRepository;
import com.airtel.inventory.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getAssetById(Long id) {
        return assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    public Asset saveAsset(Asset asset, String performedBy) {
        asset.setDateRegistered(LocalDate.now());
        Asset saved = assetRepository.save(asset);
        
        AuditLog log = new AuditLog();
        log.setAction("ASSET_CREATED");
        log.setEntityType("Asset");
        log.setEntityId(saved.getId());
        log.setDetails("Asset registered: " + saved.getAssetTag());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
        
        return saved;
    }

    public Asset updateAsset(Asset asset, String performedBy) {
        Asset updated = assetRepository.save(asset);
        
        AuditLog log = new AuditLog();
        log.setAction("ASSET_UPDATED");
        log.setEntityType("Asset");
        log.setEntityId(updated.getId());
        log.setDetails("Asset updated: " + updated.getAssetTag());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
        
        return updated;
    }

    public void deleteAsset(Long id, String performedBy) {
        Asset asset = getAssetById(id);
        assetRepository.deleteById(id);
        
        AuditLog log = new AuditLog();
        log.setAction("ASSET_DELETED");
        log.setEntityType("Asset");
        log.setEntityId(id);
        log.setDetails("Asset deleted: " + asset.getAssetTag());
        log.setPerformedBy(performedBy);
        auditLogRepository.save(log);
    }

    public List<Asset> searchAssets(String keyword) {
        return assetRepository.searchAssets(keyword);
    }

    public List<Asset> getAssetsByStatus(AssetStatus status) {
        return assetRepository.findByStatus(status);
    }

    public List<Asset> getAssetsByDeviceType(com.airtel.inventory.enums.DeviceType deviceType) {
        return assetRepository.findByDeviceType(deviceType);
    }

    public long countByStatus(AssetStatus status) {
        return assetRepository.countByStatus(status);
    }
}