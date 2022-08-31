package challenges.challenges.repository.reply;

import challenges.challenges.domain.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ReplyRepository {

    private final EntityManager em;

    public void saveReply(Reply reply) {
        em.persist(reply);
    }

}
