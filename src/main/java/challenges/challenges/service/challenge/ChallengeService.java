package challenges.challenges.service.challenge;

import challenges.challenges.controller.challenge.CreateChallengeMember;
import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.domain.State;
import challenges.challenges.repository.challenge.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    /**
     * 챌린지
     */
    @Transactional
    public void ChallengeSave(String c_title ,String c_detail, String c_donation_destination, LocalDate c_endTime, Member member) {

        Challenge challenge = Challenge.createChallenge(c_title, c_detail,c_donation_destination,c_endTime,member);
        challengeRepository.ChallengeSave(challenge);

    }

    /**
     * 챌린지에서 모든 정보를 찾고 챌린지의 endTime 과 현재 날짜가 일치하면 챌린지의 상태를 END 로 바꿔주는 로직
     */
    @Transactional
    public void ChallengeStateChange() {
        List<Challenge> challengeList = challengeRepository.findAll();
        for (Challenge challenge : challengeList) {

            if(challenge.getC_endTime().equals(LocalDate.now())) {
                challenge.setC_state(State.END);
            }

        }
    }

    public List<Challenge> ChallengeList() {
        return challengeRepository.findAll();
    }

    public CreateChallengeMember createChallengeMemberInfo(Long id) {
        Challenge challenge = challengeRepository.findById(id);
        Member member = challenge.getMember();
        CreateChallengeMember createChallengeMember = new CreateChallengeMember();
        createChallengeMember.setM_name(member.getM_name());
        createChallengeMember.setM_loginId(member.getM_loginId());
        return createChallengeMember;
    }

    public Challenge findOne(Long id) {
        return challengeRepository.findById(id);
    }



}
