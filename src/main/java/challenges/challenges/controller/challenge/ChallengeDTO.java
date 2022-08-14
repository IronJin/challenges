package challenges.challenges.controller.challenge;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Getter @Setter
public class ChallengeDTO {

    @NotEmpty
    private String c_detail;

    @NotEmpty
    private String c_donation_destination;

    //기부처까지 설정을 해주어야함
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate c_endTime;
}
