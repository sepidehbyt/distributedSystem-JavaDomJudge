package com.distributed.systems.dom_judge.repository;

import com.distributed.systems.dom_judge.enumuration.AnswerStatus;
import com.distributed.systems.dom_judge.model.Answer;
import com.distributed.systems.dom_judge.model.Question;
import com.distributed.systems.dom_judge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByQuestionAndStatusTrueOrderByCreationDateAsc(Question question);

    List<Answer> findAllByUserOrderByCreationDateDesc(User user);

    List<Answer> findAllByQuestionAndStatusOrderByCreationDateAsc(Question question, AnswerStatus status);

    List<Answer> findByQuestionAndUser(Question question, User user);

    Optional<Answer> findByQuestionAndUserAndStatus(Question question, User user, AnswerStatus status);
}