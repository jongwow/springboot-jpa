package jpabook.jpashop.service.query;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
        // 이 어노테이션은 애매한 경우엔 안쓰는게 나을지도 모른다.
public class OrderDto {
    // Dto안에서도 Entity를 넣는건 별로...
    // 즉, DTO안에서도 Entity는 DTO로 바꿔줘야함. orderItem에서 entity를 수정하면 ========®=
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    private List<OrderItemDto> orderItems; // 이런 내부 entity도 DTO로 꼭 바꿔주기!

    public OrderDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
        orderItems = order.getOrderItems().stream().map(o -> new OrderItemDto(o))
                .collect(Collectors.toList());
    }


}