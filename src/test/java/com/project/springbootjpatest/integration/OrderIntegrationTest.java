package com.project.springbootjpatest.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.springbootjpatest.domain.*;
import com.project.springbootjpatest.dto.OrderRequestDto;
import com.project.springbootjpatest.repository.MemberRepository;
import com.project.springbootjpatest.repository.OrderRepository;
import com.project.springbootjpatest.repository.ProductRepository;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderIntegrationTest {

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

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup(){
        RestAssured.port = port;

        orderRepository.deleteAll();
        memberRepository.deleteAll();

        RestAssuredMockMvc.mockMvc(mockMvc);

        member = new Member("Eddie Brock", "venom@example.com", new Address("789 Pine St", "Village", "11223"));
        memberRepository.save(member);

        product1 = new Product("Product A", "Description A", 30.0);
        product2 = new Product("Product B", "Description B", 40.0);
        productRepository.saveAll(Arrays.asList(product1, product2));
    }

    @Test
    public void shouldCreateOrderUsingRestAssured() throws Exception {
        OrderRequestDto request = new OrderRequestDto(
                member.getId(),
                Arrays.asList(new OrderItem(product1, 3), new OrderItem(product2, 2)),
                new Shipping("789 Pine St, Village, 11223", "PROCESSING")
        );

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/api/orders")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("authorName", equalTo("Eddie Brock"))
                .body("member.name", equalTo("Eddie Brock"))
                .body("orderItems.size()", equalTo(2))
                .body("tags", hasItems("new", "order"));
    }

    @Test
    public void shouldGetOrdersForMemberUsingRestAssured() throws Exception {
        // 주문 생성
        OrderItem item1 = new OrderItem(product1, 1);
        Shipping shipping = new Shipping("789 Pine St, Village, 11223", "SHIPPED");
        Order order = new Order(member, List.of(item1), shipping, List.of("completed"));
        orderRepository.save(order);

        when()
                .get("/api/orders/member/" + member.getId())
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].member.name", equalTo("Eddie Brock"))
                .body("[0].orderItems.size()", equalTo(1))
                .body("[0].tags", hasItem("completed"));
    }

    @Test
    public void shouldReturnEmptyListIfAuthorWroteNothingUsingRestAssured() throws Exception {
        // 새로운 회원 생성
        Member newMember = new Member("Peter Parker", "spiderman@example.com", new Address("101 Web St", "City", "33445"));
        memberRepository.save(newMember);

        when()
                .get("/api/orders/member/" + newMember.getId())
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }
}
