package challenges.challenges.repository.challenge;

import challenges.challenges.domain.Challenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ChallengeRepository {

    private final EntityManager em;

    /**
     * 챌린지 저장
     */
    public void ChallengeSave(Challenge challenge) {
        em.persist(challenge);
    }

}
