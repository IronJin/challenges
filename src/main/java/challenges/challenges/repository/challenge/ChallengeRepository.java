package challenges.challenges.repository.challenge;

import challenges.challenges.controller.challenge.UpdateChallengeDTO;
import challenges.challenges.domain.Challenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

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

    /**
     * 모든 챌린지 정보 조회
     */
    public List<Challenge> findAll() {
        return em.createQuery("select c from Challenge c where c.c_state = 'PROCEED' ", Challenge.class).getResultList();
    }

    public Challenge findById(Long id) {
        return em.find(Challenge.class, id);
    }

    public void deleteById(Long id) {
        Challenge findChallenge = findById(id);
        em.remove(findChallenge);
    }

    public void update(Long id, UpdateChallengeDTO updateChallengeDTO) {
        Challenge findChallenge = em.find(Challenge.class, id);
        findChallenge.setC_title(updateChallengeDTO.getC_title());
        findChallenge.setC_detail(updateChallengeDTO.getC_detail());
    }



}
