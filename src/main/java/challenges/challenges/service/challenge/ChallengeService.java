package challenges.challenges.service.challenge;

import challenges.challenges.controller.challenge.CreateChallengeMember;
import challenges.challenges.controller.challenge.UpdateChallengeDTO;
import challenges.challenges.domain.*;
import challenges.challenges.repository.challenge.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    /**
     * 챌린지
     */
    @Transactional
    public void ChallengeSave(String c_title ,String c_detail, String c_donation_destination, LocalDate c_endTime, Member member) {

        Challenge challenge = Challenge.createChallenge(c_title, c_detail,c_donation_destination,c_endTime,member);
        challengeRepository.ChallengeSave(challenge);

    }

    /**
     * 챌린지에서 모든 정보를 찾고 챌린지의 endTime 과 현재 날짜가 일치하면 챌린지의 상태를 END 로 바꿔주는 로직
     */
    @Transactional
    public void ChallengeStateChange() {
        List<Challenge> challengeList = challengeRepository.findAll();
        for (Challenge challenge : challengeList) {

            if(challenge.getC_endTime().equals(LocalDate.now())) {
                challenge.setC_state(State.END);
            }

        }
    }

    //조회
    public List<Challenge> ChallengeList() {
        return challengeRepository.findAll();
    }

    //조회
    public List<Challenge> endChallengeList() {
        return challengeRepository.findEndAll();
    }

    //조회
    public CreateChallengeMember createChallengeMemberInfo(Long id) {
        Challenge challenge = challengeRepository.findById(id);
        Member member = challenge.getMember();
        CreateChallengeMember createChallengeMember = new CreateChallengeMember();
        createChallengeMember.setM_name(member.getM_name());
        createChallengeMember.setM_loginId(member.getM_loginId());
        return createChallengeMember;
    }

    //조회
    public Challenge findOne(Long id) {
        return challengeRepository.findById(id);
    }

    //DB 변경
    @Transactional
    public void deleteChallenge(Challenge challenge) {
        challengeRepository.deleteById(challenge.getId());
    }

    //DB 변경 - update 로직
    @Transactional
    public void updateChallenge(Long id, UpdateChallengeDTO updateChallengeDTO) {
        challengeRepository.update(id, updateChallengeDTO);
    }

    public List<Challenge> findChallengesByMemberId(Member loginMember){
        List<Challenge> challengeList = challengeRepository.findChallengesByMemberId(loginMember);
        return challengeList;
    }

    //챌린지 좋아요 누른거 있나 확인
    public long checkHearts(Member member, Challenge challenge) {
        long count = challengeRepository.checkHearts(member, challenge);
        return count;
    }

    //좋아요 버튼 누르기
    @Transactional
    public void heartsUp(Member member, Challenge challenge) {
        long count = challengeRepository.checkHearts(member, challenge);
        if(count == 0) {
            Hearts hearts = Hearts.createHearts(member,challenge);
            challengeRepository.saveHearts(hearts);
        }
    }

    //좋아요 버튼 취소하기
    @Transactional
    public void heartsDown(Member member, Challenge challenge) {
        challengeRepository.deleteHearts(member, challenge);
        challenge.removeHeart();
    }

    public List<Reply> getReplyList(Challenge challenge) {
        List<Reply> replyList = challengeRepository.getReplyList(challenge);
        return replyList;
    }

    /**
     * 특정 멤버가 좋아요 누른 챌린지 보내주기
     */
    public List<Hearts> getLikeChallengeByMember(Member member) {
        List<Hearts> heartsList = challengeRepository.getLikeChallengeByMember(member);
        return heartsList;
    }

    public List<Payment> getMemberPaymentList(Member member) {
        List<Payment> paymentList = challengeRepository.getMemberPaymentList(member);
        return paymentList;
    }

    /**
     * 좋아요 순으로 챌린지 8개까지 가져오기
     */
    public List<Challenge> getChallengeLikeDesc() {
        List<Challenge> challengeList = challengeRepository.getChallengeLikeDesc();
        return challengeList;
    }

    /**
     * 제목으로 챌린지 검색하기
     */
    public List<Challenge> getChallengeListByTitle(String keyword) {
        List<Challenge> challengeList = challengeRepository.getChallengeListByTitle(keyword);
        return challengeList;
    }


}
