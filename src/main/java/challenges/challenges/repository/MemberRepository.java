package challenges.challenges.repository;

import challenges.challenges.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    /**
     * 회원저장
     */
    public void save(Member member) {
        em.persist(member);
    }

    /**
     * db에 loginId 가 존재하는지 없는지를 검사
     * return 값은 long 형의 count 결과값
     */
    public long checkId(String checkId) {
        TypedQuery<Long> query = em.createQuery("SELECT count(m) FROM Member m where m.m_loginId = :checkId", Long.class);
        query.setParameter("checkId", checkId);
        long count = query.getSingleResult();
        return count;
    }

}
