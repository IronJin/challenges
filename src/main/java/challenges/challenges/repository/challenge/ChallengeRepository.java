package challenges.challenges.repository.challenge;

import challenges.challenges.controller.challenge.UpdateChallengeDTO;
import challenges.challenges.domain.*;
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
     * 진행중인 모든 챌린지 정보 조회
     */
    public List<Challenge> findAll() {
        return em.createQuery("select c from Challenge c where c.c_state = 'PROCEED' ", Challenge.class).getResultList();
    }

    /**
     * 기간이 지난 모든 챌린지 정보 조회
     */
    public List<Challenge> findEndAll() {
        return em.createQuery("select c from Challenge c where c.c_state = 'END' ", Challenge.class).getResultList();
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

    /**
     * 현재 이 챌린지에 멤버가 로그인 멤버가 좋아요를 눌렀는지를 확인해서 값을 보내주는 로직
     */
    public long checkHearts(Member member, Challenge challenge) {
        TypedQuery<Long> query = em.createQuery("SELECT count(h) FROM Hearts h where h.h_member = :member AND h.h_challenge = :challenge", Long.class);
        query.setParameter("member", member);
        query.setParameter("challenge",challenge);
        long count = query.getSingleResult();
        return count;
    }

    /**
     * 좋아요 버튼을 누르기
     */
    public void saveHearts(Hearts hearts) {
        em.persist(hearts);
    }

    /**
     * 좋아요 버튼을 취소하기
     */
    public void deleteHearts(Member member, Challenge challenge) {
        TypedQuery<Hearts> query = em.createQuery("SELECT h FROM Hearts h where h.h_member = :member AND h.h_challenge = :challenge", Hearts.class);
        query.setParameter("member", member);
        query.setParameter("challenge",challenge);
        Hearts resultHeart = query.getSingleResult();
        em.remove(resultHeart);
    }

    /**
     * 댓글 리스트 보내주기
     */
    public List<Reply> getReplyList(Challenge challenge) {
        TypedQuery<Reply> query = em.createQuery("SELECT r FROM Reply r join fetch r.r_member WHERE r.r_challenge = :challenge", Reply.class);
        query.setParameter("challenge",challenge);
        List<Reply> replyList = query.getResultList();
        return replyList;
    }

    /**
     * 좋아요를 누른 챌린지 리스트
     * hearts 테이블에 로그인한 멤버가 존재해야하고
     */
    public List<Hearts> getLikeChallengeByMember(Member member) {
        TypedQuery<Hearts> query = em.createQuery("SELECT h FROM Hearts h join fetch h.h_challenge WHERE h.h_member = :member ", Hearts.class);
        query.setParameter("member",member);
        List<Hearts> heartsList = query.getResultList();
        return heartsList;
    }

    /**
     * 마이페이지에 결제리스트 띄우기
     */
    public List<Payment> getMemberPaymentList(Member member) {
        TypedQuery<Payment> query = em.createQuery("SELECT p FROM Payment p, ParticipantChallenge pc, Challenge c WHERE pc.pc_member = :member and p.participantChallenge = pc and pc.pc_challenge = c",Payment.class);
        query.setParameter("member",member);
        List<Payment> paymentList = query.getResultList();
        return paymentList;
    }

    /**
     * 좋아요 DESC 순으로 8개 넘겨주기
     */
    public List<Challenge> getChallengeLikeDesc() {
        TypedQuery<Challenge> query = em.createQuery("SELECT c FROM Challenge c ORDER BY c.c_hearts DESC", Challenge.class);
        query.setMaxResults(8);
        List<Challenge> challengeList = query.getResultList();
        return challengeList;
    }

    /**
     * 챌린지 title 로 검색쿼리 작성하기
     */
    public List<Challenge> getChallengeListByTitle(String keyword) {
        TypedQuery<Challenge> query = em.createQuery("SELECT c FROM Challenge c WHERE c.c_title LIKE %:keyword%", Challenge.class);
        query.setParameter("keyword",keyword);
        List<Challenge> challengeList = query.getResultList();
        return challengeList;
    }



}
