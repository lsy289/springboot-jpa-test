package com.project.springbootjpatest.service;

import com.project.springbootjpatest.domain.Member;
import com.project.springbootjpatest.domain.Order;
import com.project.springbootjpatest.domain.OrderItem;
import com.project.springbootjpatest.domain.Shipping;
import com.project.springbootjpatest.repository.MemberRepository;
import com.project.springbootjpatest.repository.OrderRepository;
import com.project.springbootjpatest.repository.ProductRepository;
import jakarta.persistence.Table;
import org.aspectj.weaver.ast.Or;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Long memberId, List<OrderItem> orderItems, Shipping shipping) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalIdentifierException("Invalid member ID"));

        Order order = new Order(member, orderItems, shipping, List.of("new", "order"));
        return orderRepository.save(order);
    }

    public List<Order> getOrdersForMember(Long memberId) {
        return orderRepository.findAll();
    }
}
