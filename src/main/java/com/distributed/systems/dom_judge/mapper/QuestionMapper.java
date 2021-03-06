package com.distributed.systems.dom_judge.mapper;

import com.distributed.systems.dom_judge.dto.QuestionDto;
import com.distributed.systems.dom_judge.dto.QuestionResponseDto;
import com.distributed.systems.dom_judge.model.Question;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class QuestionMapper {

    @Mapping(target = "startDate", source = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "endDate", source = "endDate", dateFormat = "yyyy-MM-dd")
    public abstract QuestionDto toDto(Question question);

    public abstract List<QuestionDto> toDto(List<Question> entityList);

    public abstract QuestionResponseDto toResponseDto(Question question);

    public abstract List<QuestionResponseDto> toResponseDto(List<Question> entityList);

    @Mapping(target = "startDate", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "endDate", dateFormat = "yyyy-MM-dd")
    public abstract Question create(QuestionDto questionDto);

    @AfterMapping
    protected void updateIO(@MappingTarget QuestionDto questionDto) {
        if(questionDto.getInputPath() != null)
            questionDto.setInputPath(questionDto.getId() + "*" + questionDto.getInputPath().substring(6));
        if(questionDto.getOutputPath() != null)
            questionDto.setOutputPath(questionDto.getId() + "*" + questionDto.getOutputPath().substring(6));
    }
}
