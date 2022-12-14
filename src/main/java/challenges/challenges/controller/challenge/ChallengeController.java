package challenges.challenges.controller.challenge;

import challenges.challenges.controller.member.PaymentListDTO;
import challenges.challenges.controller.member.SessionConst;
import challenges.challenges.domain.*;
import challenges.challenges.repository.challenge.ChallengeSearchRepository;
import challenges.challenges.service.challenge.ChallengeService;

import challenges.challenges.service.participant_challenge.ParticipantService;
import challenges.challenges.service.reply.ReplyService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * 마지막 수정일 2022-09-05
 * 작성자 : 양철진
 **/

/** TO DO LIST

 * 챌린지 생성하는 로직 구현 (완료)
 * 챌린지 생성을 한다음에 endTime 에 맞추어 State 가 변하도록 설정을 해주어야함 (완료)
 * 챌린지 수정하기(완료)
 * 챌린지 삭제하기(완료)
 * 내가 참여한 챌린지 조회(완료) - 마이페이지
 * 내가 만든 챌린지 리스트 조회하기(완료) - 마이페이지
 * 회원탈퇴(완료) - 마이페이지
 * 마이페이지에서 내 정보 수정하기(완료) - 마이페이지
 * 댓글 달기(완료)
 * 좋아요 버튼 구성해서 값 올려주기(완료)
 * 챌린지 기간이 끝난 챌린지 리스트를 또 따로 넘겨주어야함(완료) - 프론트해야함
 * 좋아요 한 챌린지 넘겨주기(완료) - 프론트 미완료
 * 내가 단 댓글 마이페이지에 띄우기(완료)
 * 기부하기(완료)
 * 내가 기부한 내역 띄워주기(완료)
 * 끝
 */




@Controller
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ParticipantService participantService;
    private final ReplyService replyService;
    private final IamportClient iamportClient;
    private final ChallengeSearchRepository challengeSearchRepository;

    @Autowired
    public ChallengeController(ParticipantService participantService, ChallengeService challengeService, ReplyService replyService, ChallengeSearchRepository challengeSearchRepository) {
        this.iamportClient = new IamportClient("...", "...");
        this.challengeService = challengeService;
        this.participantService = participantService;
        this.replyService = replyService;
        this.challengeSearchRepository = challengeSearchRepository;
    }

    private static String Path = "C:/KBBankStorage/";

    //완료
    @PostMapping("/challenge/new")
    public ResponseEntity<?> createChallenge(@Valid @RequestBody ChallengeDTO challengeDTO, BindingResult bindingResult, HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();

        //세션에 값이 저장되어있는지 확인 (로그인이 되어 있는지를 확인하는 로직)
        HttpSession session = request.getSession(false);

        //세션이 없으므로 로그인이 안되어 있다는 이야기 이다.
        if(session == null) {
            response.put("response","로그인을 해주세요");
            return ResponseEntity.ok(response);
        }

        //세션에서 로그인된 Member 객체를 꺼내옴
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        //세션에서 꺼낸 로그인 멤버가 빈값일 경우
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }


        //사용자가 입력한 값중에 빈값이 존재하는 경우
        if(bindingResult.hasErrors()) {
            response.put("response","빈값이 있으면 안됩니다. 값을 입력해주세요.");
            return ResponseEntity.ok(response);
        }

        //악의적인 사용자가 11시 59분에 생성하려는 경우 실패로직 작성
        if(challengeDTO.getC_endTime().equals(LocalDate.now())) {
            response.put("response","챌린지 종료일이 오늘입니다. 종료일을 다시 설정해주세요.");
            return ResponseEntity.ok(response);
        }


        /**
         * 여기서부터는 성공로직
         * 챌린지를 정상적으로 생성해준다.
         */
        challengeService.ChallengeSave(challengeDTO.getC_title(), challengeDTO.getC_detail(), challengeDTO.getC_donation_destination(), challengeDTO.getC_endTime(), loginMember);
        response.put("response", "success");
        return ResponseEntity.ok(response);
    }

    /**
     * 매 정각 EndTime 과 현재 시각이 일치하는 챌린지들을 찾아서 챌린지를 더이상 사용하지 못하도록 해준다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void changeChallengeSchedule() {
        challengeService.ChallengeStateChange();
    }


    /**
     * PROCEED 상태인 챌린지들을 보내주는 메소드
     */
    //완료
    @GetMapping("/challenge/list")
    public ResponseEntity<List<Challenge>> challengeList() {
        List<Challenge> challengeList = challengeService.ChallengeList();
        return ResponseEntity.ok(challengeList);
    }

    /**
     * END 상태인 챌린지 리스트들을 보내주는 메소드
     */
    //완료
    @GetMapping("challenge/list/end")
    public ResponseEntity<List<Challenge>> endChallengeList() {
        List<Challenge> endChallengeList = challengeService.endChallengeList();
        return ResponseEntity.ok(endChallengeList);
    }

    /**
     * 특정 챌린지를 생성한 멤버의 간단한 로그인 정보를 주는 메소드
     * 추가적으로 챌린지 정보도 함께 넘겨주어야함
     */
    //완료
    @GetMapping("/challenge/{id}/member")
    public ResponseEntity<CreateChallengeMember> challengeMember(@PathVariable Long id) {
        CreateChallengeMember createChallengeMemberInfo = challengeService.createChallengeMemberInfo(id);
        return ResponseEntity.ok(createChallengeMemberInfo);
    }

    /**
     * 리스트에서 특정 챌린지를 클릭했을때 그 챌린지에 대한 정보 주기
     */
    //완료
    @GetMapping("/challenge/{id}")
    public ResponseEntity<Challenge> challenge(@PathVariable Long id) {
        Challenge findChallenge = challengeService.findOne(id);
        return ResponseEntity.ok(findChallenge);
    }

    /**
     * 로그인 정보랑 챌린지를 만든 멤버의 정보가 일치하면 1을주고 일치하지 않으면 0을 준다.
     * 프론트엔드에서는 1일경우 버튼을 띄워주고 0일경우 버튼을 띄워주면 안된다.
     */
    //완료
    @PostMapping("/challenge/{id}/equal")
    public ResponseEntity<?> challengeEqual(@PathVariable Long id, HttpServletRequest request) {

        HashMap<String, Integer> response = new HashMap<>();

        HttpSession session = request.getSession(false);

        //로그인을 하지 않으면 띄워주지 않음
        if(session == null) {
            response.put("response",0);
            return ResponseEntity.ok(response);
        }

        //세션이 만료된 경우가 있을수 있으므로 이러한 경우에도 버튼을 띄워주지 않음
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response",0);
            return ResponseEntity.ok(response);
        }

        //로그인 정보랑 일치하는지를 확인해야함
        //일치하지 않으면 0을 줌
        CreateChallengeMember challengeMemberInfo = challengeService.createChallengeMemberInfo(id);
        if(!challengeMemberInfo.getM_loginId().equals(loginMember.getM_loginId())) {
            response.put("response",0);
            return ResponseEntity.ok(response);
        }

        //여기서부터는 성공로직
        response.put("response",1);
        return ResponseEntity.ok(response);
    }

    /**
     * 챌린지 수정하기
     * 제목이랑 상세설명만 수정할 수 있음.
     */
    //완료
    @PostMapping("/challenge/{id}/update")
    public ResponseEntity<?> updateChallenge(@PathVariable Long id,
                                             @Valid @RequestBody UpdateChallengeDTO updateChallengeDTO,
                                             BindingResult bindingResult,
                                             HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();

        //빈값이 있을때 오류 보내주기
        if(bindingResult.hasErrors()) {
            response.put("response","빈값이 있습니다.");
            return ResponseEntity.ok(response);
        }

        //세션에서 문제가 발생했을때 오류메세지 보내주기
        HttpSession session = request.getSession(false);
        if(session == null){
            response.put("response","로그인을 해주세요.");
            return ResponseEntity.ok(response);
        }

        //세션 만료
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        //로그인 정보와 일치해야함
        CreateChallengeMember challengeMemberInfo = challengeService.createChallengeMemberInfo(id);
        if(!challengeMemberInfo.getM_loginId().equals(loginMember.getM_loginId())) {
            response.put("response","권한이 없습니다.");
            return ResponseEntity.ok(response);
        }

        //성공 로직 - 제목과 상세설명만을 바꿔주는 로직 실행
        challengeService.updateChallenge(id, updateChallengeDTO);
        response.put("response","성공적으로 변경되었습니다.");
        return ResponseEntity.ok(response);

    }

    /**
     * 챌린지 삭제하기 메서드
     * 세션에 있는 값을 조회하고 세션에 있는 로그인 멤버와 챌린지를 생성한 멤버의 정보가 같다면 삭제를 해주고 나머지는 실패이다.
     */
    //완료
    @PostMapping("/challenge/{id}/delete")
    public ResponseEntity<?> deleteChallenge(@PathVariable Long id, HttpServletRequest request) throws IamportResponseException, IOException {

        HashMap<String, String> response = new HashMap<>();

        HttpSession session = request.getSession(false);

        if(session == null) {
            response.put("response","로그인을 해야합니다.");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        Challenge challenge = challengeService.findOne(id);
        //log.info("challenge loginid ={}",challenge.getMember().getM_loginId());
        //log.info("loginMember loginId={}",loginMember.getM_loginId());
        if(!challenge.getMember().getM_loginId().equals(loginMember.getM_loginId())) {
            response.put("response","해당 챌린지를 삭제할 수 있는 권한이 없습니다.");
            return ResponseEntity.ok(response);
        }

        //여기서부터는 성공로직

        //일단 환불절차를 다 진행을 해주어야 한다.
        //삭제할 챌린지를 가지고 있는 파티시팬트 챌린지를 다 가져오고 파티시팬트의 페이먼트를 가지고 있는 페이먼트 정보를 다 가져온다.
        //그리고 삭제를 해준다.
        List<ParticipantChallenge> participantChallengeList = participantService.findParticipantListByChallenge(challenge);
        for (ParticipantChallenge participantChallenge : participantChallengeList) {
            //파티시팬트를 갖고 있는 페이먼트의 정보 모두 가져오기
            List<Payment> paymentList = participantService.findPaymentList(participantChallenge);
            for (Payment payment : paymentList) {
                //캔슬 데이터 생성
                CancelData cancelData = new CancelData(payment.getImp_uid(),true);
                iamportClient.cancelPaymentByImpUid(cancelData);
                //페이먼트 삭제해주기
                participantService.deletePayment(payment);
            }
        }

        challengeService.deleteChallenge(challenge);
        response.put("response","success");
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 만든 챌린지 리스트 조회하기
     * 로그인 멤버를 통해 내가 만든 챌린지 리스트 꺼내오기
     */
    //완료
    @GetMapping("/mypage/challenges/create")
    public ResponseEntity<List<Challenge>> getCreateChallengeList(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if(session == null) {
            return ResponseEntity.ok(null);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            return ResponseEntity.ok(null);
        }

        List<Challenge> challengeList = challengeService.findChallengesByMemberId(loginMember);

        return ResponseEntity.ok(challengeList);

    }

    /**
     * 내가 참여한 챌린지 리스트 조회
     * 로그인 멤버 정보를 받아오고 participantChallenge 테이블에 값이 있는지 확인 하고 그 리스트들을 리턴해주면됨
     */
    //완료
    @GetMapping("/mypage/challenges/participation")
    public ResponseEntity<List<Challenge>> getParticipationChallengeList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null) {
            return ResponseEntity.ok(null);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            return ResponseEntity.ok(null);
        }

        List<Challenge> participantChallengeList = participantService.findParticipantListByMember(loginMember);

        return ResponseEntity.ok(participantChallengeList);
    }

    /**
     * 챌린지에 동영상 및 댓글 업로드
     */
    //완료
    @PostMapping("/challenge/{id}/reply")
    public ResponseEntity<?> uploadReply(@PathVariable Long id,
                                         @RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                         @RequestPart(value = "replyReq") ReplyDTO replyDTO,
                                         HttpServletRequest request) throws IOException {

        HashMap<String, String> response = new HashMap<>();

        //챌린지 객체 가져오기
        Challenge findChallenge = challengeService.findOne(id);

        //현재 멤버객체 가져오기
        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 해주세요");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        if(multipartFile.isEmpty()) {
            response.put("response","동영상을 반드시 첨부해야 합니다");
            return ResponseEntity.ok(response);
        }


        //사진 저장하기
        //uuid로 변경하고 저장한 다음 파일명하고 파일경로를 받아와야함
        String originalFileName = multipartFile.getOriginalFilename();

        //uuid 로 변환
        String storeFileName = createStoreFileName(originalFileName);

        //로컬에 파일 저장
        multipartFile.transferTo(new File((getFullPath(storeFileName))));


        //Reply 만들어주기
        Reply reply = Reply.createReply(replyDTO.getR_detail(), storeFileName, Path, loginMember, findChallenge);

        //DB에 저장하기기
        replyService.saveReply(reply);
        response.put("response","댓글달기 완료");
        return ResponseEntity.ok(response);
    }

    //" "여기안에 로컬저장소를 입력하면됨
    public String getFullPath(String filename) {
        return Path + filename;
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    /**
     * 댓글 리스트 프론트엔드에 보내주기
     */
    //완료
    @GetMapping("/challenge/{id}/replyList")
    public ResponseEntity<List<ReplyListDTO>> replyList(@PathVariable Long id) {
        Challenge challenge = challengeService.findOne(id);
        List<Reply> replyList = challengeService.getReplyList(challenge);
        List<ReplyListDTO> replyListDTOs = new ArrayList<>();
        for (Reply reply : replyList) {
            ReplyListDTO replyListDTO = new ReplyListDTO();
            replyListDTO.setId(reply.getId());
            replyListDTO.setR_detail(reply.getR_detail());
            replyListDTO.setR_fileName(reply.getR_fileName());
            replyListDTO.setR_filePath(reply.getR_filePath());
            replyListDTO.setR_member(reply.getR_member().getM_loginId());
            replyListDTOs.add(replyListDTO);
        }
        return ResponseEntity.ok(replyListDTOs);
    }


    /**
     * 좋아요를 누른적이 있는지 없는지를 보내주는 메소드
     * 있으면 1, 없으면 0 을 보내줌
     * 1일경우 꽉찬 하트를 화면에 표시, 0일경우 빈하트를 표시
     */
    //완료
    @GetMapping("/challenge/{id}/heart")
    public ResponseEntity<?> checkHeart(@PathVariable Long id, HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();
        Challenge challenge = challengeService.findOne(id);
        int hearts = challenge.getC_hearts();

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","0");
            response.put("hearts",String.valueOf(hearts));
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","0");
            response.put("hearts",String.valueOf(hearts));
            return ResponseEntity.ok(response);
        }

        long count = challengeService.checkHearts(loginMember, challenge);

        if(count == 1) {
            response.put("response","1");
            response.put("hearts",String.valueOf(hearts));
            return ResponseEntity.ok(response);
        }

        response.put("response","0");
        response.put("hearts",String.valueOf(hearts));
        return ResponseEntity.ok(response);
    }

    /**
     * 좋아요 버튼을 눌렀을때 작동하는 메서드
     * 프론트엔드가 서버에 1을 넘겨주면 좋아요를 누른것, 0을 넘겨주면 좋아요를 취소한 것임
     */
    //완료
    @PostMapping("/challenge/{id}/heart")
    public ResponseEntity<?> updateHearts(@PathVariable Long id, @RequestBody LikeDTO likeDTO, HttpServletRequest request) {

        HashMap<String, String> errorResponse = new HashMap<>();
        HashMap<String, Integer> response = new HashMap<>();

        HttpSession session = request.getSession(false);
        if(session == null) {
            errorResponse.put("response","로그인을 다시 해야합니다.");
            return ResponseEntity.ok(errorResponse);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            errorResponse.put("response","로그인을 다시 해야합니다");
            return ResponseEntity.ok(errorResponse);
        }

        Challenge challenge = challengeService.findOne(id);

        log.info("like : {}",likeDTO.getLike());

        //좋아요 버튼을 누른것임
        if(likeDTO.getLike().equals("0")) {
            challengeService.heartsUp(loginMember, challenge);
            response.put("response",challenge.getC_hearts());
            return ResponseEntity.ok(response);
        }

        //좋아요 버튼을 취소한것임
        if(likeDTO.getLike().equals("1")) {
            try{
                challengeService.heartsDown(loginMember,challenge);
                response.put("response",challenge.getC_hearts());
                return ResponseEntity.ok(response);
            }catch (Exception e) {
                errorResponse.put("response","에러가 발생했습니다.");
                return ResponseEntity.ok(errorResponse);
            }

        }

        //알수없는 오류가 발생한 것임
        errorResponse.put("response","알수없는 에러가 발생했습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 멤버가 좋아요한 챌린지 리스트
     */
    //완료
    @GetMapping("/mypage/challenges/like")
    public ResponseEntity<?> getLikeChallengeByMember(HttpServletRequest request) {
        HashMap<String, String> response = new HashMap<>();

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 해야합니다");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        List<Hearts> likeChallengeByMember = challengeService.getLikeChallengeByMember(loginMember);
        List<Challenge> challengeList = new ArrayList<>();

        for (Hearts hearts : likeChallengeByMember) {
            Challenge challenge = hearts.getH_challenge();
            challengeList.add(challenge);
        }

        return ResponseEntity.ok(challengeList);
    }


    /**
     * 특정 멤버가 댓글을 단 챌린지 리스트 중복허용
     */
    //미완료
    @GetMapping("/mypage/challenges/reply")
    public ResponseEntity<?> getReplyChallengeList(HttpServletRequest request) {

        HashMap<String, String> response = new HashMap<>();

        HttpSession session = request.getSession(false);
        if(session == null) {
            response.put("response","로그인을 해야합니다");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        List<Reply> replyList = replyService.getReplyChallengeByMember(loginMember);

        List<ReplyResDTO> replyResDTOList = new ArrayList<>();
        for (Reply reply : replyList) {
            ReplyResDTO replyResDTO = new ReplyResDTO();
            replyResDTO.setR_donation_destination(reply.getR_challenge().getC_donation_destination());
            replyResDTO.setR_title(reply.getR_challenge().getC_title());
            replyResDTO.setR_localDateTime(reply.getR_localDateTime());
            replyResDTO.setR_detail(reply.getR_detail());
            replyResDTO.setR_id(reply.getId());
            replyResDTOList.add(replyResDTO);
        }

        return ResponseEntity.ok(replyResDTOList);
    }

    /**
     * 지금까지 내가 결제한 내역을 보여주는 메서드
     */
    @GetMapping("/mypage/challenges/payment")
    public ResponseEntity<?> getPaymentList(HttpServletRequest request) {

        HashMap<String,String> response = new HashMap<>();

        HttpSession session = request.getSession(false);

        if(session == null) {
            response.put("response","로그인을 먼저 해야합니다");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해주세요");
            return ResponseEntity.ok(response);
        }

        List<Payment> paymentList = challengeService.getMemberPaymentList(loginMember);
        List<PaymentListDTO> paymentListDTOList = new ArrayList<>();

        Long index = 1L;

        for (Payment payment : paymentList) {
            PaymentListDTO paymentListDTO = new PaymentListDTO();
            paymentListDTO.setP_trash(index);
            paymentListDTO.setP_paymentDate(payment.getP_paymentTime());
            paymentListDTO.setP_challengeId(payment.getParticipantChallenge().getPc_challenge().getId());
            paymentListDTO.setP_challengeName(payment.getParticipantChallenge().getPc_challenge().getC_title());
            paymentListDTO.setP_price(payment.getP_price());
            paymentListDTOList.add(paymentListDTO);
            index++;
        }

        return ResponseEntity.ok(paymentListDTOList);

    }

    /**
     * 댓글 삭제하기
     * 삭제하려는 댓글을 id 로 가져온다
     */
    @PostMapping("/delete/reply/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable Long id, HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        HashMap<String, String> response = new HashMap<>();

        if(session == null) {
            response.put("response","잘못된 접근입니다");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if(loginMember == null) {
            response.put("response","로그인을 다시 해야합니다");
            return ResponseEntity.ok(response);
        }

        //댓글 가져오기
        Reply reply = replyService.findReplyById(id);

        if(!reply.getR_member().getM_id().equals(loginMember.getM_id())) {
            response.put("response","잘못된 접근입니다.");
            return ResponseEntity.ok(response);
        }

        //여기서부터는 성공로직
        replyService.deleteReply(reply);
        response.put("response","성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }

    /**
     * 메인페이지에 챌린지의 좋아요 DESC 순으로 8개 넘겨주기
     */
    @GetMapping("/main/challenges")
    public ResponseEntity<?> getChallengesLikeDesc() {
        List<Challenge> challengeList = challengeService.getChallengeLikeDesc();
        return ResponseEntity.ok(challengeList);
    }

    /**
     * 검색 쿼리 작성하기
     */
    @GetMapping("/search/challenge")
    public ResponseEntity<List<Challenge>> getChallengesByTitle(@RequestParam(value = "keyword") String keyword) {

        List<Challenge> challengeList = challengeSearchRepository.findByTitleContaining(keyword);

        return ResponseEntity.ok(challengeList);
    }

    /**
     * 기부처를 받아서 기부처별 챌린지를 넘겨주는 메소드
     */
    @GetMapping("/category/challenge")
    public ResponseEntity<List<Challenge>> getChallengesByDestination(@RequestParam(value = "destination") String destination) {
        List<Challenge> challengeList = challengeSearchRepository.findByDestinationContaining(destination);
        return ResponseEntity.ok(challengeList);
    }

}
