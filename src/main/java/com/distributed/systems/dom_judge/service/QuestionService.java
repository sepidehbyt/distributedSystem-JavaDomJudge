package com.distributed.systems.dom_judge.service;

import com.distributed.systems.dom_judge.dto.QuestionDto;
import com.distributed.systems.dom_judge.enumuration.IO;
import com.distributed.systems.dom_judge.mapper.QuestionMapper;
import com.distributed.systems.dom_judge.model.Question;
import com.distributed.systems.dom_judge.model.User;
import com.distributed.systems.dom_judge.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Value("${dom.judge.path.files}")
    private String sourceDirectory;

    public QuestionService(QuestionRepository questionRepository,
                           QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
    }

    public Question create(QuestionDto questionDto, User admin) throws DataAccessException {
        Question question = questionMapper.create(questionDto);
        question.setAdmin(admin);
        questionRepository.save(question);
        return question;
    }

    public Question findById(Long id) throws DataAccessException {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Question findAvailableById(Long id) throws DataAccessException {
        return questionRepository.findByIdAndStartDateBeforeAndEndDateAfter(id, new Date(), new Date()).orElseThrow(EntityNotFoundException::new);
    }

    public Question uploadPath(Question question, String path, IO io) {
        switch (io) {
            case INPUT:
                question.setInputPath(path);
                break;
            case OUTPUT:
                question.setOutputPath(path);
        }
        return questionRepository.save(question);
    }

    public List<Question> findMyQuestions(User admin) throws DataAccessException {
        return questionRepository.findAllByAdmin(admin);
    }

    public List<Question> findAllAvailable(String title) throws DataAccessException {
        if(title == null)
            return questionRepository.findAllByStartDateBeforeAndEndDateAfter(new Date(), new Date());
        return questionRepository.findAllByTitleContainingAndStartDateBeforeAndEndDateAfter(title, new Date(), new Date());
    }
}
