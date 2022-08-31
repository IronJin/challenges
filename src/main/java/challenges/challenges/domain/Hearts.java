package challenges.challenges.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Hearts {

    @Id @GeneratedValue
    @Column(name = "hearts_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge h_challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member h_member;

    public static Hearts createHearts(Member member, Challenge challenge) {
        Hearts hearts = new Hearts();
        hearts.setH_member(member);
        hearts.setH_challenge(challenge);
        challenge.addHeart();
        return hearts;
    }

}
