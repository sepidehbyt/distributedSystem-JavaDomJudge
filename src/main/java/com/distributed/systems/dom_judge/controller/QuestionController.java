package com.distributed.systems.dom_judge.controller;

import com.distributed.systems.dom_judge.dto.GenericRestResponse;
import com.distributed.systems.dom_judge.dto.QuestionDto;
import com.distributed.systems.dom_judge.enumuration.IO;
import com.distributed.systems.dom_judge.mapper.QuestionMapper;
import com.distributed.systems.dom_judge.model.Question;
import com.distributed.systems.dom_judge.model.User;
import com.distributed.systems.dom_judge.service.QuestionService;
import com.distributed.systems.dom_judge.service.ResourceService;
import com.distributed.systems.dom_judge.service.UserService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionMapper questionMapper;
    private final UserService userService;
    private final ResourceService uploadService;

    @Value("${dom.judge.path.files}")
    private String sourceDirectory;

    public QuestionController(QuestionService questionService,
                              QuestionMapper questionMapper,
                              UserService userService,
                              ResourceService uploadService) {
        this.questionService = questionService;
        this.questionMapper = questionMapper;
        this.userService = userService;
        this.uploadService = uploadService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<GenericRestResponse<QuestionDto>> createNewQuestion(@RequestBody QuestionDto questionDto,
                                                                              Authentication authentication) {
        User admin = userService.findByUsername(authentication.getName());
        QuestionDto res = questionMapper.toDto(questionService.create(questionDto, admin));
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS, res), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/upload/{questionId}/{io}", method = RequestMethod.PUT)
    public ResponseEntity<GenericRestResponse<QuestionDto>> uploadIO(@RequestParam("file") MultipartFile uploadFile,
                                                                     @PathVariable Long questionId,
                                                                     @PathVariable IO io) {
        try {
            Optional<String> optional = uploadService.getExtensionByStringHandling(uploadFile.getOriginalFilename());
            if (!optional.isPresent())
                return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE), HttpStatus.INTERNAL_SERVER_ERROR);
            else if (!optional.get().equals("txt"))
                return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE), HttpStatus.INTERNAL_SERVER_ERROR);
            Question question = questionService.findById(questionId);
            String folderName = "question_".concat(String.valueOf(questionId)).concat("_").concat(io.toString());
            String fileName = uploadService.getFileActualName(uploadFile.getOriginalFilename());
            String path = uploadService.saveNewResourceInFile(uploadFile, fileName, folderName, optional.get().toLowerCase());
            if(path == null)
                return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE), HttpStatus.INTERNAL_SERVER_ERROR);
            question = questionService.uploadPath(question, path, io);
            return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS,
                    questionMapper.toDto(question)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.FAILURE, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<GenericRestResponse<List<QuestionDto>>> getMyQuestions(Authentication authentication) {
        User admin = userService.findByUsername(authentication.getName());
        List<QuestionDto> res = questionMapper.toDto(questionService.findMyQuestions(admin));
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS,
                 res), HttpStatus.OK);
    }

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public ResponseEntity<GenericRestResponse<List<QuestionDto>>> getAvailableQuestions(HttpServletRequest request) {
        List<QuestionDto> res = questionMapper.toDto(questionService.findAllAvailable(request.getParameter("title")));
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS, res), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<GenericRestResponse<QuestionDto>> getQuestionById(@PathVariable Long id) {
        QuestionDto res = questionMapper.toDto(questionService.findById(id));
        return new ResponseEntity<>(new GenericRestResponse<>(GenericRestResponse.STATUS.SUCCESS, res), HttpStatus.OK);
    }

    @RequestMapping(value = "/download/{io}/{questionId}", method = RequestMethod.GET)
    @ResponseBody public void getProject(@PathVariable Long questionId,
                                         @PathVariable IO io,
                                         HttpServletResponse response) {
        try {
            Question question = questionService.findById(questionId);
            String resourcePath = io.equals(IO.INPUT) ? question.getInputPath() : question.getOutputPath();
            File file = new File(sourceDirectory.concat(resourcePath));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename="+file.getName());
            InputStream iStream = new FileInputStream(file);
            IOUtils.copy(iStream, response.getOutputStream());
            response.flushBuffer();
        } catch (java.nio.file.NoSuchFileException e) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
