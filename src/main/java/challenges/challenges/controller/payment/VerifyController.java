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
import java.math.BigDecimal;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VerifyController {

    private final IamportClient iamportClient;
    private final ParticipantService participantService;
    private final ChallengeService challengeService;



    //생성자를 통해 REST API 와 REST API secret 입력
    @Autowired
    public VerifyController(ParticipantService participantService, ChallengeService challengeService) {
        this.iamportClient = new IamportClient("...", "...");
        this.challengeService = challengeService;
        this.participantService = participantService;
    }


    //iamport를 이용하여 결제하기를 버튼을 눌렀을때 작동
    @PostMapping("/verifyIamport/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable String imp_uid, HttpServletRequest request) throws IamportResponseException, IOException {
        log.info("paymentByImpUid 진입");
        IamportResponse<Payment> paymentIamportResponse = iamportClient.paymentByImpUid(imp_uid);
        Payment payment = paymentIamportResponse.getResponse();
        HttpSession session = request.getSession(false); //로그인이 된 사용자가 세션을 사용하고 있으므로 false 세팅을 해준것임
        session.setAttribute("payment",payment);
        return paymentIamportResponse;
    }


    //DB에 값을 넣기 위한 작업
    @PostMapping("/challenge/{id}/payment")
    public ResponseEntity<?> savePayment(@PathVariable Long id, HttpServletRequest request, @RequestBody PaymentReqDTO paymentReqDTO) throws IamportResponseException, IOException {

        HashMap<String, String> response = new HashMap<>();

        HttpSession session = request.getSession(false);

        CancelData cancelData = new CancelData(paymentReqDTO.getImp_uid(), true);


        //-----------------로그인 처리 여부와 관련된 메소드이므로 결제와는 무관한 부분
        if(session == null) {
            iamportClient.cancelPaymentByImpUid(cancelData);
            response.put("response","로그인을 해야합니다.");
            return ResponseEntity.ok(response);
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        if(loginMember == null) {
            iamportClient.cancelPaymentByImpUid(cancelData);
            response.put("response","로그인을 다시 해야합니다");
            return ResponseEntity.ok(response);
        }

        //챌린지 찾아오기
        Challenge findChallenge = challengeService.findOne(id);
        //--------여기까지는 결제와 무관한 개인적인 로직입니다.-----------------------



        //verifyIamport에서 세션을 만들어서 여기서 검증한 후 없애줘야함
        //여긴 결제승인을 제대로 수행된 것이 아닐때 작동한다.
        Payment payment = (Payment) session.getAttribute("payment");
        if(payment == null) {
            response.put("response","잘못된 접근입니다.");
            return ResponseEntity.ok(response);
        }

        if(payment.getAmount().equals(paymentReqDTO.getP_price())) {
            iamportClient.cancelPaymentByImpUid(cancelData);
            response.put("response", "잘못된 접근입니다.");
            return ResponseEntity.ok(response);
        }

        //성공적으로 작동하면 try 안에 각 프로젝트의 payment table 에 알맞게 값을 넣어주는 로직을 작성하면된다.
        try {
            participantService.savePayment(paymentReqDTO.getImp_uid() ,paymentReqDTO.getP_price(), loginMember, findChallenge);
            response.put("response","1");
            return ResponseEntity.ok(response);
        }
        //Iamport 의 경우 오류가 발생해도 결제가 이루어지므로(돈이 빠져나간다는 소리) cancelPaymentByImpUid 함수를 이용해 꼭 환불이 될 수 있도록 해야한다.
        //cancelData 는 위에서 imp_uid 를 이용하여 불러왔으므로 그냥 함수의 매개변수값으로 바로 넣어주면 된다.
        catch (Exception e) {
            iamportClient.cancelPaymentByImpUid(cancelData);
            response.put("response","잘못된 접근입니다");
            return ResponseEntity.ok(response);
        }

    }


}
