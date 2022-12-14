package challenges.challenges.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class Payment {

    @Id @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    private int p_price;

    private LocalDate p_paymentTime;

    private String imp_uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id")
    private ParticipantChallenge participantChallenge;


    public static Payment createPayment(String imp_uid ,int p_price, ParticipantChallenge participantChallenge) {
        Payment payment = new Payment();
        payment.setP_paymentTime(LocalDate.now());
        payment.setP_price(p_price);
        payment.setParticipantChallenge(participantChallenge);
        payment.setImp_uid(imp_uid);
        return payment;
    }

}
