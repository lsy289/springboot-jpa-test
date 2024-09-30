package com.project.springbootjpatest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.springbootjpatest.domain.*;
import com.project.springbootjpatest.domain.Order;
import com.project.springbootjpatest.dto.OrderRequestDto;
import com.project.springbootjpatest.repository.MemberRepository;
import com.project.springbootjpatest.repository.OrderRepository;
import com.project.springbootjpatest.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Member member;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setup(){
//        orderRepository.deleteAll();
//        productRepository.deleteAll();
//        memberRepository.deleteAll();

        member = new Member("John Doe", "john@example.com", new Address("123 Main St", "City", "12345"));
        memberRepository.save(member);

        product1 = new Product("Product 1", "Description 1", 10.0);
        product2 = new Product("Product 2", "Description 2", 20.0);
        productRepository.saveAll(Arrays.asList(product1, product2));
    }

    @Test
    public void shouldCreateOrder() throws Exception {
        OrderRequestDto request = new OrderRequestDto(
                member.getId(),
                Arrays.asList(new OrderItem(product1, 2), new OrderItem(product2, 1)),
                new Shipping("123 Main St, City, 12345", "PENDING")
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.member.name").value("John Doe"))
                .andExpect(jsonPath("$.orderItems.length()").value(2))
                .andExpect(jsonPath("$.tags.length()").value(2));
    }

    @Test
    public void shouldGetOrdersForMember() throws Exception {
        // 주문 생성
        OrderItem item1 = new OrderItem(product1, 2);
        Shipping shipping = new Shipping("123 Main St, City, 12345", "PENDING");
        Order order = new Order(member, List.of(item1), shipping, Arrays.asList("new", "order"));
        orderRepository.save(order);

        mockMvc.perform(get("/api/orders/member/" + member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].member.name").value("John Doe"))
                .andExpect(jsonPath("$[0].orderItems.length()").value(0))
                .andExpect(jsonPath("$[0].tags.length()").value(2));
    }

    @Test
    public void shouldReturnEmptyListIfNotCreateOrder() throws Exception {
        // 회원 생성만 하고 주문은 생성하지 않음
        Member newMember = new Member("Jane Smith", "jane@example.com", new Address("456 Elm St", "Town", "67890"));
        memberRepository.save(newMember);

        mockMvc.perform(get("/api/orders/member/" + newMember.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}