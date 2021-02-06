package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * X to One(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // 이렇게 OrderSearch를 넘기면 모든걸 가져올것임
        for (Order order : all) {
            order.getMember().getName(); // LAZY가 강제 초기화 - query를 보내 가져오면 됨.
            order.getDelivery().getAddress();
        }
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() { // 반환은 이렇게 하기보단 그냥 result로 반환하는게 낫다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
        /* v1과 v2 둘 다 lazy loading으로 쿼리가 너무 복잡하게 많이 나가는 단점이 있음.*/
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order o) {
            /* dto가 entity를 파라미터로 받는건 괜찮음.*/
            orderId = o.getId();
            name = o.getMember().getName(); // LAZY 초기화: 영속성 콘텍스트가 멤버 아이디를 갖고 영속성 컨텍스에서 이름을 찾아보고 없으면 디비에서 끌고옴
            orderDate = o.getOrderDate();
            orderStatus = o.getStatus();
            address = o.getDelivery().getAddress();// LAZY 초기화: 여기도 마찬가지
            /*
            * v2 분석
            * ORDER -> SQL 1번 -> 결과 주문 수 2개
            *
            * 심플 디티오를 만들 때 루프가 두번 돌게됨.
            * 처음 돌 때 order의 member를 찾기 때문에 member query 를 나가고, delivery도 마찬가지.
            * 두번째 돌 때도 마찬가지
            * 그러면 총 5번의 쿼리가 실행됨.
            *
            * 이게 주문 수가 2개일때도 5번의 쿼리가 발생하는건데, 그러면 10개면 얼마나 쿼리가 많이 나갈까!! -> 성능이슈!
            * 이걸 N+1의 문제. (1+N)이라 불러야할지도...
            * order 가 2개니까, 1 + 회원 N + 배송 N. => 1 + 2 + 2 => 5개.
            *
            * 그렇다고 지연 로딩을 EAGER로 바꾸면 될까? JPA에서도 이걸 많이 썼었는데 쓰면 쓸수록 실무에서 별로....
            * */
        }
    }
}
