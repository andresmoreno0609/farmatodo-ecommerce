package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.config.propierties.SearchProperties;
import com.farmatodo.ecommerce.entity.ProductEntity;
import com.farmatodo.ecommerce.entity.ProductSearchLogEntity;
import com.farmatodo.ecommerce.enums.ERecordStatus;
import com.farmatodo.ecommerce.exceptions.BusinessException;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.ProductRepository;
import com.farmatodo.ecommerce.repository.ProductSearchLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UCProductTest {

    private ProductRepository repo;
    private ProductSearchLogRepository searchLogRepo;
    private SearchProperties props;
    private UCProduct uc;

    @BeforeEach
    void setUp() {
        repo = mock(ProductRepository.class);
        searchLogRepo = mock(ProductSearchLogRepository.class);
        props = mock(SearchProperties.class);
        when(props.getMinStock()).thenReturn(1); // default para search()
        uc = new UCProduct(repo, searchLogRepo, props);
    }

    private ProductEntity product(Long id, String sku, String name, String price, int stock, ERecordStatus status) {
        var p = new ProductEntity();
        p.setId(id);
        p.setSku(sku);
        p.setName(name);
        p.setDescription("Desc " + name);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setStatus(status);
        return p;
    }

    @Test
    void create_shouldSaveActive_whenSkuNotExists() {
        var input = product(null, "SKU-1", "Acetaminofen 500", "12.50", 50, null);
        when(repo.existsBySku("SKU-1")).thenReturn(false);
        when(repo.save(any(ProductEntity.class))).thenAnswer(inv -> {
            ProductEntity e = inv.getArgument(0);
            e.setId(100L);
            return e;
        });

        ProductEntity saved = uc.create(input);

        ArgumentCaptor<ProductEntity> cap = ArgumentCaptor.forClass(ProductEntity.class);
        verify(repo).save(cap.capture());
        assertThat(cap.getValue().getStatus()).isEqualTo(ERecordStatus.ACTIVE);

        assertThat(saved.getId()).isEqualTo(100L);
    }

    @Test
    void create_shouldThrow_whenSkuAlreadyExists() {
        var input = product(null, "DUP-1", "Prod", "10.00", 1, null);
        when(repo.existsBySku("DUP-1")).thenReturn(true);

        assertThatThrownBy(() -> uc.create(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("sku_already_exists");

        verify(repo, never()).save(any());
    }

    @Test
    void get_shouldReturn_whenFound() {
        when(repo.findById(7L)).thenReturn(Optional.of(product(7L, "S7", "P7", "1.00", 1, ERecordStatus.ACTIVE)));

        ProductEntity e = uc.get(7L);

        assertThat(e.getId()).isEqualTo(7L);
    }

    @Test
    void get_shouldThrow_whenMissing() {
        when(repo.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uc.get(7L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("product_not_found");
    }

    @Test
    void list_shouldDelegate_toFindAllWithPaging() {
        var page = new PageImpl<>(List.of(product(1L, "A", "A", "1.00", 1, ERecordStatus.ACTIVE)),
                PageRequest.of(0, 20), 1);
        when(repo.findAll(PageRequest.of(0, 20))).thenReturn(page);

        Page<ProductEntity> res = uc.list(0, 20);

        assertThat(res.getTotalElements()).isEqualTo(1);
        verify(repo).findAll(PageRequest.of(0, 20));
    }

    @Test
    void update_shouldSave_whenSkuUniqueForOtherIds() {
        var incoming = product(10L, "SKU-10", "P10", "9.99", 5, ERecordStatus.ACTIVE);

        when(repo.existsBySkuAndIdNot("SKU-10", 10L)).thenReturn(false);
        when(repo.save(incoming)).thenReturn(incoming);

        ProductEntity out = uc.update(incoming);

        assertThat(out.getId()).isEqualTo(10L);
        verify(repo).save(incoming);
    }

    @Test
    void update_shouldThrow_whenSkuExistsInAnotherProduct() {
        var incoming = product(10L, "DUP-2", "P", "9.99", 5, ERecordStatus.ACTIVE);
        when(repo.existsBySkuAndIdNot("DUP-2", 10L)).thenReturn(true);

        assertThatThrownBy(() -> uc.update(incoming))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("sku_already_exists");

        verify(repo, never()).save(any());
    }

    @Test
    void delete_shouldMarkDeleted_andSave() {
        var db = product(20L, "S20", "P20", "1.00", 2, ERecordStatus.ACTIVE);
        when(repo.findById(20L)).thenReturn(Optional.of(db));

        uc.delete(20L);

        assertThat(db.getStatus()).isEqualTo(ERecordStatus.DELETED);
        verify(repo).save(db);
    }

    @Test
    void search_shouldQueryWithMinStock_andLogAsync() {
        when(props.getMinStock()).thenReturn(2);

        var p1 = product(1L, "SKU-1", "Vitamina C", "8.00", 5, ERecordStatus.ACTIVE);
        var p2 = product(2L, "SKU-2", "Vitamina D", "9.00", 3, ERecordStatus.ACTIVE);

        var page = new PageImpl<>(List.of(p1, p2), PageRequest.of(0, 20), 2);
        when(repo.searchByNameOrSku(eq("vita"), eq(2), eq(PageRequest.of(0, 20)))).thenReturn(page);

        Page<ProductEntity> res = uc.search("vita", 0, 20);

        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getContent()).extracting(ProductEntity::getId).containsExactly(1L, 2L);

        ArgumentCaptor<ProductSearchLogEntity> logCap = ArgumentCaptor.forClass(ProductSearchLogEntity.class);
        verify(searchLogRepo).save(logCap.capture());

        ProductSearchLogEntity log = logCap.getValue();
        assertThat(log.getMinStock()).isEqualTo(2);
        assertThat(log.getResults()).isEqualTo(2);
    }

    @Test
    void search_shouldHandleNullQuery_asEmptyString_andStillLog() {
        when(props.getMinStock()).thenReturn(1);

        var page = new PageImpl<ProductEntity>(List.of(), PageRequest.of(0, 10), 0);
        when(repo.searchByNameOrSku(eq(""), eq(1), eq(PageRequest.of(0, 10)))).thenReturn(page);

        Page<ProductEntity> res = uc.search(null, 0, 10);

        assertThat(res.getTotalElements()).isZero();

        ArgumentCaptor<ProductSearchLogEntity> logCap = ArgumentCaptor.forClass(ProductSearchLogEntity.class);
        verify(searchLogRepo).save(logCap.capture());
        assertThat(logCap.getValue().getMinStock()).isEqualTo(1);
        assertThat(logCap.getValue().getResults()).isEqualTo(0);
    }
}
