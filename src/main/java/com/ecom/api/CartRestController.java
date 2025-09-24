package com.ecom.api;

import com.ecom.api.dto.CartItemDto;
import com.ecom.api.mapper.ApiMapper;
import com.ecom.entity.User;
import com.ecom.service.CartService;
import com.ecom.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartRestController {
    private final CartService cartService;
    private final UserService userService;

    public CartRestController(CartService cartService, UserService userService){
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public List<CartItemDto> getCart(@PathVariable Long userId){
        User u = userService.get(userId);
        return cartService.getCart(u).stream().map(ApiMapper::toDto).toList();
    }

    @PostMapping("/{userId}/add")
    public List<CartItemDto> add(@PathVariable Long userId, @RequestParam Long productId, @RequestParam int qty){
        User u = userService.get(userId);
        return cartService.addToCart(u, productId, qty).stream().map(ApiMapper::toDto).toList();
    }

    @PostMapping("/{userId}/update")
    public List<CartItemDto> update(@PathVariable Long userId, @RequestParam Long productId, @RequestParam int qty){
        User u = userService.get(userId);
        return cartService.updateQuantity(u, productId, qty).stream().map(ApiMapper::toDto).toList();
    }

    @DeleteMapping("/{userId}/remove")
    public List<CartItemDto> remove(@PathVariable Long userId, @RequestParam Long productId){
        User u = userService.get(userId);
        return cartService.removeFromCart(u, productId).stream().map(ApiMapper::toDto).toList();
    }

    @DeleteMapping("/{userId}/clear")
    public void clear(@PathVariable Long userId){
        User u = userService.get(userId);
        cartService.clearCart(u);
    }
}
