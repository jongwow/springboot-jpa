package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// jpa의 모든 데이터 변경이나 로직들은 트랜잭션 안에서 실행되어야함. 그런데 트랜잭션 어노테이션이 두개가 있음. javax랑 spring. spring이 제공하는 트랜잭션이 좋음.
public class MemberService {

    @Autowired // injection. 여러 방법 중 하나.
    private MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional // 읽기가 아닌 쓰기에서는 readOnly 하지 않기!
    public Long join(Member member) {

        // 중복 검증하는 비즈니스 로직
        validateDuplicateMember(member); // 문제가 있으면 예외를 터트려버릴 것임.

        // 저장
        memberRepository.save(member);

        // Persistentcontext에서  entity 를 저장하면 id를 생성하게 됨. 그러면 em.persist를 하면 id가 박히게된다~ (pk)
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) { // 뭔가 잘못된거
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    @Transactional(readOnly = true) // 데이터 변경을 하지 않으면 조금 더 최적화
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }


    /**
     * 회원 단건 조회
     */
    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    //
}
