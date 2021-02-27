package com.distributed.systems.dom_judge.repository;

import com.distributed.systems.dom_judge.model.Question;
import com.distributed.systems.dom_judge.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findByIdAndStartDateBeforeAndEndDateAfter(Long id, Date startDate, Date endDate);

    List<Question> findAllByStartDateBeforeAndEndDateAfter(Date startDate, Date endDate);

    List<Question> findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(String title, Date startDate, Date endDate);

    List<Question> findAllByAdmin(User admin);
}