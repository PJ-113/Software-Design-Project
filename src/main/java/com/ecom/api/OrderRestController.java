package com.ecom.api;

import com.ecom.api.dto.OrderDto;
import com.ecom.api.mapper.ApiMapper;
import com.ecom.entity.User;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderRestController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderRestController(OrderService orderService, UserService userService){
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/user/{userId}")
    public List<OrderDto> ofUser(@PathVariable Long userId){
        User u = userService.get(userId);
        return orderService.ofUser(u).stream().map(ApiMapper::toDto).toList();
    }

    @PostMapping("/user/{userId}/checkout")
    public OrderDto checkout(@PathVariable Long userId){
        User u = userService.get(userId);
        return ApiMapper.toDto(orderService.checkout(u));
    }

    @GetMapping("/{orderId}")
    public OrderDto get(@PathVariable Long orderId){
        return ApiMapper.toDto(orderService.getWithItems(orderId));
    }

    @GetMapping("/all/checked-out")
    public List<OrderDto> allCheckedOut(){
        return orderService.allCheckedOut().stream().map(ApiMapper::toDto).toList();
    }
}
