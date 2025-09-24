package com.ecom.repo;

import com.ecom.entity.CartItem;
import com.ecom.entity.User;
import com.ecom.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  List<CartItem> findByUser(User user);
  Optional<CartItem> findByUserAndProduct(User user, Product product);
  void deleteByUser(User user);
}

