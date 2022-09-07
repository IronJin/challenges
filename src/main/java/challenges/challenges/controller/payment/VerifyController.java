package challenges.challenges.controller.payment;

import challenges.challenges.controller.member.SessionConst;
import challenges.challenges.controller.participant_challenge.PaymentReqDTO;
import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.service.challenge.ChallengeService;
import challenges.challenges.service.participant_challenge.ParticipantService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
//@RequiredArgsConstructor
@Controller
@RequestMapping("/verifyIamport")
public class VerifyController {

    private final IamportClient iamportClient;
    private final ParticipantService participantService;
    private final ChallengeService challengeService;



    // 생성자를 통해 REST API 와 REST API secret 입력
    @Autowired
    public VerifyController(ParticipantService participantService, ChallengeService challengeService){
        this.iamportClient = new IamportClient("...", "...");
        this.challengeService = challengeService;
        this.participantService = participantService;
    }


    @PostMapping("/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        log.info("paymentByImpUid 진입");
        return iamportClient.paymentByImpUid(imp_uid);
//        CancelData cancelData = new CancelData(imp_uid, true);
//        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    @PostMapping("/challenge/{id}/payment")
    public ResponseEntity<?> savePayment(@PathVariable Long id, HttpServletRequest request, @RequestBody PaymentReqDTO paymentReqDTO) {

        HashMap<String, String> response = new HashMap<>();

        HttpSession session = request.getSession(false);
        CancelData cancelData = new CancelData(paymentReqDTO.getImp_uid(), true);

        if(session == null) {
            response.put("response","로그인을 해야합니다.");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if(loginMember == null) {
            response.put("response","로그인을 다시 해야합니다");

            try {
                iamportClient.cancelPaymentByImpUid(cancelData);
            } catch (IamportResponseException e) {

            } catch (IOException e) {

            }
            return ResponseEntity.ok(response);
        }

        //챌린지 찾아오기
        Challenge findChallenge = challengeService.findOne(id);

        //verifyIamport에서 세션을 만들어서 여기서 검증한 후 없애줘야함
        //여긴 결제승인을 한곳이 아니므로 잘못된 결제먼저해달라고하면됨

        try {
            iamportClient.cancelPaymentByImpUid(cancelData);
            response.put("response","1");
            return ResponseEntity.ok(response);
        } catch (IamportResponseException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            participantService.savePayment(paymentReqDTO.getImp_uid() ,paymentReqDTO.getP_price(), loginMember, findChallenge);
            response.put("response","1");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("response","잘못된 접근입니다");
            return ResponseEntity.ok(response);
        }

    }




}
