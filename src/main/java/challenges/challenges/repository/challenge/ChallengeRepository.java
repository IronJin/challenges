package challenges.challenges.repository.challenge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ChallengeRepository {

    private final EntityManager em;

}
