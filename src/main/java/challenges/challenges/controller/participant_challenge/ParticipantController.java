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
import org.springframework.web.bind.annotation.PathVariable;
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
    //완료
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

    /**
     * 특정 챌린지를 클릭해서 상세정보 창을 들어갔을 때 즉, challenge/{id} 화면에 들어갔을 때
     * 참여중이라면 댓글 달기 텍스트박스를 띄울 수 있고 참여중이 아니라면 신청하기 버튼을 띄워주는 로직
     * 참여중이 아닐때는 0을 보내고 참여중이라면 1을 보냄 -> 프론트 엔드는 이 값을 가지고 댓글달수 있는 텍스트박스를 띄울건지 신청하기 버튼을 띄울건지 하면됨
     */
    //완료
    @PostMapping("/challenge/{id}/participant")
    public ResponseEntity<?> getParticipationStatus(@PathVariable Long id, HttpServletRequest request) {
        HashMap<String, Integer> response = new HashMap<>();

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response",0);
            return ResponseEntity.ok(response);
        }
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response",0);
            return ResponseEntity.ok(response);
        }

        Challenge findChallenge = challengeService.findOne(id);
        long status = participantService.checkParticipationStatus(findChallenge, loginMember);
        if(status == 0) {
            response.put("response",0);
            return ResponseEntity.ok(response);
        }

        response.put("response",1);
        return ResponseEntity.ok(response);
    }


}
