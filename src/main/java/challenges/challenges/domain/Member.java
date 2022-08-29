package challenges.challenges.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

import java.util.*;


@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long m_id;

    @Column(nullable = false)
    private String m_name;

    @Column(nullable = false, length = 20)
    private String m_phoneNumber;

    @Column(nullable = false)
    private String m_birth;

    @Column(nullable = false, unique = true)
    private String m_loginId;

    @Column(nullable = false)
    private String m_password;

    @Column(nullable = false)
    private LocalDate m_createTime;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Challenge> challenges = new ArrayList<>();

    @OneToMany(mappedBy = "pc_member",cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<ParticipantChallenge> participantChallenges = new ArrayList<>();

    public static Member createMember(String m_name, String m_phoneNumber, String m_birth, String m_loginId, String m_password) {
        Member member = new Member();

        member.setM_birth(m_birth);
        member.setM_loginId(m_loginId);
        member.setM_name(m_name);
        member.setM_password(m_password);
        member.setM_phoneNumber(m_phoneNumber);
        member.setM_createTime(LocalDate.now());

        return member;
    }

}
