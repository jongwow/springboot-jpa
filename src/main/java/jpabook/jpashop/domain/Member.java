package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id") // 단순하게 id라고 하면 찾거나 join할 때 쉽지 않음.
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // order table에 있는 member에 맵핑된 것일뿐이야. FK가 변경되지 않아.
    private List<Order> orders = new ArrayList<>();
}
