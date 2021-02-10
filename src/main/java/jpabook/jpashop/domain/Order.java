package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class  Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // 다대일관계. 연관관계 주인
    @JoinColumn(name = "member_id")
    private Member member;

//    @BatchSize(size = 1000) 컬렉션일 때는 이런식으로 적어두면 됨. 근데 그냥 default_batch_fetch_size로 설정하는게 나을지도.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 일대다관계. 컬렉션이기때문에 복잡해짐.
    private List<OrderItem> orderItems = new ArrayList<>();

    //persist(orderItemA); persist(orderItemB); persist(orderItemC); 그 다음에 persist(order)를 해야하는데
    // cascade를 하면 persist(order)를 해야함. 이러면 order안에 있는 orderItemA,B,C도 persist를 해준다.

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER, CANCEL] ENUM type

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    /*
     * 왜 연관관계 메서드가 필요한가?
     * Member member = new Member();
     * Order order = new Order();
     *
     * member.getOrders().add(order);
     * order.setMember(member);
     * 원래는 위와같이 해야하는데, 저걸 깜빡하면 어떡해. setMember는 했는데 getOrders().add(order)는 안하면? 망함.
     * 그래서 원자성을 주듯이 연관관계 메서드를 선언해서 깜빡하지 않도록 챙겨준다.
     *
     * 연관관계 메서드를 쓰면,
     * order.setMember(member) 이것만 써도 된다.
     */

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //=== 생성 메서드===//
    // 복잡한 생성은 생성 메소드로 하는게 좋다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        System.out.println("createOrder Called");
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //=== 비즈니스 로직===//

    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.CAMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel();
        }
    }

    //=== 조회 로직 ===//

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return this.orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice).sum();
        // 람다를 활용해 더 깔끔하다.
        /*
        int totalPrice = 0;
        for (OrderItem orderItem : this.orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;*/
    }
}
