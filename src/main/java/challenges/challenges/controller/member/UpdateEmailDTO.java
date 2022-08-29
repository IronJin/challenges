package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class UpdateEmailDTO {

    @NotEmpty
    private String m_email;

}
