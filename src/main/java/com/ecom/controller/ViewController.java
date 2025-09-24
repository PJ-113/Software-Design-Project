package com.ecom.controller;

import com.ecom.api.dto.ProductDto;
import com.ecom.api.mapper.ApiMapper;
import com.ecom.entity.Product;
import com.ecom.service.ProductService;
import com.ecom.service.RedeemCodeService;
import com.ecom.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Controller
public class ViewController {

  private final ProductService productService;
  private final RedeemCodeService redeemCodeService;
  private final UserService userService;

  public ViewController(ProductService productService,
                        RedeemCodeService redeemCodeService,
                        UserService userService) {
    this.productService = productService;
    this.redeemCodeService = redeemCodeService;
    this.userService = userService;
  }

  @GetMapping("/")
  public String index(Model model, Principal principal) {
    
    List<ProductDto> products = productService.all().stream()
        .sorted(Comparator.comparing(Product::getId).reversed())
        .map(p -> ApiMapper.toDto(p, redeemCodeService.availableStock(p)))
        .toList();
    model.addAttribute("products", products);

    
    if (principal != null) {
      userService.findByEmail(principal.getName())
                 .ifPresent(u -> model.addAttribute("user", u));
    }

    return "index"; // templates/index.html
  }

  @GetMapping("/login")
  public String login() {
      return "login";
  }

  @GetMapping("/register")
  public String register() {
      return "register";
  }
}

