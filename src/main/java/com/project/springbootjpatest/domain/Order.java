package com.project.springbootjpatest.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Embedded
    private Shipping shipping;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "order_tags", joinColumns = @JoinColumn(name = "ORDER_ID"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    public Order(Member member, List<OrderItem> orderItems, Shipping shipping, List<String> strings) {
        this.member = member;
        this.orderItems = orderItems;
        this.shipping = shipping;
        this.tags = strings;
    }
}
