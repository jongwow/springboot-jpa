package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository // 이걸 사용해서 component scan으로 spring bean으로 등록됨
public class MemberRepository {

    @PersistenceContext // jpa에서 제공하는 것. spring이 entity manager를 만들어서 이것을 주입해줌
    private EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // JP QL인데, SQL이랑 아주 조금 다름. SQL은 table을 대상으로, JPQL은 entity 객체에 대해서 조회
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }
    public List<Member> findByName(String name){
        return em.createQuery("SELECT m FROM Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
