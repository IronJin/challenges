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

    private int c_challengers;

    private LocalDateTime c_startTime;

    private LocalDateTime c_endTime;

    @Enumerated(EnumType.STRING)
    private Donation_Destination c_donation_destination;

    private String c_detail;

    //추천수
    private int c_recommendation;

    //조회수
    private int c_hits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //챌린지 삭제시 댓글도 모두 삭제
    @OneToMany(mappedBy = "r_challenge", cascade = CascadeType.ALL)
    private List<Reply> replyList = new ArrayList<>();

    //챌린지 삭제시 참여 챌린지 모두 삭제
    @OneToMany(mappedBy = "pc_challenge", cascade = CascadeType.ALL)
    private List<ParticipantChallenge> participantChallengeList = new ArrayList<>();

}
