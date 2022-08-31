package challenges.challenges.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Reply {

    @Id @GeneratedValue
    @Column(name = "reply_id")
    private Long id;

    private String r_detail;

    private String r_filePath;

    private String r_fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member r_member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    @JsonIgnore
    private Challenge r_challenge;

    public static Reply createReply(String r_detail, String r_fileName, String r_filePath, Member member, Challenge challenge) {

        Reply reply = new Reply();

        reply.setR_detail(r_detail);
        reply.setR_fileName(r_fileName);
        reply.setR_filePath(r_filePath);
        reply.setR_member(member);
        reply.setR_challenge(challenge);

        return reply;
    }

}
