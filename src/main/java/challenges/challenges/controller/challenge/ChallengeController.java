package challenges.challenges.controller.challenge;

import challenges.challenges.controller.member.SessionConst;
import challenges.challenges.domain.Member;
import challenges.challenges.service.challenge.ChallengeService;

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


/**
 * 마지막 수정일 2022-08-14
 * 챌린지 생성하는 로직 구현 (완료)
 * 챌린지 생성을 한다음에 endTime 에 맞추어 State 가 변하도록 설정을 해주어야함 (미완료)
 */

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping("challenge/new")
    public ResponseEntity<?> createChallenge(@Valid @RequestBody ChallengeDTO challengeDTO, BindingResult bindingResult, HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();

        //세션에 값이 저장되어있는지 확인 (로그인이 되어 있는지를 확인하는 로직)
        HttpSession session = request.getSession(false);

        //세션이 없으므로 로그인이 안되어 있다는 이야기 이다.
        if(session == null) {
            response.put("response","fail");
            return ResponseEntity.ok(response);
        }

        //세션에서 로그인된 Member 객체를 꺼내옴
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에서 꺼낸 로그인 멤버가 빈값일 경우
        if(loginMember == null) {
            response.put("response","fail");
            return ResponseEntity.ok(response);
        }


        //사용자가 입력한 값중에 빈값이 존재하는 경우
        if(bindingResult.hasErrors()) {
            response.put("response","inputFail");
            return ResponseEntity.ok(response);
        }


        /**
         * 여기서부터는 성공로직
         * 챌린지를 정상적으로 생성해준다.
         */
        challengeService.ChallengeSave(challengeDTO.getC_detail(), challengeDTO.getC_donation_destination(), challengeDTO.getC_endTime(), loginMember);
        response.put("response", "success");
        return ResponseEntity.ok(response);
    }


}
