package com.distributed.systems.dom_judge.mapper;

import com.distributed.systems.dom_judge.dto.AnswerDto;
import com.distributed.systems.dom_judge.dto.WinnerDto;
import com.distributed.systems.dom_judge.model.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface AnswerMapper {

    @Mapping(source = "creationDate", target = "sentDate")
    AnswerDto toDto(Answer answer);

    List<AnswerDto> toDto(List<Answer> entityList);

    @Mapping(source = "creationDate", target = "sentDate")
    @Mapping(source = "user.username", target = "username")
    WinnerDto toWinnerDto(Answer answer);

    List<WinnerDto> toWinnerDto(List<Answer> entityList);
}
