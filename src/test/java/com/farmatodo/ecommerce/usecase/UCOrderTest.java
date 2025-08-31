package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.CreateOrderItem;
import com.farmatodo.ecommerce.DTOs.Request.CreateOrderRequest;
import com.farmatodo.ecommerce.entity.OrderEntity;
import com.farmatodo.ecommerce.entity.OrderItemEntity;
import com.farmatodo.ecommerce.entity.PaymentEntity;
import com.farmatodo.ecommerce.entity.ProductEntity;
import com.farmatodo.ecommerce.enums.EOrderStatus;
import com.farmatodo.ecommerce.enums.EPaymentStatus;
import com.farmatodo.ecommerce.exceptions.BusinessException;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.OrderRepository;
import com.farmatodo.ecommerce.repository.ProductRepository;
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

class UCOrderTest {

    private OrderRepository orderRepo;
    private ProductRepository productRepo;
    private UCOrder uc;

    @BeforeEach
    void setup() {
        orderRepo = mock(OrderRepository.class);
        productRepo = mock(ProductRepository.class);
        uc = new UCOrder(orderRepo, productRepo);
    }

    private ProductEntity product(long id, String sku, String name, String price, int stock) {
        var p = new ProductEntity();
        p.setId(id);
        p.setSku(sku);
        p.setName(name);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        return p;
    }

    @Test
    void create_shouldReserveStock_BuildItems_PaymentPending_andPersist() {
        // given
        var req = new CreateOrderRequest(
                5L,
                "tok_abc123",
                "Calle 123 #45-67",
                List.of(
                        new CreateOrderItem(10L, 2), // 2 * 12.50 = 25.00
                        new CreateOrderItem(11L, 1)  // 1 *  7.00 =  7.00
                )
        );

        when(productRepo.findById(10L)).thenReturn(Optional.of(product(10L, "SKU-10", "Prod 10", "12.50", 10)));
        when(productRepo.findById(11L)).thenReturn(Optional.of(product(11L, "SKU-11", "Prod 11", "7.00", 3)));
        when(orderRepo.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity o = inv.getArgument(0);
            o.setId(123L);
            return o;
        });

        OrderEntity saved = uc.create(req);

        ArgumentCaptor<ProductEntity> prodCap = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepo, times(2)).save(prodCap.capture());
        assertThat(prodCap.getAllValues())
                .extracting(ProductEntity::getId, ProductEntity::getStock)
                .containsExactlyInAnyOrder(
                        tuple(10L, 8), // 10 - 2
                        tuple(11L, 2)  // 3  - 1
                );

        ArgumentCaptor<OrderEntity> orderCap = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepo).save(orderCap.capture());
        OrderEntity persisted = orderCap.getValue();

        assertThat(persisted.getCustomerId()).isEqualTo(5L);
        assertThat(persisted.getShippingAddress()).isEqualTo("Calle 123 #45-67");
        assertThat(persisted.getStatus()).isEqualTo(EOrderStatus.CREATED);
        assertThat(persisted.getTotal()).isEqualByComparingTo("32.00"); // 25.00 + 7.00

        List<OrderItemEntity> items = persisted.getItems();
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getProductId()).isIn(10L, 11L);
        assertThat(items).extracting(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .containsExactlyInAnyOrder(new BigDecimal("25.00"), new BigDecimal("7.00"));

        PaymentEntity pay = persisted.getPayment();
        assertThat(pay).isNotNull();
        assertThat(pay.getTokenizedCard()).isEqualTo("tok_abc123");
        assertThat(pay.getStatus()).isEqualTo(EPaymentStatus.PENDING);
        assertThat(pay.getAttempts()).isZero();

        assertThat(saved.getId()).isEqualTo(123L);
    }

    @Test
    void create_shouldThrowBusiness_whenEmptyItems() {
        var req = new CreateOrderRequest(1L, "tok", "addr", List.of());
        assertThatThrownBy(() -> uc.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("empty_order");
        verifyNoInteractions(productRepo, orderRepo);
    }

    @Test
    void create_shouldThrowNotFound_whenProductMissing() {
        var req = new CreateOrderRequest(1L, "tok", "addr", List.of(new CreateOrderItem(99L, 1)));
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uc.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("product_not_found");

        verify(productRepo).findById(99L);
        verifyNoMoreInteractions(productRepo);
        verifyNoInteractions(orderRepo);
    }

    @Test
    void create_shouldThrowBusiness_whenInsufficientStock() {
        var req = new CreateOrderRequest(1L, "tok", "addr", List.of(new CreateOrderItem(10L, 5)));
        when(productRepo.findById(10L)).thenReturn(Optional.of(product(10L, "SKU-10", "Prod 10", "12.50", 3)));

        assertThatThrownBy(() -> uc.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("insufficient_stock");

        verify(productRepo, never()).save(any());
        verifyNoInteractions(orderRepo);
    }

    @Test
    void get_shouldReturnEntity_whenExists() {
        var o = new OrderEntity(); o.setId(7L);
        when(orderRepo.findById(7L)).thenReturn(Optional.of(o));

        OrderEntity found = uc.get(7L);

        assertThat(found.getId()).isEqualTo(7L);
    }

    @Test
    void get_shouldThrowNotFound_whenMissing() {
        when(orderRepo.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uc.get(7L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("order_not_found");
    }

    @Test
    void list_shouldDelegateToRepository_withPaging() {
        var page = new PageImpl<>(List.of(new OrderEntity()), PageRequest.of(0, 20), 1);
        when(orderRepo.findAll(PageRequest.of(0, 20))).thenReturn(page);

        Page<OrderEntity> res = uc.list(0, 20);

        assertThat(res.getTotalElements()).isEqualTo(1);
        verify(orderRepo).findAll(PageRequest.of(0, 20));
    }

    @Test
    void markPaid_shouldSetStatusAndSave() {
        var o = new OrderEntity(); o.setId(3L); o.setStatus(EOrderStatus.CREATED);

        uc.markPaid(o);

        assertThat(o.getStatus()).isEqualTo(EOrderStatus.PAID);
        verify(orderRepo).save(o);
    }

    @Test
    void markFailed_shouldSetStatusAndSave() {
        var o = new OrderEntity(); o.setId(4L); o.setStatus(EOrderStatus.CREATED);

        uc.markFailed(o);

        assertThat(o.getStatus()).isEqualTo(EOrderStatus.FAILED);
        verify(orderRepo).save(o);
    }
}
