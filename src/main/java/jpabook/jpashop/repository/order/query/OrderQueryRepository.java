package jpabook.jpashop.repository.order.query;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    // 특정 화면에 fit한 쿼리들은 이렇게 따로 레포지토리를 빼내서 놓는 것.
    // 중요한 핵심 로직들은 기존의 orderRepository 에 놓고 사용.
    // 이렇게 관심사분리를 해주는 것.

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id = :orderId",
                OrderItemQueryDto.class
        ).setParameter("orderId", orderId).getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = toOrderIds(result);

        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        // v4랑 차이는, v4에선 루프에서 쿼리를 계속 호출했는데 여기선 한번 크게 실행 하고 메모리에서 값을 가져오는 느낌.
        List<OrderItemQueryDto> orderitems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds).getResultList();

        // 그냥 O(NM)으로 루프 두번 돌려도 되는데, 그냥 Map 구조체에 올려놓고 밑 forEach에서 get해서 접근 O(N+1)
        return orderitems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto -> OrderItemQueryDto.getOrderId()));
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
    }
}
