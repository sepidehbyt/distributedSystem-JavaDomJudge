package com.distributed.systems.dom_judge.dto;

import com.distributed.systems.dom_judge.enumuration.AnswerStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnswerDto {

    private QuestionResponseDto question;
    private AnswerStatus success;
}
