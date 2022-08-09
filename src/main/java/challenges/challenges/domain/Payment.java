package challenges.challenges.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Payment {

    @Id @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    private int p_price;

    //결제수단이랑 결제 여부 같은거 해야함
}
