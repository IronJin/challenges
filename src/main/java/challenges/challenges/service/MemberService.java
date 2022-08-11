package challenges.challenges.service;

import challenges.challenges.controller.member.CheckLoginIdDTO;
import challenges.challenges.controller.member.MemberDTO;
import challenges.challenges.domain.Member;
import challenges.challenges.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입 정보를 통한 회원가입
     */
    @Transactional
    public void save(MemberDTO memberDTO) {

        Member member = Member.createMember(memberDTO.getM_name(), memberDTO.getM_phoneNumber(), memberDTO.getM_birth(), memberDTO.getM_loginId(),
                memberDTO.getM_password());

        memberRepository.save(member);
    }

    /**
     * 중복성 검사
     */
    public long checkId(CheckLoginIdDTO checkLoginIdDTO) {
        String checkId = checkLoginIdDTO.getM_loginId();
        long count = memberRepository.checkId(checkId);
        return count;
    }

}
