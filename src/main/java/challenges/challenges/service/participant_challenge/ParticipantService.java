package challenges.challenges.service.participant_challenge;

import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.domain.ParticipantChallenge;
import challenges.challenges.repository.participant_challenge.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    @Transactional
    public void participantSave(Challenge challenge, Member member) {

        ParticipantChallenge participantChallenge = ParticipantChallenge.createParticipant(challenge, member);

        participantRepository.participantSave(participantChallenge);
    }

    public long checkParticipationStatus(Challenge challenge, Member loginMember) {
        long count = participantRepository.checkParticipation(challenge, loginMember);
        return count;
    }



}
