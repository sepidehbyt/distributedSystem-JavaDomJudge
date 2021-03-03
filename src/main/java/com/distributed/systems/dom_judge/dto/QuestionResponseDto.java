package com.distributed.systems.dom_judge.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionResponseDto {

    private Long id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
}
