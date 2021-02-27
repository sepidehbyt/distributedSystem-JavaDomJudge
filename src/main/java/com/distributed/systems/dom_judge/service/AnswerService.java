package com.distributed.systems.dom_judge.service;

import com.distributed.systems.dom_judge.enumuration.AnswerStatus;
import com.distributed.systems.dom_judge.model.Answer;
import com.distributed.systems.dom_judge.model.Question;
import com.distributed.systems.dom_judge.model.User;
import com.distributed.systems.dom_judge.repository.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer create(Question question, User user, String path) {
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setStatus(AnswerStatus.PROCESSING);
        answer.setCodePath(path);
        return answerRepository.save(answer);
    }

    public boolean findProcessingByUserAndQuestion(User user, Question question) {
        return answerRepository.findByQuestionAndUserAndStatus(question, user, AnswerStatus.PROCESSING).isPresent();
    }

    public List<Answer> findMyAnswers(User user) {
        return answerRepository.findAllByUserOrderByCreationDateDesc(user);
    }

    public List<Answer> findAllWinners(Question question) {
        return answerRepository.findAllByQuestionAndStatusOrderByCreationDateAsc(question, AnswerStatus.SUCCESS);
    }
}
