package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.CreateProductRequest;
import com.farmatodo.ecommerce.DTOs.Response.ProductResponse;
import com.farmatodo.ecommerce.DTOs.Response.UpdateProductRequest;
import com.farmatodo.ecommerce.adapter.ProductAdapter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Products")
@RestController
@RequestMapping("/api/v1/products")
@SecurityRequirement(name = "apiKeyAuth")
public class ProductsController {

    private final ProductAdapter adapter;
    public ProductsController(ProductAdapter adapter){ this.adapter = adapter; }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @org.springframework.web.bind.annotation.RequestBody CreateProductRequest r){
        var body = adapter.create(r);
        return ResponseEntity.created(URI.create("/api/v1/products/"+body.id())).body(body);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id){ return adapter.get(id); }

    @GetMapping
    public Page<ProductResponse> list(@RequestParam(defaultValue="0") int page,
                                      @RequestParam(defaultValue="20") int size){
        return adapter.list(page, size);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @Valid @org.springframework.web.bind.annotation.RequestBody UpdateProductRequest r){
        // el adapter hace get+merge+update
        return adapter.update(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<ProductResponse> search(@RequestParam String q,
                                        @RequestParam(defaultValue="0") int page,
                                        @RequestParam(defaultValue="20") int size){
        return adapter.search(q, page, size);
    }

}
