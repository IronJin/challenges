package challenges.challenges.controller.member;

import challenges.challenges.controller.member.CheckLoginIdDTO;
import challenges.challenges.controller.member.MemberDTO;
import challenges.challenges.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 컨트롤러
     */
    @PostMapping("/member/new")
    public ResponseEntity<?> saveMember(@RequestBody MemberDTO memberDTO) {
        HashMap<String, String> response = new HashMap<>();

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

}
