package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // (default) EnumType.ORDINAL 쓰면 숫자라서 망함. 중간에 다른 type이 추가되면 순서 밀리기 때문
    // STRING 쓰자
    private DeliveryStatus status; // READY, CAMP
}
