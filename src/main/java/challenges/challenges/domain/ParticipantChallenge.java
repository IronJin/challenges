package challenges.challenges.domain;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment pc_payment;

    public static ParticipantChallenge createParticipant(Challenge challenge, Member member) {
        ParticipantChallenge participantChallenge = new ParticipantChallenge();
        participantChallenge.setPc_challenge(challenge);
        participantChallenge.setPc_member(member);

        return participantChallenge;
    }

}
