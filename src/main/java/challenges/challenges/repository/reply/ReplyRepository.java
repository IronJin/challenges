package challenges.challenges.repository.reply;

import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.domain.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Repository
@RequiredArgsConstructor
public class ReplyRepository {

    private final EntityManager em;

    public void saveReply(Reply reply) {
        em.persist(reply);
    }

    /**
     * 특정 멤버가 댓글 단 챌린지 리스트 가져오기
     */
    public List<Reply> getReplyChallengesByMember(Member member) {
        TypedQuery<Reply> query = em.createQuery("SELECT r FROM Reply r join fetch r.r_challenge WHERE r.r_member = :member", Reply.class);
        query.setParameter("member",member);
        List<Reply> challengeList = query.getResultList();
        return challengeList;
    }

}
