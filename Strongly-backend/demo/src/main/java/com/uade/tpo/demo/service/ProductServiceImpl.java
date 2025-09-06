package com.uade.tpo.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.entity.dto.ProductRequest;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.repository.CategoryRepository;
import com.uade.tpo.demo.repository.ProductRepository;
import com.uade.tpo.demo.repository.UserRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository; // para setear createdBy si lo querés usar

    // Página de productos mapeada a DTO de request (sí, raro para respuesta,
    // pero con esto evitás romper más código ahora)
    public Page<ProductRequest> getProduct(PageRequest pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::toRequest);
    }

    public Optional<ProductRequest> getProductById(Long productId) {
        return productRepository.findById(productId).map(this::toRequest);
    }

    public ProductRequest createProduct(
            String name,
            String description,
            int stock,
            BigDecimal price,
            long category_id,
            long id_user
    ) throws ProductDuplicateException, CategoryNotFoundException {
        // ¿ya existe un producto con ese nombre?
        List<Product> products = productRepository.findByname(name); // asegúrate que exista este método
        if (!products.isEmpty()) {
            throw new ProductDuplicateException();
        }

        Category category = categoryRepository.findById(category_id)
                .orElseThrow(() -> new CategoryNotFoundException("La categoria " + category_id + " no existe"));

        // (Opcional) setear createdBy si querés
        User creator = userRepository.findById(id_user).orElse(null);

        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        // mejor slug desde el name
        p.setSlug(name.trim().toLowerCase().replace(" ", "-"));
        p.setCategory(category);
        if (creator != null) {
            p.setCreatedBy(creator);
        }
        // p.setIsActive(true); // si tu entidad lo tiene, dejalo en true por defecto

        Product saved = productRepository.save(p);
        return toRequest(saved);
    }

    public List<ProductRequest> getProductsByCategory(Long categoryId) throws CategoryNotFoundException {
        Category categoria = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("La categoria " + categoryId + " no existe"));
        List<Product> productos = productRepository.findProductByCategory(categoria);
        return productos.stream().map(this::toRequest).toList();
    }

    public ProductRequest updatePrice(Long productId, BigDecimal newPrice) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));

        product.setPrice(newPrice);
        Product updated = productRepository.save(product);
        return toRequest(updated);
    }

    public ProductRequest updateStock(Long productId, int newStock) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));

        product.setStock(newStock);
        Product updated = productRepository.save(product);
        return toRequest(updated);
    }

    // ----------------- helper de mapeo -----------------

    private ProductRequest toRequest(Product p) {
        ProductRequest dto = new ProductRequest();
        dto.setId(p.getId() != null ? p.getId() : 0L);
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock() != null ? p.getStock() : 0);

        // id_category
        dto.setId_category(p.getCategory() != null ? p.getCategory().getId() : null);
        // id_user (created_by)
        dto.setId_User(p.getCreatedBy() != null ? p.getCreatedBy().getId() : null);

        // si tu ProductRequest ahora tiene is_active, podés setearlo acá
        // dto.setIs_active(Boolean.TRUE.equals(p.getIsActive()));

        return dto;
    }
}


/* 
    public List<ProductRequest> getProductsByCategory(Long categoryId) throws CategoryNotFoundException {
        
        Category categoria = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("La categoria" + categoryId +" no existe"));
        List<Product> productos = productRepository.findProductByCategory(categoria);
        return productos;

    }    

    public ProductRequest updatePrice(Long productId, BigDecimal newPrice) throws ProductNotFoundException {
         Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));
    product.setPrice(newPrice);
    return productRepository.save(product);
    }

    
    public ProductRequest updateStock(Long productId, int newStock) throws ProductNotFoundException {
         Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));
        product.setStock(newStock);
        return productRepository.save(product);
    }
 */
  

   