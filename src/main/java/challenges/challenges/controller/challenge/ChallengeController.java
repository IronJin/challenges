package challenges.challenges.controller.challenge;

import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.service.challenge.ChallengeService;
import challenges.challenges.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.naming.Binding;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final MemberService memberService;
    private final ChallengeService challengeService;

    @PostMapping("challenge/new")
    public ResponseEntity<?> createChallenge(@Valid @RequestBody ChallengeDTO challengeDTO, BindingResult bindingResult) {

        HashMap<String, String> response = new HashMap<>();

        if(bindingResult.hasErrors()) {
            response.put("response","빈 값이 존재합니다.");
            return ResponseEntity.ok(response);
        }

        Optional<Member> findMemberByLoginId = memberService.findByLoginId(challengeDTO.getM_loginId());
        Member member = findMemberByLoginId.get();
        challengeService.ChallengeSave(challengeDTO.getC_detail(), challengeDTO.getC_donation_destination(), challengeDTO.getC_endTime(), member);

        response.put("response", "성공적으로 챌린지를 생성하였습니다.");
        return ResponseEntity.ok(response);
    }


}
