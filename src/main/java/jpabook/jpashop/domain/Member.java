package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") // order table에 있는 member에 맵핑된 것일뿐이야. FK가 변경되지 않아.
    private List<Order> orders = new ArrayList<>(); // 초기화의 BEST Practice.(NULL safe)
    // 영속화된 컬렉션이 바뀔 수도 있으니까 건드리지 않는 것이 좋다.
}
