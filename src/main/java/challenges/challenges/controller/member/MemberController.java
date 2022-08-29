package challenges.challenges.controller.member;

import challenges.challenges.domain.Member;
import challenges.challenges.service.member.EmailService;
import challenges.challenges.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;


/**
 * 최종 수정일 2022-08-14
 * 회원가입 로직 (완료)
 * 회원가입시 이메일 인증코드 구현 (완료)
 * 로그인 시 세션에 값 저장하는것 (완료)
 * 로그아웃 구현 (완료)
 */

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
        Optional<Member> findMemberById = memberService.findByLoginId(loginDTO.getM_loginId());

        if(findMemberById.isEmpty()) {
            response.put("response","id");
            return ResponseEntity.ok(response);
        }

        //비밀번호가 맞는지를 확인
        Optional<Member> findMemberByPassword = memberService.findByPassword(loginDTO.getM_loginId(), loginDTO.getM_password());
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
    public ResponseEntity<?> authEmail(@Valid @RequestBody EmailRequestDTO emailRequestDTO, HttpServletRequest request, BindingResult bindingResult) {
        HashMap<String, String> map = new HashMap<>();

        if(bindingResult.hasErrors()) {
            map.put("response","이메일을 입력하세요");
            return ResponseEntity.ok(map);
        }

        String mailCheck = emailService.mailCheck(emailRequestDTO.getM_email());

        CodeDTO codeDTO = new CodeDTO();
        codeDTO.setCode(mailCheck); // 전달형식은 다음과 같다. {"code" : "인증코드번호"}

        HttpSession session = request.getSession();
        session.setAttribute("code",codeDTO);
        session.setMaxInactiveInterval(300); //5분간 유지



        map.put("response","success");

        return ResponseEntity.ok(map);
    }


    /**
     * 코드를 입력하고 확인버튼을 눌렀을 때 작동하는 코드
     */
    @PostMapping("/email/check")
    public ResponseEntity<?> codeCheck(@Valid @RequestBody CodeDTO codeDTO, BindingResult bindingResult, HttpServletRequest request) {

        HashMap<String, String> map = new HashMap<>();
        //빈값이면
        if(bindingResult.hasErrors()) {
            map.put("response","인증코드를 입력해주세요.");
        }


        //이메일주소 입력창 [인증] //url : /email/send
        //인증코드 입력창 [확인] //url : /email/check

        HttpSession session = request.getSession(false);

        //세션 만료 5분 지났을때 동작
        if(session == null) {
            map.put("response","인증번호를 먼저 보내야 합니다.");
            return ResponseEntity.ok(map);
        }


        //이메일로 보낸 인증코드
        //세션에 저장된 인증코드
        CodeDTO sessionCode = (CodeDTO) session.getAttribute("code");

        if(sessionCode == null) {
            map.put("response","시간 초과등의 문제가 발생했습니다.");
            return ResponseEntity.ok(map);
        }

        //세션에 저장된 코드와 입력코드가 다르면 인증코드가 틀립니다 return 해줌
        if(!sessionCode.getCode().equals(codeDTO.getCode())) {
            map.put("response","인증코드가 틀립니다.");
            return ResponseEntity.ok(map);
        }

        //성공 로직
        map.put("response","이메일 인증에 성공했습니다.");
        session.invalidate(); //세션을 지워줌
        return ResponseEntity.ok(map);
    }

    //로그아웃 로직
    //동작 방식 : 세션을 지워줌
    @PostMapping("/member/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        response.put("response","success");
        return ResponseEntity.ok(response);
    }

    //--------------------------------------------------------------------------------------22-08-25 완료

    /**
     * 회원탈퇴 로직
     */
    //완료
    @PostMapping("/mypage/member/delete")
    public ResponseEntity<?> deleteMember(@Valid @RequestBody PasswordDTO passwordDTO, BindingResult bindingResult, HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            response.put("response","값을 입력해주세요.");
            return ResponseEntity.ok(response);
        }

        //로그인이 되어있는지부터 확인할것
        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 해주세요.");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요.");
            return ResponseEntity.ok(response);
        }

        //비밀번호가 일치하는지를 확인할것
        if(!passwordDTO.getM_password().equals(loginMember.getM_password())) {
            response.put("response","비밀번호가 틀렸습니다.");
            return ResponseEntity.ok(response);
        }

        //회원탈퇴 로직
        memberService.deleteMember(loginMember);
        //세션에 있는 값도 지워줘야함
        session.invalidate();
        response.put("response","성공적으로 탈퇴되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 수정 로직
     */
    //미완료
    @PostMapping("/mypage/update/password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordDTO updatePasswordDTO, BindingResult bindingResult, HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            response.put("response","빈값이 있습니다.");
            return ResponseEntity.ok(response);
        }

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 해주세요");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        //로그인 멤버의 비밀번호와 현재 입력한 비밀번호가 일치하는지를 확인하는 로직
        if(!loginMember.getM_password().equals(updatePasswordDTO.getM_oldpassword())){
            response.put("response","기존 비밀번호가 일치하지 않습니다");
            return ResponseEntity.ok(response);
        }

        //성공로직
        try {
            Member updatedMember = memberService.updatePassword(loginMember, updatePasswordDTO);
            //세션에 업데이트된 멤버를 다시 넣어줌
            session.setAttribute(SessionConst.LOGIN_MEMBER,updatedMember);
            response.put("response","성공적으로 변경되었습니다");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            response.put("response","알수없는 오류 발생");
            return ResponseEntity.ok(response);
        }


    }

    /**
     * 멤버 정보 넘겨주기
     * 마이페이지에 띄울 값임
     */
    //미완료
    @GetMapping("/mypage")
    public ResponseEntity<Member> memberInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session == null) {
            return ResponseEntity.ok(null);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        return ResponseEntity.ok(loginMember);
    }

    /**
     * 전화번호 변경 로직
     */
    //미완료
    @PostMapping("/mypage/update/phone")
    public ResponseEntity<?> updatePhoneNumber(@Valid @RequestBody UpdatePhoneNumberDTO updatePhoneNumberDTO, BindingResult bindingResult, HttpServletRequest request) {
        HashMap<String, String> response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            response.put("response","빈값이 있습니다.");
            return ResponseEntity.ok(response);
        }

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 해주세요");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        //성공로직
        try {
            Member updatedMember = memberService.updatePhoneNumber(loginMember, updatePhoneNumberDTO);
            //세션에 업데이트된 멤버를 다시 넣어줌
            session.setAttribute(SessionConst.LOGIN_MEMBER,updatedMember);
            response.put("response","성공적으로 변경되었습니다");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            response.put("response","알수없는 오류 발생");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 이메일 변경로직에서
     * 이메일 입력받고 인증번호를 보내는 것까지의 로직
     */
    //미완료
    @PostMapping("/mypage/update/email")
    public ResponseEntity<?> updateEmailSend(@Valid @RequestBody UpdateEmailDTO updateEmailDTO, BindingResult bindingResult, HttpServletRequest request) {
        HashMap<String, String> response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            response.put("response","이메일을 입력해주세요");
            return ResponseEntity.ok(response);
        }

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 해주세요");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        String mailCheck = emailService.mailCheck(updateEmailDTO.getM_email());

        CodeDTO codeDTO = new CodeDTO();
        codeDTO.setCode(mailCheck); // 전달형식은 다음과 같다. {"code" : "인증코드번호"}

        session.setAttribute("code",codeDTO);
        session.setMaxInactiveInterval(300); //5분간 유지

        response.put("response","success");
        return ResponseEntity.ok(response);
    }

    /**
     * 이메일 변경 로직에서
     * 바뀐 이메일에 전송된 인증번호를 가지고 멤버의 이메일정보를 바꿀지 말지 결정하는 로직
     */
    //미완료
    @PostMapping("/mypage/update/email/check")
    public ResponseEntity<?> updateEmail(@Valid @RequestBody CodeDTO codeDTO,
                                         @RequestBody UpdateEmailDTO updateEmailDTO,
                                         BindingResult bindingResult, HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            response.put("response","인증번호를 입력해주세요");
            return ResponseEntity.ok(response);
        }

        HttpSession session = request.getSession(false);

        //세션 만료 5분 지났을때 동작
        if(session == null) {
            response.put("response","인증번호를 먼저 보내야 합니다.");
            return ResponseEntity.ok(response);
        }

        //이메일로 보낸 인증코드
        //세션에 저장된 인증코드
        CodeDTO sessionCode = (CodeDTO) session.getAttribute("code");

        if(sessionCode == null) {
            response.put("response","인증번호를 다시보내주세요.");
            return ResponseEntity.ok(response);
        }

        //세션에 저장된 코드와 입력코드가 다르면 인증코드가 틀립니다 return 해줌
        if(!sessionCode.getCode().equals(codeDTO.getCode())) {
            response.put("response","인증코드가 틀립니다.");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시해주세요");
            return ResponseEntity.ok(response);
        }

        //성공로직
        try {
            Member updatedMember = memberService.updateEmail(loginMember, updateEmailDTO);
            //세션에 업데이트된 멤버를 다시 넣어줌
            session.setAttribute(SessionConst.LOGIN_MEMBER,updatedMember);
            response.put("response","성공적으로 변경되었습니다");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            response.put("response","알수없는 오류 발생");
            return ResponseEntity.ok(response);
        }

    }




}
