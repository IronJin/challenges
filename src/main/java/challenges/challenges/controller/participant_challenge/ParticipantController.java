package challenges.challenges.controller.participant_challenge;

import challenges.challenges.controller.member.SessionConst;
import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.service.challenge.ChallengeService;
import challenges.challenges.service.member.MemberService;
import challenges.challenges.service.participant_challenge.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;
    private final MemberService memberService;
    private final ChallengeService challengeService;

    /**
     * 챌린지 참가 버튼을 눌렀을 때 작동하는 메서드
     * 챌린지에 참가하기 위해서는 로그인이 되어있어야함.
     * 따라서 세션을 먼저 조회해서 세션에 값이 있는지 없는지부터 확인을 해주어야함
     */
    @PostMapping("/challenge/participate")
    public ResponseEntity<?> participateChallenge(@RequestBody ParticipateDTO participateDTO, HttpServletRequest request) {
        HashMap<String, String> response = new HashMap<>();

        //로그인이 되어있는지 부터 확인을 해야함
        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 먼저 해주세요");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        //여기서부터는 성공로직임
        Long id = participateDTO.getChallenge_id();
        Challenge challenge = challengeService.findOne(id);
        participantService.participantSave(challenge, loginMember);
        response.put("response","success");
        return ResponseEntity.ok(response);
    }


}
