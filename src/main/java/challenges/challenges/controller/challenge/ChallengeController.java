package challenges.challenges.controller.challenge;

import challenges.challenges.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final MemberService memberService;



}
