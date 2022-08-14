package challenges.challenges.controller.member;

import challenges.challenges.domain.Member;
import challenges.challenges.service.member.EmailService;
import challenges.challenges.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
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
    public ResponseEntity<?> saveMember(@Valid @RequestBody MemberDTO memberDTO, BindingResult bindingResult) {
        HashMap<String, String> response = new HashMap<>();
        //log.info("{}",memberDTO.getM_birth().toString());
        try {

            if(bindingResult.hasErrors()) {
                response.put("response","fail");
                return ResponseEntity.ok(response);
            }

            memberService.save(memberDTO);
            response.put("response", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.info("여기서 발생");
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
     * Email 정보를 받으면 Email 에 인증번호를 보내고 세션에 인증코드를 저장해서 나중에 비교할거임
     * 인증번호 정보는 세션에 저장
     */
    @PostMapping("/email/send")
    public ResponseEntity<?> authEmail(@RequestBody EmailRequestDTO emailRequestDTO, HttpServletRequest request) {
        String mailCheck = emailService.mailCheck(emailRequestDTO.getM_email());

        CodeDTO codeDTO = new CodeDTO();
        codeDTO.setCode(mailCheck); // 전달형식은 다음과 같다. {"code" : "인증코드번호"}

        HttpSession session = request.getSession();
        session.setAttribute("code",codeDTO);
        session.setMaxInactiveInterval(300); //5분간 유지


        HashMap<String, String> map = new HashMap<>();
        map.put("response","success");

        return ResponseEntity.ok(map);
    }


    @PostMapping("/email/check")
    public ResponseEntity<?> codeCheck(@RequestBody CodeDTO codeDTO, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        HashMap<String, String> map = new HashMap<>();

        if(session == null) {
            map.put("response","인증번호를 먼저 보내야 합니다.");
            return ResponseEntity.ok(map);
        }


        //이메일로 보낸 인증코드
        CodeDTO sessionCode = (CodeDTO) session.getAttribute("code");

        if(sessionCode == null) {
            map.put("response","시간 초과등의 문제가 발생했습니다.");
            return ResponseEntity.ok(map);
        }

        if(!sessionCode.getCode().equals(codeDTO.getCode())) {
            map.put("response","인증코드가 틀립니다.");
            return ResponseEntity.ok(map);
        }

        //성공 로직
        map.put("response","이메일 인증에 성공했습니다.");
        session.invalidate(); //세션을 지워줌
        return ResponseEntity.ok(map);
    }

}
