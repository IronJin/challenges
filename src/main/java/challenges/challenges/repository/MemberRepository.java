package challenges.challenges.repository;

import challenges.challenges.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.text.html.Option;

import java.util.List;
import java.util.Optional;

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

    /**
     * Id 만 가지고 멤버를 찾기
     */
    public Optional<Member> loginByLoginId(String loginId) {

        for(Member member : findAll()){
            if(member.getM_loginId().equals(loginId)) {
                return Optional.of(member);
            }
        }
        return Optional.empty();

    }

    /**
     * 비밀번호와 아이디를 가지고 멤버를 찾기
     */
    public Optional<Member> loginByPassword(String loginId, String password) {

        for(Member member : findAll()) {
            if (member.getM_loginId().equals(loginId) && member.getM_password().equals(password)) {
                return Optional.of(member);
            }
        }
            return Optional.empty();
    }


    /**
     * 전체회원 조회
     */
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

}
