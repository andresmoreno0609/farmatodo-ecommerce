package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.CreateProductRequest;
import com.farmatodo.ecommerce.DTOs.Response.ProductResponse;
import com.farmatodo.ecommerce.DTOs.Response.UpdateProductRequest;
import com.farmatodo.ecommerce.adapter.ProductAdapter;
import com.farmatodo.ecommerce.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ProductImpl implements ProductAdapter {

    private final UCProduct uc;
    public ProductImpl(UCProduct uc){ this.uc = uc; }

    private static ProductEntity toEntity(CreateProductRequest r){
        var e = new ProductEntity();
        e.setSku(r.sku()); e.setName(r.name());
        e.setDescription(r.description()); e.setPrice(r.price()); e.setStock(r.stock());
        return e;
    }
    private static void merge(ProductEntity e, UpdateProductRequest r){
        e.setName(r.name()); e.setDescription(r.description());
        e.setPrice(r.price()); e.setStock(r.stock());
    }
    private static ProductResponse toResponse(ProductEntity e){
        return new ProductResponse(e.getId(), e.getSku(), e.getName(), e.getDescription(),
                e.getPrice(), e.getStock(), e.getStatus()!=null?e.getStatus().name():null);
    }

    @Override public ProductResponse create(CreateProductRequest req){
        return toResponse(uc.create(toEntity(req)));
    }
    @Override public ProductResponse get(Long id){ return toResponse(uc.get(id)); }
    @Override public Page<ProductResponse> list(int page, int size){
        return uc.list(page, size).map(ProductImpl::toResponse);
    }
    @Override public ProductResponse update(Long id, UpdateProductRequest req){
        var db = uc.get(id);
        merge(db, req);
        return toResponse(uc.update(db));
    }
    @Override public void delete(Long id){ uc.delete(id); }
    @Override public Page<ProductResponse> search(String q, int page, int size){
        return uc.search(q, page, size).map(ProductImpl::toResponse);
    }

}
