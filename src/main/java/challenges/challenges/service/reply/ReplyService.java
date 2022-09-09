package challenges.challenges.service.reply;

import challenges.challenges.domain.Challenge;
import challenges.challenges.domain.Member;
import challenges.challenges.domain.Reply;
import challenges.challenges.repository.reply.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;

    @Transactional
    public void saveReply(Reply reply) {
        replyRepository.saveReply(reply);
    }

    public List<Reply> getReplyChallengeByMember(Member member) {
        List<Reply> challengeList = replyRepository.getReplyChallengesByMember(member);
        return challengeList;
    }

    public Reply findReplyById(Long id) {
        Reply reply = replyRepository.findReplyById(id);
        return reply;
    }

    @Transactional
    public void deleteReply(Reply reply) {
        replyRepository.deleteReply(reply);
    }

}
