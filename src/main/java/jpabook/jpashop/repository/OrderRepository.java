package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //=== 검색기능===//

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        // jpa에서 동적쿼리 작성할 수 있게 제공해주는 표준 방법
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
        // 치명적인 단점...
        // 실무랑 동떨어져있는 코드, 가독성이 매우 떨어짐.
        // 쿼리dsl로 작성 추천.
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        // 첫번째 방법
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class).setMaxResults(1000);
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        List<Order> resultList = query.getResultList();
        return resultList;


        /*
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status " + // 값이 없으면 이게 있으면 안되고, 다 가져와야함. 그런 경우는?
                동적쿼리! 매우 어려운 것 중 하나다.
                " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
//                .setFirstResult() 페이징
                .setMaxResults(1000)
                .getResultList();

         */
    }

    public List<Order> findAllWithMemberDelivery() {
        // 한번 쿼리로 order랑 delivery랑 member를 그냥 다 값을 채워서 가져오는 것. LAZY 무시.
        // 이걸 fetch join이라고 함. 기술적으론 sql의 join인데, jpa에서 제공하는 fetch 사용.
        // fetch join은 성능 개선을 위해서 항상 쓰는 거기 때문에 JPA 책 참고하길...
        // 실무에서는 정말 90%의 성능문제가 N+1로 생기는 것. 근데 fetch join을 사용하는 것이 정말 중요
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<Order> findAllWithItem() {
        // 실무에서는 이런 경우에 querydsl를 많이 씀.

        // order 입장에서는 order의 데이터가 뻥튀기가 됨. (left join이라서, left table의 값이 여럿이 됨)
        // 그래서 distinct 라는 것을 사용해준다. 이 distinct는 db의 그것이기도 하지만 jpa에서의 distinct이기도 함.
        // db상에서는 한 줄이 모두 같아야 중복이라 보는데, jpa의 distinct에선 pk만 같아 짜른다.
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
        // 어마어마한 단점! 페이징이 불가능함.
        // 컬렉션에 페치 조인은 한개만 할 수 있다. 1대다에 대한 페이조인은 하나만! 컬렉션 둘 이상에 패치 조인을 사용하면 N*N이 되기 때문에 JPA에서 데이터를 못맞출 수 있음.
    }

    public List<Order> findAllWithItemV2(int offset, int limit) {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class) // 여기까진 ToOne관계이기때문에 페치조연을 한다.
//                        " join fetch o.orderItems oi" + // 컬렉션은 지연로딩으로 조회한다.
//                        " join fetch oi.item i", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
