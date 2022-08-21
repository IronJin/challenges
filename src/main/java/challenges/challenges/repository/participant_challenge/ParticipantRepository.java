package challenges.challenges.repository.participant_challenge;

import challenges.challenges.domain.ParticipantChallenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ParticipantRepository {

    private final EntityManager em;

    public void participantSave(ParticipantChallenge participantChallenge) {
        em.persist(participantChallenge);
    }


}
