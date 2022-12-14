package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class UpdatePasswordDTO {

    @NotEmpty
    private String m_oldpassword;

    @NotEmpty
    private String m_newpassword;

}
