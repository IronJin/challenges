package challenges.challenges.service.participant_challenge;

import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.domain.ParticipantChallenge;
import challenges.challenges.domain.Payment;
import challenges.challenges.repository.participant_challenge.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    @Transactional
    public void participantSave(Challenge challenge, Member member) {

        ParticipantChallenge participantChallenge = ParticipantChallenge.createParticipant(challenge, member);

        participantRepository.participantSave(participantChallenge);
    }

    public long checkParticipationStatus(Challenge challenge, Member loginMember) {
        long count = participantRepository.checkParticipation(challenge, loginMember);
        return count;
    }

    public List<Challenge> findParticipantListByMember(Member loginMember){
        List<Challenge> participantChallengeList = participantRepository.findParticipantListByMember(loginMember);
        return participantChallengeList;
    }

    /**
     * 챌린지로 참가 챌린지 테이블 리스트 가져오기
     */
    public List<ParticipantChallenge> findParticipantListByChallenge(Challenge challenge) {
        List<ParticipantChallenge> participantChallengeList = participantRepository.findParticipantChallengeByChallenge(challenge);
        return participantChallengeList;
    }

    /**
     * 결제시 작동하는 메서드
     */
    @Transactional
    public void savePayment(String imp_uid, int price, Member member, Challenge challenge) {
        ParticipantChallenge findParticipantChallenge = participantRepository.findParticipantChallengeByChallengeAndMember(challenge, member);
        Payment payment = Payment.createPayment(imp_uid ,price, findParticipantChallenge);
        participantRepository.savePayment(payment);
        log.info("-------------------------------------------price : {}",price);
        //챌린지와 참여중인 챌린지의 토탈 가격도 올려줘야함
        findParticipantChallenge.addPrice(price);
        challenge.addTotalPrice(price);
    }

    /**
     * 참가챌린지로 페이먼트 정보 다 긁어오기
     */
    public List<Payment> findPaymentList(ParticipantChallenge participantChallenge) {
        List<Payment> paymentList = participantRepository.findPaymentListByParticipantChallenge(participantChallenge);
        return paymentList;
    }

    /**
     * 페이먼트 삭제하기
     */
    @Transactional
    public void deletePayment(Payment payment) {
        participantRepository.deletePayment(payment);
    }



}
