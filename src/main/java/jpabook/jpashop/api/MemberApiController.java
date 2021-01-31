package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Mmap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController //@Controller @ResponseBody: 응답을 JSON으로 보내자~ 그런거
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        // 이렇게 보면 상당히 쉬워보이지만
        // 회원에 대한 정보만 줘야하는데 다른 정보까지 주게 된다는 것.
        // entity에서 jsonIgnore같은 spring annotation을 쓴다면 되긴 되는데 다른 곳에서도 적용되니까 꼬여버릴수도
        // 그리고 entity에 대한 정보가 바뀌면 api 스펙이 달라지는 것(프레젠테이션이랑 바로 닿는거니까.
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        //        for (Member member : members) {
        //            new MemberDto =
        // 이런식으로 해도 되지만 java8이니까 스트림으로!
        //        }
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }
    //API를 만들 때는 엔티티를 그대로 보내지말고 DTO를 꼭 만들어서 하길!

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // @RequestBody: JSON 으로 온 바디를 Member 로 바꿔준다.
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();

        member.setName(request.getName());
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        // 수정할 때는 가급적 변경감지를
        memberService.update(id, request.getName()); // 영한님은 그냥 update할 때 아무것도 반환 안하거나 그냥 id하나만 반환함
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        // DTO는 롬복 많이 씀.
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
