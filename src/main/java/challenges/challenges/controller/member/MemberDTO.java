package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberDTO {

    private String m_name;

    private String m_phoneNumber;

    private String m_birth;

    private String m_loginId;

    private String m_password;
}
