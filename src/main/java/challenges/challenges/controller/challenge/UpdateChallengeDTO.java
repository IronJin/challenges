package challenges.challenges.controller.challenge;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class UpdateChallengeDTO {

    @NotEmpty
    private String c_title;

    @NotEmpty
    private String c_detail;

}
