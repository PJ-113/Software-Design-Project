package com.ecom.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.entity.Product;
import com.ecom.repo.ProductRepository;
import com.ecom.service.RedeemCodeService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/admin/redeem-codes")
public class AdminRedeemCodeController {

    private final ProductRepository productRepo;
    private final RedeemCodeService redeemCodeService;

    public AdminRedeemCodeController(ProductRepository productRepo, RedeemCodeService redeemCodeService) {
        this.productRepo = productRepo;
        this.redeemCodeService = redeemCodeService;
    }

    // เพิ่มโค้ดเป็นลิสต์ (JSON) เช่น ["AAAA-BBBB","CCCC-DDDD"]
    @PostMapping("/{productId}/import")
    public ResponseEntity<?> importCodes(@PathVariable Long productId, @RequestBody List<String> codes) {
        Product p = productRepo.findById(productId).orElseThrow();
        int added = redeemCodeService.importCodes(p, codes);
        Map<String, Object> resp = new HashMap<>();
        resp.put("productId", productId);
        resp.put("added", added);
        resp.put("available", redeemCodeService.availableStock(p));
        return ResponseEntity.ok(resp);
    }

    // ดูสต็อกที่เหลือ (นับจาก AVAILABLE)
    @GetMapping("/{productId}/stock")
    public Map<String,Object> available(@PathVariable Long productId){
        Product p = productRepo.findById(productId).orElseThrow();
        return Map.of("productId", productId, "available", redeemCodeService.availableStock(p));
    }
}
