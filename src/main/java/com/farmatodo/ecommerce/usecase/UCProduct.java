package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.config.trasversal.SettingsConfig;
import com.farmatodo.ecommerce.entity.ProductEntity;
import com.farmatodo.ecommerce.enums.ERecordStatus;
import com.farmatodo.ecommerce.exceptions.BusinessException;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.ProductRepository;
import com.farmatodo.ecommerce.repository.ProductSearchLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UCProduct {

    private final ProductRepository repo;
    private final ProductSearchLogRepository searchLogRepo;
    private final SettingsConfig settings;

    public UCProduct(ProductRepository repo, ProductSearchLogRepository searchLogRepo, SettingsConfig settings) {
        this.repo = repo;
        this.searchLogRepo = searchLogRepo;
        this.settings = settings;
    }

    @Transactional
    public ProductEntity create(ProductEntity e){
        if (repo.existsBySku(e.getSku())) throw new BusinessException("sku_already_exists");
        e.setStatus(ERecordStatus.ACTIVE);
        return repo.save(e);
    }

    @Transactional(readOnly = true)
    public ProductEntity get(Long id){
        return repo.findById(id).orElseThrow(() -> new NotFoundException("product_not_found"));
    }

    @Transactional(readOnly = true)
    public Page<ProductEntity> list(int page, int size){
        return repo.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public ProductEntity update(ProductEntity e){
        if (repo.existsBySkuAndIdNot(e.getSku(), e.getId()))
            throw new BusinessException("sku_already_exists");
        return repo.save(e);
    }

    @Transactional
    public void delete(Long id){
        var db = get(id);
        db.setStatus(ERecordStatus.DELETED);
        repo.save(db);
    }

    @Transactional(readOnly = true)
    public Page<ProductEntity> search(String q, int page, int size){
        int minStock = settings.getMinVisibleStock(); // configurable
        var p = repo.searchByNameOrSku(q == null ? "" : q, minStock, PageRequest.of(page, size));
        logSearchAsync(q, minStock, p.getTotalElements());
        return p;
    }

    @Async
    protected void logSearchAsync(String q, int minStock, long total){
        searchLogRepo.save(new com.farmatodo.ecommerce.entity.ProductSearchLogEntity(q, minStock, total));
    }

}
