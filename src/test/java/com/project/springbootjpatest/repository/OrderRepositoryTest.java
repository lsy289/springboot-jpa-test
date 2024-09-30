package com.project.springbootjpatest.repository;

import com.project.springbootjpatest.domain.*;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Slf4j
class OrderRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(OrderRepositoryTest.class);
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveEachDomainForCompletedOrder() {
        Member member = new Member(
                "flexibleLee",
                "flexible@sample.com",
                new Address("123 Main st", "Seoul", "500-082")
        );

        Product product1 = new Product("product_1", "desc_1", 1000.0);
        Product product2 = new Product("product_2", "desc_2", 2000.0);
        productRepository.saveAll(Arrays.asList(product1, product2));

        OrderItem orderItem1 = new OrderItem(product1, 50);
        OrderItem orderItem2 = new OrderItem(product2, 100);

        Shipping shipping = new Shipping("Seoul 59-10", "PENDING");

        Order order = new Order(member, Arrays.asList(orderItem1, orderItem2), shipping, Arrays.asList("new", "order"));
        orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(order.getId());
        assertThat(found).isNotNull();
        assertThat(found.get().getOrderItems()).hasSize(2);
        assertThat(found.get().getOrderItems()).extracting(OrderItem::getProduct);
        assertThat(found.get().getOrderItems()).extracting(OrderItem::getQuantity);
        assertThat(found.get().getTags()).contains("new", "order");
        assertThat(found.get().getShipping()).isEqualTo(shipping);

        log.info("Order found: {}", found.get().getOrderItems());
    }
}