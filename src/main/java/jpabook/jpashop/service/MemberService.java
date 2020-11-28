package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// jpa의 모든 데이터 변경이나 로직들은 트랜잭션 안에서 실행되어야함. 그런데 트랜잭션 어노테이션이 두개가 있음. javax랑 spring. spring이 제공하는 트랜잭션이 좋음.
@Service
@Transactional(readOnly = true) // 이렇게 바깥으로 빼내면 default가 되는거.
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    /*
    @Autowired // injection. 여러 방법 중 하나. spring bean에 들어있는 memberRepository를 inject. field inject이라고도 함.
    위와 같이 하는 것도 많이 쓰긴 쓰는데 바꾸지 못함.
    (그래서 setter injection을 쓰기도 함)
    // private MemberRepository memberRepository;
        @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository; // 근데 이것도 중간에 바뀔 수도 있어서 아래를 추천.(생성자 주입)
    }
    @Autowired
    public MemberService(MemberRepository memberRepository) {
    //    생성될때만 호출이라 중간에 바뀔 염려 없음. Test Case같은 거 작성할 때도 직접 주입하는 것을 강제하기 때문에 명시적이라 또 좋음.
        this.memberRepository = memberRepository;
    }
    // 근데 또 요즘에는 그냥 생성자의 Autowired annotation을 빼고 final로 선언하기도 함. 그래서 최종적인 모습은 아래와 같음
    // 근데 이걸 lombok의 RequiredArgsConstructor(final만 생성자에 주입)을 쓰면 생성자코드도 안써도 됨.
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }*/

    /**
     * 회원가입
     */
    @Transactional
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
        // WAS가 동시에 여러개가 실행될텐데, 어떤 이름이 동시에 Insert를 하게 되면 이 Logic 이 동시에 실행될 수 있다.
        // 이렇게 되어있어도 실무에서는 한번 더 체크해주는 것이 좋음. 그래서 DB의 member의 name을 unique하게 하는 것도 ㄱㅊ
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }


    /**
     * 회원 단건 조회
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    //
}
