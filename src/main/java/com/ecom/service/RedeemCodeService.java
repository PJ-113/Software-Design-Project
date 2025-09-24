package com.ecom.service;

import com.ecom.entity.Product;
import com.ecom.entity.RedeemCode;
import com.ecom.repo.RedeemCodeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedeemCodeService {

    private final RedeemCodeRepository repo;

    public RedeemCodeService(RedeemCodeRepository repo) {
        this.repo = repo;
    }

    /** นับ stock จากจำนวนโค้ด AVAILABLE */
    public long availableStock(Product p) {
        return repo.countByProductAndStatus(p, RedeemCode.Status.AVAILABLE);
    }

    /** จองโค้ดตามจำนวน qty แบบกันชน (FOR UPDATE SKIP LOCKED) */
    @Transactional
    public List<RedeemCode> allocateCodes(Product product, int qty) {
        List<RedeemCode> picked = repo.pickAvailableForUpdate(
                product.getId(),
                PageRequest.of(0, qty)   // 👈 ใช้ Pageable แทน LIMIT ?
        );
        if (picked.size() < qty) {
            throw new IllegalStateException("Stock not enough for product id=" + product.getId());
        }
        return picked;
    }

    /** เปลี่ยนสถานะโค้ดเป็น ASSIGNED และ stamp เวลา */
    @Transactional
    public void markAssigned(List<RedeemCode> codes) {
        LocalDateTime now = LocalDateTime.now();
        for (RedeemCode c : codes) {
            c.setStatus(RedeemCode.Status.ASSIGNED);
            c.setAssignedAt(now);
        }
        repo.saveAll(codes);
    }

    /** นำเข้าโค้ดเป็นลิสต์ข้อความ (admin import) */
    @Transactional
    public int importCodes(Product product, List<String> rawCodes) {
        List<RedeemCode> toSave = new ArrayList<>();
        if (rawCodes == null) return 0;
        for (String raw : rawCodes) {
            String code = raw == null ? null : raw.trim();
            if (code == null || code.isEmpty()) continue;
            RedeemCode rc = new RedeemCode();
            rc.setProduct(product);
            rc.setCode(code);
            rc.setStatus(RedeemCode.Status.AVAILABLE);
            toSave.add(rc);
        }
        return repo.saveAll(toSave).size();
    }
}

