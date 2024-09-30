package com.project.springbootjpatest.dto;

import com.project.springbootjpatest.domain.OrderItem;
import com.project.springbootjpatest.domain.Shipping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    private Long memberId;
    private List<OrderItem> orderItemList;
    private Shipping shipping;
}
