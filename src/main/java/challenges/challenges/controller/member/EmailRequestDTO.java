package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class EmailRequestDTO {

    @NotEmpty
    @Email
    private String m_email; //{"m_email":"이메일주소"}

}
