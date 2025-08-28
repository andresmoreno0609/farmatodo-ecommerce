package com.farmatodo.ecommerce.DTOs.Response;

import com.farmatodo.ecommerce.enums.EStatusTokenizacion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Respuesta de tokenización de tarjeta")
public record TokenizacionResponse(
        String token,
        String panEnmascarado,
        String marca,
        Instant creadoEn,
        String idTransaccion,
        EStatusTokenizacion status
) {}