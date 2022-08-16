package challenges.challenges.controller.challenge;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Getter @Setter
public class ChallengeDTO {

    @NotEmpty
    private String c_title;

    @NotEmpty
    private String c_detail;

    //기부할 단체
    @NotEmpty
    private String c_donation_destination;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate c_endTime;
}
