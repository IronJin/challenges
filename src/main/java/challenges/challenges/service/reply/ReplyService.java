package challenges.challenges.service.reply;

import challenges.challenges.domain.Reply;
import challenges.challenges.repository.reply.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {

    private final ReplyRepository replyRepository;

    @Transactional
    public void saveReply(Reply reply) {
        replyRepository.saveReply(reply);
    }

}
