package com.uade.tpo.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.dto.ProductRequest;
import com.uade.tpo.demo.entity.dto.UpdateProductPrice;
import com.uade.tpo.demo.entity.dto.UpdateProductStock;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.service.ProductService;


import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
public ResponseEntity<Page<ProductRequest>> getProduct(
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size) {

    PageRequest pageable = (page == null || size == null) 
                           ? PageRequest.of(0, Integer.MAX_VALUE)
                           : PageRequest.of(page, size);

    Page<ProductRequest> products = productService.getProduct(pageable);

    return ResponseEntity.ok(products);
}

@GetMapping("/category/{categoryId}")
public ResponseEntity<List<ProductRequest>> getProductsByCategory(@PathVariable Long categoryId) 
        throws CategoryNotFoundException {

    List<ProductRequest> productos = productService.getProductsByCategory(categoryId);

    if (productos.isEmpty()) {
        return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(productos);
}


@PostMapping("/updatePrice")
public ResponseEntity<ProductRequest> updatePrice(@RequestBody UpdateProductPrice req) 
        throws ProductNotFoundException {

    ProductRequest result = productService.updatePrice(req.getIdProducto(), req.getPrecio());
    return ResponseEntity.ok(result);
}

@PostMapping("/updateStock")
public ResponseEntity<ProductRequest> updateStock(@RequestBody UpdateProductStock req) 
        throws ProductNotFoundException {

    ProductRequest result = productService.updateStock(req.getIdProducto(), req.getStock());
    return ResponseEntity.ok(result);
}


    @GetMapping("/{productId}")
public ResponseEntity<ProductRequest> getProductById(@PathVariable Long productId) {
    Optional<ProductRequest> result = productService.getProductById(productId);

    return result
            .map(ResponseEntity::ok)          // si existe, devuelve 200 con el DTO
            .orElseGet(() -> ResponseEntity.noContent().build()); // si no, 204
}


@PostMapping
public ResponseEntity<ProductRequest> createProduct(@RequestBody ProductRequest pr)  throws ProductDuplicateException {

    ProductRequest response =productService.createProduct(pr.getName(),pr.getDescription(),pr.getStock(),pr.getPrice(),pr.getId_category(),pr.getId_User());
    return ResponseEntity.created(URI.create("/product/" + response.getId())).body(response);

}

}

