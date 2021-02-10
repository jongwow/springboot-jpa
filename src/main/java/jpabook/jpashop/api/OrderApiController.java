package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            // lazy loading이라서 강제초기화를 해주는것
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> dtos = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return dtos;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithItemV2(offset, limit);

        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data // 이 어노테이션은 애매한 경우엔 안쓰는게 나을지도 모른다.
    static class OrderDto {
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

        @Getter
        static class OrderItemDto {
            private String itemName; // 상품명
            private int orderPrice; // 주문 가격
            private int count; //주문 수량

            public OrderItemDto(OrderItem o) {
                itemName = o.getItem().getName();
                orderPrice = o.getOrderPrice();
                count = o.getCount();
            }
        }
    }
}
