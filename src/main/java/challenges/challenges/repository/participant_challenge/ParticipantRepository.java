package challenges.challenges.repository.participant_challenge;

import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.domain.ParticipantChallenge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Repository
@RequiredArgsConstructor
public class ParticipantRepository {

    private final EntityManager em;

    public void participantSave(ParticipantChallenge participantChallenge) {
        em.persist(participantChallenge);
    }

    public long checkParticipation(Challenge challenge, Member loginMember) {
        TypedQuery<Long> query = em.createQuery("SELECT count(pc) FROM ParticipantChallenge pc where pc.pc_member = :loginMember and pc.pc_challenge = :challenge", Long.class);
        query.setParameter("loginMember", loginMember);
        query.setParameter("challenge",challenge);
        long count = query.getSingleResult();
        return count;
    }


}
