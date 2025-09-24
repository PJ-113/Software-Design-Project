package com.ecom.api.mapper;

import com.ecom.api.dto.*;
import com.ecom.entity.*;
import java.util.stream.Collectors;

public class ApiMapper {

    public static ProductDto toDto(Product p, long stock){
        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), stock, p.getImagePath());
    }

    public static void apply(Product p, ProductDto d){
        if(d.name()!=null) p.setName(d.name());
        if(d.description()!=null) p.setDescription(d.description());
        if(d.price()!=null) p.setPrice(d.price());
        if(d.imagePath()!=null) p.setImagePath(d.imagePath());
    }

    public static UserDto toDto(User u){
        return new UserDto(u.getId(), u.getName(), u.getEmail(), u.getPhone());
    }

    public static CartItemDto toDto(CartItem ci){
        return new CartItemDto(ci.getId(), ci.getProduct().getId(), ci.getProduct().getName(), ci.getQuantity());
    }

    public static OrderItemDto toDto(OrderItem oi){
        return new OrderItemDto(oi.getProduct().getId(), oi.getProduct().getName(), oi.getQuantity(), oi.getPrice());
    }

    public static OrderDto toDto(Order o){
        return new OrderDto(
            o.getId(),
            o.getUser()!=null ? o.getUser().getId() : null,
            o.getCreatedAt(),
            o.getTotal(),
            o.getItems()==null? java.util.List.of() :
                o.getItems().stream().map(ApiMapper::toDto).collect(Collectors.toList())
        );
    }
}
