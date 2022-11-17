package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class MemberDTO {

    @NotBlank
    private String m_name;

    @NotBlank
    private String m_phoneNumber;

    @NotBlank
    private String m_birth;

    @NotBlank
    private String m_loginId;

    @NotBlank
    @Range(min = 8)
    //숫자와 영어 대소문자, 특수기호가 적어도 하나씩 이상 포함된 8자에서 20자 사이의 비밀번호
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}")
    private String m_password;

    @NotBlank
    @Email
    private String m_email;
}
