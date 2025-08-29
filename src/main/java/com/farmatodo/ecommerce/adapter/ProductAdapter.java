package com.farmatodo.ecommerce.adapter;

import com.farmatodo.ecommerce.DTOs.Request.CreateProductRequest;
import com.farmatodo.ecommerce.DTOs.Response.ProductResponse;
import com.farmatodo.ecommerce.DTOs.Response.UpdateProductRequest;
import org.springframework.data.domain.Page;

public interface ProductAdapter {
    ProductResponse create(CreateProductRequest req);
    ProductResponse get(Long id);
    Page<ProductResponse> list(int page, int size);
    ProductResponse update(Long id, UpdateProductRequest req);
    void delete(Long id);
    Page<ProductResponse> search(String q, int page, int size);
}
