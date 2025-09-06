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

import jakarta.validation.Valid;

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

    @Autowired
    private com.uade.tpo.demo.repository.CategoryRepository categoryRepository;

    @Autowired
    private com.uade.tpo.demo.repository.UserRepository userRepository;

    @Autowired
    private com.uade.tpo.demo.repository.ProductRepository productRepository;

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
public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest req) {

    // 1) validar que venga id_category e id_user (si los necesitás)
    if (req.getId_category() == null) {
        return ResponseEntity.badRequest().body("Falta el campo 'id_category' en el JSON");
    }
    if (req.getId_User() == null) {
        return ResponseEntity.badRequest().body("Falta el campo 'id_user' en el JSON");
    }

    Long categoryId = req.getId_category();  // SIN .longValue()
    Long userId     = req.getId_User();

    // 2) buscar FK (si no existen, 404)
    var category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new CategoryNotFoundException("La categoria " + categoryId + " no existe"));

    var creator = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("El usuario " + userId + " no existe"));

    // 3) construir y guardar el Product
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

    // 4) devolver algo útil (podés armar un ProductResponse si querés)
    return ResponseEntity.ok(saved);
}

}

