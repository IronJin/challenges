package challenges.challenges.controller.challenge;

import challenges.challenges.controller.member.SessionConst;
import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.service.challenge.ChallengeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;


/**
 * 마지막 수정일 2022-08-21
 * 챌린지 생성하는 로직 구현 (완료)
 * 챌린지 생성을 한다음에 endTime 에 맞추어 State 가 변하도록 설정을 해주어야함 (완료)
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
            response.put("response","로그인을 해주세요");
            return ResponseEntity.ok(response);
        }

        //세션에서 로그인된 Member 객체를 꺼내옴
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에서 꺼낸 로그인 멤버가 빈값일 경우
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }


        //사용자가 입력한 값중에 빈값이 존재하는 경우
        if(bindingResult.hasErrors()) {
            response.put("response","빈값이 있으면 안됩니다. 값을 입력해주세요.");
            return ResponseEntity.ok(response);
        }

        //악의적인 사용자가 11시 59분에 생성하려는 경우 실패로직 작성
        if(challengeDTO.getC_endTime().equals(LocalDate.now())) {
            response.put("response","챌린지 종료일이 오늘입니다. 종료일을 다시 설정해주세요.");
            return ResponseEntity.ok(response);
        }


        /**
         * 여기서부터는 성공로직
         * 챌린지를 정상적으로 생성해준다.
         */
        challengeService.ChallengeSave(challengeDTO.getC_title(), challengeDTO.getC_detail(), challengeDTO.getC_donation_destination(), challengeDTO.getC_endTime(), loginMember);
        response.put("response", "success");
        return ResponseEntity.ok(response);
    }

    /**
     * 매 정각 EndTime 과 현재 시각이 일치하는 챌린지들을 찾아서 챌린지를 더이상 사용하지 못하도록 해준다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void changeChallengeSchedule() {
        challengeService.ChallengeStateChange();
    }

    /**
     * PROCEED 상태인 챌린지들을 보내주는 메소드
     */
    @GetMapping("/challenge/list")
    public ResponseEntity<List<Challenge>> challengeList() {
        List<Challenge> challengeList = challengeService.ChallengeList();
        return ResponseEntity.ok(challengeList);
    }


}
