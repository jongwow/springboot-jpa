package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository // 이걸 사용해서 component scan으로 spring bean으로 등록됨
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em; // JPA의 entity manager를 주입을 해준다.

    // 만약 Factory를 주입하고 싶다.
    // PersistentUnit이라고 있음.

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id); // 한개 조회
    }

    public List<Member> findAll() {
        // JP QL인데, SQL이랑 아주 조금 다름. SQL은 table을 대상으로, JPQL은 entity 객체에 대해서 조회
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("SELECT m FROM Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
