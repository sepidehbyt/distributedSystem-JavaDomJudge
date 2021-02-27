package com.distributed.systems.dom_judge.controller;

import com.distributed.systems.dom_judge.dto.AnswerDto;
import com.distributed.systems.dom_judge.dto.GenericRestResponse;
import com.distributed.systems.dom_judge.dto.WinnerDto;
import com.distributed.systems.dom_judge.mapper.AnswerMapper;
import com.distributed.systems.dom_judge.model.Answer;
import com.distributed.systems.dom_judge.model.Question;
import com.distributed.systems.dom_judge.model.User;
import com.distributed.systems.dom_judge.service.AnswerService;
import com.distributed.systems.dom_judge.service.QuestionService;
import com.distributed.systems.dom_judge.service.UploadService;
import com.distributed.systems.dom_judge.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;
    private final AnswerMapper answerMapper;
    private final QuestionService questionService;
    private final UploadService uploadService;
    private final UserService userService;

    public AnswerController(AnswerService answerService,
                            AnswerMapper answerMapper,
                            QuestionService questionService,
                            UploadService uploadService,
                            UserService userService) {
        this.answerService = answerService;
        this.answerMapper = answerMapper;
        this.questionService = questionService;
        this.uploadService = uploadService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/upload/{questionId}", method = RequestMethod.POST)
    public ResponseEntity<GenericRestResponse<AnswerDto>> uploadAnswer(@RequestParam("file") MultipartFile uploadFile,
                                                                       @PathVariable Long questionId,
                                                                       Authentication authentication) {
        try {
            Optional<String> optional = uploadService.getExtensionByStringHandling(uploadFile.getOriginalFilename());
            if (!optional.isPresent())
                return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE), HttpStatus.INTERNAL_SERVER_ERROR);
            else if (!optional.get().equals("java"))
                return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE), HttpStatus.INTERNAL_SERVER_ERROR);
            User user = userService.findByUsername(authentication.getName());
            Question question = questionService.findById(questionId);
            if(answerService.findProcessingByUserAndQuestion(user, question)) throw new EntityExistsException();
            String name = "answer_".concat(String.valueOf(user.getId())).concat("_").concat(String.valueOf(questionId));
            String path = uploadService.saveNewResourceInFile(uploadFile, name, "java");
            if (path == null)
                return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE), HttpStatus.INTERNAL_SERVER_ERROR);
            Answer answer = answerService.create(question, user, path);
            return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS,
                    answerMapper.toDto(answer)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<GenericRestResponse<List<AnswerDto>>> getMyAnswers(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        List<AnswerDto> res = answerMapper.toDto(answerService.findMyAnswers(user));
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS, res), HttpStatus.OK);
    }

    @RequestMapping(value = "/winners/{questionId}", method = RequestMethod.GET)
    public ResponseEntity<GenericRestResponse<List<WinnerDto>>> getAvailableQuestions(@PathVariable Long questionId) {
        Question question = questionService.findById(questionId);
        List<WinnerDto> res = answerMapper.toWinnerDto(answerService.findAllWinners(question));
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS, res), HttpStatus.OK);
    }
}
