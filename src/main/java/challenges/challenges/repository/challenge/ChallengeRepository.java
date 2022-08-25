package challenges.challenges.repository.challenge;

import challenges.challenges.controller.challenge.UpdateChallengeDTO;
import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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

    /**
     * 멤버를 가지고 챌린지 리스트 뽑아내기
     */
    public List<Challenge> findChallengesByMemberId(Member loginMember) {
        TypedQuery<Challenge> query = em.createQuery("SELECT c FROM Challenge c where c.member = :member", Challenge.class);
        query.setParameter("member", loginMember);
        List<Challenge> challengeList = query.getResultList();
        return challengeList;
    }


}
