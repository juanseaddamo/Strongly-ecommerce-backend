package com.uade.tpo.demo.entity.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // ignora campos extra del JSON
public class ProductRequest {

    // Si este DTO es para CREAR, no mandes 'id' desde el cliente
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @JsonProperty("id_user")       // <-- acepta "id_user" en JSON
    private Long id_User;          // (dejé tu nombre para no tocar controller)

    @NotNull
    @JsonProperty("id_category")   // <-- acepta "id_category" en JSON
    private Long id_category;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stock;

    @JsonProperty("is_active")
    private Boolean is_active = true; // por si querés activarlo al crear

    // Eliminé el constructor que recibía Product:
    // mezclaba responsabilidades de request/response.
    // Si lo necesitás para devolver data, hacé un ProductResponse aparte.
}
