package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * UserA
 *   *JPA1 BOOK
 *   *JPA2 BOOK
 * UserB
 *   *
 *   *
 *
 * */
@Component // spring의 컴포넌트 스캔 대상이 되도록
@RequiredArgsConstructor
public class initDB {

    private final InitService initService;

    @PostConstruct // application 시작 시점에 호출해주기 위함.
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {
            // 이걸 postConstruct에서 이런 transaction등을 하는건 lifecycle에서 조금 꼬일 수 있는 가능성이 있어서
            // 이렇게 따로 빼서 사용함.
            Member member = createMember("userA", "서울", "1", "111");
            em.persist(member);

            Book book1 = createBook("JPA1 Book", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 Book", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
            // 이건 persist안함

            Delivery delivery = createDelivery(member);
            //여기도 persist안함

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order); // 위 delivery와 orderItem을 persist안해도 여기서 order가 persist해서 같이 됨.
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        public void dbInit2() {
            // 이걸 postConstruct에서 이런 transaction등을 하는건 lifecycle에서 조금 꼬일 수 있는 가능성이 있어서
            // 이렇게 따로 빼서 사용함.
            Member member = createMember("userB", "부산", "2", "222");
            em.persist(member);

            Book book1 = createBook("SPRING1 Book", 20000, 100);
            em.persist(book1);

            Book book2 = createBook("SPRING2 Book", 40000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);
            // 이건 persist안함

            Delivery delivery = createDelivery(member);
            //여기도 persist안함

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order); // 위 delivery와 orderItem을 persist안해도 여기서 order가 persist해서 같이 됨.
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }
    }
}
