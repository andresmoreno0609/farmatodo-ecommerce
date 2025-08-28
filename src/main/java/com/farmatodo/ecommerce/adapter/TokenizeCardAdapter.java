package com.farmatodo.ecommerce.adapter;

import com.farmatodo.ecommerce.DTOs.Request.TokenizacionRequest;
import com.farmatodo.ecommerce.DTOs.Response.TokenizacionResponse;

public interface TokenizeCardAdapter {
    TokenizacionResponse execute(TokenizacionRequest request, String correlationId);
}
