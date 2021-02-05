package jpabook.jpashop.api;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * X to One(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 *
 * */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // 이렇게 OrderSearch를 넘기면 모든걸 가져올것임
        for (Order order : all) {
            order.getMember().getName(); // LAZY가 강제 초기화 - aqueryㅇ를 보내 가져오면 됨.
            order.getDelivery().getAddress();
        }
        return all;
    }
}
