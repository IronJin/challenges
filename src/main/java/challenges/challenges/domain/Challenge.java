package challenges.challenges.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Challenge {

    @Id @GeneratedValue
    @Column(name = "challenge_id")
    private Long id;

    private Long c_price;

    //총 챌린지 참여자수
    private int c_challengers;

    private LocalDateTime c_startTime;

    private LocalDateTime c_endTime;

    //기부처
    @Enumerated(EnumType.STRING)
    private Donation_Destination c_donation_destination;

    //설명
    private String c_detail;

    //추천수
    private int c_recommendation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //챌린지 삭제시 댓글도 모두 삭제
    @OneToMany(mappedBy = "r_challenge", cascade = CascadeType.ALL)
    private List<Reply> replyList = new ArrayList<>();

    //챌린지 삭제시 참여 챌린지 모두 삭제
    @OneToMany(mappedBy = "pc_challenge", cascade = CascadeType.ALL)
    private List<ParticipantChallenge> participantChallengeList = new ArrayList<>();

    public static Challenge createChallenge(String c_detail, Donation_Destination c_donation_destination, LocalDateTime c_endTime, Member member) {

        Challenge challenge = new Challenge();

        challenge.setC_challengers(0);
        challenge.setC_detail(c_detail);
        challenge.setC_endTime(c_endTime);
        challenge.setC_price(0L);
        challenge.setMember(member);
        challenge.setC_startTime(LocalDateTime.now());
        challenge.setParticipantChallengeList(new ArrayList<>());
        challenge.setC_donation_destination(c_donation_destination);
        //challenge.setReplyList(new ArrayList<>());
        return challenge;
    }

}
