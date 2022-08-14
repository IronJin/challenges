package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberDTO {

    @NotEmpty
    private String m_name;

    @NotEmpty
    private String m_phoneNumber;

    @NotEmpty
    private String m_birth;

    @NotEmpty
    private String m_loginId;

    @NotEmpty
    private String m_password;
}
