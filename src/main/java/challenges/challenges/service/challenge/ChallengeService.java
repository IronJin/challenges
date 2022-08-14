package challenges.challenges.service.challenge;

import challenges.challenges.controller.challenge.ChallengeDTO;
import challenges.challenges.controller.member.MemberDTO;
import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.repository.challenge.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    /**
     * 챌린지
     */
    @Transactional
    public void ChallengeSave(String c_detail, String c_donation_destination, LocalDate c_endTime, Member member) {

        Challenge challenge = Challenge.createChallenge(c_detail,c_donation_destination,c_endTime,member);
        challengeRepository.ChallengeSave(challenge);

    }

}
