package jpabook.jpashop.controller;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm()); // controller에서 view로 넘어갈 때 data를 실어서 보냄
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model) {
        List<Member> memberList = memberService.findMembers(); // 원래는 dto같은걸로 processing해서 주는것이 낫다.
        // API를 만들때는 이유를 불문하고 entity 를 넘기면 절대 안됨. DTO를 넘겨주라.
        // API는 password같은 것이 그대로 노출 + API 스펙이 변함.
        // ssr은 어차피 서버에서 화면을 보내주는거니까 ㄱㅊ.
        model.addAttribute("members", memberList);
        return "members/memberList";
    }
}
