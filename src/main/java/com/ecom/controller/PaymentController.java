package com.ecom.controller;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.time.YearMonth;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.ecom.entity.CartItem;

import com.ecom.entity.User;
import com.ecom.service.CartService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;

@Controller
public class PaymentController {

	private final UserService userService;
	private final OrderService orderService;
	 private final CartService cartService;

  public PaymentController(UserService userService, OrderService orderService,CartService cartService) {
    this.userService = userService;
    this.orderService = orderService;
    this.cartService = cartService;
  }

  @GetMapping("/payment")
  public String payment(Principal principal, Model model) {
    if (principal == null) return "redirect:/login";

    var u = userService.getByEmail(principal.getName());
    var items = u == null ? java.util.List.of() : cartService.items(u);  

    List<CartItem> items1 = (u == null) ? new ArrayList<>() : cartService.items(u);

    BigDecimal total = items1.stream()
        .map(it -> it.getProduct().getPrice()
            .multiply(BigDecimal.valueOf(it.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    model.addAttribute("items", items1);
    model.addAttribute("total", total);
    return "payment";  // templates/payment.html
  }
  
  @GetMapping("/payment/success/{orderId}")
  public String paymentSuccess(@PathVariable Long orderId,
                               Principal principal,
                               Model model) {
    if (principal == null) return "redirect:/login";
    User u = userService.getByEmail(principal.getName());
    var order = orderService.getWithItems(orderId);

    
    if (!order.getUser().getId().equals(u.getId())) {
      return "redirect:/";
    }

    model.addAttribute("order", order);
    return "payment-success";   // => templates/payment-success.html
  }
}