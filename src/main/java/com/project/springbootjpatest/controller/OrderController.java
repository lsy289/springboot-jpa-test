package com.project.springbootjpatest.controller;

import com.project.springbootjpatest.domain.Order;
import com.project.springbootjpatest.dto.OrderRequestDto;
import com.project.springbootjpatest.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Order>> getOrdersByMemberId(@PathVariable("memberId") Long memberId) {
        List<Order> orders = orderService.getOrdersForMember(memberId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequestDto requestDto) {
        Order order = orderService.createOrder(requestDto.getMemberId(), requestDto.getOrderItemList(), requestDto.getShipping());
        return ResponseEntity.ok(order);
    }
}
