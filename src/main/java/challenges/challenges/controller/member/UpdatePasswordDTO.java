package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePasswordDTO {

    private String m_oldpassword;

    private String m_newpassword;

}
