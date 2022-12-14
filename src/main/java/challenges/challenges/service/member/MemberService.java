package challenges.challenges.service.member;

import challenges.challenges.controller.member.*;
import challenges.challenges.domain.Member;
import challenges.challenges.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
                memberDTO.getM_password(), memberDTO.getM_email());

        memberRepository.save(member);
    }

    /**
     * 회원가입시 중복성 검사
     */
    public long checkId(CheckLoginIdDTO checkLoginIdDTO) {
        String checkId = checkLoginIdDTO.getM_loginId();
        long count = memberRepository.checkId(checkId);
        return count;
    }

    /**
     * LoginId 로 Member 를 찾기
     */
    public Optional<Member> findByLoginId(String loginId) {
        Optional<Member> findMember = memberRepository.loginByLoginId(loginId);
        return findMember;
    }

    /**
     * LoginId 와 Password 로 Id 찾기
     */
    public Optional<Member> findByPassword(String loginId, String password) {
        Optional<Member> findMember = memberRepository.loginByPassword(loginId, password);
        return findMember;
    }

    /**
     * 회원탈퇴 로직
     */
    @Transactional
    public void deleteMember(Member member) {
        memberRepository.deleteMember(member);
    }

    @Transactional
    public Member updatePassword(Member member, UpdatePasswordDTO updatePasswordDTO) {
        Member updatedMember = memberRepository.updatePassword(member.getM_id(), updatePasswordDTO);
        return updatedMember;
    }

    @Transactional
    public Member updatePhoneNumber(Member member, UpdatePhoneNumberDTO updatePhoneNumberDTO) {
        Member updatedMember = memberRepository.updatePhoneNumber(member.getM_id(), updatePhoneNumberDTO);
        return updatedMember;
    }

    @Transactional
    public Member updateEmail(Member member, EmailCodeDTO emailCodeDTO){
        Member updatedMember = memberRepository.updateEmail(member.getM_id(), emailCodeDTO);
        return updatedMember;
    }



}
