package com.uade.tpo.demo.entity.dto;

import java.util.Locale.Category;

import lombok.Data;

@Data
public class CategoryRequest {
    private long id;
    private String name;
    private String description;
    private long parent_id;
}
