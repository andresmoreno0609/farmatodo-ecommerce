package com.farmatodo.ecommerce.controller;

import com.farmatodo.ecommerce.DTOs.Request.CreateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Request.UpdateCustomerRequest;
import com.farmatodo.ecommerce.DTOs.Response.CustomerResponse;
import com.farmatodo.ecommerce.adapter.CustomerAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Customers")
@RestController
@RequestMapping("/api/v1/customers")
@SecurityRequirement(name = "apiKeyAuth")
public class CustomersController {

    private final CustomerAdapter adapter;

    public CustomersController(CustomerAdapter adapter) { this.adapter = adapter; }

    @Operation(
            summary = "Crear cliente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateCustomerRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Creado",
                            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validación o negocio"),
                    @ApiResponse(responseCode = "401", description = "No autorizado (API Key)"),
            }
    )
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request){
        var body = adapter.create(request);
        return ResponseEntity.created(URI.create("/customers/" + body.id())).body(body);
    }

    @Operation(
            summary = "Obtener cliente por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode = "404", description = "No encontrado")
            }
    )
    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id){ return adapter.get(id); }

    @Operation(
            summary = "Listar clientes (paginado)",
            responses = @ApiResponse(responseCode = "200", description = "OK")
    )
    @GetMapping
    public Page<CustomerResponse> list(@RequestParam(defaultValue="0") int page,
                                       @RequestParam(defaultValue="20") int size){
        return adapter.list(page, size);
    }

    @Operation(
            summary = "Actualizar cliente",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Actualizado",
                            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validación o negocio"),
                    @ApiResponse(responseCode = "404", description = "No encontrado")
            }
    )
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCustomerRequest request){
        return adapter.update(id, request);
    }

    @Operation(
            summary = "Eliminar cliente (lógico)",
            responses = @ApiResponse(responseCode = "204", description = "Eliminado")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        adapter.delete(id);
        return ResponseEntity.noContent().build();
    }
}
