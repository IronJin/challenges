package challenges.challenges.controller.challenge;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReplyResDTO {

    private LocalDateTime r_localDateTime;

    private String r_title;

    private String r_donation_destination;

    private String r_detail;

}
