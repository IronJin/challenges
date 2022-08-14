package challenges.challenges.controller.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class CodeDTO {

    @NotEmpty
    private String code; //{"code":"코드입력"}

}
