package challenges.challenges.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class ParticipantChallenge {

    @Id @GeneratedValue
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge pc_challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member pc_member;

    private int pc_totalPrice;

//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "payment_id")
//    private Payment pc_payment;

    public static ParticipantChallenge createParticipant(Challenge challenge, Member member) {
        ParticipantChallenge participantChallenge = new ParticipantChallenge();
        participantChallenge.setPc_challenge(challenge);
        participantChallenge.setPc_member(member);
        challenge.addChallengers();
        //참가시 기부금액을 기본으로 0으로 설정
        participantChallenge.pc_totalPrice = 0;
        return participantChallenge;
    }

}
