package com.uade.tpo.demo.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.demo.entity.Category;
import com.uade.tpo.demo.entity.Product;
import com.uade.tpo.demo.entity.User;
import com.uade.tpo.demo.entity.dto.ProductResponse;
import com.uade.tpo.demo.exceptions.CategoryNotFoundException;
import com.uade.tpo.demo.exceptions.ProductDuplicateException;
import com.uade.tpo.demo.exceptions.ProductNotFoundException;
import com.uade.tpo.demo.repository.CategoryRepository;
import com.uade.tpo.demo.repository.ProductRepository;
import com.uade.tpo.demo.repository.UserRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    // --------------------- GET PRODUCTS ---------------------
    @Override
    public Page<ProductResponse> getProduct(Pageable pageable) {
        return productRepository.findAll(pageable).map(p -> new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock()
            ));
    }

    @Override
    public Optional<ProductResponse> getProductById(Long productId) {
        return productRepository.findById(productId)
            .map(p -> new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock()
            ));
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
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

    // --------------------- CREATE ---------------------
    @Override
    public ProductResponse createProduct(String name, String description, int stock, BigDecimal price, long category_id, long id_user)
            throws ProductDuplicateException, CategoryNotFoundException {

        if (!productRepository.findByname(name).isEmpty()) {
            throw new ProductDuplicateException();
        }

        Category category = categoryRepository.findById(category_id)
                .orElseThrow(() -> new CategoryNotFoundException("La categoria " + category_id + " no existe"));

        User creator = userRepository.findById(id_user).orElse(null);

        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setSlug(name.trim().toLowerCase().replace(" ", "-"));
        p.setCategory(category);
        if (creator != null) p.setCreatedBy(creator);

        Product saved = productRepository.save(p);

        return new ProductResponse(
            saved.getId(),
            saved.getName(),
            saved.getDescription(),
            saved.getPrice(),
            saved.getStock()
        );
    }

    // --------------------- UPDATE ---------------------
    @Override
    public ProductResponse updatePrice(Long productId, BigDecimal newPrice) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));

        product.setPrice(newPrice);
        Product updated = productRepository.save(product);

        return new ProductResponse(
            updated.getId(),
            updated.getName(),
            updated.getDescription(),
            updated.getPrice(),
            updated.getStock()
        );
    }

    @Override
    public ProductResponse updateStock(Long productId, int newStock) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Producto " + productId + " no encontrado"));

        product.setStock(newStock);
        Product updated = productRepository.save(product);

        return new ProductResponse(
            updated.getId(),
            updated.getName(),
            updated.getDescription(),
            updated.getPrice(),
            updated.getStock()
        );
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
  

   