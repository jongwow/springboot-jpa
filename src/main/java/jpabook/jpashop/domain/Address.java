package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address { // 리플랙션같은 기술을 써야하는데 그게 안되면(기본생성자가 없으면) 안됨.

    private String city;
    private String street;
    private String zipcode;

    protected Address() { // public 대신 protected. JPA 스펙상 달아놓은거구나~ 이렇게 이해 가능.
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // immutable -> 변경되지 않게 해야함.
    // setter를 아예 제공 X.

}
