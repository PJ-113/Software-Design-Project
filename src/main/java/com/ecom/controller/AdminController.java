package com.ecom.controller;

import com.ecom.api.dto.ProductDto;
import com.ecom.api.mapper.ApiMapper;
import com.ecom.entity.Product;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.RedeemCodeService;
import com.ecom.service.StorageService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final ProductService productService;
    private final StorageService storageService;
    private final OrderService orderService;
    private final RedeemCodeService redeemCodeService;

    public AdminController(ProductService productService,
                           StorageService storageService,
                           OrderService orderService,
                           RedeemCodeService redeemCodeService) {
        this.productService = productService;
        this.storageService = storageService;
        this.orderService = orderService;
        this.redeemCodeService = redeemCodeService;
    }

    // ---------- Dashboard ----------
    @GetMapping
    public String dashboard() {
        return "redirect:/admin/products";
    }

    // ---------- PRODUCTS ----------
    @GetMapping("/products")
    public String listProducts(Model m) {
        // ส่ง DTO ที่มี stock (นับจาก RedeemCode) ไปยัง view
        List<ProductDto> items = productService.all().stream()
                .map(p -> ApiMapper.toDto(p, redeemCodeService.availableStock(p)))
                .toList();
        m.addAttribute("items", items);
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String createProductForm(Model m) {
        m.addAttribute("form", new ProductForm());
        return "admin/product_form";
    }

    @PostMapping("/products")
    public String createProduct(@ModelAttribute("form") @Validated ProductForm form,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                Model m) {
        try {
            String imagePath = storageService.saveImage(image); // อาจคืน null ได้ถ้าไม่อัปโหลด
            Product p = new Product();
            p.setName(form.getName());
            p.setDescription(form.getDescription());
            p.setPrice(new BigDecimal(form.getPrice()));
            p.setImagePath(imagePath);
            productService.save(p);
            return "redirect:/admin/products";
        } catch (Exception e) {
            m.addAttribute("err", e.getMessage());
            return "admin/product_form";
        }
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model m) {
        Product p = productService.get(id);
        ProductForm f = new ProductForm();
        f.setName(p.getName());
        f.setDescription(p.getDescription());
        f.setPrice(p.getPrice().toPlainString());
        m.addAttribute("form", f);
        m.addAttribute("productId", id);
        m.addAttribute("imagePath", p.getImagePath());
        return "admin/product_form";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("form") @Validated ProductForm form,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                Model m) {
        try {
            Product p = productService.get(id);
            p.setName(form.getName());
            p.setDescription(form.getDescription());
            p.setPrice(new BigDecimal(form.getPrice()));
            if (image != null && !image.isEmpty()) {
                p.setImagePath(storageService.saveImage(image));
            }
            productService.save(p);
            return "redirect:/admin/products";
        } catch (Exception e) {
            m.addAttribute("err", e.getMessage());
            return "admin/product_form";
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }

    // ---------- ORDERS ----------
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model m) {
        var order = orderService.getWithItems(id); 
        m.addAttribute("order", order);
        return "admin/order_detail";
    }

    @GetMapping("/orders")
    public String listOrders(Model m) {
        m.addAttribute("orders", orderService.allCheckedOut());
        return "admin/orders";
    }
 // ฟอร์มเพิ่มโค้ด
    @GetMapping("/products/{id}/codes")
    public String addCodesForm(@PathVariable Long id, Model m) {
        Product p = productService.get(id);
        long available = redeemCodeService.availableStock(p);
        m.addAttribute("product", p);
        m.addAttribute("available", available);
        return "admin/redeem_form";
    }

    // รับโค้ดจากฟอร์ม (textarea: 1 โค้ดต่อ 1 บรรทัด)
    @PostMapping("/products/{id}/codes")
    public String addCodes(@PathVariable Long id,
                           @RequestParam("codes") String codesRaw,
                           Model m) {
        Product p = productService.get(id);
        // split ตามบรรทัด, ตัดช่องว่าง, ตัดค่าว่าง
        var codes = java.util.Arrays.stream(codesRaw.split("\\r?\\n"))
                .map(String::trim).filter(s -> !s.isEmpty()).toList();
        int added = redeemCodeService.importCodes(p, codes);
        m.addAttribute("msg", "เพิ่มโค้ดสำเร็จ: " + added + " รายการ");
        return "redirect:/admin/products";
    }

    // ---------- Form DTO ----------
    public static class ProductForm {
        @NotBlank private String name;
        private String description;
        @Positive private String price; 
        private String imagePath;       
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }
        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    }
}
