package challenges.challenges.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long m_id;
    
    private String m_name;

    private String m_phoneNumber;

    private String m_birth;

    private String m_loginId;

    private String m_password;

    private LocalDateTime m_localDateTime;



}
