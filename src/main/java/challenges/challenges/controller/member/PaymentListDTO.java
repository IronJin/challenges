package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class PaymentListDTO {

    //챌린지 이름
    private String p_challengeName;

    //챌린지 눌렀을때 PathVariable 을 통한 Url 매핑
    private Long p_challengeId;

    //기부한 날짜
    private LocalDate p_paymentDate;

    //기부금액
    private int p_price;

}
