package challenges.challenges.controller.member;

import challenges.challenges.domain.Member;
import challenges.challenges.service.EmailService;
import challenges.challenges.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    /**
     * 회원가입 컨트롤러
     */
    @PostMapping("/member/new")
    public ResponseEntity<?> saveMember(@RequestBody MemberDTO memberDTO) {
        HashMap<String, String> response = new HashMap<>();
        //log.info("{}",memberDTO.getM_birth().toString());
        try {
            memberService.save(memberDTO);
            response.put("response", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("response", "fail");
            return ResponseEntity.ok(response);
        }

    }

    /**
     * 멤버 아이디 중복성 검사
     * 이미 존재하면 1을 보내주고 존재하지 않으면 0을 보내준다.
     */
    @PostMapping("/member/new/check")
    public ResponseEntity<?> checkMemberId(@RequestBody CheckLoginIdDTO checkLoginId) {

        long count = memberService.checkId(checkLoginId);

        HashMap<String, Long> response = new HashMap<>();
        response.put("response", count);

        return ResponseEntity.ok(response);
    }

    /**
     * 세션을 이용한 로그인 로직
     */
    @PostMapping("/member/login")
    public ResponseEntity<?> loginMember(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();
        //Id 가 존재하는지를 확인
        Optional<Member> findMemberById = memberService.loginByLoginId(loginDTO.getM_loginId());

        if(findMemberById.isEmpty()) {
            response.put("response","id");
            return ResponseEntity.ok(response);
        }
        //비밀번호가 맞는지를 확인
        Optional<Member> findMemberByPassword = memberService.loginByPassword(loginDTO.getM_loginId(), loginDTO.getM_password());
        if(findMemberByPassword.isEmpty()) {
            response.put("response", "password");
            return ResponseEntity.ok(response);
        }

        //로그인 성공로직
        Member member = findMemberByPassword.get();
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        response.put("response","success");
        return ResponseEntity.ok(response);

        //{"response":"success"}
        //{"response":"id"}
        //{"response":"password"}
    }

    /**
     * Email 정보를 받으면 Email 과 리액트에 인증번호를 보냄
     */
    @PostMapping("/email/check")
    public ResponseEntity<?> authEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
        String mailCheck = emailService.mailCheck(emailRequestDTO.getM_email());
        HashMap<String, String> response = new HashMap<>();
        response.put("response",mailCheck);
        return ResponseEntity.ok(response);
    }

}
