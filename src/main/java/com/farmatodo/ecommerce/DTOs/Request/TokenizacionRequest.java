package com.farmatodo.ecommerce.DTOs.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para tokenizar una tarjeta")
public record TokenizacionRequest(
        @NotBlank @Pattern(regexp = "^[0-9]{12,19}$") String numeroTarjeta,
        @NotBlank @Pattern(regexp = "^[0-9]{3,4}$")     String cvv,
        @NotBlank @Pattern(regexp = "^(0[1-9]|1[0-2])/(\\d{2})$") String expiracion,
        @Size(max = 64) String titular
) {}
