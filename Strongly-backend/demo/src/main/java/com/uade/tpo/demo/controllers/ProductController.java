package com.uade.tpo.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.dto.ProductRequest;
import com.uade.tpo.demo.entity.dto.ProductResponse;
import com.uade.tpo.demo.entity.dto.ProductResponseSimple;
import com.uade.tpo.demo.entity.dto.UpdateProductPrice;
import com.uade.tpo.demo.entity.dto.UpdateProductStock;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.service.ProductService;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

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

    @Autowired
    private com.uade.tpo.demo.repository.CategoryRepository categoryRepository;

    @Autowired
    private com.uade.tpo.demo.repository.UserRepository userRepository;

    @Autowired
    private com.uade.tpo.demo.repository.ProductRepository productRepository;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(p -> new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock()
            ))
            .toList();
        }
    
     @GetMapping("/category/{categoryId}")
    public List<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
            .map(p -> new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock()
            ))
            .toList();
    }


@PostMapping("/updatePrice")
public ResponseEntity<ProductResponse> updatePrice(@RequestBody UpdateProductPrice req) 
        throws ProductNotFoundException {

    ProductResponse result = productService.updatePrice(req.getIdProducto(), req.getPrecio());
    return ResponseEntity.ok(result);
}

@PostMapping("/updateStock")
public ResponseEntity<ProductResponse> updateStock(@RequestBody UpdateProductStock req) 
        throws ProductNotFoundException {

    ProductResponse result = productService.updateStock(req.getIdProducto(), req.getStock());
    return ResponseEntity.ok(result);
}


   @GetMapping("/{productId}")
public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
    Optional<ProductResponse> result = productService.getProductById(productId);

    return result
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
}



@PostMapping
public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest req) 
        throws CategoryNotFoundException {
    // 1) validar que venga id_category e id_user (si los necesitás)

     if (req.getId_category() == null) {
        return ResponseEntity.badRequest().body("Falta el campo 'id_category' en el JSON");
    }
    if (req.getId_User() == null) {
        return ResponseEntity.badRequest().body("Falta el campo 'id_user' en el JSON");
    }

    


    // 2) buscar FK (si no existen, 404)
    var category = categoryRepository.findById(req.getId_category())
        .orElseThrow(() -> new CategoryNotFoundException("La categoria " + req.getId_category() + " no existe"));

    var creator = userRepository.findById(req.getId_User())
        .orElseThrow(() -> new RuntimeException("El usuario " + req.getId_User() + " no existe"));


    // 3) construir y guardar el Product
    // Crear producto
    var p = new Product();
    p.setName(req.getName());
    p.setDescription(req.getDescription());
    p.setPrice(req.getPrice());
    p.setStock(req.getStock());
    p.setSlug(req.getName().trim().toLowerCase().replace(" ", "-"));
    p.setCategory(category);
    p.setCreatedBy(creator);
    if (req.getIs_active() != null) {
        p.setIsActive(req.getIs_active());
    }

    var saved = productRepository.save(p);

    // Devolver solo datos esenciales + email del creador
    ProductResponseSimple response = new ProductResponseSimple(
        saved.getId(),
        saved.getName(),
        saved.getDescription(),
        saved.getPrice(),
        saved.getStock(),
        saved.getCreatedBy().getEmail()
    );

    return ResponseEntity.ok(response);
    }

}

