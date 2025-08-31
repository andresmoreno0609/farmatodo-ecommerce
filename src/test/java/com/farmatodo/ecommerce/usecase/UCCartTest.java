package com.farmatodo.ecommerce.usecase;

import com.farmatodo.ecommerce.DTOs.Request.AddCartItemRequest;
import com.farmatodo.ecommerce.DTOs.Response.CartResponse;
import com.farmatodo.ecommerce.entity.CartEntity;
import com.farmatodo.ecommerce.entity.CartItemEntity;
import com.farmatodo.ecommerce.entity.ProductEntity;
import com.farmatodo.ecommerce.exceptions.NotFoundException;
import com.farmatodo.ecommerce.repository.CartItemRepository;
import com.farmatodo.ecommerce.repository.CartRepository;
import com.farmatodo.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UCCartTest {

    private CartRepository cartRepo;
    private CartItemRepository itemRepo;
    private ProductRepository productRepo;
    private UCCart uc;

    @BeforeEach
    void setUp() {
        cartRepo = mock(CartRepository.class);
        itemRepo = mock(CartItemRepository.class);
        productRepo = mock(ProductRepository.class);
        uc = new UCCart(cartRepo, itemRepo, productRepo);
    }

    @Test
    void addItem_shouldCreateCartAndItem_whenCartMissing() {
        // Arrange
        Long customerId = 99L;
        var req = new AddCartItemRequest(10L, 2);

        when(cartRepo.findByCustomerId(customerId)).thenReturn(Optional.empty());
        var newCart = new CartEntity();
        newCart.setId(1L);
        newCart.setCustomerId(customerId);
        when(cartRepo.save(any(CartEntity.class))).thenReturn(newCart);

        var product = new ProductEntity();
        product.setId(10L);
        product.setSku("SKU-10");
        product.setName("Producto 10");
        product.setPrice(new BigDecimal("19.99"));
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));

        when(itemRepo.findByCartIdAndProductId(1L, 10L)).thenReturn(Optional.empty());

        uc.addItem(customerId, req);

        ArgumentCaptor<CartItemEntity> captor = ArgumentCaptor.forClass(CartItemEntity.class);
        verify(itemRepo).save(captor.capture());
        var saved = captor.getValue();
        assertThat(saved.getCart().getId()).isEqualTo(1L);
        assertThat(saved.getProductId()).isEqualTo(10L);
        assertThat(saved.getProductSku()).isEqualTo("SKU-10");
        assertThat(saved.getProductName()).isEqualTo("Producto 10");
        assertThat(saved.getUnitPrice()).isEqualTo(new BigDecimal("19.99"));
        assertThat(saved.getQuantity()).isEqualTo(2);
    }

    @Test
    void addItem_shouldIncrementQuantity_whenItemAlreadyExists() {
        // Arrange
        Long customerId = 77L;
        var req = new AddCartItemRequest(10L, 3);

        var cart = new CartEntity();
        cart.setId(5L);
        cart.setCustomerId(customerId);
        when(cartRepo.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        var product = new ProductEntity();
        product.setId(10L);
        product.setSku("SKU-10");
        product.setName("Prod 10");
        product.setPrice(new BigDecimal("5.00"));
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));

        var existing = new CartItemEntity();
        existing.setId(123L);
        existing.setCart(cart);
        existing.setProductId(10L);
        existing.setProductSku("SKU-10");
        existing.setProductName("Prod 10");
        existing.setUnitPrice(new BigDecimal("5.00"));
        existing.setQuantity(2);
        when(itemRepo.findByCartIdAndProductId(5L, 10L)).thenReturn(Optional.of(existing));

        uc.addItem(customerId, req);

        ArgumentCaptor<CartItemEntity> captor = ArgumentCaptor.forClass(CartItemEntity.class);
        verify(itemRepo).save(captor.capture());
        assertThat(captor.getValue().getQuantity()).isEqualTo(5);
    }

    @Test
    void addItem_shouldThrowNotFound_whenProductMissing() {
        Long customerId = 1L;
        var req = new AddCartItemRequest(99L, 1);

        var cart = new CartEntity(); cart.setId(3L); cart.setCustomerId(customerId);
        when(cartRepo.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uc.addItem(customerId, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("product_not_found");
    }

    @Test
    void getCart_shouldReturnVirtualEmpty_whenNoCart() {
        when(cartRepo.findWithItemsByCustomerId(100L)).thenReturn(Optional.empty());

        CartResponse res = uc.getCart(100L);

        assertThat(res.cartId()).isNull();
        assertThat(res.customerId()).isEqualTo(100L);
        assertThat(res.items()).isEmpty();
    }

    @Test
    void getCart_shouldAggregateItems_andReturnTotal() {
        Long customerId = 42L;

        var cart = new CartEntity();
        cart.setId(7L);
        cart.setCustomerId(customerId);

        var it1 = new CartItemEntity();
        it1.setCart(cart);
        it1.setProductId(10L);
        it1.setProductSku("SKU-10");
        it1.setProductName("Prod 10");
        it1.setUnitPrice(new BigDecimal("2.50"));
        it1.setQuantity(2); // 2 * 2.50 = 5.00

        var it2 = new CartItemEntity();
        it2.setCart(cart);
        it2.setProductId(11L);
        it2.setProductSku("SKU-11");
        it2.setProductName("Prod 11");
        it2.setUnitPrice(new BigDecimal("1.25"));
        it2.setQuantity(4); // 4 * 1.25 = 5.00

        cart.setItems(List.of(it1, it2));
        when(cartRepo.findWithItemsByCustomerId(customerId)).thenReturn(Optional.of(cart));

        CartResponse res = uc.getCart(customerId);

        assertThat(res.cartId()).isEqualTo(7L);
        assertThat(res.customerId()).isEqualTo(42L);
        assertThat(res.items()).hasSize(2);
        assertThat(res.items().get(0).lineTotal()).isEqualByComparingTo("5.00");
        assertThat(res.items().get(1).lineTotal()).isEqualByComparingTo("5.00");
        assertThat(res.total()).isEqualByComparingTo("10.00");
    }


    @Test
    void removeItem_shouldDeleteByCartIdAndProductId() {
        Long customerId = 9L;
        Long productId = 55L;

        var cart = new CartEntity(); cart.setId(4L); cart.setCustomerId(customerId);
        when(cartRepo.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        uc.removeItem(customerId, productId);

        verify(itemRepo).deleteByCartIdAndProductId(4L, 55L);
    }

    @Test
    void removeItem_shouldThrowNotFound_whenCartMissing() {
        when(cartRepo.findByCustomerId(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uc.removeItem(9L, 55L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("cart_not_found");
    }

    @Test
    void clear_shouldDeleteAllItemsByCartId() {
        Long customerId = 77L;
        var cart = new CartEntity(); cart.setId(8L); cart.setCustomerId(customerId);
        when(cartRepo.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        uc.clear(customerId);

        verify(itemRepo).deleteByCartId(8L);
    }

    @Test
    void clear_shouldThrowNotFound_whenCartMissing() {
        when(cartRepo.findByCustomerId(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> uc.clear(77L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("cart_not_found");
    }
}
