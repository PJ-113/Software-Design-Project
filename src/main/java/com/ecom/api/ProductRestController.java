package com.ecom.api;

import com.ecom.api.dto.ProductDto;
import com.ecom.api.mapper.ApiMapper;
import com.ecom.entity.Product;
import com.ecom.service.ProductService;
import com.ecom.service.RedeemCodeService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductRestController {

    private final ProductService productService;
    private final RedeemCodeService redeemCodeService;

    public ProductRestController(ProductService productService,
            RedeemCodeService redeemCodeService) {
    	this.productService = productService;
    	this.redeemCodeService = redeemCodeService;
}

    @GetMapping
    public List<ProductDto> all() {
        return productService.all().stream()
                .map(p -> ApiMapper.toDto(p, redeemCodeService.availableStock(p))) // ← นับจาก RedeemCode
                .toList();
    }

    @GetMapping("/{id}")
    public ProductDto get(@PathVariable Long id) {
        Product p = productService.get(id);
        long stock = redeemCodeService.availableStock(p);
        return ApiMapper.toDto(p, stock); 
    }

    @PostMapping
    public ProductDto create(@RequestBody @Valid ProductDto in) {
        Product p = new Product();
        // apply จะไม่แตะ stock แล้ว
        ApiMapper.apply(p, in);
        p = productService.save(p);

        long stock = redeemCodeService.availableStock(p);
        return ApiMapper.toDto(p, stock);
    }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @RequestBody @Valid ProductDto in) {
        Product p = productService.get(id);
        ApiMapper.apply(p, in);  
        p = productService.save(p);

        long stock = redeemCodeService.availableStock(p);
        return ApiMapper.toDto(p, stock);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
