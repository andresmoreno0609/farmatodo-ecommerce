package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.TokenizacionRequest;
import com.farmatodo.ecommerce.DTOs.Response.TokenizacionResponse;
import com.farmatodo.ecommerce.adapter.TokenizeCardAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Tokenization")
@RestController
@RequestMapping("/api/v1/tokenize")
@SecurityRequirement(name = "apiKeyAuth")
public class TokenizationController {

    private static final String CORR = "X-CORRELATION-ID";
    private final TokenizeCardAdapter adapter;

    public TokenizationController(TokenizeCardAdapter adapter) {
        this.adapter = adapter;
    }
    @Operation(
            summary = "Tokenizar tarjeta",
            description = """
            Genera un token único para una tarjeta.
            Aplica probabilidad de rechazo y cifra datos sensibles antes de almacenar.
            Protegido por API Key (header X-API-KEY).""",
        responses = {
            @ApiResponse(responseCode = "200", description = "Tokenización aprobada",
                    content = @Content(schema = @Schema(implementation = TokenizacionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "No autorizado (API Key faltante o inválida)"),
            @ApiResponse(responseCode = "409", description = "Tokenización rechazada por política"),
            @ApiResponse(responseCode = "500", description = "Error interno")
        }
    )
    @PostMapping
    public ResponseEntity<TokenizacionResponse> tokenize(@Valid @RequestBody TokenizacionRequest request, @RequestHeader(value = CORR, required = false) String corrHeader) {
        String correlationId = Optional.ofNullable(corrHeader)
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());

        TokenizacionResponse res = adapter.execute(request, correlationId);

        return ResponseEntity
                .created(URI.create("/api/v1/tokenize/" + res.token()))
                .body(res);
    }


}
