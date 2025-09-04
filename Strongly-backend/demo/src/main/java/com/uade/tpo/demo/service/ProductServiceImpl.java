package com.uade.tpo.demo.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.dto.ProductRequest;
import com.uade.tpo.demo.exceptions.CategoryDuplicateException;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.repository.CategoryRepository;
import com.uade.tpo.demo.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // public Page<ProductRequest> getProduct(PageRequest pageable) {
    //     return productRepository.findAll(pageable);
    // }
    public Page<ProductRequest> getProduct(PageRequest pageable) {
    Page<Product> products = productRepository.findAll(pageable);

    // Mapear cada Product a ProductRequest
    return products.map(ProductRequest::new);
}


    public Optional<ProductRequest> getProductById(Long productId) {        
        return productRepository.findById(productId).map(ProductRequest::new);
    }
 

    public ProductRequest createProduct(String name,String description,int stock, BigDecimal price,long category_id, long id_user) throws ProductDuplicateException{
    List<Product> products = productRepository.findByname(name);
    if (products.isEmpty()) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setSlug(description.replace(" ", "-"));
        Category categoria = categoryRepository.findById(category_id).orElse(null);
        p.setCategory(categoria);

        // User usuario = usaurioRepository.findById(user_id).orElse(null);
        //p.setCreatedBy(usuario);
        ProductRequest productRequest = new ProductRequest(p) ;
        productRepository.save(p);
        return productRequest;
    }
    throw new ProductDuplicateException();
}

public List<ProductRequest> getProductsByCategory(Long categoryId) throws CategoryNotFoundException {
    Category categoria = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException("La categoria " + categoryId +" no existe"));
    List<Product> productos = productRepository.findProductByCategory(categoria);
    // Mapear a DTO
    return productos.stream().map(ProductRequest::new).toList();
}

public ProductRequest updatePrice(Long productId, BigDecimal newPrice) throws ProductNotFoundException {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));

    product.setPrice(newPrice);
    Product updatedProduct = productRepository.save(product);

    return new ProductRequest(updatedProduct);
}

public ProductRequest updateStock(Long productId, int newStock) throws ProductNotFoundException {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));

    product.setStock(newStock);
    Product updatedProduct = productRepository.save(product);

    return new ProductRequest(updatedProduct);
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
  

   