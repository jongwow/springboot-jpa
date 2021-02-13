package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이정도면 해도 자동으로 만들어줌....호달달
    // select from Member m where m.name =
    List<Member> findByName(String name);
}
